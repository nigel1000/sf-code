package cn.sf.tools.id.snow;

import cn.sf.bean.constants.LogString;
import cn.sf.bean.excps.ThrowKnowException;
import lombok.extern.slf4j.Slf4j;

import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 000 - 00000 - 00000 - 0000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，48位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下面程序IdWorker类的startTime属性）。 43位的时间截，可以使用278年，年T = (1L << 43) / (1000L *
 * 60 * 60 * 24 * 365) = 278<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位dataCenterId和5位workerId<br>
 * 10位序列，毫秒内的计数，支持每个节点每毫秒(同一机器，同一时间截)产生1023个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
@Slf4j
public class IdUtil {

    /** 开始时间截 (2017-01-01) */
    private static final long twEpoch = 1483200000000L;

    /** 机器id所占的位数 */
    private static final long workerIdBits = 5L;
    /** 数据标识id所占的位数 */
    private static final long dataCenterIdBits = 5L;
    /** 序列在id中占的位数 */
    private static final long sequenceBits = 10L;

    /** 支持的最大机器id (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    /** 支持的最大数据中心id，结果是31 （0x11111） */
    private static final long maxDataCenterId = ~(-1L << dataCenterIdBits);
    /** 生成序列的掩码 */
    private static final long sequenceMask = ~(-1L << sequenceBits);

    /** 机器ID向左移5位 */
    private static final long workerIdShift = sequenceBits;
    /** 数据标识id向左移位数 */
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;
    /** 时间截向左移位数 */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /** 工作机器ID(0~31) */
    private long workerId;
    /** 数据中心ID(0~31) */
    private long dataCenterId;
    /** 毫秒内序列 2的sequenceBits次减1 */
    private long sequence = 0L;
    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    /** 单例模式 */
    private static class InnerInstance {
        private static long workId;
        private static long dataCenterId;
        static {
            // 配置比较麻烦 按服务器mac的属性做md5定workId值
            try {
                StringBuilder sb = new StringBuilder();
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements()) {
                    NetworkInterface ni = e.nextElement();
                    byte[] mac = ni.getHardwareAddress();
                    if (mac != null) {
                        sb.append(ni.toString());
                        for (int i = 0; i < mac.length; i++) {
                            if (i != 0) {
                                sb.append("-");
                            }
                            // 字节转换为整数
                            // 每8位一个数字
                            String str = Integer.toHexString(mac[i] & 0xff);
                            if (str.length() == 1) {
                                sb.append("0");
                            }
                            sb.append(str);
                        }
                        sb.append("|");
                    }
                }
                String macAddress = sb.toString();
                log.info(LogString.initPre + "mac address:" + macAddress + ",hashCode:" + macAddress.hashCode());
                // hash倒数10-5位
                int hash = macAddress.hashCode();
                dataCenterId = (hash >> workerIdBits) & maxDataCenterId;
                // hash倒数五位
                workId = hash & maxWorkerId;
                // System.out.println(Long.toBinaryString(macAddress.hashCode()));
                // System.out.println(Long.toBinaryString(dataCenterId));
                // System.out.println(Long.toBinaryString(workId));
            } catch (Exception e) {
                throw ThrowKnowException.valueOf("init 机器ID workId 数据中心ID failed!!", e);
            }
            log.info(LogString.initPre + "workId:" + workId + ",dataCenterId:" + dataCenterId);
        }
        private static IdUtil instance = new IdUtil(workId, dataCenterId);
    }

    private static IdUtil getInstance() {
        return InnerInstance.instance;
    }

    /**
     * 构造函数
     * 
     * @param workerId 工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    private IdUtil(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     * 
     * @return SnowflakeId
     */
    public static synchronized long nextId() {
        IdUtil idUtil = IdUtil.getInstance();
        long timestamp = System.currentTimeMillis();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < idUtil.lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                            idUtil.lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (idUtil.lastTimestamp == timestamp) {
            idUtil.sequence = (idUtil.sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (idUtil.sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = System.currentTimeMillis();
                while (timestamp <= idUtil.lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            idUtil.sequence = 0L;
        }

        // 上次生成ID的时间截
        idUtil.lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twEpoch) << timestampLeftShift) //
                | (idUtil.dataCenterId << dataCenterIdShift) //
                | (idUtil.workerId << workerIdShift) //
                | idUtil.sequence;
    }

    /** 测试 单线程一秒一百万个id sequence 为10 一毫秒最多1024个 1024*1000 */
    public static void main(String[] args) {
        System.out.println(IdUtil.nextId());
        int count = 0;
        long start = System.currentTimeMillis();
        for (; System.currentTimeMillis() - start < 1000; count++) {
            IdUtil.nextId();
        }
        long end = System.currentTimeMillis() - start;
        System.out.println(end);
        System.out.println(count);
    }

}
