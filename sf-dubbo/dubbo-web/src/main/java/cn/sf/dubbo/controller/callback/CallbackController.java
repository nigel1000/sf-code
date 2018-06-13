package cn.sf.dubbo.controller.callback;

import cn.sf.dubbo.service.callback.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nijianfeng on 17/2/23.
 */

@RestController
@RequestMapping("/dubbo/callback")
public class CallbackController {

    @Autowired(required = false)
    private BusinessService businessService;

    @RequestMapping(value = "returnTest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Boolean returnTest(String model) {
        return businessService.saveModel(model, (param1) -> {
                if(param1!=null) {
                    System.out.println("controller:true");
                    return Boolean.TRUE;
                }else {
                    System.out.println("controller:false");
                    return Boolean.FALSE;
                }
        });

    }
}
