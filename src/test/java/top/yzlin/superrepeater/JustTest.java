package top.yzlin.superrepeater;


import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JustTest {

    @Test
    public void JSTest() throws FileNotFoundException, ScriptException, NoSuchMethodException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        FileReader reader = new FileReader(ResourceUtils.getFile("classpath:js/TestJS.js"));
        engine.eval(reader);

        ScriptObjectMirror s = (ScriptObjectMirror) engine.get("praiseBang");
        s.put("newOP", new TestEntiy());
        s = (ScriptObjectMirror) s.get("check");

        System.out.println(s.isFunction());

        System.out.println(s.call(null));
    }
}
