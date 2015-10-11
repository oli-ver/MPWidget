/**
 * 
 */
package de.mediaportal.mpwidget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mediaportal.mpwidget.model.Schedule;
import de.mediaportal.mpwidget.persistence.Config;
import de.mediaportal.mpwidget.persistence.DatabaseConnection;
import de.mediaportal.mpwidget.view.SetConfigurationViewController;
import de.mediaportal.mpwidget.view.ViewController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author oliver
 * 
 */
public class MPWidget extends Application {
	/**
	 * Logger of the class {@link MPEpisodeNumberGenerator}
	 */
	private Logger logger = null;

	/**
	 * Holds configuration set in settings.properties
	 */
	private Config config = null;

	/**
	 * ViewController instance
	 */
	private ViewController viewController = null;

	/**
	 * JavaFX Stage
	 */
	private static Stage stage;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {
		MPWidget.stage = stage;
		// Initialize Logger
		System.setProperty("log4j.configurationFile", "config" + File.separator + "log4j2.xml");
		logger = LogManager.getLogger(this.getClass());
		logger.warn("****************************************");
		logger.warn("***           MPWidget started       ***");
		logger.warn("****************************************");
		try {
			if (!new File("config" + File.separator + "settings.properties").exists()) {

				showConfigurationDialog(stage);

			} else {
				showMainWindow(stage);
			}
		} catch (Exception e) {
			logger.error("When starting the application an Exception has been thrown (" + e.getMessage() + ")", e);
		}
	}

	public void showMainWindow(Stage stage) throws FileNotFoundException, IOException {

		// Create config instance
		config = new Config();

		// Load View
		URL url = new File("config" + File.separator + "MPWidgetView.fxml").toURI().toURL();
		logger.debug("Binding FXML at URL: " + url);
		FXMLLoader loader = new FXMLLoader(url);
		AnchorPane anchorPane = (AnchorPane) loader.load();
		viewController = loader.getController();
		viewController.setConfig(config);
		viewController.setMpWidget(this);

		// Get timer config
		String strUpdateInterval = config.getProperty("updateinterval", "20000");
		int updateInterval = 20000;
		try {
			updateInterval = Integer.parseInt(strUpdateInterval);
		} catch (Exception e) {
			logger.warn("Error when parsing " + strUpdateInterval + " to Integer. Using default value: " + updateInterval);
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					logger.info("Updating tableView with current schedule...");
					// Retrieve list item
					TableView<Schedule> tableView = viewController.getTableView();
					ObservableList<Schedule> list = tableView.getItems();

					// Try to reach host, if not reachable send wol
					// package
					boolean tvserverDatabaseActive = hostAvailabilityCheck(config.getProperty("mediaportaldbhost"), 3306);

					if (!tvserverDatabaseActive) {
						logger.warn("Connection to MediaPortal database not available. Send wol package in GUI to wake up the server");
						viewController.addConsoleLine("Connection to MediaPortal database not available. "
								+ "Send wol package in GUI to wake up the server by double clicking this log line");
					} else {
						// Connect to Database
						DatabaseConnection conn = new DatabaseConnection(config);
						// Get Schedule Statement
						PreparedStatement stmt = conn.getScheduleStmt();

						ResultSet rs = stmt.executeQuery();

						// Fill view with schedule items
						Vector<Schedule> scheduleList = new Vector<>();
						int counter = 0;
						while (rs.next()) {
							scheduleList.add(new Schedule(rs));
							counter++;
						}

						// Clear list and add new items
						list.clear();
						list.addAll(scheduleList);

						logger.info("Added " + counter + " items to the list");
						viewController.addConsoleLine("Added " + counter + " items to the list");

						rs.close();
						stmt.close();
						conn.close();
						rs = null;
						stmt = null;
						conn = null;
					}
				} catch (Exception e) {
					logger.error("When updating the table view an Exception has been thrown(" + e.getMessage() + ")", e);
				}

			}
		}, 0, updateInterval);

		Scene scene = new Scene(anchorPane);
		scene.getStylesheets().add(new File("config/MPWidgetView.css").toURI().toURL().toExternalForm());
		stage.setScene(scene);
		makeResizable(scene);
		stage.setTitle("MPWidget - Keep recordings under your control");
		// Show View
		stage.show();
	}

	/**
	 * Checks online availability of a remote server<br>
	 * Source: <a href=
	 * "http://stackoverflow.com/questions/17147352/checking-if-server-is-online-from-java-code"
	 * >Stephen C</a>
	 * 
	 * @param host
	 *            host name
	 * @param port
	 *            Port of the service
	 * @return true, if connection to host and port can be established, false,
	 *         if not
	 */
	public static boolean hostAvailabilityCheck(String host, int port) {
		try (Socket s = new Socket(host, port)) {
			return true;
		} catch (IOException ex) {
			/* ignore */
		}
		return false;
	}

	private void makeResizable(Scene scene) {
		// Make tableView resizable
		scene.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				viewController.getTableView().setMinHeight(newValue.doubleValue() * 0.8);

				viewController.getListViewPane().setMinHeight(newValue.doubleValue() * 0.2);
				viewController.getListView().setMinHeight(newValue.doubleValue() * 0.2);

				viewController.getListViewPane().setLayoutY(newValue.doubleValue() * 0.8);
				viewController.getListView().setLayoutY(newValue.doubleValue() * 0.8);
			}
		});

		scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				viewController.getTableView().setMinWidth(newValue.doubleValue());
				viewController.getListViewPane().setMinWidth(newValue.doubleValue());
				viewController.getListView().setMinWidth(newValue.doubleValue());

			}
		});

	}

	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}

	private void showConfigurationDialog(Stage stage) throws IOException {
		// Load View
		URL url = new File("config" + File.separator + "SetConfigurationView" + File.separator + "AlertDialog_css.fxml").toURI().toURL();
		logger.debug("Binding FXML at URL: " + url);
		FXMLLoader loader = new FXMLLoader(url);
		GridPane gridPane = (GridPane) loader.load();
		SetConfigurationViewController controller = loader.getController();
		controller.setMainApplication(this);
		Scene scene = new Scene(gridPane);
		scene.getStylesheets().add(new File("config" + File.separator + "SetConfigurationView" + File.separator + "AlertDialog.css").toURI()
				.toURL().toExternalForm());
		stage.setScene(scene);
		stage.setTitle("MPWidget - Keep recordings under your control");
		// Show View
		stage.show();

	}

	public void showMainWindow() throws FileNotFoundException, IOException {
		showMainWindow(stage);
	}

	public void showConfigurationDialog() throws FileNotFoundException, IOException {
		showConfigurationDialog(stage);
	}

}
