package top.yzlin.superrepeater.pythonparse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import top.yzlin.superrepeater.LanguageFile;
import top.yzlin.superrepeater.Tools;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class PythonFile implements LanguageFile {

    private File[] files;

    @Value("${user.pythonpath}")
    public void setPythonpath(String pythonpath) {
        try {
            File path = ResourceUtils.getFile(pythonpath);
            if (path.exists()) {
                files = path.listFiles(Tools.filterBySuffix("py"));
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
