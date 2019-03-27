package top.yzlin.superrepeater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.yzlin.superrepeater.javaparse.ClassLoaderFactory;

import java.lang.reflect.Method;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperRepeaterApplicationTests {

    @Autowired
    private ClassLoaderFactory loaderFactory;

    @Test
    public void contextLoads() throws Exception {
//        for (int i = 0; i < 30; i++) {
        Class c = loaderFactory.compiler("Test");
        Method method = c.getMethod("t", String.class);
        System.out.println(method.invoke(c.newInstance(), ""));
//            Tools.sleep(2000);
//        }
    }

}

