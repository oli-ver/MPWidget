package de.mediaportal.mpwidget.view;

/**
 * Sample Skeleton for 'MPWidgetView.fxml' Controller Class
 */

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mediaportal.mpwidget.model.Schedule;

public class ViewController {
	protected Logger logger = null;

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
		
		// Bind Colums to objects
		tcStart.setCellValueFactory(new Callback<CellDataFeatures<Schedule, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Schedule, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getStartTime());
			}
		});

		tcEnd.setCellValueFactory(new Callback<CellDataFeatures<Schedule, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Schedule, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getEndTime());
			}
		});

		tcTitle.setCellValueFactory(new Callback<CellDataFeatures<Schedule, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Schedule, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getProgramName());
			}
		});

		tcChannel.setCellValueFactory(new Callback<CellDataFeatures<Schedule, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Schedule, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getChannelName());
			}
		});

		tcEpisode.setCellValueFactory(new Callback<CellDataFeatures<Schedule, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Schedule, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getEpisodeName());
			}
		});

		tcNumber.setCellValueFactory(new Callback<CellDataFeatures<Schedule, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Schedule, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getSeriesNum() != null
						&& !"".equalsIgnoreCase(p.getValue().getSeriesNum()) ? p.getValue().getSeriesNum() + "x"
						+ p.getValue().getEpisodeNum() : "");
			}
		});

		tableView.setRowFactory(new Callback<TableView<Schedule>, TableRow<Schedule>>() {
			@Override
			public TableRow<Schedule> call(TableView<Schedule> tableView) {
				final TableRow<Schedule> row = new TableRow<Schedule>() {
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
				};

				return row;
			}
		});
	}

	/**
	 * @return the tableView
	 */
	public TableView<Schedule> getTableView() {
		return tableView;
	}

}
