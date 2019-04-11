package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.yzlin.superrepeater.javaparse.JavaClass;
import top.yzlin.superrepeater.javaparse.JavaParse;

@RunWith(SpringRunner.class)
@SpringBootTest

public class SuperRepeaterApplicationTests {

    @Autowired
    JavaParse javaParse;

    @Autowired
    JavaClass javaClass;

    @Test
    public void contextLoads() throws Exception {
        JSONObject j = new JSONObject()
                .fluentPut("message", "asdasdasdsa")
                .fluentPut("user_id", "123456789");
        MethodEvent parse = javaParse.parse(javaClass.getFiles()[0]);
        parse.operate(j);
    }

}

