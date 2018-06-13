package cn.sf.auto.mybatis.util;

import cn.sf.auto.exps.AutoCodeException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nijianfeng on 18/1/29.
 */
public class FileUtils {


    public static void genFile(String file, String content) {
        try {
            Path path = Paths.get(file);
            if (!path.toFile().getParentFile().exists()) {
                if (!path.toFile().getParentFile().mkdirs()) {
                    throw AutoCodeException.valueOf("创建目录失败!");
                }
            }
            if (path.toFile().exists()) {
                if(!path.toFile().delete()){
                    throw AutoCodeException.valueOf("删除文件失败!");
                }
            }
            System.out.println("生成文件:" + file);
            Files.write(Files.createFile(path), content.getBytes());
        } catch (Exception e) {
            throw AutoCodeException.valueOf("生成文件失败! ", e);
        }
    }


}
