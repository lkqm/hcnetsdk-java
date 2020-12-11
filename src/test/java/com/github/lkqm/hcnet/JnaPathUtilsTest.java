package com.github.lkqm.hcnet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.jna.Pointer;
import org.junit.jupiter.api.Test;

class JnaPathUtilsTest {

    @Test
    void initJnaLibraryPath() {
        String oldJnaPathProperty = System.getProperty(JnaPathUtils.JNA_PATH_PROPERTY_NAME);
        boolean modified = JnaPathUtils.initJnaLibraryPath(JnaPathUtilsTest.class, false);
        String newJnaPathProperty = System.getProperty(JnaPathUtils.JNA_PATH_PROPERTY_NAME);
        assertTrue(modified, "非jar执行应该修改了系统变量");
        assertNotEquals(oldJnaPathProperty, newJnaPathProperty, "系统变量应该被修改了");
    }

    @Test
    void isRunJar() {
        assertFalse(JnaPathUtils.isRunJar(), "运行再非Jar中");
    }

    @Test
    void getJarDirectoryPath() {
        String path = JnaPathUtils.getJarDirectoryPath(Pointer.class);
        assertNotNull(path, "返回该jar所在目录");

        path = JnaPathUtils.getJarDirectoryPath(String.class);
        assertNotNull(path, "返回该jar所在目录");

        path = JnaPathUtils.getJarDirectoryPath(JnaPathUtilsTest.class);
        assertNotNull(path, "返回该jar所在目录");
    }
}