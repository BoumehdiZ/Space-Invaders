// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.core;

import org.sqlite.SQLiteConnectionConfig;
import java.sql.Date;
import org.sqlite.date.FastDateFormat;
import java.util.Calendar;
import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import java.util.BitSet;
import org.sqlite.jdbc4.JDBC4Statement;

public abstract class CorePreparedStatement extends JDBC4Statement
{
    protected int columnCount;
    protected int paramCount;
    protected int batchQueryCount;
    protected BitSet paramValid;
    
    protected CorePreparedStatement(final SQLiteConnection conn, final String sql) throws SQLException {
        super(conn);
        this.sql = sql;
        final DB db = conn.getDatabase();
        db.prepare(this);
        this.rs.colsMeta = db.column_names(this.pointer);
        this.columnCount = db.column_count(this.pointer);
        this.paramCount = db.bind_parameter_count(this.pointer);
        this.paramValid = new BitSet(this.paramCount);
        this.batchQueryCount = 0;
        this.batch = null;
        this.batchPos = 0;
    }
    
    @Override
    protected void finalize() throws SQLException {
        this.close();
    }
    
    protected void checkParameters() throws SQLException {
        if (this.paramValid.cardinality() != this.paramCount) {
            throw new SQLException("Values not bound to statement");
        }
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        if (this.batchQueryCount == 0) {
            return new int[0];
        }
        this.checkParameters();
        try {
            return this.conn.getDatabase().executeBatch(this.pointer, this.batchQueryCount, this.batch, this.conn.getAutoCommit());
        }
        finally {
            this.clearBatch();
        }
    }
    
    @Override
    public void clearBatch() throws SQLException {
        super.clearBatch();
        this.paramValid.clear();
        this.batchQueryCount = 0;
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        if (this.pointer == 0L || this.resultsWaiting || this.rs.isOpen()) {
            return -1;
        }
        return this.conn.getDatabase().changes();
    }
    
    protected void batch(final int pos, final Object value) throws SQLException {
        this.checkOpen();
        if (this.batch == null) {
            this.batch = new Object[this.paramCount];
            this.paramValid.clear();
        }
        this.batch[this.batchPos + pos - 1] = value;
        this.paramValid.set(pos - 1);
    }
    
    protected void setDateByMilliseconds(final int pos, final Long value, final Calendar calendar) throws SQLException {
        final SQLiteConnectionConfig config = this.conn.getConnectionConfig();
        switch (config.getDateClass()) {
            case TEXT: {
                this.batch(pos, FastDateFormat.getInstance(config.getDateStringFormat(), calendar.getTimeZone()).format(new Date(value)));
                break;
            }
            case REAL: {
                this.batch(pos, new Double(value / 8.64E7 + 2440587.5));
                break;
            }
            default: {
                this.batch(pos, new Long(value / config.getDateMultiplier()));
                break;
            }
        }
    }
}
