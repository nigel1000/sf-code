package cn.sf.tools.zip;

import cn.sf.bean.excps.ThrowKnowException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by nijianfeng on 17/12/4.
 */
@Slf4j
public class ZipUtil {

    private static final String ZIP_SUFFIX = ".zip";
    private static final String RENAME = ":rename:";
    public static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    /**
     * 压缩文件夹
     *
     * @param zip 默认为文件夹+.zip
     * @param filePath 需压缩文件的路径
     */
    public static void zip(String zip, @NonNull String filePath) {
        log.info("dirPath:{}", filePath);
        Path path = Paths.get(filePath);
        if (!path.toFile().exists()) {
            throw ThrowKnowException.valueOf("此文件不存在!");
        }
        if (StringUtils.isEmpty(zip)) {
            zip = filePath + ZIP_SUFFIX;
        } else {
            if (!zip.endsWith(ZIP_SUFFIX)) {
                throw ThrowKnowException.valueOf("压缩文件名必需是.zip文件");
            }
            Path zipPath = Paths.get(zip);
            if (!zipPath.toFile().getParentFile().exists()) {
                if (!zipPath.toFile().getParentFile().mkdirs()) {
                    throw ThrowKnowException.valueOf("创建目录失败!");
                }
            }
        }
        if (Paths.get(zip).toFile().exists()) {
            throw ThrowKnowException.valueOf(zip + "此压缩文件已存在,请删除后再压缩");
        }

        try {
            ZipOutputStream _zipOut = new ZipOutputStream(new FileOutputStream(zip));
            _zipOut.setEncoding("GBK");
            handlerFile(zip, _zipOut, Paths.get(filePath).toFile(), "");
            _zipOut.close();
        } catch (IOException e) {
            File file = Paths.get(zip).toFile();
            if (file.exists()) {
                file.deleteOnExit();
            }
            throw ThrowKnowException.valueOf("压缩文件出错!", e);
        }
    }

    /**
     * 压缩文件或路径
     *
     * @param zip 压缩的目的地址
     * @param srcFiles 压缩的相对路径和压缩的源文件
     */
    public static void zip(@NonNull String zip, @NonNull Map<String, List<String>> srcFiles) {
        log.info("zip:{},srcFiles:{}", zip, JSONObject.toJSONString(srcFiles));
        if (!zip.endsWith(ZIP_SUFFIX)) {
            throw ThrowKnowException.valueOf("压缩文件名必需是.zip文件");
        }
        Path path = Paths.get(zip);
        if (!path.toFile().getParentFile().exists()) {
            if (!path.toFile().getParentFile().mkdirs()) {
                throw ThrowKnowException.valueOf("创建目录失败!");
            }
        }
        try {
            ZipOutputStream _zipOut = new ZipOutputStream(new FileOutputStream(zip));
            _zipOut.setEncoding("GBK");
            for (String relativePath : srcFiles.keySet()) {
                if (relativePath == null) {
                    continue;
                }
                if (relativePath.startsWith(File.separator)) {
                    if (relativePath.length() == 1) {
                        relativePath = "";
                    } else {
                        relativePath = relativePath.substring(1);
                    }
                }
                List<String> pathList = srcFiles.get(relativePath);
                if (CollectionUtils.isEmpty(pathList)) {
                    if (!"".equals(relativePath.trim())) {
                        _zipOut.putNextEntry(new ZipEntry(relativePath + File.separator));
                        _zipOut.closeEntry();
                    }
                    continue;
                }
                Set<String> filePaths = Sets.newHashSet(pathList);
                for (String filePath : filePaths) {
                    if (filePath.contains(RENAME)) {
                        String[] names = filePath.split(RENAME);
                        if (names.length == 2) {
                            File srcFile = Paths.get(names[0]).toFile();
                            if (srcFile.exists()) {
                                if (!PathUtil.isAvailFileName(names[1])) {
                                    continue;
                                }
                                handlerFile(zip, _zipOut, srcFile, names[1], relativePath);
                            }
                        }
                    } else {
                        File srcFile = Paths.get(filePath).toFile();
                        if (srcFile.exists()) {
                            handlerFile(zip, _zipOut, srcFile, relativePath);
                        }
                    }
                }
            }
            _zipOut.close();
        } catch (IOException e) {
            File file = Paths.get(zip).toFile();
            if (file.exists()) {
                file.deleteOnExit();
            }
            throw ThrowKnowException.valueOf("压缩文件出错!", e);
        }
    }

    /**
     * @param zip 压缩的目的地址
     * @param zipOut zip地址
     * @param srcFile 被压缩的文件信息
     * @param relativePath 在zip中的相对路径
     * @throws IOException
     */
    private static void handlerFile(String zip, ZipOutputStream zipOut, File srcFile, String relativePath)
            throws IOException {
        log.info("begin to compression file {} to {}'s relativePath {}", srcFile.getPath(), zip,
                relativePath + File.separator + srcFile.getName());
        if (!relativePath.endsWith(File.separator) && !StringUtils.isEmpty(relativePath)) {
            relativePath += File.separator;
        }
        if (!srcFile.getPath().equals(zip)) {
            if (srcFile.isDirectory()) {
                File[] _files = srcFile.listFiles();
                if (_files == null || _files.length == 0) {
                    zipOut.putNextEntry(new ZipEntry(relativePath + srcFile.getName() + File.separator));
                    zipOut.closeEntry();
                } else {
                    for (File _f : _files) {
                        handlerFile(zip, zipOut, _f, relativePath + srcFile.getName());
                    }
                }
            } else {
                InputStream _in = new FileInputStream(srcFile);
                zipOut.putNextEntry(new ZipEntry(relativePath + srcFile.getName()));
                int len;
                byte[] _byte = new byte[1024];
                while ((len = _in.read(_byte)) > 0) {
                    zipOut.write(_byte, 0, len);
                }
                _in.close();
                zipOut.closeEntry();
            }
        }
    }

    /**
     * @param zip 压缩的目的地址
     * @param zipOut zip地址
     * @param srcFile 被压缩的文件信息
     * @param rename 被压缩的文件重命名
     * @param relativePath 在zip中的相对路径
     * @throws IOException
     */
    private static void handlerFile(String zip, ZipOutputStream zipOut, File srcFile, String rename,
            String relativePath) throws IOException {
        String fileName = StringUtils.isEmpty(rename) ? srcFile.getName() : rename;
        log.info("begin to compression file {} to {}'s relativePath {}", srcFile.getPath(), zip,
                relativePath + File.separator + fileName);
        if (!relativePath.endsWith(File.separator) && !StringUtils.isEmpty(relativePath)) {
            relativePath += File.separator;
        }
        if (!srcFile.getPath().equals(zip)) {
            if (srcFile.isDirectory()) {
                File[] _files = srcFile.listFiles();
                if (_files == null || _files.length == 0) {
                    zipOut.putNextEntry(new ZipEntry(relativePath + fileName + File.separator));
                    zipOut.closeEntry();
                } else {
                    for (File _f : _files) {
                        handlerFile(zip, zipOut, _f, null, relativePath + fileName);
                    }
                }
            } else {
                InputStream _in = new FileInputStream(srcFile);
                zipOut.putNextEntry(new ZipEntry(relativePath + fileName));
                int len;
                byte[] _byte = new byte[1024];
                while ((len = _in.read(_byte)) > 0) {
                    zipOut.write(_byte, 0, len);
                }
                _in.close();
                zipOut.closeEntry();
            }
        }
    }

    /**
     * 对.zip文件进行解压缩
     *
     * @param zipPath 解压缩文件
     * @param descDir 压缩的目标地址，如：D:\\测试 或 /mnt/d/测试
     * @param isCover 是否覆盖已存在的文件
     * @return
     */
    public static List<String> unzip(@NonNull String zipPath, @NonNull String descDir, boolean isCover) {
        log.info("zipPath:{},descDir:{}", zipPath, descDir);
        File zipFile = Paths.get(zipPath).toFile();
        if (!zipPath.endsWith(ZIP_SUFFIX) || !zipFile.exists()) {
            throw ThrowKnowException.valueOf(zipPath + "此文件不是压缩文件!");
        }
        List<String> _list = Lists.newArrayList();
        try {
            ZipFile _zipFile = new ZipFile(zipFile, "GBK");
            for (Enumeration entries = _zipFile.getEntries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File _file = Paths.get(descDir + File.separator + entry.getName()).toFile();
                if (entry.isDirectory()) {
                    if (!_file.exists()) {
                        _file.mkdirs();
                    }
                } else {
                    File _parent = _file.getParentFile();
                    if (!_parent.exists()) {
                        _parent.mkdirs();
                    }
                    // 已存在的文件不做覆盖操作
                    if (!isCover) {
                        if (_file.exists()) {
                            log.info("{}已存在,不覆盖.", _file.getPath());
                            continue;
                        }
                    }
                    InputStream _in = _zipFile.getInputStream(entry);
                    OutputStream _out = new FileOutputStream(_file);
                    int len;
                    byte[] _byte = new byte[1024];
                    while ((len = _in.read(_byte)) > 0) {
                        _out.write(_byte, 0, len);
                    }
                    _in.close();
                    _out.flush();
                    _out.close();
                    _list.add(_file.getPath());
                }
            }
        } catch (IOException e) {
            File file = Paths.get(descDir).toFile();
            if (file.exists()) {
                file.deleteOnExit();
            }
            throw ThrowKnowException.valueOf("解压缩文件出错!", e);
        }
        return _list;
    }

    public static void main(String[] args) {

        String prePath = "/Users/nijianfeng/Documents/projects/car-tv/docs/";
        Map<String, List<String>> srcFiles = Maps.newHashMap();
        srcFiles.put("", null);
        srcFiles.put("null", null);
        srcFiles.put("", Lists.newArrayList(prePath + "mysql.md", prePath + "mysql.md",
                prePath + "mysql.md:rename:mysql.md.rename", prePath + ":rename:doc-rename"));
        srcFiles.put("info", Lists.newArrayList(prePath + "mysql.md", prePath + "mysql.md",
                prePath + "mysql.md:rename:mysql.md.rename", prePath + ":rename:doc-rename"));

        zip("/Users/nijianfeng/demo.zip", srcFiles);

        System.out.println(unzip("/Users/nijianfeng/demo.zip", "/Users/nijianfeng/demo", true));
        //
        // zip(null, "/Users/nijianfeng/projects/nginx-pg");
        // zip("/Users/nijianfeng/projects/nginx-pg-demo.zip", "/Users/nijianfeng/projects/nginx-pg");

    }

    static class PathUtil {

        private static boolean isContainSpecial(String path) {
            if (StringUtils.isEmpty(path)) {
                return true;
            }
            if (path.contains(File.separator + '.') || path.contains('.' + File.separator) || path.startsWith(".")
                    || path.endsWith(".") || INSECURE_URI.matcher(path).matches()) {
                return true;
            }
            if (path.contains("..")) {
                return true;
            }
            return false;
        }

        private static boolean isAvailFileName(String fileName) {
            if (isContainSpecial(fileName)) {
                return false;
            }
            if (fileName.contains(File.separator)) {
                return false;
            }
            if (fileName.contains("\\")) {
                return false;
            }
            return true;
        }
    }
}
