package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;

@Component
public class LocalClassLoader extends URLClassLoader {
    private JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    private String classpath;
    private File javapath;

    public LocalClassLoader(@Value("${user.classpath}") String classpath,
                            @Value("${user.javapath}") String javapath) throws FileNotFoundException {
        super(new URL[]{ResourceUtils.getURL(classpathCreate(classpath))});
        this.classpath = classpath;
        this.javapath = new File(javapath);
    }

    private static String classpathCreate(String classpath) {
        File file = new File(classpath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return classpath;
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
            return loadClass(name.substring(0, name.lastIndexOf('.')));
        }
    }

    public Class<?> loadClass2(String name) throws ClassNotFoundException {
//        find
        resolveClass(super.loadClass(name));
        return super.loadClass(name, true);
    }

//    private
}
