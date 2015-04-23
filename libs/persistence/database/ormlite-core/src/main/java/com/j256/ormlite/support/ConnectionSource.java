package com.j256.ormlite.support;

import java.io.Closeable;
import java.sql.SQLException;

import com.j256.ormlite.db.DatabaseType;

/**
 * A reduction of the SQL DataSource so we can implement its functionality outside of JDBC.
 * 
 * @author graywatson
 */
public interface ConnectionSource extends Closeable {

	/**
	 * Return a database connectionSource suitable for read-only operations. After you are done, you should call
	 * {@link #releaseConnection(DatabaseConnection)}.
	 */
	public DatabaseConnection getReadOnlyConnection() throws SQLException;

	/**
	 * Return a database connectionSource suitable for read or write operations. After you are done, you should call
	 * {@link #releaseConnection(DatabaseConnection)}.
	 */
	public DatabaseConnection getReadWriteConnection() throws SQLException;

	/**
	 * Release a database connectionSource previously returned by {@link #getReadOnlyConnection()} or
	 * {@link #getReadWriteConnection()}.
	 */
	public void releaseConnection(DatabaseConnection connection) throws SQLException;

	/**
	 * Save this connectionSource and return it for all calls to {@link #getReadOnlyConnection()} and
	 * {@link #getReadWriteConnection()} unless the {@link #clearSpecialConnection(DatabaseConnection)} method is
	 * called, all This is used by the transaction mechanism since since all operations within a transaction must
	 * operate on the same connectionSource. It is also used by the Android code during initialization.
	 * 
	 * <p>
	 * <b> NOTE: </b> This should be a read-write connectionSource since transactions and Android need it to be so.
	 * </p>
	 * 
	 * <p>
	 * <b> NOTE: </b> Saving a connectionSource is usually accomplished using ThreadLocals so multiple threads should not be
	 * using connections in this scenario.
	 * </p>
	 * 
	 * @return True if the connectionSource was saved or false if we were already inside of a saved connectionSource.
	 */
	public boolean saveSpecialConnection(DatabaseConnection connection) throws SQLException;

	/**
	 * Clear the saved connectionSource.
	 */
	public void clearSpecialConnection(DatabaseConnection connection);

	/**
	 * Return the currently saved connectionSource or null if none.
	 */
	public DatabaseConnection getSpecialConnection();

	/**
	 * Close any outstanding database connections.
	 */
	public void closeQuietly();

	/**
	 * Return the DatabaseTypre associated with this connectionSource.
	 */
	public DatabaseType getDatabaseType();

	/**
	 * Return true if the connectionSource source is open. Once {@link #close()} has been called, this should return false.
	 */
	public boolean isOpen();
}