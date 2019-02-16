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
            File path = ResourceUtils.getFile(jspath);
            if (path.exists()) {
                files = path.listFiles(pathname -> {
                    if (pathname.isFile()) {
                        String fileName = pathname.getName();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
                        return "JS".equals(suffix);
                    } else {
                        return false;
                    }
                });
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
