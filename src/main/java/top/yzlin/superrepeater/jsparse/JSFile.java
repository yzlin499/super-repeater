package top.yzlin.superrepeater.jsparse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class JSFile {
    private File[] files;

    @Value("${user.jspath}")
    public void setJspath(String jspath) {
        try {
            files = ResourceUtils.getFile(jspath).listFiles(pathname -> {
                if (pathname.isFile()) {
                    String fileName = pathname.getName();
                    String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
                    return "JS".equals(suffix);
                } else {
                    return false;
                }
            });
        } catch (FileNotFoundException e) {
            files = new File[0];
            e.printStackTrace();
        }
    }

    public File[] getFiles() {
        return files;
    }
}
