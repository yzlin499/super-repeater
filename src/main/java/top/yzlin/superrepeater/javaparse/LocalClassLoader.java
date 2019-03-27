package top.yzlin.superrepeater.javaparse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class LocalClassLoader extends ClassLoader {
    private File classpath;
    private String className;

    public LocalClassLoader(File classpath, String className) {
        super();
        this.classpath = classpath;
        this.className = className;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data = loadClassData(name);
        Class<?> clazz = defineClass(name, data, 0, data.length);
        resolveClass(clazz);
        return clazz;
    }

    private byte[] loadClassData(String name) throws ClassNotFoundException {
        try {
            name = name.replace(".", "/");
            FileInputStream is = new FileInputStream(new File(classpath, name + ".class"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1) {
                baos.write(b);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException("包装一下这个" + e.getMessage());
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (Objects.equals(className, name)) {
            return findClass(name);
        }
        return super.loadClass(name);
    }

    public Class<?> loadClass() throws ClassNotFoundException {
        return loadClass(className);
    }
}
