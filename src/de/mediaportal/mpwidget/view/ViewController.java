package de.mediaportal.mpwidget.view;

import java.io.IOException;

/**
 * Sample Skeleton for 'MPWidgetView.fxml' Controller Class
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mediaportal.mpwidget.MPWidget;
import de.mediaportal.mpwidget.model.Schedule;
import de.mediaportal.mpwidget.persistence.Config;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

public class ViewController {
	protected Logger logger = null;

	private MPWidget mpWidget = null;

	@FXML
	// ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML
	// URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML
	// fx:id="anchorPane"
	private AnchorPane anchorPane;

	@FXML
	// fx:id="tcNumber"
	private TableColumn<Schedule, String> tcNumber;

	@FXML
	// fx:id="tableView"
	private TableView<Schedule> tableView;

	@FXML
	// fx:id="tcStart"
	private TableColumn<Schedule, String> tcStart;

	@FXML
	// fx:id="tcTitle"
	private TableColumn<Schedule, String> tcTitle;

	@FXML
	// fx:id="tcEnd"
	private TableColumn<Schedule, String> tcEnd;

	@FXML
	// fx:id="tcEpisode"
	private TableColumn<Schedule, String> tcEpisode;

	@FXML
	// fx:id="tcChannel"
	private TableColumn<Schedule, String> tcChannel;

	@FXML
	// fx:id="listView"
	private ListView<String> listView;

	@FXML
	// fx:id="listViewPane"
	private ScrollPane listViewPane;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

	private Config config;

	@FXML
	// This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		logger = LogManager.getLogger(this.getClass());
		assert tcNumber != null : "fx:id=\"tcNumber\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert tcStart != null : "fx:id=\"tcStart\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert tcTitle != null : "fx:id=\"tcTitle\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert tcEnd != null : "fx:id=\"tcEnd\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert tcEpisode != null : "fx:id=\"tcEpisode\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert tcChannel != null : "fx:id=\"tcChannel\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'MPWidgetView.fxml'.";
		assert listViewPane != null
				: "fx:id=\"listViewPane\" was not injected: check your FXML file 'MPWidgetView.fxml'.";

		logger.debug("All FXML items successfully injected. Binding Columns of tableView");
		// Bind Colums to objects
		tcStart.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(p.getValue().getStartTime()));
		tcEnd.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(p.getValue().getEndTime()));

		tcTitle.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(p.getValue().getProgramName()));

		tcChannel.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(p.getValue().getChannelName()));

		tcEpisode.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(p.getValue().getEpisodeName()));

		tcNumber.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(
				p.getValue().getSeriesNum() != null && !"".equalsIgnoreCase(p.getValue().getSeriesNum())
						? p.getValue().getSeriesNum() + "x" + p.getValue().getEpisodeNum()
						: ""));

		tableView.setRowFactory(tr -> new TableRow<Schedule>() {
			@Override
			protected void updateItem(Schedule schedule, boolean empty) {
				super.updateItem(schedule, empty);
				Date startDate;
				Date endDate;
				try {
					if (schedule != null) {
						String startDateStr = schedule.getStartTime();
						String endDateStr = schedule.getEndTime();
						if (startDateStr != null && endDateStr != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							startDate = sdf.parse(startDateStr);
							endDate = sdf.parse(endDateStr);
							if (startDate.before(new Date()) && endDate.after(new Date())) {
								if (!getStyleClass().contains("priorityHigh")) {
									getStyleClass().add("priorityHigh");
								}
							} else {
								getStyleClass().removeAll("priorityHigh");
							}
						}
					}
				} catch (ParseException e) {
					logger.error("Error when parsing startTime " + schedule.getStartTime() + " to Java Date");
				}
			}
		});

		// Add double click listener to listView
		listView.setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
				if (mouseEvent.getClickCount() == 2) {
					sendWol(config.getProperty("mediaportaldbhost"), config.getProperty("mediaportaldbhostmac"));
				}
			}
		});
	}

	/**
	 * @return the tableView
	 */
	public TableView<Schedule> getTableView() {
		return tableView;
	}

	public void addConsoleLine(final String string) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (listView.getItems() != null) {
					listView.getItems().add(sdf.format(new Date()) + " - " + string);
					listView.getItems().sort(new Comparator<String>() {

						@Override
						public int compare(String o1, String o2) {
							return -o1.compareTo(o2);
						}
					});
				}
			}
		});

	}

	/**
	 * Send a wol package to the specified host and mac address
	 * 
	 * @param hostname
	 *                   host name or ip of the remote server
	 * @param macAddress
	 *                   mac address of the remote server
	 */
	public void sendWol(String hostname, String macAddress) {

		try {
			byte[] macBytes = getMacBytes(macAddress);
			byte[] bytes = new byte[6 + 16 * macBytes.length];
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) 0xff;
			}
			for (int i = 6; i < bytes.length; i += macBytes.length) {
				System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
			}

			InetAddress address = InetAddress.getByName(hostname);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();

			logger.info("Wake-on-LAN packet sent.");
			addConsoleLine("Wake-on-LAN packet sent.");
		} catch (Exception e) {
			logger.warn("Failed to send Wake-on-LAN packet: " + e.getMessage(), e);
		}

	}

	/**
	 * Parse mac String in a byte array
	 * 
	 * @param macStr
	 *               mac address String
	 * @return mac address as byte array
	 * @throws IllegalArgumentException
	 *                                  will be thrown if the mac address has a
	 *                                  wrong syntax
	 */
	private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(:|-)");
		if (hex.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address.");
		}
		try {
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid hex digit in MAC address.");
		}
		return bytes;
	}

	@FXML
	public void menuItemCloseAction(ActionEvent event) {
		logger.info("User closed application by menu item");
		System.exit(0);
	}

	@FXML
	public void menuItemEditConfigurationAction() {
		logger.info("Starting configuration dialog by menu item");
		try {
			this.mpWidget.showConfigurationDialog();
		} catch (IOException e) {
			logger.error("Exception thrown while starting configuration dialog: " + e.getMessage(), e);
		}
	}

	/**
	 * @param config
	 *               the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * @return the listViewPane
	 */
	public ScrollPane getListViewPane() {
		return listViewPane;
	}

	/**
	 * @return the listView
	 */
	public ListView<String> getListView() {
		return listView;
	}

	/**
	 * @param mpWidget
	 *                 the mpWidget to set
	 */
	public void setMpWidget(MPWidget mpWidget) {
		this.mpWidget = mpWidget;
	}

}
