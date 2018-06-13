package cn.sf.dubbo.service.callback;

import cn.sf.dubbo.callback.ICallback;
import org.springframework.stereotype.Service;

@Service
public class BusinessServiceImpl implements BusinessService {

    public Boolean saveModel(String model, ICallback callback) {
        Boolean ret = callback.validBusiness(model);
        System.out.println("service:"+ret);
        return ret;
    }
}
