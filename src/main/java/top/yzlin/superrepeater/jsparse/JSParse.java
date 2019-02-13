package top.yzlin.superrepeater.jsparse;

import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class JSParse {
    private ScriptEngineManager manager = new ScriptEngineManager();
    private SimpleHttpAPI simpleHttpAPI;

    @Autowired
    public void setSimpleHttpAPI(SimpleHttpAPI simpleHttpAPI) {
        this.simpleHttpAPI = simpleHttpAPI;
    }

    public MethodEvent parse(File file) throws FileNotFoundException, ScriptException {
        ScriptEngine engine = manager.getEngineByName("javascript");
        FileReader reader = new FileReader(file);
        engine.eval(reader);

        ScriptObjectMirror s = (ScriptObjectMirror) engine.get("praiseBang");
        s.put("robot", simpleHttpAPI);
        Object temp = s.get("check");

        JSMethodEvent jsMethodEvent = new JSMethodEvent();
        if (temp instanceof Integer || temp instanceof String) {
            jsMethodEvent.setCheckFunction(j -> Objects.equals(temp, j.getString("message")));
        }


        System.out.println(s.isFunction());
        System.out.println(s.call(null, new JSONObject().fluentPut("asd", 456465)));
        return jsMethodEvent;
    }
}
