package top.yzlin.superrepeater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.yzlin.superrepeater.jsparse.JSParse;

import javax.script.ScriptException;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperRepeaterApplicationTests {

    @Autowired
    JSParse jsParse;

    @Test
    public void contextLoads() throws FileNotFoundException, ScriptException {
//        jsParse.parse()
    }

}

