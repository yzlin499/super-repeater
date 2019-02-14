package top.yzlin.superrepeater.jsparse;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JSParse {
    private ScriptEngineManager manager = new ScriptEngineManager();
    private SimpleHttpAPI simpleHttpAPI;
    private String groupID;

    @Autowired
    public void setSimpleHttpAPI(SimpleHttpAPI simpleHttpAPI) {
        this.simpleHttpAPI = simpleHttpAPI;
    }

    @Value("${user.groupID}")
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public MethodEvent parse(File file) throws FileNotFoundException, ScriptException {
        ScriptEngine engine = manager.getEngineByName("javascript");
        FileReader reader = new FileReader(file);
        engine.eval(reader);
        //获取最外层的赞美棒哥节点
        ScriptObjectMirror s = (ScriptObjectMirror) engine.get("praiseBang");
        //注入机器人的操作函数
        s.put("robot", simpleHttpAPI);
        //获取检查节点
        Object check = s.get("check");

        JSMethodEvent jsMethodEvent = new JSMethodEvent();
        //为方法设置检查方法
        //如果值为数字或者字符串则进行值对比，如果为数组则判断数组的值，如果为函数着将函数的返回结果作为判断
        if (check instanceof Integer || check instanceof String) {
            jsMethodEvent.setCheckFunction(j -> Objects.equals(check, j.getString("message")));
        } else if (check instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) check;
            if (mirror.isArray()) {
                Set<String> keySet = mirror.values().stream().map(Object::toString).collect(Collectors.toSet());
                jsMethodEvent.setCheckFunction(j -> keySet.contains(j.getString("message")));
            } else if (mirror.isFunction()) {
                jsMethodEvent.setCheckFunction(j -> {
                    Object result = mirror.call(null, j);
                    return Boolean.TRUE.equals(result) || result instanceof Integer && (!Integer.valueOf(0).equals(result));
                });
            } else {
                return null;
            }
        } else {
            return null;
        }

        //操作
        Object operate = s.get("operate");
        //如果操作的值为数字或者字符串则返回该值
        if (operate instanceof Integer || operate instanceof String) {
            jsMethodEvent.setOperateFunction(j -> simpleHttpAPI.sendGroupMsg(groupID, operate.toString()));
        } else if (operate instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) operate;
            if (mirror.isFunction()) {
                jsMethodEvent.setOperateFunction(j -> {
                    Object result = mirror.call(null, j);
                    if (result instanceof Integer || result instanceof String) {
                        simpleHttpAPI.sendGroupMsg(groupID, operate.toString());
                    }
                });
            } else {
                return null;
            }
        } else {
            return null;
        }
        return jsMethodEvent;
    }
}
