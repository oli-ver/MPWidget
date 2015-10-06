package de.mediaportal.mpwidget.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Class representation of the local settings.properties file. Implements the
 * {@link SettingsFields} interface for convenience
 * 
 * @author Oliver
 * 
 * @see java.util.Properties
 * 
 */
public class Config extends Properties implements SettingsFields {

	/**
	 * default serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of the Config class by reading from
	 * settings.properties file
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred. This
	 *             class is the general class of exceptions produced by failed
	 *             or interrupted I/O operations.
	 * @throws FileNotFoundException
	 *             Signals that an attempt to open the file denoted by a
	 *             specified pathname has failed.
	 */
	public Config() throws FileNotFoundException, IOException {
		super();
		load(new FileInputStream(new File(FILE_SETTINGS_PROPERTIES)));
		// Fetch top level domain and language options
	}
}
