// 
// Decompiled by Procyon v0.5.36
// 

package org.sqlite;

import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import org.sqlite.core.NativeDB;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executor;
import java.sql.DatabaseMetaData;
import org.sqlite.jdbc4.JDBC4DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import org.sqlite.core.CoreDatabaseMetaData;
import org.sqlite.core.DB;
import java.sql.Connection;

public abstract class SQLiteConnection implements Connection
{
    private static final String RESOURCE_NAME_PREFIX = ":resource:";
    private final DB db;
    private CoreDatabaseMetaData meta;
    private final SQLiteConnectionConfig connectionConfig;
    
    public SQLiteConnection(final DB db) {
        this.meta = null;
        this.db = db;
        this.connectionConfig = db.getConfig().newConnectionConfig();
    }
    
    public SQLiteConnection(final String url, final String fileName) throws SQLException {
        this(url, fileName, new Properties());
    }
    
    public SQLiteConnection(final String url, final String fileName, final Properties prop) throws SQLException {
        this.meta = null;
        this.db = open(url, fileName, prop);
        final SQLiteConfig config = this.db.getConfig();
        this.connectionConfig = this.db.getConfig().newConnectionConfig();
        config.apply(this);
    }
    
    public SQLiteConnectionConfig getConnectionConfig() {
        return this.connectionConfig;
    }
    
    public CoreDatabaseMetaData getSQLiteDatabaseMetaData() throws SQLException {
        this.checkOpen();
        if (this.meta == null) {
            this.meta = new JDBC4DatabaseMetaData(this);
        }
        return this.meta;
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.getSQLiteDatabaseMetaData();
    }
    
    public String getUrl() {
        return this.db.getUrl();
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
    }
    
    @Override
    public String getSchema() throws SQLException {
        return null;
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }
    
    protected void checkCursor(final int rst, final int rsc, final int rsh) throws SQLException {
        if (rst != 1003) {
            throw new SQLException("SQLite only supports TYPE_FORWARD_ONLY cursors");
        }
        if (rsc != 1007) {
            throw new SQLException("SQLite only supports CONCUR_READ_ONLY cursors");
        }
        if (rsh != 2) {
            throw new SQLException("SQLite only supports closing cursors at commit");
        }
    }
    
    protected void setTransactionMode(final SQLiteConfig.TransactionMode mode) {
        this.connectionConfig.setTransactionMode(mode);
    }
    
    @Override
    public int getTransactionIsolation() {
        return this.connectionConfig.getTransactionIsolation();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.checkOpen();
        switch (level) {
            case 8: {
                this.getDatabase().exec("PRAGMA read_uncommitted = false;", this.getAutoCommit());
                break;
            }
            case 1: {
                this.getDatabase().exec("PRAGMA read_uncommitted = true;", this.getAutoCommit());
                break;
            }
            default: {
                throw new SQLException("SQLite supports only TRANSACTION_SERIALIZABLE and TRANSACTION_READ_UNCOMMITTED.");
            }
        }
        this.connectionConfig.setTransactionIsolation(level);
    }
    
    private static DB open(final String url, final String origFileName, final Properties props) throws SQLException {
        final Properties newProps = new Properties();
        newProps.putAll(props);
        String fileName = extractPragmasFromFilename(url, origFileName, newProps);
        final SQLiteConfig config = new SQLiteConfig(newProps);
        if (!fileName.isEmpty() && !":memory:".equals(fileName) && !fileName.startsWith("file:") && !fileName.contains("mode=memory")) {
            if (fileName.startsWith(":resource:")) {
                final String resourceName = fileName.substring(":resource:".length());
                final ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
                URL resourceAddr = contextCL.getResource(resourceName);
                if (resourceAddr == null) {
                    try {
                        resourceAddr = new URL(resourceName);
                    }
                    catch (MalformedURLException e) {
                        throw new SQLException(String.format("resource %s not found: %s", resourceName, e));
                    }
                }
                try {
                    fileName = extractResource(resourceAddr).getAbsolutePath();
                }
                catch (IOException e2) {
                    throw new SQLException(String.format("failed to load %s: %s", resourceName, e2));
                }
            }
            else {
                final File file = new File(fileName).getAbsoluteFile();
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    for (File up = parent; up != null && !up.exists(); up = up.getParentFile()) {
                        parent = up;
                    }
                    throw new SQLException("path to '" + fileName + "': '" + parent + "' does not exist");
                }
                try {
                    if (!file.exists() && file.createNewFile()) {
                        file.delete();
                    }
                }
                catch (Exception e3) {
                    throw new SQLException("opening db: '" + fileName + "': " + e3.getMessage());
                }
                fileName = file.getAbsolutePath();
            }
        }
        DB db = null;
        try {
            NativeDB.load();
            db = new NativeDB(url, fileName, config);
        }
        catch (Exception e4) {
            final SQLException err = new SQLException("Error opening connection");
            err.initCause(e4);
            throw err;
        }
        db.open(fileName, config.getOpenModeFlags());
        return db;
    }
    
    private static File extractResource(final URL resourceAddr) throws IOException {
        if (resourceAddr.getProtocol().equals("file")) {
            try {
                return new File(resourceAddr.toURI());
            }
            catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        }
        final String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        final String dbFileName = String.format("sqlite-jdbc-tmp-%d.db", resourceAddr.hashCode());
        final File dbFile = new File(tempFolder, dbFileName);
        if (dbFile.exists()) {
            final long resourceLastModified = resourceAddr.openConnection().getLastModified();
            final long tmpFileLastModified = dbFile.lastModified();
            if (resourceLastModified < tmpFileLastModified) {
                return dbFile;
            }
            final boolean deletionSucceeded = dbFile.delete();
            if (!deletionSucceeded) {
                throw new IOException("failed to remove existing DB file: " + dbFile.getAbsolutePath());
            }
        }
        final byte[] buffer = new byte[8192];
        final FileOutputStream writer = new FileOutputStream(dbFile);
        final InputStream reader = resourceAddr.openStream();
        try {
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            return dbFile;
        }
        finally {
            writer.close();
            reader.close();
        }
    }
    
    public DB getDatabase() {
        return this.db;
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        return this.connectionConfig.isAutoCommit();
    }
    
    @Override
    public void setAutoCommit(final boolean ac) throws SQLException {
        this.checkOpen();
        if (this.connectionConfig.isAutoCommit() == ac) {
            return;
        }
        this.connectionConfig.setAutoCommit(ac);
        this.db.exec(this.connectionConfig.isAutoCommit() ? "commit;" : this.connectionConfig.transactionPrefix(), ac);
    }
    
    public int getBusyTimeout() {
        return this.db.getConfig().getBusyTimeout();
    }
    
    public void setBusyTimeout(final int timeoutMillis) throws SQLException {
        this.db.getConfig().setBusyTimeout(timeoutMillis);
        this.db.busy_timeout(timeoutMillis);
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.db.isClosed();
    }
    
    @Override
    public void close() throws SQLException {
        if (this.isClosed()) {
            return;
        }
        if (this.meta != null) {
            this.meta.close();
        }
        this.db.close();
    }
    
    protected void checkOpen() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException("database connection closed");
        }
    }
    
    public String libversion() throws SQLException {
        this.checkOpen();
        return this.db.libversion();
    }
    
    @Override
    public void commit() throws SQLException {
        this.checkOpen();
        if (this.connectionConfig.isAutoCommit()) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("commit;", this.getAutoCommit());
        this.db.exec(this.connectionConfig.transactionPrefix(), this.getAutoCommit());
    }
    
    @Override
    public void rollback() throws SQLException {
        this.checkOpen();
        if (this.connectionConfig.isAutoCommit()) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("rollback;", this.getAutoCommit());
        this.db.exec(this.connectionConfig.transactionPrefix(), this.getAutoCommit());
    }
    
    protected static String extractPragmasFromFilename(final String url, final String filename, final Properties prop) throws SQLException {
        final int parameterDelimiter = filename.indexOf(63);
        if (parameterDelimiter == -1) {
            return filename;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(filename.substring(0, parameterDelimiter));
        int nonPragmaCount = 0;
        final String[] parameters = filename.substring(parameterDelimiter + 1).split("&");
        for (int i = 0; i < parameters.length; ++i) {
            final String parameter = parameters[parameters.length - 1 - i].trim();
            if (!parameter.isEmpty()) {
                final String[] kvp = parameter.split("=");
                final String key = kvp[0].trim().toLowerCase();
                if (SQLiteConfig.pragmaSet.contains(key)) {
                    if (kvp.length == 1) {
                        throw new SQLException(String.format("Please specify a value for PRAGMA %s in URL %s", key, url));
                    }
                    final String value = kvp[1].trim();
                    if (!value.isEmpty()) {
                        if (!prop.containsKey(key)) {
                            prop.setProperty(key, value);
                        }
                    }
                }
                else {
                    sb.append((nonPragmaCount == 0) ? '?' : '&');
                    sb.append(parameter);
                    ++nonPragmaCount;
                }
            }
        }
        final String newFilename = sb.toString();
        return newFilename;
    }
}
