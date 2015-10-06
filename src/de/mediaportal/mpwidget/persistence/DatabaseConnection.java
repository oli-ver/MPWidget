package de.mediaportal.mpwidget.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.mediaportal.mpwidget.model.Schedule;

/**
 * The singleton class DatabaseConnection handles every connection to
 * MediaPortal's MySQL database and provides PreparedStatements for SELECT and
 * UPDATE commands
 * 
 * @author Oliver
 * 
 */
public class DatabaseConnection {
	/**
	 * Singleton-Instance of the class {@link DatabaseConnection}
	 */
	private static DatabaseConnection instance = null;

	/**
	 * Connection-Object
	 */
	protected Connection connection = null;

	/**
	 * Database name as defined in settings.properties field
	 * <code>mediaportaldbname</code>
	 */
	private String dbName = null;

	/**
	 * Database host as defined in settings.properties field
	 * <code>mediaportaldbhost</code>
	 */
	private String dbHost = null;

	/**
	 * Database user as defined in settings.properties field
	 * <code>mediaportaldbuser</code>
	 */
	private String mediaportaldbuser = null;

	/**
	 * Database password as defined in settings.properties field
	 * <code>mediaportaldbpassword</code>
	 */
	private String mediaportaldbpassword = null;

	/**
	 * Private constructor of singleton class {@link DatabaseConnection}
	 * 
	 * @throws SQLException
	 *             Thrown if connection to database cannot be established
	 * @throws ClassNotFoundException
	 *             Thrown if MySQL driver is not found
	 */
	public DatabaseConnection(Config config) throws ClassNotFoundException, SQLException {
		dbHost = config.getProperty(Config.FIELD_MPDB_DBHOST);
		dbName = config.getProperty(Config.FIELD_MPDB_NAME);
		mediaportaldbuser = config.getProperty(Config.FIELD_MPDB_USER);
		mediaportaldbpassword = config.getProperty(Config.FIELD_MPDB_PASSWORD);

		// This will load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");
		// Setup the connection with the DB
		connection = DriverManager.getConnection("jdbc:mysql://" + dbHost + "/" + dbName + "?" + "user=" + mediaportaldbuser + "&password="
				+ mediaportaldbpassword);

	}

	/**
	 * Closes the database connection
	 */
	public void close() {
		try {

			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Creates a {@link PreparedStatement} to get MediaPortal's schedule with
	 * series information
	 * 
	 * @return PreparedStatement object
	 * @throws SQLException
	 *             if a database access error occurs or this method is called on
	 *             a closed connection
	 */
	public PreparedStatement getScheduleStmt() throws SQLException {
		PreparedStatement statement = connection.prepareStatement(Schedule.SQL_SCHEDULE);
		return statement;

	}
}
