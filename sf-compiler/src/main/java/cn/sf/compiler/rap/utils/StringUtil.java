package cn.sf.compiler.rap.utils;

import lombok.NonNull;

/**
 * Created by nijianfeng on 18/5/2.
 */
public class StringUtil {


    public static String removeArray(@NonNull String str) {
        return str.replace("[", "").replace("]", "");
    }

    public static String removeDoublePath(String path) {
        return path.replaceAll("/+", "/").replaceAll("\\\\+", "\\\\");
    }

    public static String removeLast(@NonNull String str) {
        if(str.length()==0){
            return str;
        }
        if(str.length()==1){
            return "";
        }
        return str.substring(0,str.length()-1);
    }

}
