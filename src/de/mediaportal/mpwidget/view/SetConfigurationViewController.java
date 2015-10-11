package de.mediaportal.mpwidget.view;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mediaportal.mpwidget.MPWidget;
import de.mediaportal.mpwidget.persistence.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SetConfigurationViewController {

	@FXML
	private Label messageLabel;

	@FXML
	private Label detailsLabel;

	@FXML
	private TextField tfDbUser;

	@FXML
	private PasswordField tfDbPassword;

	@FXML
	private TextField tfDbHost;

	@FXML
	private TextField tfDbMacAddress;

	@FXML
	private TextField tfDbSchema;

	@FXML
	private TextField tfUpdateInterval;

	@FXML
	private HBox actionParent;

	@FXML
	private Button cancelButton;

	@FXML
	private HBox okParent;

	@FXML
	private Button okButton;

	private Logger logger;

	private MPWidget mpWidget;

	@FXML
	void cancelAction(ActionEvent event) {
		logger.info("User cancelled setting of configuration options. Exitting.");
		System.exit(0);
	}

	@FXML
	void okAction(ActionEvent event) {
		logger.info("Persisting configuration settings");
		Properties props = new Properties();
		props.setProperty(Config.FIELD_MPDB_HOST_MAC, tfDbMacAddress.getText());
		props.setProperty(Config.FIELD_MPDB_HOSTNAME, tfDbHost.getText());
		props.setProperty(Config.FIELD_MPDB_PASSWORD, tfDbPassword.getText());
		props.setProperty(Config.FIELD_MPDB_SCHEMA, tfDbSchema.getText());
		props.setProperty(Config.FIELD_MPDB_USER, tfDbUser.getText());
		props.setProperty(Config.FIELD_UPDATE_INTERVAL, tfUpdateInterval.getText());
		try {
			props.store(new FileWriter(new File(Config.FILE_SETTINGS_PROPERTIES)), null);
			mpWidget.showMainWindow();
		} catch (IOException e) {
			logger.error("Error when persisting config file: " + e.getMessage(), e);
		}

	}

	@FXML
	// This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		this.logger = LogManager.getLogger(this.getClass());
		File configFile = new File(Config.FILE_SETTINGS_PROPERTIES);
		if (configFile.exists()) {
			Properties props = new Properties();
			try {
				props.load(new FileReader(configFile));
				tfDbMacAddress.setText(props.getProperty(Config.FIELD_MPDB_HOST_MAC));
				tfDbHost.setText(props.getProperty(Config.FIELD_MPDB_HOSTNAME));
				tfDbPassword.setText(props.getProperty(Config.FIELD_MPDB_PASSWORD));
				tfDbSchema.setText(props.getProperty(Config.FIELD_MPDB_SCHEMA));
				tfDbUser.setText(props.getProperty(Config.FIELD_MPDB_USER));
				tfUpdateInterval.setText(props.getProperty(Config.FIELD_UPDATE_INTERVAL));
			} catch (IOException e) {
				logger.error("Exception thrown while reading from config file: " + e.getMessage(), e);
			}
		} else {
			logger.debug("No properties file found. Default values will be shown in view");
		}
	}

	public void setMainApplication(MPWidget mpWidget) {
		this.mpWidget = mpWidget;
	}
}
