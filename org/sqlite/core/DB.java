// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.core;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import java.sql.BatchUpdateException;
import org.sqlite.ProgressHandler;
import org.sqlite.Function;
import java.util.Iterator;
import org.sqlite.BusyHandler;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.sqlite.SQLiteConfig;

public abstract class DB implements Codes
{
    private final String url;
    private final String fileName;
    private final SQLiteConfig config;
    private final AtomicBoolean closed;
    long begin;
    long commit;
    private final Map<Long, CoreStatement> stmts;
    
    public DB(final String url, final String fileName, final SQLiteConfig config) throws SQLException {
        this.closed = new AtomicBoolean(true);
        this.begin = 0L;
        this.commit = 0L;
        this.stmts = new HashMap<Long, CoreStatement>();
        this.url = url;
        this.fileName = fileName;
        this.config = config;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public boolean isClosed() {
        return this.closed.get();
    }
    
    public SQLiteConfig getConfig() {
        return this.config;
    }
    
    public abstract void interrupt() throws SQLException;
    
    public abstract void busy_timeout(final int p0) throws SQLException;
    
    public abstract void busy_handler(final BusyHandler p0) throws SQLException;
    
    abstract String errmsg() throws SQLException;
    
    public abstract String libversion() throws SQLException;
    
    public abstract int changes() throws SQLException;
    
    public abstract int total_changes() throws SQLException;
    
    public abstract int shared_cache(final boolean p0) throws SQLException;
    
    public abstract int enable_load_extension(final boolean p0) throws SQLException;
    
    public final synchronized void exec(final String sql, final boolean autoCommit) throws SQLException {
        long pointer = 0L;
        try {
            pointer = this.prepare(sql);
            final int rc = this.step(pointer);
            switch (rc) {
                case 101: {
                    this.ensureAutoCommit(autoCommit);
                }
                case 100: {}
                default: {
                    this.throwex(rc);
                    break;
                }
            }
        }
        finally {
            this.finalize(pointer);
        }
    }
    
    public final synchronized void open(final String file, final int openFlags) throws SQLException {
        this._open(file, openFlags);
        this.closed.set(false);
        if (this.fileName.startsWith("file:") && !this.fileName.contains("cache=")) {
            this.shared_cache(this.config.isEnabledSharedCache());
        }
        this.enable_load_extension(this.config.isEnabledLoadExtension());
        this.busy_timeout(this.config.getBusyTimeout());
    }
    
    public final synchronized void close() throws SQLException {
        synchronized (this.stmts) {
            final Iterator<Map.Entry<Long, CoreStatement>> i = this.stmts.entrySet().iterator();
            while (i.hasNext()) {
                final Map.Entry<Long, CoreStatement> entry = i.next();
                final CoreStatement stmt = entry.getValue();
                this.finalize(entry.getKey());
                if (stmt != null) {
                    stmt.pointer = 0L;
                }
                i.remove();
            }
        }
        this.free_functions();
        if (this.begin != 0L) {
            this.finalize(this.begin);
            this.begin = 0L;
        }
        if (this.commit != 0L) {
            this.finalize(this.commit);
            this.commit = 0L;
        }
        this.closed.set(true);
        this._close();
    }
    
    public final synchronized void prepare(final CoreStatement stmt) throws SQLException {
        if (stmt.sql == null) {
            throw new NullPointerException();
        }
        if (stmt.pointer != 0L) {
            this.finalize(stmt);
        }
        stmt.pointer = this.prepare(stmt.sql);
        this.stmts.put(new Long(stmt.pointer), stmt);
    }
    
    public final synchronized int finalize(final CoreStatement stmt) throws SQLException {
        if (stmt.pointer == 0L) {
            return 0;
        }
        int rc = 1;
        try {
            rc = this.finalize(stmt.pointer);
        }
        finally {
            this.stmts.remove(new Long(stmt.pointer));
            stmt.pointer = 0L;
        }
        return rc;
    }
    
    protected abstract void _open(final String p0, final int p1) throws SQLException;
    
    protected abstract void _close() throws SQLException;
    
    public abstract int _exec(final String p0) throws SQLException;
    
    protected abstract long prepare(final String p0) throws SQLException;
    
    protected abstract int finalize(final long p0) throws SQLException;
    
    public abstract int step(final long p0) throws SQLException;
    
    public abstract int reset(final long p0) throws SQLException;
    
    public abstract int clear_bindings(final long p0) throws SQLException;
    
    abstract int bind_parameter_count(final long p0) throws SQLException;
    
    public abstract int column_count(final long p0) throws SQLException;
    
    public abstract int column_type(final long p0, final int p1) throws SQLException;
    
    public abstract String column_decltype(final long p0, final int p1) throws SQLException;
    
    public abstract String column_table_name(final long p0, final int p1) throws SQLException;
    
    public abstract String column_name(final long p0, final int p1) throws SQLException;
    
    public abstract String column_text(final long p0, final int p1) throws SQLException;
    
    public abstract byte[] column_blob(final long p0, final int p1) throws SQLException;
    
    public abstract double column_double(final long p0, final int p1) throws SQLException;
    
    public abstract long column_long(final long p0, final int p1) throws SQLException;
    
    public abstract int column_int(final long p0, final int p1) throws SQLException;
    
    abstract int bind_null(final long p0, final int p1) throws SQLException;
    
    abstract int bind_int(final long p0, final int p1, final int p2) throws SQLException;
    
    abstract int bind_long(final long p0, final int p1, final long p2) throws SQLException;
    
    abstract int bind_double(final long p0, final int p1, final double p2) throws SQLException;
    
    abstract int bind_text(final long p0, final int p1, final String p2) throws SQLException;
    
    abstract int bind_blob(final long p0, final int p1, final byte[] p2) throws SQLException;
    
    public abstract void result_null(final long p0) throws SQLException;
    
    public abstract void result_text(final long p0, final String p1) throws SQLException;
    
    public abstract void result_blob(final long p0, final byte[] p1) throws SQLException;
    
    public abstract void result_double(final long p0, final double p1) throws SQLException;
    
    public abstract void result_long(final long p0, final long p1) throws SQLException;
    
    public abstract void result_int(final long p0, final int p1) throws SQLException;
    
    public abstract void result_error(final long p0, final String p1) throws SQLException;
    
    public abstract String value_text(final Function p0, final int p1) throws SQLException;
    
    public abstract byte[] value_blob(final Function p0, final int p1) throws SQLException;
    
    public abstract double value_double(final Function p0, final int p1) throws SQLException;
    
    public abstract long value_long(final Function p0, final int p1) throws SQLException;
    
    public abstract int value_int(final Function p0, final int p1) throws SQLException;
    
    public abstract int value_type(final Function p0, final int p1) throws SQLException;
    
    public abstract int create_function(final String p0, final Function p1, final int p2) throws SQLException;
    
    public abstract int destroy_function(final String p0) throws SQLException;
    
    abstract void free_functions() throws SQLException;
    
    public abstract int backup(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    public abstract int restore(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    public abstract void register_progress_handler(final int p0, final ProgressHandler p1) throws SQLException;
    
    public abstract void clear_progress_handler() throws SQLException;
    
    abstract boolean[][] column_metadata(final long p0) throws SQLException;
    
    public final synchronized String[] column_names(final long stmt) throws SQLException {
        final String[] names = new String[this.column_count(stmt)];
        for (int i = 0; i < names.length; ++i) {
            names[i] = this.column_name(stmt, i);
        }
        return names;
    }
    
    final synchronized int sqlbind(final long stmt, int pos, final Object v) throws SQLException {
        ++pos;
        if (v == null) {
            return this.bind_null(stmt, pos);
        }
        if (v instanceof Integer) {
            return this.bind_int(stmt, pos, (int)v);
        }
        if (v instanceof Short) {
            return this.bind_int(stmt, pos, (int)v);
        }
        if (v instanceof Long) {
            return this.bind_long(stmt, pos, (long)v);
        }
        if (v instanceof Float) {
            return this.bind_double(stmt, pos, (double)v);
        }
        if (v instanceof Double) {
            return this.bind_double(stmt, pos, (double)v);
        }
        if (v instanceof String) {
            return this.bind_text(stmt, pos, (String)v);
        }
        if (v instanceof byte[]) {
            return this.bind_blob(stmt, pos, (byte[])v);
        }
        throw new SQLException("unexpected param type: " + v.getClass());
    }
    
    final synchronized int[] executeBatch(final long stmt, final int count, final Object[] vals, final boolean autoCommit) throws SQLException {
        if (count < 1) {
            throw new SQLException("count (" + count + ") < 1");
        }
        final int params = this.bind_parameter_count(stmt);
        final int[] changes = new int[count];
        try {
            for (int i = 0; i < count; ++i) {
                this.reset(stmt);
                for (int j = 0; j < params; ++j) {
                    final int rc = this.sqlbind(stmt, j, vals[i * params + j]);
                    if (rc != 0) {
                        this.throwex(rc);
                    }
                }
                final int rc = this.step(stmt);
                if (rc != 101) {
                    this.reset(stmt);
                    if (rc == 100) {
                        throw new BatchUpdateException("batch entry " + i + ": query returns results", changes);
                    }
                    this.throwex(rc);
                }
                changes[i] = this.changes();
            }
        }
        finally {
            this.ensureAutoCommit(autoCommit);
        }
        this.reset(stmt);
        return changes;
    }
    
    public final synchronized boolean execute(final CoreStatement stmt, final Object[] vals) throws SQLException {
        if (vals != null) {
            final int params = this.bind_parameter_count(stmt.pointer);
            if (params > vals.length) {
                throw new SQLException("assertion failure: param count (" + params + ") > value count (" + vals.length + ")");
            }
            for (int i = 0; i < params; ++i) {
                final int rc = this.sqlbind(stmt.pointer, i, vals[i]);
                if (rc != 0) {
                    this.throwex(rc);
                }
            }
        }
        final int statusCode = this.step(stmt.pointer) & 0xFF;
        switch (statusCode) {
            case 101: {
                this.reset(stmt.pointer);
                this.ensureAutoCommit(stmt.conn.getAutoCommit());
                return false;
            }
            case 100: {
                return true;
            }
            case 5:
            case 6:
            case 19:
            case 21: {
                throw this.newSQLException(statusCode);
            }
            default: {
                this.finalize(stmt);
                throw this.newSQLException(statusCode);
            }
        }
    }
    
    final synchronized boolean execute(final String sql, final boolean autoCommit) throws SQLException {
        final int statusCode = this._exec(sql);
        switch (statusCode) {
            case 0: {
                return false;
            }
            case 101: {
                this.ensureAutoCommit(autoCommit);
                return false;
            }
            case 100: {
                return true;
            }
            default: {
                throw this.newSQLException(statusCode);
            }
        }
    }
    
    public final synchronized int executeUpdate(final CoreStatement stmt, final Object[] vals) throws SQLException {
        try {
            if (this.execute(stmt, vals)) {
                throw new SQLException("query returns results");
            }
        }
        finally {
            if (stmt.pointer != 0L) {
                this.reset(stmt.pointer);
            }
        }
        return this.changes();
    }
    
    final void throwex() throws SQLException {
        throw new SQLException(this.errmsg());
    }
    
    public final void throwex(final int errorCode) throws SQLException {
        throw this.newSQLException(errorCode);
    }
    
    static final void throwex(final int errorCode, final String errorMessage) throws SQLiteException {
        throw newSQLException(errorCode, errorMessage);
    }
    
    public static SQLiteException newSQLException(final int errorCode, final String errorMessage) {
        final SQLiteErrorCode code = SQLiteErrorCode.getErrorCode(errorCode);
        final SQLiteException e = new SQLiteException(String.format("%s (%s)", code, errorMessage), code);
        return e;
    }
    
    private SQLiteException newSQLException(final int errorCode) throws SQLException {
        return newSQLException(errorCode, this.errmsg());
    }
    
    final void ensureAutoCommit(final boolean autoCommit) throws SQLException {
        if (!autoCommit) {
            return;
        }
        if (this.begin == 0L) {
            this.begin = this.prepare("begin;");
        }
        if (this.commit == 0L) {
            this.commit = this.prepare("commit;");
        }
        try {
            if (this.step(this.begin) != 101) {
                return;
            }
            final int rc = this.step(this.commit);
            if (rc != 101) {
                this.reset(this.commit);
                this.throwex(rc);
            }
        }
        finally {
            this.reset(this.begin);
            this.reset(this.commit);
        }
    }
    
    public interface ProgressObserver
    {
        void progress(final int p0, final int p1);
    }
}
