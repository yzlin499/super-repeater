package top.yzlin.superrepeater;


import org.junit.Test;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class JustTest {

    @Test
    public void JSTest() throws ScriptException, NoSuchMethodException, FileNotFoundException {
//        ScriptEngineManager manager = new ScriptEngineManager();
//        JSMethodEvent jsMethodEvent = new JSMethodEvent();
//        ScriptEngine engine = manager.getEngineByName("javascript");
//        FileReader reader = new FileReader(ResourceUtils.getFile("classpath:js/TestJS.js"));
//
//        engine.eval(reader);
//        //获取最外层的赞美棒哥节点
//        ScriptObjectMirror s = (ScriptObjectMirror) engine.get("praiseBang");
//        s.put("test", (Consumer) t -> {
//            System.out.println(t.toString() + "asdsaddasdasdas");
//        });
//
//        //获取检查节点
//        ScriptObjectMirror aaa = (ScriptObjectMirror) s.get("operate");
//        aaa.call(null);
        try {
            String a = null;
            a.toString();
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            String aaaa = stringWriter.toString();
//            System.out.println(aaaa);
        }
    }
}
