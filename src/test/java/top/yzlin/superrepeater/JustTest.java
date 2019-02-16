package top.yzlin.superrepeater;


import org.junit.Test;
import org.springframework.util.ResourceUtils;

import javax.script.ScriptException;
import java.io.File;

public class JustTest {

    @Test
    public void JSTest() throws ScriptException, NoSuchMethodException {
        try {
            File file = ResourceUtils.getFile("d:/sad");
            System.out.println(file.exists());
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
//        ScriptEngineManager manager = new ScriptEngineManager();
//        JSMethodEvent jsMethodEvent = new JSMethodEvent();
//        ScriptEngine engine = manager.getEngineByName("javascript");
//        FileReader reader = new FileReader(ResourceUtils.getFile("classpath:js/TestJS.js"));
//
//        engine.eval(reader);
//        //获取最外层的赞美棒哥节点
//        ScriptObjectMirror s = (ScriptObjectMirror) engine.get("praiseBang");
//
//        //获取检查节点
//        Object check = s.get("check");
//        System.out.println(((ScriptObjectMirror)check));
    }
}
