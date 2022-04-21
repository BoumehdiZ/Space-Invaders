// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.jdbc4;

import java.io.Writer;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.net.URL;
import java.sql.Ref;
import java.util.Map;
import java.sql.Clob;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.sql.Array;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Reader;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.RowId;
import java.sql.SQLException;
import org.sqlite.core.CoreStatement;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import org.sqlite.jdbc3.JDBC3ResultSet;

public class JDBC4ResultSet extends JDBC3ResultSet implements ResultSet, ResultSetMetaData
{
    public JDBC4ResultSet(final CoreStatement stmt) {
        super(stmt);
    }
    
    @Override
    public void close() throws SQLException {
        final boolean wasOpen = this.isOpen();
        super.close();
        if (wasOpen && this.stmt instanceof JDBC4Statement) {
            final JDBC4Statement stat = (JDBC4Statement)this.stmt;
            if (stat.closeOnCompletion && !stat.isClosed()) {
                stat.close();
            }
        }
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
    public RowId getRowId(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return !this.isOpen();
    }
    
    @Override
    public void updateNString(final int columnIndex, final String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNString(final String columnLabel, final String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public NClob getNClob(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public NClob getNClob(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public Reader getNCharacterStream(final int col) throws SQLException {
        final String data = this.getString(col);
        return this.getNCharacterStreamInternal(data);
    }
    
    private Reader getNCharacterStreamInternal(final String data) {
        if (data == null) {
            return null;
        }
        final Reader reader = new StringReader(data);
        return reader;
    }
    
    @Override
    public Reader getNCharacterStream(final String col) throws SQLException {
        final String data = this.getString(col);
        return this.getNCharacterStreamInternal(data);
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    protected SQLException unused() {
        return new SQLFeatureNotSupportedException();
    }
    
    @Override
    public Array getArray(final int i) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Array getArray(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public InputStream getAsciiStream(final int col) throws SQLException {
        final String data = this.getString(col);
        return this.getAsciiStreamInternal(data);
    }
    
    @Override
    public InputStream getAsciiStream(final String col) throws SQLException {
        final String data = this.getString(col);
        return this.getAsciiStreamInternal(data);
    }
    
    private InputStream getAsciiStreamInternal(final String data) {
        if (data == null) {
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = new ByteArrayInputStream(data.getBytes("ASCII"));
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return inputStream;
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int col, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String col, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Blob getBlob(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Blob getBlob(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Clob getClob(final int col) throws SQLException {
        return new SqliteClob(this.getString(col));
    }
    
    @Override
    public Clob getClob(final String col) throws SQLException {
        return new SqliteClob(this.getString(col));
    }
    
    @Override
    public Object getObject(final int col, final Map map) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Object getObject(final String col, final Map map) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Ref getRef(final int i) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Ref getRef(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public InputStream getUnicodeStream(final int col) throws SQLException {
        return this.getAsciiStream(col);
    }
    
    @Override
    public InputStream getUnicodeStream(final String col) throws SQLException {
        return this.getAsciiStream(col);
    }
    
    @Override
    public URL getURL(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public URL getURL(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void insertRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean last() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean previous() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean absolute(final int row) throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void afterLast() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean first() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateArray(final int col, final Array x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateArray(final String col, final Array x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateAsciiStream(final int col, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateAsciiStream(final String col, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBigDecimal(final int col, final BigDecimal x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBigDecimal(final String col, final BigDecimal x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBinaryStream(final int c, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBinaryStream(final String c, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBlob(final int col, final Blob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBlob(final String col, final Blob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBoolean(final int col, final boolean x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBoolean(final String col, final boolean x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateByte(final int col, final byte x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateByte(final String col, final byte x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBytes(final int col, final byte[] x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBytes(final String col, final byte[] x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateCharacterStream(final int c, final Reader x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateCharacterStream(final String c, final Reader r, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateClob(final int col, final Clob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateClob(final String col, final Clob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDate(final int col, final Date x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDate(final String col, final Date x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDouble(final int col, final double x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDouble(final String col, final double x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateFloat(final int col, final float x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateFloat(final String col, final float x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateInt(final int col, final int x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateInt(final String col, final int x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateLong(final int col, final long x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateLong(final String col, final long x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateNull(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateNull(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final int c, final Object x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final int c, final Object x, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final String col, final Object x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final String c, final Object x, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateRef(final int col, final Ref x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateRef(final String c, final Ref x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateRow() throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateShort(final int c, final short x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateShort(final String c, final short x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateString(final int c, final String x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateString(final String c, final String x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTime(final int c, final Time x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTime(final String c, final Time x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTimestamp(final int c, final Timestamp x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTimestamp(final String c, final Timestamp x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        throw this.unused();
    }
    
    class SqliteClob implements NClob
    {
        private String data;
        
        protected SqliteClob(final String data) {
            this.data = data;
        }
        
        @Override
        public void free() throws SQLException {
            this.data = null;
        }
        
        @Override
        public InputStream getAsciiStream() throws SQLException {
            return JDBC4ResultSet.this.getAsciiStreamInternal(this.data);
        }
        
        @Override
        public Reader getCharacterStream() throws SQLException {
            return JDBC4ResultSet.this.getNCharacterStreamInternal(this.data);
        }
        
        @Override
        public Reader getCharacterStream(final long arg0, final long arg1) throws SQLException {
            return JDBC4ResultSet.this.getNCharacterStreamInternal(this.data);
        }
        
        @Override
        public String getSubString(final long position, final int length) throws SQLException {
            if (this.data == null) {
                throw new SQLException("no data");
            }
            if (position < 1L) {
                throw new SQLException("Position must be greater than or equal to 1");
            }
            if (length < 0) {
                throw new SQLException("Length must be greater than or equal to 0");
            }
            final int start = (int)position - 1;
            return this.data.substring(start, Math.min(start + length, this.data.length()));
        }
        
        @Override
        public long length() throws SQLException {
            if (this.data == null) {
                throw new SQLException("no data");
            }
            return this.data.length();
        }
        
        @Override
        public long position(final String arg0, final long arg1) throws SQLException {
            JDBC4ResultSet.this.unused();
            return -1L;
        }
        
        @Override
        public long position(final Clob arg0, final long arg1) throws SQLException {
            JDBC4ResultSet.this.unused();
            return -1L;
        }
        
        @Override
        public OutputStream setAsciiStream(final long arg0) throws SQLException {
            JDBC4ResultSet.this.unused();
            return null;
        }
        
        @Override
        public Writer setCharacterStream(final long arg0) throws SQLException {
            JDBC4ResultSet.this.unused();
            return null;
        }
        
        @Override
        public int setString(final long arg0, final String arg1) throws SQLException {
            JDBC4ResultSet.this.unused();
            return -1;
        }
        
        @Override
        public int setString(final long arg0, final String arg1, final int arg2, final int arg3) throws SQLException {
            JDBC4ResultSet.this.unused();
            return -1;
        }
        
        @Override
        public void truncate(final long arg0) throws SQLException {
            JDBC4ResultSet.this.unused();
        }
    }
}
