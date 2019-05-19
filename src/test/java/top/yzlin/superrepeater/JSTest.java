package top.yzlin.superrepeater;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;
import org.springframework.util.ResourceUtils;
import top.yzlin.superrepeater.jsparse.JSMethodEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JSTest {

    @Test
    public void jsTest() throws FileNotFoundException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        File file = ResourceUtils.getFile("classpath:js/TestJS.js");
        JSMethodEvent jsMethodEvent = new JSMethodEvent();
        jsMethodEvent.setName(file.getName());
        ScriptEngine engine = manager.getEngineByName("javascript");
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        engine.eval(isr);
        ScriptObjectMirror s = (ScriptObjectMirror) engine.get("praiseBang");
        System.out.println(s.get("priority"));

    }
}
