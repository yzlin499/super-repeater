package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;

public interface MethodEvent {

    String getName();

    boolean check(JSONObject data);

    void operate(JSONObject data);
}
