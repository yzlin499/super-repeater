package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class JavaClass implements InitializingBean {
    private Class[] clazz;
    private File file;


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
