package top.yzlin.superrepeater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperRepeaterApplicationTests {

    @Value("${user.jspath}")
    String jspath;

    @Test
    public void contextLoads() throws FileNotFoundException, ScriptException {
        File[] files = ResourceUtils.getFile(jspath).listFiles(pathname -> {
            if (pathname.isFile()) {
                String fileName = pathname.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
                return suffix.equals("JS");
            } else {
                return false;
            }
        });
        System.out.println(Arrays.toString(files));
    }

}

