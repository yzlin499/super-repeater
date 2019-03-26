package top.yzlin.superrepeater;

import java.io.FileFilter;

public final class Tools {
    public static FileFilter filterBySuffix(String suffix) {
        String s = suffix.toUpperCase();
        return pathname -> {
            if (pathname.isFile()) {
                String fileName = pathname.getName();
                return s.equals(fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase());
            } else {
                return false;
            }
        };
    }
}
