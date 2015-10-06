/**
 * 
 */
package de.mediaportal.mpwidget;

import java.io.File;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mediaportal.mpwidget.model.Schedule;
import de.mediaportal.mpwidget.persistence.Config;
import de.mediaportal.mpwidget.persistence.DatabaseConnection;
import de.mediaportal.mpwidget.view.ViewController;

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
		// Initialize Logger
		System.setProperty("log4j.configurationFile", "config/log4j2.xml");
		logger = LogManager.getLogger(this.getClass());
		logger.warn("****************************************");
		logger.warn("***           MPWidget started       ***");
		logger.warn("****************************************");
		try {
			// Create config instance
			config = new Config();

			// Load View
			URL url = new File("config/MPWidgetView.fxml").toURI().toURL();
			logger.debug("Binding FXML at URL: " + url);
			FXMLLoader loader = new FXMLLoader(url);
			AnchorPane anchorPane = (AnchorPane) loader.load();
			viewController = loader.getController();

			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						logger.info("Updating tableView with current schedule...");
						// Retrieve list item
						TableView<Schedule> tableView = viewController.getTableView();
						ObservableList<Schedule> list = tableView.getItems();

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

						rs.close();
						stmt.close();
						conn.close();
						rs = null;
						stmt = null;
						conn = null;
					} catch (Exception e) {
						logger.error("When updating the table view an Exception has been thrown(" + e.getMessage() + ")", e);
					}

				}
			}, 0, 10000);

			Scene scene = new Scene(anchorPane);
			scene.getStylesheets().add(new File("config/MPWidgetView.css").toURI().toURL().toExternalForm());
			stage.setScene(scene);
			// Show View
			stage.show();

		} catch (Exception e) {
			logger.error("When starting the application an Exception has been thrown (" + e.getMessage() + ")", e);
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}
}
