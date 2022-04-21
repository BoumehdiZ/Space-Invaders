// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.jdbc3;

import java.sql.Connection;
import java.sql.SQLWarning;
import java.sql.BatchUpdateException;
import org.sqlite.core.DB;
import java.sql.ResultSet;
import org.sqlite.ExtendedCommand;
import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CoreStatement;

public abstract class JDBC3Statement extends CoreStatement
{
    protected JDBC3Statement(final SQLiteConnection conn) {
        super(conn);
    }
    
    public void close() throws SQLException {
        this.internalClose();
    }
    
    @Override
    protected void finalize() throws SQLException {
        this.close();
    }
    
    public boolean execute(final String sql) throws SQLException {
        this.internalClose();
        final ExtendedCommand.SQLExtension ext = ExtendedCommand.parse(sql);
        if (ext != null) {
            ext.execute(this.conn.getDatabase());
            return false;
        }
        this.sql = sql;
        this.conn.getDatabase().prepare(this);
        return this.exec();
    }
    
    @Override
    public ResultSet executeQuery(final String sql, final boolean closeStmt) throws SQLException {
        this.rs.closeStmt = closeStmt;
        return this.executeQuery(sql);
    }
    
    public ResultSet executeQuery(final String sql) throws SQLException {
        this.internalClose();
        this.sql = sql;
        this.conn.getDatabase().prepare(this);
        if (!this.exec()) {
            this.internalClose();
            throw new SQLException("query does not return ResultSet", "SQLITE_DONE", 101);
        }
        return this.getResultSet();
    }
    
    public int executeUpdate(final String sql) throws SQLException {
        this.internalClose();
        this.sql = sql;
        final DB db = this.conn.getDatabase();
        int changes = 0;
        final ExtendedCommand.SQLExtension ext = ExtendedCommand.parse(sql);
        if (ext != null) {
            ext.execute(db);
        }
        else {
            try {
                changes = db.total_changes();
                final int statusCode = db._exec(sql);
                if (statusCode != 0) {
                    throw DB.newSQLException(statusCode, "");
                }
                changes = db.total_changes() - changes;
            }
            finally {
                this.internalClose();
            }
        }
        return changes;
    }
    
    public ResultSet getResultSet() throws SQLException {
        this.checkOpen();
        if (this.rs.isOpen()) {
            throw new SQLException("ResultSet already requested");
        }
        final DB db = this.conn.getDatabase();
        if (db.column_count(this.pointer) == 0) {
            return null;
        }
        if (this.rs.colsMeta == null) {
            this.rs.colsMeta = db.column_names(this.pointer);
        }
        this.rs.cols = this.rs.colsMeta;
        this.rs.open = this.resultsWaiting;
        this.resultsWaiting = false;
        return (ResultSet)this.rs;
    }
    
    public int getUpdateCount() throws SQLException {
        final DB db = this.conn.getDatabase();
        if (this.pointer != 0L && !this.rs.isOpen() && !this.resultsWaiting && db.column_count(this.pointer) == 0) {
            return db.changes();
        }
        return -1;
    }
    
    public void addBatch(final String sql) throws SQLException {
        this.internalClose();
        if (this.batch == null || this.batchPos + 1 >= this.batch.length) {
            final Object[] nb = new Object[Math.max(10, this.batchPos * 2)];
            if (this.batch != null) {
                System.arraycopy(this.batch, 0, nb, 0, this.batch.length);
            }
            this.batch = nb;
        }
        this.batch[this.batchPos++] = sql;
    }
    
    public void clearBatch() throws SQLException {
        this.batchPos = 0;
        if (this.batch != null) {
            for (int i = 0; i < this.batch.length; ++i) {
                this.batch[i] = null;
            }
        }
    }
    
    public int[] executeBatch() throws SQLException {
        this.internalClose();
        if (this.batch == null || this.batchPos == 0) {
            return new int[0];
        }
        final int[] changes = new int[this.batchPos];
        final DB db = this.conn.getDatabase();
        synchronized (db) {
            try {
                for (int i = 0; i < changes.length; ++i) {
                    try {
                        this.sql = (String)this.batch[i];
                        db.prepare(this);
                        changes[i] = db.executeUpdate(this, null);
                    }
                    catch (SQLException e) {
                        throw new BatchUpdateException("batch entry " + i + ": " + e.getMessage(), changes);
                    }
                    finally {
                        db.finalize(this);
                    }
                }
            }
            finally {
                this.clearBatch();
            }
        }
        return changes;
    }
    
    public void setCursorName(final String name) {
    }
    
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    public void clearWarnings() throws SQLException {
    }
    
    public Connection getConnection() throws SQLException {
        return this.conn;
    }
    
    public void cancel() throws SQLException {
        this.conn.getDatabase().interrupt();
    }
    
    public int getQueryTimeout() throws SQLException {
        return this.conn.getBusyTimeout();
    }
    
    public void setQueryTimeout(final int seconds) throws SQLException {
        if (seconds < 0) {
            throw new SQLException("query timeout must be >= 0");
        }
        this.conn.setBusyTimeout(1000 * seconds);
    }
    
    public int getMaxRows() throws SQLException {
        return this.rs.maxRows;
    }
    
    public void setMaxRows(final int max) throws SQLException {
        if (max < 0) {
            throw new SQLException("max row count must be >= 0");
        }
        this.rs.maxRows = max;
    }
    
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }
    
    public void setMaxFieldSize(final int max) throws SQLException {
        if (max < 0) {
            throw new SQLException("max field size " + max + " cannot be negative");
        }
    }
    
    public int getFetchSize() throws SQLException {
        return ((ResultSet)this.rs).getFetchSize();
    }
    
    public void setFetchSize(final int r) throws SQLException {
        ((ResultSet)this.rs).setFetchSize(r);
    }
    
    public int getFetchDirection() throws SQLException {
        return ((ResultSet)this.rs).getFetchDirection();
    }
    
    public void setFetchDirection(final int d) throws SQLException {
        ((ResultSet)this.rs).setFetchDirection(d);
    }
    
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.conn.getSQLiteDatabaseMetaData().getGeneratedKeys();
    }
    
    public boolean getMoreResults() throws SQLException {
        return this.getMoreResults(0);
    }
    
    public boolean getMoreResults(final int c) throws SQLException {
        this.checkOpen();
        this.internalClose();
        return false;
    }
    
    public int getResultSetConcurrency() throws SQLException {
        return 1007;
    }
    
    public int getResultSetHoldability() throws SQLException {
        return 2;
    }
    
    public int getResultSetType() throws SQLException {
        return 1003;
    }
    
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        if (enable) {
            throw this.unused();
        }
    }
    
    protected SQLException unused() {
        return new SQLException("not implemented by SQLite JDBC driver");
    }
    
    public boolean execute(final String sql, final int[] colinds) throws SQLException {
        throw this.unused();
    }
    
    public boolean execute(final String sql, final String[] colnames) throws SQLException {
        throw this.unused();
    }
    
    public int executeUpdate(final String sql, final int autoKeys) throws SQLException {
        throw this.unused();
    }
    
    public int executeUpdate(final String sql, final int[] colinds) throws SQLException {
        throw this.unused();
    }
    
    public int executeUpdate(final String sql, final String[] cols) throws SQLException {
        throw this.unused();
    }
    
    public boolean execute(final String sql, final int autokeys) throws SQLException {
        throw this.unused();
    }
    
    static class BackupObserver implements DB.ProgressObserver
    {
        @Override
        public void progress(final int remaining, final int pageCount) {
            System.out.println(String.format("remaining:%d, page count:%d", remaining, pageCount));
        }
    }
}
