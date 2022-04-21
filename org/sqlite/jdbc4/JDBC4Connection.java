// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.jdbc4;

import java.sql.Array;
import java.sql.SQLClientInfoException;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Blob;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import org.sqlite.SQLiteConnection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;
import org.sqlite.jdbc3.JDBC3Connection;

public class JDBC4Connection extends JDBC3Connection
{
    public JDBC4Connection(final String url, final String fileName, final Properties prop) throws SQLException {
        super(url, fileName, prop);
    }
    
    @Override
    public Statement createStatement(final int rst, final int rsc, final int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4Statement(this);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int rst, final int rsc, final int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4PreparedStatement(this, sql);
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return super.isClosed();
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
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        if (this.isClosed()) {
            return false;
        }
        final Statement statement = this.createStatement();
        try {
            return statement.execute("select 1");
        }
        finally {
            statement.close();
        }
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return null;
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return null;
    }
}
