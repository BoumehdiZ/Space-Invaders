// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite.jdbc3;

import java.util.regex.Matcher;
import java.util.Locale;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Time;
import org.sqlite.date.FastDateFormat;
import java.util.Calendar;
import org.sqlite.core.DB;
import java.sql.Date;
import java.io.StringReader;
import java.io.Reader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import org.sqlite.core.CoreStatement;
import java.util.regex.Pattern;
import org.sqlite.core.CoreResultSet;

public abstract class JDBC3ResultSet extends CoreResultSet
{
    protected static final Pattern COLUMN_TYPENAME;
    protected static final Pattern COLUMN_TYPECAST;
    protected static final Pattern COLUMN_PRECISION;
    
    protected JDBC3ResultSet(final CoreStatement stmt) {
        super(stmt);
    }
    
    public int findColumn(final String col) throws SQLException {
        this.checkOpen();
        final Integer index = this.findColumnIndexInCache(col);
        if (index != null) {
            return index;
        }
        for (int i = 0; i < this.cols.length; ++i) {
            if (col.equalsIgnoreCase(this.cols[i])) {
                return this.addColumnIndexInCache(col, i + 1);
            }
        }
        throw new SQLException("no such column: '" + col + "'");
    }
    
    public boolean next() throws SQLException {
        if (!this.open) {
            return false;
        }
        this.lastCol = -1;
        if (this.row == 0) {
            ++this.row;
            return true;
        }
        if (this.maxRows != 0 && this.row == this.maxRows) {
            return false;
        }
        final int statusCode = this.getDatabase().step(this.stmt.pointer);
        switch (statusCode) {
            case 101: {
                this.close();
                return false;
            }
            case 100: {
                ++this.row;
                return true;
            }
            default: {
                this.getDatabase().throwex(statusCode);
                return false;
            }
        }
    }
    
    public int getType() throws SQLException {
        return 1003;
    }
    
    public int getFetchSize() throws SQLException {
        return this.limitRows;
    }
    
    public void setFetchSize(final int rows) throws SQLException {
        if (0 > rows || (this.maxRows != 0 && rows > this.maxRows)) {
            throw new SQLException("fetch size " + rows + " out of bounds " + this.maxRows);
        }
        this.limitRows = rows;
    }
    
    public int getFetchDirection() throws SQLException {
        this.checkOpen();
        return 1000;
    }
    
    public void setFetchDirection(final int d) throws SQLException {
        this.checkOpen();
        if (d != 1000) {
            throw new SQLException("only FETCH_FORWARD direction supported");
        }
    }
    
    public boolean isAfterLast() throws SQLException {
        return !this.open;
    }
    
    public boolean isBeforeFirst() throws SQLException {
        return this.open && this.row == 0;
    }
    
    public boolean isFirst() throws SQLException {
        return this.row == 1;
    }
    
    public boolean isLast() throws SQLException {
        throw new SQLException("function not yet implemented for SQLite");
    }
    
    @Override
    protected void finalize() throws SQLException {
        this.close();
    }
    
    public int getRow() throws SQLException {
        return this.row;
    }
    
    public boolean wasNull() throws SQLException {
        return this.getDatabase().column_type(this.stmt.pointer, this.markCol(this.lastCol)) == 5;
    }
    
    public BigDecimal getBigDecimal(final int col) throws SQLException {
        final String stringValue = this.getString(col);
        if (stringValue == null) {
            return null;
        }
        try {
            return new BigDecimal(stringValue);
        }
        catch (NumberFormatException e) {
            throw new SQLException("Bad value for type BigDecimal : " + stringValue);
        }
    }
    
    public BigDecimal getBigDecimal(final String col) throws SQLException {
        return this.getBigDecimal(this.findColumn(col));
    }
    
    public boolean getBoolean(final int col) throws SQLException {
        return this.getInt(col) != 0;
    }
    
    public boolean getBoolean(final String col) throws SQLException {
        return this.getBoolean(this.findColumn(col));
    }
    
    public InputStream getBinaryStream(final int col) throws SQLException {
        final byte[] bytes = this.getBytes(col);
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        }
        return null;
    }
    
    public InputStream getBinaryStream(final String col) throws SQLException {
        return this.getBinaryStream(this.findColumn(col));
    }
    
    public byte getByte(final int col) throws SQLException {
        return (byte)this.getInt(col);
    }
    
    public byte getByte(final String col) throws SQLException {
        return this.getByte(this.findColumn(col));
    }
    
    public byte[] getBytes(final int col) throws SQLException {
        return this.getDatabase().column_blob(this.stmt.pointer, this.markCol(col));
    }
    
    public byte[] getBytes(final String col) throws SQLException {
        return this.getBytes(this.findColumn(col));
    }
    
    public Reader getCharacterStream(final int col) throws SQLException {
        final String string = this.getString(col);
        return (string == null) ? null : new StringReader(string);
    }
    
    public Reader getCharacterStream(final String col) throws SQLException {
        return this.getCharacterStream(this.findColumn(col));
    }
    
    public Date getDate(final int col) throws SQLException {
        final DB db = this.getDatabase();
        switch (db.column_type(this.stmt.pointer, this.markCol(col))) {
            case 5: {
                return null;
            }
            case 3: {
                try {
                    return new Date(this.getConnectionConfig().getDateFormat().parse(db.column_text(this.stmt.pointer, this.markCol(col))).getTime());
                }
                catch (Exception e) {
                    final SQLException error = new SQLException("Error parsing date");
                    error.initCause(e);
                    throw error;
                }
            }
            case 2: {
                return new Date(this.julianDateToCalendar(db.column_double(this.stmt.pointer, this.markCol(col))).getTimeInMillis());
            }
            default: {
                return new Date(db.column_long(this.stmt.pointer, this.markCol(col)) * this.getConnectionConfig().getDateMultiplier());
            }
        }
    }
    
    public Date getDate(final int col, final Calendar cal) throws SQLException {
        this.checkCalendar(cal);
        final DB db = this.getDatabase();
        switch (db.column_type(this.stmt.pointer, this.markCol(col))) {
            case 5: {
                return null;
            }
            case 3: {
                try {
                    final FastDateFormat dateFormat = FastDateFormat.getInstance(this.getConnectionConfig().getDateStringFormat(), cal.getTimeZone());
                    return new Date(dateFormat.parse(db.column_text(this.stmt.pointer, this.markCol(col))).getTime());
                }
                catch (Exception e) {
                    final SQLException error = new SQLException("Error parsing time stamp");
                    error.initCause(e);
                    throw error;
                }
            }
            case 2: {
                return new Date(this.julianDateToCalendar(db.column_double(this.stmt.pointer, this.markCol(col)), cal).getTimeInMillis());
            }
            default: {
                cal.setTimeInMillis(db.column_long(this.stmt.pointer, this.markCol(col)) * this.getConnectionConfig().getDateMultiplier());
                return new Date(cal.getTime().getTime());
            }
        }
    }
    
    public Date getDate(final String col) throws SQLException {
        return this.getDate(this.findColumn(col), Calendar.getInstance());
    }
    
    public Date getDate(final String col, final Calendar cal) throws SQLException {
        return this.getDate(this.findColumn(col), cal);
    }
    
    public double getDouble(final int col) throws SQLException {
        final DB db = this.getDatabase();
        if (db.column_type(this.stmt.pointer, this.markCol(col)) == 5) {
            return 0.0;
        }
        return db.column_double(this.stmt.pointer, this.markCol(col));
    }
    
    public double getDouble(final String col) throws SQLException {
        return this.getDouble(this.findColumn(col));
    }
    
    public float getFloat(final int col) throws SQLException {
        final DB db = this.getDatabase();
        if (db.column_type(this.stmt.pointer, this.markCol(col)) == 5) {
            return 0.0f;
        }
        return (float)db.column_double(this.stmt.pointer, this.markCol(col));
    }
    
    public float getFloat(final String col) throws SQLException {
        return this.getFloat(this.findColumn(col));
    }
    
    public int getInt(final int col) throws SQLException {
        final DB db = this.getDatabase();
        return db.column_int(this.stmt.pointer, this.markCol(col));
    }
    
    public int getInt(final String col) throws SQLException {
        return this.getInt(this.findColumn(col));
    }
    
    public long getLong(final int col) throws SQLException {
        final DB db = this.getDatabase();
        return db.column_long(this.stmt.pointer, this.markCol(col));
    }
    
    public long getLong(final String col) throws SQLException {
        return this.getLong(this.findColumn(col));
    }
    
    public short getShort(final int col) throws SQLException {
        return (short)this.getInt(col);
    }
    
    public short getShort(final String col) throws SQLException {
        return this.getShort(this.findColumn(col));
    }
    
    public String getString(final int col) throws SQLException {
        final DB db = this.getDatabase();
        return db.column_text(this.stmt.pointer, this.markCol(col));
    }
    
    public String getString(final String col) throws SQLException {
        return this.getString(this.findColumn(col));
    }
    
    public Time getTime(final int col) throws SQLException {
        final DB db = this.getDatabase();
        switch (db.column_type(this.stmt.pointer, this.markCol(col))) {
            case 5: {
                return null;
            }
            case 3: {
                try {
                    return new Time(this.getConnectionConfig().getDateFormat().parse(db.column_text(this.stmt.pointer, this.markCol(col))).getTime());
                }
                catch (Exception e) {
                    final SQLException error = new SQLException("Error parsing time");
                    error.initCause(e);
                    throw error;
                }
            }
            case 2: {
                return new Time(this.julianDateToCalendar(db.column_double(this.stmt.pointer, this.markCol(col))).getTimeInMillis());
            }
            default: {
                return new Time(db.column_long(this.stmt.pointer, this.markCol(col)) * this.getConnectionConfig().getDateMultiplier());
            }
        }
    }
    
    public Time getTime(final int col, final Calendar cal) throws SQLException {
        this.checkCalendar(cal);
        final DB db = this.getDatabase();
        switch (db.column_type(this.stmt.pointer, this.markCol(col))) {
            case 5: {
                return null;
            }
            case 3: {
                try {
                    final FastDateFormat dateFormat = FastDateFormat.getInstance(this.getConnectionConfig().getDateStringFormat(), cal.getTimeZone());
                    return new Time(dateFormat.parse(db.column_text(this.stmt.pointer, this.markCol(col))).getTime());
                }
                catch (Exception e) {
                    final SQLException error = new SQLException("Error parsing time");
                    error.initCause(e);
                    throw error;
                }
            }
            case 2: {
                return new Time(this.julianDateToCalendar(db.column_double(this.stmt.pointer, this.markCol(col)), cal).getTimeInMillis());
            }
            default: {
                cal.setTimeInMillis(db.column_long(this.stmt.pointer, this.markCol(col)) * this.getConnectionConfig().getDateMultiplier());
                return new Time(cal.getTime().getTime());
            }
        }
    }
    
    public Time getTime(final String col) throws SQLException {
        return this.getTime(this.findColumn(col));
    }
    
    public Time getTime(final String col, final Calendar cal) throws SQLException {
        return this.getTime(this.findColumn(col), cal);
    }
    
    public Timestamp getTimestamp(final int col) throws SQLException {
        final DB db = this.getDatabase();
        switch (db.column_type(this.stmt.pointer, this.markCol(col))) {
            case 5: {
                return null;
            }
            case 3: {
                try {
                    return new Timestamp(this.getConnectionConfig().getDateFormat().parse(db.column_text(this.stmt.pointer, this.markCol(col))).getTime());
                }
                catch (Exception e) {
                    final SQLException error = new SQLException("Error parsing time stamp");
                    error.initCause(e);
                    throw error;
                }
            }
            case 2: {
                return new Timestamp(this.julianDateToCalendar(db.column_double(this.stmt.pointer, this.markCol(col))).getTimeInMillis());
            }
            default: {
                return new Timestamp(db.column_long(this.stmt.pointer, this.markCol(col)) * this.getConnectionConfig().getDateMultiplier());
            }
        }
    }
    
    public Timestamp getTimestamp(final int col, final Calendar cal) throws SQLException {
        if (cal == null) {
            return this.getTimestamp(col);
        }
        final DB db = this.getDatabase();
        switch (db.column_type(this.stmt.pointer, this.markCol(col))) {
            case 5: {
                return null;
            }
            case 3: {
                try {
                    final FastDateFormat dateFormat = FastDateFormat.getInstance(this.getConnectionConfig().getDateStringFormat(), cal.getTimeZone());
                    return new Timestamp(dateFormat.parse(db.column_text(this.stmt.pointer, this.markCol(col))).getTime());
                }
                catch (Exception e) {
                    final SQLException error = new SQLException("Error parsing time stamp");
                    error.initCause(e);
                    throw error;
                }
            }
            case 2: {
                return new Timestamp(this.julianDateToCalendar(db.column_double(this.stmt.pointer, this.markCol(col)), cal).getTimeInMillis());
            }
            default: {
                cal.setTimeInMillis(db.column_long(this.stmt.pointer, this.markCol(col)) * this.getConnectionConfig().getDateMultiplier());
                return new Timestamp(cal.getTime().getTime());
            }
        }
    }
    
    public Timestamp getTimestamp(final String col) throws SQLException {
        return this.getTimestamp(this.findColumn(col));
    }
    
    public Timestamp getTimestamp(final String c, final Calendar ca) throws SQLException {
        return this.getTimestamp(this.findColumn(c), ca);
    }
    
    public Object getObject(final int col) throws SQLException {
        switch (this.getDatabase().column_type(this.stmt.pointer, this.markCol(col))) {
            case 1: {
                final long val = this.getLong(col);
                if (val > 2147483647L || val < -2147483648L) {
                    return new Long(val);
                }
                return new Integer((int)val);
            }
            case 2: {
                return new Double(this.getDouble(col));
            }
            case 4: {
                return this.getBytes(col);
            }
            case 5: {
                return null;
            }
            default: {
                return this.getString(col);
            }
        }
    }
    
    public Object getObject(final String col) throws SQLException {
        return this.getObject(this.findColumn(col));
    }
    
    public Statement getStatement() {
        return (Statement)this.stmt;
    }
    
    public String getCursorName() throws SQLException {
        return null;
    }
    
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    public void clearWarnings() throws SQLException {
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        return (ResultSetMetaData)this;
    }
    
    public String getCatalogName(final int col) throws SQLException {
        return this.getDatabase().column_table_name(this.stmt.pointer, this.checkCol(col));
    }
    
    public String getColumnClassName(final int col) throws SQLException {
        this.checkCol(col);
        return "java.lang.Object";
    }
    
    public int getColumnCount() throws SQLException {
        this.checkCol(1);
        return this.colsMeta.length;
    }
    
    public int getColumnDisplaySize(final int col) throws SQLException {
        return Integer.MAX_VALUE;
    }
    
    public String getColumnLabel(final int col) throws SQLException {
        return this.getColumnName(col);
    }
    
    public String getColumnName(final int col) throws SQLException {
        return this.getDatabase().column_name(this.stmt.pointer, this.checkCol(col));
    }
    
    public int getColumnType(final int col) throws SQLException {
        final String typeName = this.getColumnTypeName(col);
        final int valueType = this.getDatabase().column_type(this.stmt.pointer, this.checkCol(col));
        if (valueType == 1 || valueType == 5) {
            if ("BOOLEAN".equals(typeName)) {
                return 16;
            }
            if ("TINYINT".equals(typeName)) {
                return -6;
            }
            if ("SMALLINT".equals(typeName) || "INT2".equals(typeName)) {
                return 5;
            }
            if ("BIGINT".equals(typeName) || "INT8".equals(typeName) || "UNSIGNED BIG INT".equals(typeName)) {
                return -5;
            }
            if ("DATE".equals(typeName) || "DATETIME".equals(typeName)) {
                return 91;
            }
            if ("TIMESTAMP".equals(typeName)) {
                return 93;
            }
            if (valueType == 1 || "INT".equals(typeName) || "INTEGER".equals(typeName) || "MEDIUMINT".equals(typeName)) {
                return 4;
            }
        }
        if (valueType == 2 || valueType == 5) {
            if ("DECIMAL".equals(typeName)) {
                return 3;
            }
            if ("DOUBLE".equals(typeName) || "DOUBLE PRECISION".equals(typeName)) {
                return 8;
            }
            if ("NUMERIC".equals(typeName)) {
                return 2;
            }
            if ("REAL".equals(typeName)) {
                return 7;
            }
            if (valueType == 2 || "FLOAT".equals(typeName)) {
                return 6;
            }
        }
        if (valueType == 3 || valueType == 5) {
            if ("CHARACTER".equals(typeName) || "NCHAR".equals(typeName) || "NATIVE CHARACTER".equals(typeName) || "CHAR".equals(typeName)) {
                return 1;
            }
            if ("CLOB".equals(typeName)) {
                return 2005;
            }
            if ("DATE".equals(typeName) || "DATETIME".equals(typeName)) {
                return 91;
            }
            if (valueType == 3 || "VARCHAR".equals(typeName) || "VARYING CHARACTER".equals(typeName) || "NVARCHAR".equals(typeName) || "TEXT".equals(typeName)) {
                return 12;
            }
        }
        if (valueType == 4 || valueType == 5) {
            if ("BINARY".equals(typeName)) {
                return -2;
            }
            if (valueType == 4 || "BLOB".equals(typeName)) {
                return 2004;
            }
        }
        return 2;
    }
    
    public String getColumnTypeName(final int col) throws SQLException {
        final String declType = this.getColumnDeclType(col);
        if (declType != null) {
            final Matcher matcher = JDBC3ResultSet.COLUMN_TYPENAME.matcher(declType);
            matcher.find();
            return matcher.group(1).toUpperCase(Locale.ENGLISH);
        }
        switch (this.getDatabase().column_type(this.stmt.pointer, this.checkCol(col))) {
            case 1: {
                return "INTEGER";
            }
            case 2: {
                return "FLOAT";
            }
            case 4: {
                return "BLOB";
            }
            case 5: {
                return "NUMERIC";
            }
            case 3: {
                return "TEXT";
            }
            default: {
                return "NUMERIC";
            }
        }
    }
    
    public int getPrecision(final int col) throws SQLException {
        final String declType = this.getColumnDeclType(col);
        if (declType != null) {
            final Matcher matcher = JDBC3ResultSet.COLUMN_PRECISION.matcher(declType);
            return matcher.find() ? Integer.parseInt(matcher.group(1).split(",")[0].trim()) : 0;
        }
        return 0;
    }
    
    private String getColumnDeclType(final int col) throws SQLException {
        final DB db = this.getDatabase();
        String declType = db.column_decltype(this.stmt.pointer, this.checkCol(col));
        if (declType == null) {
            final Matcher matcher = JDBC3ResultSet.COLUMN_TYPECAST.matcher(db.column_name(this.stmt.pointer, this.checkCol(col)));
            declType = (matcher.find() ? matcher.group(1) : null);
        }
        return declType;
    }
    
    public int getScale(final int col) throws SQLException {
        final String declType = this.getColumnDeclType(col);
        if (declType != null) {
            final Matcher matcher = JDBC3ResultSet.COLUMN_PRECISION.matcher(declType);
            if (matcher.find()) {
                final String[] array = matcher.group(1).split(",");
                if (array.length == 2) {
                    return Integer.parseInt(array[1].trim());
                }
            }
        }
        return 0;
    }
    
    public String getSchemaName(final int col) throws SQLException {
        return "";
    }
    
    public String getTableName(final int col) throws SQLException {
        final String tableName = this.getDatabase().column_table_name(this.stmt.pointer, this.checkCol(col));
        if (tableName == null) {
            return "";
        }
        return tableName;
    }
    
    public int isNullable(final int col) throws SQLException {
        this.checkMeta();
        return this.meta[this.checkCol(col)][1] ? 0 : 1;
    }
    
    public boolean isAutoIncrement(final int col) throws SQLException {
        this.checkMeta();
        return this.meta[this.checkCol(col)][2];
    }
    
    public boolean isCaseSensitive(final int col) throws SQLException {
        return true;
    }
    
    public boolean isCurrency(final int col) throws SQLException {
        return false;
    }
    
    public boolean isDefinitelyWritable(final int col) throws SQLException {
        return true;
    }
    
    public boolean isReadOnly(final int col) throws SQLException {
        return false;
    }
    
    public boolean isSearchable(final int col) throws SQLException {
        return true;
    }
    
    public boolean isSigned(final int col) throws SQLException {
        final String typeName = this.getColumnTypeName(col);
        return "NUMERIC".equals(typeName) || "INTEGER".equals(typeName) || "REAL".equals(typeName);
    }
    
    public boolean isWritable(final int col) throws SQLException {
        return true;
    }
    
    public int getConcurrency() throws SQLException {
        return 1007;
    }
    
    public boolean rowDeleted() throws SQLException {
        return false;
    }
    
    public boolean rowInserted() throws SQLException {
        return false;
    }
    
    public boolean rowUpdated() throws SQLException {
        return false;
    }
    
    private Calendar julianDateToCalendar(final Double jd) {
        return this.julianDateToCalendar(jd, Calendar.getInstance());
    }
    
    private Calendar julianDateToCalendar(final Double jd, final Calendar cal) {
        if (jd == null) {
            return null;
        }
        final double w = jd + 0.5;
        final int Z = (int)w;
        final double F = w - Z;
        int A;
        if (Z < 2299161) {
            A = Z;
        }
        else {
            final int alpha = (int)((Z - 1867216.25) / 36524.25);
            A = Z + 1 + alpha - (int)(alpha / 4.0);
        }
        final int B = A + 1524;
        final int C = (int)((B - 122.1) / 365.25);
        final int D = (int)(365.25 * C);
        final int E = (int)((B - D) / 30.6001);
        final int mm = E - ((E < 13.5) ? 1 : 13);
        final int yyyy = C - ((mm > 2.5) ? 4716 : 4715);
        final double jjd = B - D - (int)(30.6001 * E) + F;
        final int dd = (int)jjd;
        final double hhd = jjd - dd;
        final int hh = (int)(24.0 * hhd);
        final double mnd = 24.0 * hhd - hh;
        final int mn = (int)(60.0 * mnd);
        final double ssd = 60.0 * mnd - mn;
        final int ss = (int)(60.0 * ssd);
        final double msd = 60.0 * ssd - ss;
        final int ms = (int)(1000.0 * msd);
        cal.set(yyyy, mm - 1, dd, hh, mn, ss);
        cal.set(14, ms);
        if (yyyy < 1) {
            cal.set(0, 0);
            cal.set(1, -(yyyy - 1));
        }
        return cal;
    }
    
    public void checkCalendar(final Calendar cal) throws SQLException {
        if (cal != null) {
            return;
        }
        final SQLException e = new SQLException("Expected a calendar instance.");
        e.initCause(new NullPointerException());
        throw e;
    }
    
    static {
        COLUMN_TYPENAME = Pattern.compile("([^\\(]*)");
        COLUMN_TYPECAST = Pattern.compile("cast\\(.*?\\s+as\\s+(.*?)\\s*\\)");
        COLUMN_PRECISION = Pattern.compile(".*?\\((.*?)\\)");
    }
}
