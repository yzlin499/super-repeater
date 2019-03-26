package top.yzlin.superrepeater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.yzlin.superrepeater.javaparse.LocalClassLoader;
import top.yzlin.tools.Tools;

import java.lang.reflect.Method;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperRepeaterApplicationTests {

    @Autowired
    private LocalClassLoader loadClass;

    @Test
    public void contextLoads() throws Exception {
        loadClass.compiler("Test");
        Class c = loadClass.loadClass("Test");

        Method method = c.getMethod("t");
        System.out.println(method.invoke(c.newInstance()));
        Tools.sleep(10000);
        loadClass.compiler("Test");
        c = loadClass.loadClass2("Test");

        method = c.getMethod("t");
        System.out.println(method.invoke(c.newInstance()));
    }

}

