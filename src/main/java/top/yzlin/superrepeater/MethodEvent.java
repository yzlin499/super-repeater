package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;

public interface MethodEvent {

    default int getPriority() {
        return 1;
    }

    String getName();

    boolean check(JSONObject data);

    void operate(JSONObject data);

    default void destroy() {
    }
}
