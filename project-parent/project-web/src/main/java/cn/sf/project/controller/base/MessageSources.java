package cn.sf.project.controller.base;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

@Component
public class MessageSources implements MessageSource {
    //@see MvcConfig.messageSource
    @Resource(name="messageSource")
    private MessageSource messageSource;
    private final String defaultMessage = "MessageSource默认返回值设定";

    public String get(String code) {
        return this.get(code, new Object[0]);
    }
    public String get(String code, Object... args) {

        if(this.messageSource == null){
            return code;
        }
        String retCode = this.messageSource.getMessage(code, args, defaultMessage, Locale.getDefault());
        if(defaultMessage.equals(retCode)){
            return "";
        }
        return retCode;
    }
    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return this.messageSource == null ? code : this.messageSource.getMessage(code, args, defaultMessage, locale);
    }
    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return this.messageSource == null ? code : this.messageSource.getMessage(code, args, locale);
    }
    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, locale);
    }
}
