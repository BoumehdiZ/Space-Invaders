// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.core;

import org.sqlite.ProgressHandler;
import java.io.UnsupportedEncodingException;
import org.sqlite.Function;
import org.sqlite.BusyHandler;
import org.sqlite.SQLiteJDBCLoader;
import java.sql.SQLException;
import org.sqlite.SQLiteConfig;

public final class NativeDB extends DB
{
    long pointer;
    private static boolean isLoaded;
    private static boolean loadSucceeded;
    private final long udfdatalist = 0L;
    
    public NativeDB(final String url, final String fileName, final SQLiteConfig config) throws SQLException {
        super(url, fileName, config);
        this.pointer = 0L;
    }
    
    public static boolean load() throws Exception {
        if (NativeDB.isLoaded) {
            return NativeDB.loadSucceeded;
        }
        NativeDB.loadSucceeded = SQLiteJDBCLoader.initialize();
        NativeDB.isLoaded = true;
        return NativeDB.loadSucceeded;
    }
    
    @Override
    protected synchronized void _open(final String file, final int openFlags) throws SQLException {
        this._open_utf8(stringToUtf8ByteArray(file), openFlags);
    }
    
    synchronized native void _open_utf8(final byte[] p0, final int p1) throws SQLException;
    
    @Override
    protected synchronized native void _close() throws SQLException;
    
    @Override
    public synchronized int _exec(final String sql) throws SQLException {
        return this._exec_utf8(stringToUtf8ByteArray(sql));
    }
    
    synchronized native int _exec_utf8(final byte[] p0) throws SQLException;
    
    @Override
    public synchronized native int shared_cache(final boolean p0);
    
    @Override
    public synchronized native int enable_load_extension(final boolean p0);
    
    @Override
    public native void interrupt();
    
    @Override
    public synchronized native void busy_timeout(final int p0);
    
    @Override
    public synchronized native void busy_handler(final BusyHandler p0);
    
    @Override
    protected synchronized long prepare(final String sql) throws SQLException {
        return this.prepare_utf8(stringToUtf8ByteArray(sql));
    }
    
    synchronized native long prepare_utf8(final byte[] p0) throws SQLException;
    
    @Override
    synchronized String errmsg() {
        return utf8ByteArrayToString(this.errmsg_utf8());
    }
    
    synchronized native byte[] errmsg_utf8();
    
    @Override
    public synchronized String libversion() {
        return utf8ByteArrayToString(this.libversion_utf8());
    }
    
    native byte[] libversion_utf8();
    
    @Override
    public synchronized native int changes();
    
    @Override
    public synchronized native int total_changes();
    
    @Override
    protected synchronized native int finalize(final long p0);
    
    @Override
    public synchronized native int step(final long p0);
    
    @Override
    public synchronized native int reset(final long p0);
    
    @Override
    public synchronized native int clear_bindings(final long p0);
    
    @Override
    synchronized native int bind_parameter_count(final long p0);
    
    @Override
    public synchronized native int column_count(final long p0);
    
    @Override
    public synchronized native int column_type(final long p0, final int p1);
    
    @Override
    public synchronized String column_decltype(final long stmt, final int col) {
        return utf8ByteArrayToString(this.column_decltype_utf8(stmt, col));
    }
    
    synchronized native byte[] column_decltype_utf8(final long p0, final int p1);
    
    @Override
    public synchronized String column_table_name(final long stmt, final int col) {
        return utf8ByteArrayToString(this.column_table_name_utf8(stmt, col));
    }
    
    synchronized native byte[] column_table_name_utf8(final long p0, final int p1);
    
    @Override
    public synchronized String column_name(final long stmt, final int col) {
        return utf8ByteArrayToString(this.column_name_utf8(stmt, col));
    }
    
    synchronized native byte[] column_name_utf8(final long p0, final int p1);
    
    @Override
    public synchronized String column_text(final long stmt, final int col) {
        return utf8ByteArrayToString(this.column_text_utf8(stmt, col));
    }
    
    synchronized native byte[] column_text_utf8(final long p0, final int p1);
    
    @Override
    public synchronized native byte[] column_blob(final long p0, final int p1);
    
    @Override
    public synchronized native double column_double(final long p0, final int p1);
    
    @Override
    public synchronized native long column_long(final long p0, final int p1);
    
    @Override
    public synchronized native int column_int(final long p0, final int p1);
    
    @Override
    synchronized native int bind_null(final long p0, final int p1);
    
    @Override
    synchronized native int bind_int(final long p0, final int p1, final int p2);
    
    @Override
    synchronized native int bind_long(final long p0, final int p1, final long p2);
    
    @Override
    synchronized native int bind_double(final long p0, final int p1, final double p2);
    
    @Override
    synchronized int bind_text(final long stmt, final int pos, final String v) {
        return this.bind_text_utf8(stmt, pos, stringToUtf8ByteArray(v));
    }
    
    synchronized native int bind_text_utf8(final long p0, final int p1, final byte[] p2);
    
    @Override
    synchronized native int bind_blob(final long p0, final int p1, final byte[] p2);
    
    @Override
    public synchronized native void result_null(final long p0);
    
    @Override
    public synchronized void result_text(final long context, final String val) {
        this.result_text_utf8(context, stringToUtf8ByteArray(val));
    }
    
    synchronized native void result_text_utf8(final long p0, final byte[] p1);
    
    @Override
    public synchronized native void result_blob(final long p0, final byte[] p1);
    
    @Override
    public synchronized native void result_double(final long p0, final double p1);
    
    @Override
    public synchronized native void result_long(final long p0, final long p1);
    
    @Override
    public synchronized native void result_int(final long p0, final int p1);
    
    @Override
    public synchronized void result_error(final long context, final String err) {
        this.result_error_utf8(context, stringToUtf8ByteArray(err));
    }
    
    synchronized native void result_error_utf8(final long p0, final byte[] p1);
    
    @Override
    public synchronized String value_text(final Function f, final int arg) {
        return utf8ByteArrayToString(this.value_text_utf8(f, arg));
    }
    
    synchronized native byte[] value_text_utf8(final Function p0, final int p1);
    
    @Override
    public synchronized native byte[] value_blob(final Function p0, final int p1);
    
    @Override
    public synchronized native double value_double(final Function p0, final int p1);
    
    @Override
    public synchronized native long value_long(final Function p0, final int p1);
    
    @Override
    public synchronized native int value_int(final Function p0, final int p1);
    
    @Override
    public synchronized native int value_type(final Function p0, final int p1);
    
    @Override
    public synchronized int create_function(final String name, final Function func, final int flags) {
        return this.create_function_utf8(stringToUtf8ByteArray(name), func, flags);
    }
    
    synchronized native int create_function_utf8(final byte[] p0, final Function p1, final int p2);
    
    @Override
    public synchronized int destroy_function(final String name) {
        return this.destroy_function_utf8(stringToUtf8ByteArray(name));
    }
    
    synchronized native int destroy_function_utf8(final byte[] p0);
    
    @Override
    synchronized native void free_functions();
    
    @Override
    public int backup(final String dbName, final String destFileName, final ProgressObserver observer) throws SQLException {
        return this.backup(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(destFileName), observer);
    }
    
    synchronized native int backup(final byte[] p0, final byte[] p1, final ProgressObserver p2) throws SQLException;
    
    @Override
    public synchronized int restore(final String dbName, final String sourceFileName, final ProgressObserver observer) throws SQLException {
        return this.restore(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(sourceFileName), observer);
    }
    
    synchronized native int restore(final byte[] p0, final byte[] p1, final ProgressObserver p2) throws SQLException;
    
    @Override
    synchronized native boolean[][] column_metadata(final long p0);
    
    static void throwex(final String msg) throws SQLException {
        throw new SQLException(msg);
    }
    
    static byte[] stringToUtf8ByteArray(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is not supported", e);
        }
    }
    
    static String utf8ByteArrayToString(final byte[] utf8bytes) {
        if (utf8bytes == null) {
            return null;
        }
        try {
            return new String(utf8bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is not supported", e);
        }
    }
    
    @Override
    public synchronized native void register_progress_handler(final int p0, final ProgressHandler p1) throws SQLException;
    
    @Override
    public synchronized native void clear_progress_handler() throws SQLException;
    
    static {
        if ("The Android Project".equals(System.getProperty("java.vm.vendor"))) {
            System.loadLibrary("sqlitejdbc");
            NativeDB.isLoaded = true;
            NativeDB.loadSucceeded = true;
        }
        else {
            NativeDB.isLoaded = false;
            NativeDB.loadSucceeded = false;
        }
    }
}
