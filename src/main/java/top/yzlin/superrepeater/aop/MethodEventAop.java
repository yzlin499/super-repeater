package top.yzlin.superrepeater.aop;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.log.LogOperate;
import top.yzlin.superrepeater.log.LoggerManager;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 切面，用于日志与异常处理
 */
public class MethodEventAop implements MethodEvent {
    private LogOperate logOperate;

    private MethodEvent methodEvent;

    public void setLoggerManager(LoggerManager loggerManager) {
        logOperate = loggerManager.getLogOperate(getName());
    }

    public MethodEventAop(MethodEvent methodEvent) {
        this.methodEvent = methodEvent;
    }

    @Override
    public String getName() {
        return methodEvent.getName();
    }

    @Override
    public boolean check(JSONObject data) {
        try {
            return methodEvent.check(data);
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            logOperate.log(stringWriter);
            return false;
        }
    }

    @Override
    public void operate(JSONObject data) {
        try {
            methodEvent.operate(data);
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            logOperate.log(stringWriter);
        }
    }
}
