package cn.sf.bean.utils;

import cn.sf.bean.beans.Response;
import cn.sf.bean.excps.KnowException;
import cn.sf.bean.excps.ThrowKnowException;

import java.util.Optional;

public class ResponseUtil {

    private ResponseUtil() {
    }

    public static <T> T parseRethrow(Response<T> response) {
        if(response == null) {
            throw ThrowKnowException.valueOf("response 为空!");
        }
        if(response.isSuccess()) {
            return response.getResult();
        }
        Optional<Object> error = Optional.ofNullable(response.getError());
        throw ThrowKnowException.valueOf("response error message: " + error.orElse(""));
    }

    public static <T> T parse(Response<T> response) {
        if(response == null) {
            throw KnowException.valueOf("response 为空!");
        }
        if(response.isSuccess()) {
            return response.getResult();
        }
        Optional<Object> error = Optional.ofNullable(response.getError());
        throw KnowException.valueOf("response error message: " + error.orElse(""));
    }

}