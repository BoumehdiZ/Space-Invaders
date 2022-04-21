// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.jdbc4;

import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import java.sql.Statement;
import org.sqlite.jdbc3.JDBC3Statement;

public class JDBC4Statement extends JDBC3Statement implements Statement
{
    private boolean closed;
    boolean closeOnCompletion;
    
    public JDBC4Statement(final SQLiteConnection conn) {
        super(conn);
        this.closed = false;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws ClassCastException {
        return iface.cast(this);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) {
        return iface.isInstance(this);
    }
    
    @Override
    public void close() throws SQLException {
        super.close();
        this.closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        if (this.closed) {
            throw new SQLException("statement is closed");
        }
        this.closeOnCompletion = true;
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        if (this.closed) {
            throw new SQLException("statement is closed");
        }
        return this.closeOnCompletion;
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }
}
