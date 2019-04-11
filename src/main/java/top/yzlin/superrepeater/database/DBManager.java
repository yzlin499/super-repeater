package top.yzlin.superrepeater.database;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DBManager implements InitializingBean {
    private File dbPath;

    @Override
    public void afterPropertiesSet() throws Exception {
        Class.forName("org.sqlite.JDBC");
    }

    @Value("${user.dbpath}")
    public void setDbPath(String dbPath) {
        this.dbPath = new File(dbPath);
        if (!this.dbPath.exists()) {
            this.dbPath.mkdirs();
        }
    }

    public Connection createDBConnection(String name) throws SQLException {
        File file = new File(dbPath, name + ".db");
        return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    public boolean isExistsDB(String name) throws SQLException {
        File file = new File(dbPath, name + ".db");
        return isExistsDB(file);
    }

    public boolean isExistsDB(File file) throws SQLException {
        return file.exists();
    }
}
