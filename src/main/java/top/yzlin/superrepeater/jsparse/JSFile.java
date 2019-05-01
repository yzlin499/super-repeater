package top.yzlin.superrepeater.jsparse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import top.yzlin.superrepeater.LanguageFile;
import top.yzlin.superrepeater.Tools;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class JSFile implements LanguageFile {
    private File[] files;

    @Value("${user.jspath}")
    public void setJspath(String jspath) {
        try {
            File path = ResourceUtils.getFile(jspath);
            if (path.exists()) {
                files = path.listFiles(Tools.filterBySuffix("js"));
            } else {
                path.mkdirs();
                files = new File[0];
            }
        } catch (FileNotFoundException e) {
            files = new File[0];
            e.printStackTrace();
        }
    }

    @Override
    public File[] getFiles() {
        return files;
    }
}
