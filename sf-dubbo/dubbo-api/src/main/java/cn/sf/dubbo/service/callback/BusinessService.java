package cn.sf.dubbo.service.callback;

import cn.sf.dubbo.callback.ICallback;

public interface BusinessService {

    Boolean saveModel(String model, ICallback callback);
}