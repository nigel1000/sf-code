package cn.sf.project.controller.base;

import cn.sf.bean.beans.Response;
import cn.sf.bean.excps.KnowException;
import cn.sf.bean.excps.SysErrorCode;
import cn.sf.bean.excps.ThrowKnowException;
import cn.sf.project.trace.TraceIdUtil;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.RpcException;
import com.google.common.base.Throwables;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by nijianfeng on 16/10/10.
 */
@ControllerAdvice
@Slf4j
public class ControllerErrorHandler {

    @Resource
    private MessageSources messageSources;

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<String> processBindValidator(BindException ex) {
        // @Length等,Hibernate Validator 附加的 constraint抛出的异常
        BindingResult errors = ex.getBindingResult();
        String errorMessage = errors.getFieldError().getDefaultMessage();
        log.warn("arguments invalid {}", errors);
        return returnMessages(errorMessage,Boolean.TRUE,messageSources.get(SysErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION.getDesc()));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<String> processRequiredValidatorError(ServletRequestBindingException ex) {
        log.warn(Throwables.getStackTraceAsString(ex));
        // @RequestParam(required=true)时,参数为空抛出的异常
        if (ex instanceof MissingServletRequestParameterException) {
            return Response.fail(messageSources.get(SysErrorCode.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getDesc(),
                    ((MissingServletRequestParameterException) ex).getParameterName()));
        } else if (ex instanceof MissingPathVariableException) {
            return Response.fail(messageSources.get(SysErrorCode.MISSING_PATH_VARIABLE_EXCEPTION.getDesc(),
                    ((MissingPathVariableException) ex).getVariableName()));
        } else {
            return Response.fail(messageSources.get(SysErrorCode.SERVLET_REQUEST_BINDING_EXCEPTION.getDesc()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<String> processJsr303ValidatorError(MethodArgumentNotValidException ex) {
        // JSR303注解参数校验异常
        BindingResult errors = ex.getBindingResult();
        String errorMessage = errors.getFieldError().getDefaultMessage();
        log.warn("arguments invalid {}", errors);
        return returnMessages(errorMessage,Boolean.TRUE,messageSources.get(SysErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION.getDesc()));
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<String> processTypeValidatorError(TypeMismatchException ex) {
        log.warn(Throwables.getStackTraceAsString(ex));
        // @RequestParam @PathValiable 参数非法时异常
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return Response.fail(messageSources.get(SysErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION.getDesc(),
                    ((MethodArgumentTypeMismatchException) ex).getName()));
        } else {
            return Response.fail(messageSources.get(SysErrorCode.TYPE_MISMATCH_EXCEPTION.getDesc()));
        }
    }

    @ExceptionHandler(KnowException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<String> processKnowException(KnowException ex) {
        // controller抛出异常全局捕获
        String errorCode = ex.getMessage();
        log.warn("拦截到异常的code:{}, message:{}, errorMessage:{}, context:{}", ex.getErrorCode(), ex.getMessage(), ex.getErrorMessage(), ex.getContext());
        if(ex.getCause()!=null){
            log.warn(Throwables.getStackTraceAsString(ex));
        }
        return returnMessages(errorCode,Boolean.TRUE,messageSources.get(SysErrorCode.DEFAULT_KNOW_EXCEPTION.getDesc()));
    }

    @ExceptionHandler(ThrowKnowException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<String> processThrowKnowException(ThrowKnowException ex) {
        // controller抛出异常全局捕获
        String errorCode = ex.getMessage();
        log.warn("拦截到异常的code:{}, message:{}, errorMessage:{}, context:{}", ex.getErrorCode(), ex.getMessage(), ex.getErrorMessage(), ex.getContext());
        if(ex.getCause()!=null){
            log.warn(Throwables.getStackTraceAsString(ex));
        }
        return returnMessages(errorCode,Boolean.TRUE,messageSources.get(SysErrorCode.DEFAULT_KNOW_EXCEPTION.getDesc()));
    }

    @ExceptionHandler({TimeoutException.class, RpcException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<String> processTimeOutError(Exception ex) {
        // controller抛出异常全局捕获
        log.warn(Throwables.getStackTraceAsString(ex));
        return Response.fail(messageSources.get(SysErrorCode.DUBBO_TIMEOUT_EXCEPTION.getDesc()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<String> processControllerError(Exception ex) {
        // controller抛出异常全局捕获
        String errorCode = ex.getMessage();
        log.error(Throwables.getStackTraceAsString(ex));
        return returnMessages(errorCode,Boolean.FALSE,SysErrorCode.CONTROLLER_ERROR.getDesc());
    }


    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView processShiroError(Exception ex) {
        // 登录异常
        log.warn(Throwables.getStackTraceAsString(ex));
        ModelAndView model = new ModelAndView();
        model.addObject("message", ex.getMessage());
        model.setViewName("error/deny");
        return model;
    }

    //@see MvcConfig.dispatcherRegistration
    //会造成对js，css等资源的过滤
//    @ExceptionHandler(value = NoHandlerFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ModelAndView defaultErrorHandler(Exception ex) throws Exception {
//        ModelAndView model = new ModelAndView();
//        model.addObject("messages", ex.getMessage());
//        model.setViewName("error/404");
//        return model;
//    }

    private Response<String> returnMessages(String errorCode,@NonNull Boolean isUserDefine, String defaultMessage){
        if (!StringUtils.isBlank(errorCode)) {
            String ret = messageSources.get(errorCode);
            if (!StringUtils.isBlank(ret)) {
                return Response.fail(ret);
            }
            //是否是自定义的message  譬如knowexception抛出的异常
            if(isUserDefine) {
                return Response.fail(errorCode);
            }
        }
        return Response.fail(StringUtils.isEmpty(defaultMessage)?getTraceId():defaultMessage+getTraceId());
    }

    private String getTraceId(){
       return  "\t日志码:"+ TraceIdUtil.getCurrentTraceId()+"...";
    }

}
