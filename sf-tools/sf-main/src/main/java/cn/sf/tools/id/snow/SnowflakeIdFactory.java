package cn.sf.tools.id.snow;

import com.google.common.collect.Maps;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@ToString
@Slf4j
public class SnowflakeIdFactory {

    private SnowflakeIdFactory(){}

    private static HashMap<Long,HashMap<Long,SnowflakeId>> instances = Maps.newHashMap();
    public synchronized static SnowflakeId newInstance(long workerId, long dataCenterId){
        if(instances.get(dataCenterId)==null||
                instances.get(dataCenterId).get(workerId)==null){
            HashMap<Long,SnowflakeId> temp = Maps.newHashMap();
            temp.put(workerId,new SnowflakeIdFactory.SnowflakeId(workerId,dataCenterId));
            instances.put(dataCenterId,temp);
        }
        return instances.get(dataCenterId).get(workerId);
    }
    
    static class SnowflakeId{
        private final long twEpoch = 1288834974657L;
        private final long workerIdBits = 5L;  //31
        private final long dataCenterIdBits = 5L;
        private final long maxWorkerId = -1L ^ (-1L << workerIdBits);  //workerIdBits位全是1
        private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);  //dataCenterIdBits位全是1
        private final long sequenceBits = 12L;  //4096
        private final long workerIdShift = sequenceBits;
        private final long dataCenterIdShift = sequenceBits + workerIdBits;
        private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
        private final long sequenceMask = -1L ^ (-1L << sequenceBits);

        private long workerId;
        private long dataCenterId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        private SnowflakeId(long workerId, long dataCenterId) {
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
                throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
            }
            this.workerId = workerId;
            this.dataCenterId = dataCenterId;
        }

        public synchronized long nextId() {
            long timestamp = System.currentTimeMillis();
            if (timestamp < lastTimestamp) {
                //服务器时钟被调整了,ID生成器停止服务.
                throw new RuntimeException(
                        String.format("Clock moved backwards or concurrent greater than 4096 time.  " +
                                "Refusing to generate id for lastTimestamp=%d and timestamp=%d", lastTimestamp,timestamp));
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask; //4096&4095=0
                if (sequence == 0) {
                    //超过4095,阻塞到下一毫秒才给返回id值
                    timestamp = System.currentTimeMillis();
                    while (timestamp <= lastTimestamp) {
                        timestamp = System.currentTimeMillis();
                    }
                    lastTimestamp = timestamp;
//                lastTimestamp = timestamp + 1;  超过4095,直接时间加一毫秒,在这未加一毫秒时间前的时间内生成id直接失败
                }
            } else {
                sequence = 0L;
                lastTimestamp = timestamp;
            }
            return ((timestamp - twEpoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
        }

    }

    public static void testProductIdByMoreThread(int dataCenterId, int workerId, int n) throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        Set<Long> setAll = new HashSet<>();
        int tCount = 10;  //线程数
        CountDownLatch cdLatch = new CountDownLatch(tCount);
        long start = System.currentTimeMillis();
        int threadNo = dataCenterId;
        Map<String,SnowflakeId> idFactories = new HashMap<>();
        for(int i=1;i<=tCount;i++){
            //用线程名称做map key.
            idFactories.put("snowflake"+i,SnowflakeIdFactory.newInstance(workerId, threadNo++));
        }
        for(int i=1;i<=tCount;i++){
            Thread temp =new Thread(() -> {
                    Set<Long> setId = new HashSet<>();
                    SnowflakeId idWorker = idFactories.get(Thread.currentThread().getName());
                    for(int j=0;j<n;j++){
                        try {
                            setId.add(idWorker.nextId());
                        }catch (Exception e){
                            log.warn(e.getMessage());
                        }
                    }
                    synchronized (setAll){
                        setAll.addAll(setId);
                        log.info("{}生产了{}个id,并成功加入到setAll中.",Thread.currentThread().getName(),n);
                    }
                    cdLatch.countDown();
            },"snowflake"+i);
            threadList.add(temp);
        }
        for(int i=0;i<tCount;i++){
            threadList.get(i).start();
        }
        cdLatch.await();

        long end1 = System.currentTimeMillis() - start;

        log.info("共耗时:{}毫秒,预期应该生产{}个id, 实际合并总计生成ID个数:{}",end1,tCount * n,setAll.size());

    }

    public static void testProductId(int dataCenterId, int workerId, int n){
        SnowflakeId idWorker = SnowflakeIdFactory.newInstance(workerId, dataCenterId);
        SnowflakeId idWorker2 = SnowflakeIdFactory.newInstance(workerId+1, dataCenterId);
        Set<Long> setOne = new HashSet<>();
        Set<Long> setTow = new HashSet<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            setOne.add(idWorker.nextId());//加入set
        }
        long end1 = System.currentTimeMillis() - start;
        log.info("第一批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}",n,setOne.size(),end1);

        for (int i = 0; i < n; i++) {
            setTow.add(idWorker2.nextId());//加入set
        }
        long end2 = System.currentTimeMillis() - start;
        log.info("第二批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}",n,setTow.size(),end2);

        setOne.addAll(setTow);
        log.info("合并总计生成ID个数:{}",setOne.size());

    }

    public static void testPerSecondProductIdNums(){
        SnowflakeId idWorker = SnowflakeIdFactory.newInstance(22, 20);
        long start = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; System.currentTimeMillis()-start<1000; i++,count=i) {
            /**  测试方法一: 此用法纯粹的生产ID,每秒生产ID个数为300w+ */
            idWorker.nextId();
            /**  测试方法二: 在log中打印,同时获取ID,此用法生产ID的能力受限于log.error()的吞吐能力.
             * 每秒徘徊在10万左右. */
            //log.error("{}",idWorker.nextId());
        }
        long end = System.currentTimeMillis()-start;
        System.out.println(end);
        System.out.println(count);
    }

    public static void main(String[] args) {
        /** case1: 测试每秒生产id个数?
         *   结论: 每秒生产id个数300w+ */
        testPerSecondProductIdNums();

        /** case2: 单线程-测试多个生产者同时生产N个id,验证id是否有重复?
         *   结论: 验证通过,没有重复. */
//        testProductId(1,2,10000);//验证通过!
//        testProductId(1,2,20000);//验证通过!

        /** case3: 多线程-测试多个生产者同时生产N个id, 全部id在全局范围内是否会重复?
         *   结论: 验证通过,没有重复. */
//        try {
//            testProductIdByMoreThread(1,2,100000);//单机测试此场景,性能损失至少折半!
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}

