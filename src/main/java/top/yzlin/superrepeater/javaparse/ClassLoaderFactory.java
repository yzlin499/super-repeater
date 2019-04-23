package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClassLoaderFactory {
    private JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private String classpath;
    private String systemPath;
    private File classpathFile;
    private File javapath;

    private static File pathCreate(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    @Value("${loca.classpath}")
    public void setSystemPath(String systemPath) {
        if (systemPath != null && !"".equals(systemPath)) {
            File file = new File(systemPath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    this.systemPath = Stream.of(file.listFiles())
                            .map(File::getAbsolutePath)
                            .collect(Collectors.joining(";"));
                } else if (file.isFile()) {
                    this.systemPath = systemPath;
                }
            }
            return;
        }
        this.systemPath = System.getProperty("java.class.path");
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

    public Class compiler(String name) throws ClassNotFoundException, IOException {
        return compiler(new File(javapath, name + ".java"));
    }

    public Class compiler(File file) throws ClassNotFoundException, IOException {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        //这里45有一次null
        System.out.println(systemPath + ";" + file.getAbsolutePath());
        javac.run(null, null, err,
                "-encoding", "UTF-8",
                "-classpath", systemPath + ";" + file.getAbsolutePath(),
                "-d", classpath, file.getAbsolutePath());
        if (err.size() > 0) {
            throw new IOException(err.toString());
        } else {
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf('.'));
            return new LocalClassLoader(classpathFile, name).loadClass();
        }
    }

}
