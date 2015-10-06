package de.mediaportal.mpwidget.persistence;

/**
 * Fields used in {@value #FILE_SETTINGS_PROPERTIES} file
 * 
 * @author Oliver
 *
 */
public interface SettingsFields {
	/**
	 * File name of the settings file
	 */
	public static final String FILE_SETTINGS_PROPERTIES = "config/settings.properties";

	/**
	 * field for the local MySQL database binary path
	 */
	public static final String FIELD_MPDB_DBPATH = "mysqldatabasepath";

	/**
	 * field for the EPG string to find only series episodes an no movies in the
	 * database table
	 */
	public static final String FIELD_EPG_DESCRIPTION_SERIESINDICATOR = "epgdescriptionseriesindicator";

	/**
	 * field for the MediaPortal database user
	 */
	public static final String FIELD_MPDB_USER = "mediaportaldbuser";

	/**
	 * field for the top level domain that should be best used as mirror
	 */
	public static final String FIELD_TOP_LEVEL_DOMAIN = "tld";

	/**
	 * field for the primary language, to find episode names in the correct
	 * language
	 */
	public static final String FIELD_LANGUAGE = "language";

	/**
	 * field for the thetvdb.com API key
	 */
	public static final String FIELD_API_KEY = "apikey";

	/**
	 * field for MediaPortal user's password
	 */
	public static final String FIELD_MPDB_PASSWORD = "mediaportaldbpassword";

	/**
	 * field for the path to databse backup dumps
	 */
	public static final String FIELD_MPDB_BACKUP_PATH = "backuppath";

	/**
	 * field for the database host of the MediaPortal MySQL database
	 */
	public static final String FIELD_MPDB_DBHOST = "mediaportaldbhost";

	/**
	 * field for MediaPortal's database name
	 */
	public static final String FIELD_MPDB_NAME = "mediaportaldbname";

	/**
	 * field for thetvdb proxy
	 */
	public static final String FIELD_PROXY_NAME = "thetvdb.proxy";

	/**
	 * field for the description pattern in the EPG that can be used to resolve
	 * series and episode number if there could not be found anything for the
	 * series on thetvdb.com
	 */
	public static final String FIELD_EPG_DESCRIPTION_PATTERN = "epgdescriptionpattern";

	/**
	 * field for the offline flag of the application
	 */
	public static final String FIELD_OFFLINE = "offline";

	/**
	 * File name of the substitutions file
	 */
	public static final String FILE_SUBSTITUTIONS_PROPERTIES = "config/substitutions.properties";

	/**
	 * field for the series name list in the substitutions properties file
	 */
	public static final String FIELD_SUBSTITUTIONS_SERIES = "seriesname.substitutions";

	/**
	 * field for the episode name list in the substitutions properties file
	 */
	public static final String FIELD_SUBSTITUTIONS_EPISODES = "episodename.substitutions";
}
