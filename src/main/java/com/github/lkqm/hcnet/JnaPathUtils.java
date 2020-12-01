package com.github.lkqm.hcnet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import sun.awt.OSInfo;

/**
 * Jna加载本地库相关工具类。
 */
public class JnaPathUtils {

    public static final String JNA_PATH_PROPERTY_NAME = "jna.library.path";

    /**
     * 初始化设置加载目录，只影响开发模式下（非jar运行）
     */
    public static void initJnaLibraryPathDev() {
        initJnaLibraryPath(false);
    }

    /**
     * 检查并设置本地库加载目录系统变量(jna.library.path)，
     * <p>
     * 设置的路径, 资源目录下： natives/{type}, 其中type在不同操作系统下对应值不一样(so, dll, dylib)
     */
    public static void initJnaLibraryPath(boolean effectiveJar) {
        String jnaLibPath = System.getProperty(JNA_PATH_PROPERTY_NAME);
        boolean isJnaLibEmpty = (jnaLibPath == null || jnaLibPath.trim().length() == 0);
        if (isJnaLibEmpty) {
            Map<OSInfo.OSType, String> libDirMap = new HashMap<>();
            libDirMap.put(OSInfo.OSType.WINDOWS, "natives/dll");
            libDirMap.put(OSInfo.OSType.LINUX, "natives/so");
            libDirMap.put(OSInfo.OSType.MACOSX, "natives/dylib");

            String libDir = libDirMap.get(OSInfo.getOSType());
            if (libDir == null) {
                throw new RuntimeException("Unsupported operator system: " + OSInfo.getOSType().name());
            }

            if (!isRunWithJar()) {
                URL uri = JnaPathUtils.class.getClassLoader().getResource(libDir);
                if (uri == null) {
                    throw new IllegalStateException("Not found relation library: " + libDir);
                }
                jnaLibPath = uri.getPath();
                System.setProperty(JNA_PATH_PROPERTY_NAME, jnaLibPath);
            } else if (effectiveJar) {
                jnaLibPath = getJarDirectoryPath() + File.separator + libDir;
                System.setProperty(JNA_PATH_PROPERTY_NAME, jnaLibPath);
            }
        }
    }

    /**
     * 是否以可执行jar方式启动
     */
    public static boolean isRunWithJar() {
        String protocol = JnaPathUtils.class.getResource("").getProtocol();
        return "jar".equals(protocol);
    }

    /**
     * 获取执行jar所在目录
     */
    public static String getJarDirectoryPath() {
        URL url = getLocation(JnaPathUtils.class);
        File file = urlToFile(url);
        return file.getParent();
    }


    /**
     * Gets the base location of the given class.
     * <p>
     * If the class is directly on the file system (e.g., "/path/to/my/package/MyClass.class") then it will return the
     * base directory (e.g., "file:/path/to").
     * </p>
     * <p>
     * If the class is within a JAR file (e.g., "/path/to/my-jar.jar!/my/package/MyClass.class") then it will return the
     * path to the JAR (e.g., "file:/path/to/my-jar.jar").
     * </p>
     *
     * @param c The class whose location is desired.
     */
    private static URL getLocation(final Class<?> c) {
        if (c == null) {
            return null; // could not load the class
        }

        // try the easy way first
        try {
            final URL codeSourceLocation =
                    c.getProtectionDomain().getCodeSource().getLocation();
            if (codeSourceLocation != null) {
                return codeSourceLocation;
            }
        } catch (final SecurityException e) {
            // NB: Cannot access protection domain.
        } catch (final NullPointerException e) {
            // NB: Protection domain or code source is null.
        }

        // NB: The easy way failed, so we try the hard way. We ask for the class
        // itself as a resource, then strip the class's path from the URL string,
        // leaving the base path.

        // get the class's raw resource path
        final URL classResource = c.getResource(c.getSimpleName() + ".class");
        if (classResource == null) {
            return null; // cannot find class resource
        }

        final String url = classResource.toString();
        final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
        if (!url.endsWith(suffix)) {
            return null; // weird URL
        }

        // strip the class's path from the URL string
        final String base = url.substring(0, url.length() - suffix.length());

        String path = base;

        // remove the "jar:" prefix and "!/" suffix, if present
        if (path.startsWith("jar:")) {
            path = path.substring(4, path.length() - 2);
        }

        try {
            return new URL(path);
        } catch (final MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts the given {@link URL} to its corresponding {@link File}.
     * <p>
     * This method is similar to calling {@code new File(url.toURI())} except that it also handles "jar:file:" URLs,
     * returning the path to the JAR file.
     * </p>
     *
     * @param url The URL to convert.
     * @throws IllegalArgumentException if the URL does not correspond to a file.
     */
    private static File urlToFile(final URL url) {
        return url == null ? null : urlToFile(url.toString());
    }

    /**
     * Converts the given URL string to its corresponding {@link File}.
     *
     * @param url The URL to convert.
     * @throws IllegalArgumentException if the URL does not correspond to a file.
     */
    private static File urlToFile(final String url) {
        String path = url;
        if (path.startsWith("jar:")) {
            // remove "jar:" prefix and "!/" suffix
            final int index = path.indexOf("!/");
            path = path.substring(4, index);
        }
        try {
            if (OSInfo.getOSType() == OSInfo.OSType.WINDOWS && path.matches("file:[A-Za-z]:.*")) {
                path = "file:/" + path.substring(5);
            }
            return new File(new URL(path).toURI());
        } catch (final MalformedURLException e) {
            // NB: URL is not completely well-formed.
        } catch (final URISyntaxException e) {
            // NB: URL is not completely well-formed.
        }
        if (path.startsWith("file:")) {
            // pass through the URL as-is, minus "file:" prefix
            path = path.substring(5);
            return new File(path);
        }
        throw new IllegalArgumentException("Invalid URL: " + url);
    }

}
