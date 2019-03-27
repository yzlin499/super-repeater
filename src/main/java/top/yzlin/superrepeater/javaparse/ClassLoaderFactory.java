package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Component
public class ClassLoaderFactory {
    private JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private String classpath;
    private File classpathFile;
    private File javapath;

    private static File pathCreate(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    @Value("${user.classpath}")
    public void setClasspath(String classpath) {
        this.classpath = classpath;
        classpathFile = pathCreate(classpath);
    }

    @Value("${user.javapath}")
    public void setJavapath(String javapath) {
        this.javapath = pathCreate(javapath);
    }

    public Class compiler(String name) throws ClassNotFoundException {
        return compiler(new File(javapath, name + ".java"));
    }

    public Class compiler(File file) throws ClassNotFoundException {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        javac.run(null, null, err,
                "-encoding", "UTF-8",
                "-classpath", file.getAbsolutePath(),
                "-d", classpath, file.getAbsolutePath());
        if (err.size() > 0) {
            throw new RuntimeException(err.toString());
        } else {
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf('.'));
            return new LocalClassLoader(classpathFile, name).loadClass();
        }
    }

}
