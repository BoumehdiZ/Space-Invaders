// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite;

import java.net.URL;
import java.util.Properties;
import org.sqlite.util.OSInfo;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.io.ByteArrayOutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.FilenameFilter;
import java.io.File;

public class SQLiteJDBCLoader
{
    private static boolean extracted;
    
    public static synchronized boolean initialize() throws Exception {
        if (!SQLiteJDBCLoader.extracted) {
            cleanup();
        }
        loadSQLiteNativeLibrary();
        return SQLiteJDBCLoader.extracted;
    }
    
    private static File getTempDir() {
        return new File(System.getProperty("org.sqlite.tmpdir", System.getProperty("java.io.tmpdir")));
    }
    
    static void cleanup() {
        final String tempFolder = getTempDir().getAbsolutePath();
        final File dir = new File(tempFolder);
        final File[] nativeLibFiles = dir.listFiles(new FilenameFilter() {
            private final String searchPattern = "sqlite-" + SQLiteJDBCLoader.getVersion();
            
            @Override
            public boolean accept(final File dir, final String name) {
                return name.startsWith(this.searchPattern) && !name.endsWith(".lck");
            }
        });
        if (nativeLibFiles != null) {
            for (final File nativeLibFile : nativeLibFiles) {
                final File lckFile = new File(nativeLibFile.getAbsolutePath() + ".lck");
                if (!lckFile.exists()) {
                    try {
                        nativeLibFile.delete();
                    }
                    catch (SecurityException e) {
                        System.err.println("Failed to delete old native lib" + e.getMessage());
                    }
                }
            }
        }
    }
    
    @Deprecated
    static boolean getPureJavaFlag() {
        return Boolean.parseBoolean(System.getProperty("sqlite.purejava", "false"));
    }
    
    @Deprecated
    public static boolean isPureJavaMode() {
        return false;
    }
    
    public static boolean isNativeMode() throws Exception {
        initialize();
        return SQLiteJDBCLoader.extracted;
    }
    
    static String md5sum(final InputStream input) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(input);
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final DigestInputStream digestInputStream = new DigestInputStream(in, digest);
            while (digestInputStream.read() >= 0) {}
            final ByteArrayOutputStream md5out = new ByteArrayOutputStream();
            md5out.write(digest.digest());
            return md5out.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available: " + e);
        }
        finally {
            in.close();
        }
    }
    
    private static boolean contentsEquals(InputStream in1, InputStream in2) throws IOException {
        if (!(in1 instanceof BufferedInputStream)) {
            in1 = new BufferedInputStream(in1);
        }
        if (!(in2 instanceof BufferedInputStream)) {
            in2 = new BufferedInputStream(in2);
        }
        for (int ch = in1.read(); ch != -1; ch = in1.read()) {
            final int ch2 = in2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = in2.read();
        return ch2 == -1;
    }
    
    private static boolean extractAndLoadLibraryFile(final String libFolderForCurrentOS, final String libraryFileName, final String targetFolder) {
        final String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;
        final String uuid = UUID.randomUUID().toString();
        final String extractedLibFileName = String.format("sqlite-%s-%s-%s", getVersion(), uuid, libraryFileName);
        final String extractedLckFileName = extractedLibFileName + ".lck";
        final File extractedLibFile = new File(targetFolder, extractedLibFileName);
        final File extractedLckFile = new File(targetFolder, extractedLckFileName);
        try {
            final InputStream reader = SQLiteJDBCLoader.class.getResourceAsStream(nativeLibraryFilePath);
            if (!extractedLckFile.exists()) {
                new FileOutputStream(extractedLckFile).close();
            }
            final FileOutputStream writer = new FileOutputStream(extractedLibFile);
            try {
                final byte[] buffer = new byte[8192];
                int bytesRead = 0;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, bytesRead);
                }
            }
            finally {
                extractedLibFile.deleteOnExit();
                extractedLckFile.deleteOnExit();
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            extractedLibFile.setReadable(true);
            extractedLibFile.setWritable(true, true);
            extractedLibFile.setExecutable(true);
            final InputStream nativeIn = SQLiteJDBCLoader.class.getResourceAsStream(nativeLibraryFilePath);
            final InputStream extractedLibIn = new FileInputStream(extractedLibFile);
            try {
                if (!contentsEquals(nativeIn, extractedLibIn)) {
                    throw new RuntimeException(String.format("Failed to write a native library file at %s", extractedLibFile));
                }
            }
            finally {
                if (nativeIn != null) {
                    nativeIn.close();
                }
                if (extractedLibIn != null) {
                    extractedLibIn.close();
                }
            }
            return loadNativeLibrary(targetFolder, extractedLibFileName);
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    
    private static boolean loadNativeLibrary(final String path, final String name) {
        final File libPath = new File(path, name);
        if (libPath.exists()) {
            try {
                System.load(new File(path, name).getAbsolutePath());
                return true;
            }
            catch (UnsatisfiedLinkError e) {
                System.err.println("Failed to load native library:" + name + ". osinfo: " + OSInfo.getNativeLibFolderPathForCurrentOS());
                System.err.println(e);
                return false;
            }
        }
        return false;
    }
    
    private static void loadSQLiteNativeLibrary() throws Exception {
        if (SQLiteJDBCLoader.extracted) {
            return;
        }
        String sqliteNativeLibraryPath = System.getProperty("org.sqlite.lib.path");
        String sqliteNativeLibraryName = System.getProperty("org.sqlite.lib.name");
        if (sqliteNativeLibraryName == null) {
            sqliteNativeLibraryName = System.mapLibraryName("sqlitejdbc");
            if (sqliteNativeLibraryName != null && sqliteNativeLibraryName.endsWith(".dylib")) {
                sqliteNativeLibraryName = sqliteNativeLibraryName.replace(".dylib", ".jnilib");
            }
        }
        if (sqliteNativeLibraryPath != null && loadNativeLibrary(sqliteNativeLibraryPath, sqliteNativeLibraryName)) {
            SQLiteJDBCLoader.extracted = true;
            return;
        }
        final String packagePath = SQLiteJDBCLoader.class.getPackage().getName().replaceAll("\\.", "/");
        sqliteNativeLibraryPath = String.format("/%s/native/%s", packagePath, OSInfo.getNativeLibFolderPathForCurrentOS());
        boolean hasNativeLib = hasResource(sqliteNativeLibraryPath + "/" + sqliteNativeLibraryName);
        if (!hasNativeLib && OSInfo.getOSName().equals("Mac")) {
            final String altName = "libsqlitejdbc.jnilib";
            if (hasResource(sqliteNativeLibraryPath + "/" + altName)) {
                sqliteNativeLibraryName = altName;
                hasNativeLib = true;
            }
        }
        if (!hasNativeLib) {
            SQLiteJDBCLoader.extracted = false;
            throw new Exception(String.format("No native library is found for os.name=%s and os.arch=%s. path=%s", OSInfo.getOSName(), OSInfo.getArchName(), sqliteNativeLibraryPath));
        }
        final String tempFolder = getTempDir().getAbsolutePath();
        if (extractAndLoadLibraryFile(sqliteNativeLibraryPath, sqliteNativeLibraryName, tempFolder)) {
            SQLiteJDBCLoader.extracted = true;
            return;
        }
        SQLiteJDBCLoader.extracted = false;
    }
    
    private static boolean hasResource(final String path) {
        return SQLiteJDBCLoader.class.getResource(path) != null;
    }
    
    private static void getNativeLibraryFolderForTheCurrentOS() {
        final String osName = OSInfo.getOSName();
        final String archName = OSInfo.getArchName();
    }
    
    public static int getMajorVersion() {
        final String[] c = getVersion().split("\\.");
        return (c.length > 0) ? Integer.parseInt(c[0]) : 1;
    }
    
    public static int getMinorVersion() {
        final String[] c = getVersion().split("\\.");
        return (c.length > 1) ? Integer.parseInt(c[1]) : 0;
    }
    
    public static String getVersion() {
        URL versionFile = SQLiteJDBCLoader.class.getResource("/META-INF/maven/org.xerial/sqlite-jdbc/pom.properties");
        if (versionFile == null) {
            versionFile = SQLiteJDBCLoader.class.getResource("/META-INF/maven/org.xerial/sqlite-jdbc/VERSION");
        }
        String version = "unknown";
        try {
            if (versionFile != null) {
                final Properties versionData = new Properties();
                versionData.load(versionFile.openStream());
                version = versionData.getProperty("version", version);
                version = version.trim().replaceAll("[^0-9\\.]", "");
            }
        }
        catch (IOException e) {
            System.err.println(e);
        }
        return version;
    }
    
    static {
        SQLiteJDBCLoader.extracted = false;
    }
}
