package top.yzlin.superrepeater.aop;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.superrepeater.MethodEvent;

/**
 * 切面，用于日志与异常处理
 */
public class MethodEventAop implements MethodEvent {
    private MethodEvent methodEvent;

    public MethodEventAop(MethodEvent methodEvent) {
        this.methodEvent = methodEvent;
    }

    @Override
    public String getName() {
        return methodEvent.getName();
    }

    @Override
    public boolean check(JSONObject data) {
        return methodEvent.check(data);
    }

    @Override
    public void operate(JSONObject data) {
        methodEvent.operate(data);
    }
}
