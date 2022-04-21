// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.jdbc4;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.RowIdLifetime;
import org.sqlite.SQLiteConnection;
import org.sqlite.jdbc3.JDBC3DatabaseMetaData;

public class JDBC4DatabaseMetaData extends JDBC3DatabaseMetaData
{
    public JDBC4DatabaseMetaData(final SQLiteConnection conn) {
        super(conn);
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
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
