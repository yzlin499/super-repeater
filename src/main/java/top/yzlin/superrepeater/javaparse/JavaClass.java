package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class JavaClass implements InitializingBean {
    private Class[] clazz;
    private File file;
    private ClassLoader classLoader;

    @Autowired
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
//        Stream.of(file.listFiles(Tools.filterBySuffix("class")))
//                .map(f->{
//                    String name=f.getName()
//                })
    }


    public Class[] getClazz() {
        return clazz;
    }
}
