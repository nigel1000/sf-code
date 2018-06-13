package cn.sf.bean.beans;

import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Setter
    private boolean success;
    private T result;
    private Object error;

    public Response() {
    }

    public boolean isSuccess() {
        return this.success;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.success = true;
        this.result = result;
    }

    public Object getError() {
        return this.error;
    }

    public void setError(Object error) {
        this.success = false;
        this.error = error;
    }

    public static <T> Response<T> ok(T data) {
        Response<T> resp = new Response<>();
        resp.setResult(data);
        return resp;
    }

    public static <T> Response<T> fail(Object error) {
        Response<T> resp = new Response<>();
        resp.setError(error);
        return resp;
    }

}