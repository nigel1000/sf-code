package cn.sf.utils.serializes;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

@Slf4j
public class SerializeUtil {

     // java序列化
    public static byte[] javaSerialize(Object object) {

        ObjectOutputStream oos;
        ByteArrayOutputStream baos;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {

        }
        return null;
    }

     // java反序列化
    public static Object javaDeserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {

        }
        return null;
    }

    private final static int SIZE = 4 * 1024 * 1024;
     // protostuff 序列化
    public static <T> byte[] protostuffSerialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("序列化对象(" + obj + ")!");
        }
        @SuppressWarnings("unchecked")
        LinkedBuffer buffer = LinkedBuffer.allocate(SIZE);
        byte[] protostuff = null;
        try {
            Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            log.debug("[SerializeUtil] serialize " + obj + "failed");
        } finally {
            buffer.clear();
        }
        return protostuff;
    }
    //protostuff 反序列化
    public static <T> T protostuffDeserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }
        T instance = null;
        try {
            instance = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.debug("[deserialize] deserialize " + "failed");
        } finally {
            Schema<T> schema = RuntimeSchema.getSchema(targetClass);
            if (instance == null) {
                instance = schema.newMessage();
            }
            ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
        }

        return instance;
    }

    public static <T> byte[] protostuffSerializeList(List<T> objList) {
        if (objList == null || objList.isEmpty()) {
            throw new RuntimeException("序列化对象列表(" + objList + ")参数异常!");
        }
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(SIZE);
        byte[] protostuff = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
            protostuff = bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("序列化对象列表(" + objList + ")发生异常!", e);
        } finally {
            buffer.clear();
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return protostuff;
    }

    public static <T> List<T> protostuffDeserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }

        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
        List<T> result = null;
        try {
            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
        } catch (IOException e) {
            throw new RuntimeException("反序列化对象列表发生异常!", e);
        }
        return result;
    }

//    public static void main(String[] args) {
//
//        Test a = new Test(3);
//        a.set(1,2,"123456qwert");
//
//
//        byte[] test = SerializeUtil.serialize(a);
//
//
//        Test d = SerializeUtil.deserialize(test, Test.class);
//
//        byte[] c = {8,1,16,2} ;
//        try {
//            Test b = SerializeUtil.deserialize(c, Test.class) ;
//            System.out.println("Test: " + b);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Test: ");
//    }
}