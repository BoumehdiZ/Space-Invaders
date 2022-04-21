// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.util;

import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class OSInfo
{
    private static HashMap<String, String> archMapping;
    public static final String X86 = "x86";
    public static final String X86_64 = "x86_64";
    public static final String IA64_32 = "ia64_32";
    public static final String IA64 = "ia64";
    public static final String PPC = "ppc";
    public static final String PPC64 = "ppc64";
    
    public static void main(final String[] args) {
        if (args.length >= 1) {
            if ("--os".equals(args[0])) {
                System.out.print(getOSName());
                return;
            }
            if ("--arch".equals(args[0])) {
                System.out.print(getArchName());
                return;
            }
        }
        System.out.print(getNativeLibFolderPathForCurrentOS());
    }
    
    public static String getNativeLibFolderPathForCurrentOS() {
        return getOSName() + "/" + getArchName();
    }
    
    public static String getOSName() {
        return translateOSNameToFolderName(System.getProperty("os.name"));
    }
    
    public static boolean isAndroid() {
        return System.getProperty("java.runtime.name", "").toLowerCase().contains("android");
    }
    
    static String getHardwareName() {
        try {
            final Process p = Runtime.getRuntime().exec("uname -m");
            p.waitFor();
            final InputStream in = p.getInputStream();
            try {
                int readLen = 0;
                final ByteArrayOutputStream b = new ByteArrayOutputStream();
                final byte[] buf = new byte[32];
                while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
                    b.write(buf, 0, readLen);
                }
                return b.toString();
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        catch (Throwable e) {
            System.err.println("Error while running uname -m: " + e.getMessage());
            return "unknown";
        }
    }
    
    static String resolveArmArchType() {
        if (System.getProperty("os.name").contains("Linux")) {
            final String armType = getHardwareName();
            if (armType.startsWith("armv6")) {
                return "armv6";
            }
            if (armType.startsWith("armv7")) {
                return "armv7";
            }
            if (armType.startsWith("armv5")) {
                return "arm";
            }
            if (armType.equals("aarch64")) {
                return "arm64";
            }
            final String abi = System.getProperty("sun.arch.abi");
            if (abi != null && abi.startsWith("gnueabihf")) {
                return "armv7";
            }
            final String javaHome = System.getProperty("java.home");
            try {
                int exitCode = Runtime.getRuntime().exec("which readelf").waitFor();
                if (exitCode == 0) {
                    final String[] cmdarray = { "/bin/sh", "-c", "find '" + javaHome + "' -name 'libjvm.so' | head -1 | xargs readelf -A | grep 'Tag_ABI_VFP_args: VFP registers'" };
                    exitCode = Runtime.getRuntime().exec(cmdarray).waitFor();
                    if (exitCode == 0) {
                        return "armv7";
                    }
                }
                else {
                    System.err.println("WARNING! readelf not found. Cannot check if running on an armhf system, armel architecture will be presumed.");
                }
            }
            catch (IOException ex) {}
            catch (InterruptedException ex2) {}
        }
        return "arm";
    }
    
    public static String getArchName() {
        String osArch = System.getProperty("os.arch");
        if (isAndroid()) {
            return "android-arm";
        }
        if (osArch.startsWith("arm")) {
            osArch = resolveArmArchType();
        }
        else {
            final String lc = osArch.toLowerCase(Locale.US);
            if (OSInfo.archMapping.containsKey(lc)) {
                return OSInfo.archMapping.get(lc);
            }
        }
        return translateArchNameToFolderName(osArch);
    }
    
    static String translateOSNameToFolderName(final String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        }
        if (osName.contains("Mac") || osName.contains("Darwin")) {
            return "Mac";
        }
        if (osName.contains("Linux")) {
            return "Linux";
        }
        if (osName.contains("AIX")) {
            return "AIX";
        }
        return osName.replaceAll("\\W", "");
    }
    
    static String translateArchNameToFolderName(final String archName) {
        return archName.replaceAll("\\W", "");
    }
    
    static {
        (OSInfo.archMapping = new HashMap<String, String>()).put("x86", "x86");
        OSInfo.archMapping.put("i386", "x86");
        OSInfo.archMapping.put("i486", "x86");
        OSInfo.archMapping.put("i586", "x86");
        OSInfo.archMapping.put("i686", "x86");
        OSInfo.archMapping.put("pentium", "x86");
        OSInfo.archMapping.put("x86_64", "x86_64");
        OSInfo.archMapping.put("amd64", "x86_64");
        OSInfo.archMapping.put("em64t", "x86_64");
        OSInfo.archMapping.put("universal", "x86_64");
        OSInfo.archMapping.put("ia64", "ia64");
        OSInfo.archMapping.put("ia64w", "ia64");
        OSInfo.archMapping.put("ia64_32", "ia64_32");
        OSInfo.archMapping.put("ia64n", "ia64_32");
        OSInfo.archMapping.put("ppc", "ppc");
        OSInfo.archMapping.put("power", "ppc");
        OSInfo.archMapping.put("powerpc", "ppc");
        OSInfo.archMapping.put("power_pc", "ppc");
        OSInfo.archMapping.put("power_rs", "ppc");
        OSInfo.archMapping.put("ppc64", "ppc64");
        OSInfo.archMapping.put("power64", "ppc64");
        OSInfo.archMapping.put("powerpc64", "ppc64");
        OSInfo.archMapping.put("power_pc64", "ppc64");
        OSInfo.archMapping.put("power_rs64", "ppc64");
    }
}
