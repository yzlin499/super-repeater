package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import top.yzlin.superrepeater.Tools;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class JavaClass {
    private File[] files;

    @Value("${user.javapath}")
    public void setJspath(String jspath) {
        try {
            File path = ResourceUtils.getFile(jspath);
            if (path.exists()) {
                files = path.listFiles(Tools.filterBySuffix("java"));
            } else {
                path.mkdirs();
                files = new File[0];
            }
        } catch (FileNotFoundException e) {
            files = new File[0];
            e.printStackTrace();
        }
    }

    public File[] getFiles() {
        return files;
    }
}
