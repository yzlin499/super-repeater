package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest

public class SuperRepeaterApplicationTests {

    @Autowired
    @Qualifier("methodEventMap")
    Map<String, MethodEvent> methodEvents;
    @Test
    public void contextLoads() throws Exception {
        JSONObject j = new JSONObject()
                .fluentPut("message", "asdasdasdsa")
                .fluentPut("user_id", "123456789");
        methodEvents.forEach((k, v) -> {
            System.out.println(k + "\\" + v.getClass());
        });
    }

}

