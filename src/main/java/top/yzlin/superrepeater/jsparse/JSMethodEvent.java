package top.yzlin.superrepeater.jsparse;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.superrepeater.MethodEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class JSMethodEvent implements MethodEvent {
    private String name;
    private Predicate<JSONObject> checkFunction;
    private Consumer<JSONObject> operateFunction;

    public void setCheckFunction(Predicate<JSONObject> checkFunction) {
        this.checkFunction = checkFunction;
    }

    public void setOperateFunction(Consumer<JSONObject> operateFunction) {
        this.operateFunction = operateFunction;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean check(JSONObject jsonObject) {
        return checkFunction.test(jsonObject);
    }

    @Override
    public void operate(JSONObject jsonObject) {
        operateFunction.accept(jsonObject);
    }
}
