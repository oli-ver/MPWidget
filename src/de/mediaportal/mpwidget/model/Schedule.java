package de.mediaportal.mpwidget.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Schedule {
	protected final static String FIELD_START_TIME = "startTime";
	protected final static String FIELD_END_TIME = "endTime";
	protected final static String FIELD_CHANNEL_NAME = "channelName";
	protected final static String FIELD_SERIES_NUM = "seriesNum";
	protected final static String FIELD_EPISODE_NUM = "episodeNum";
	protected final static String FIELD_PROGRAM_NAME = "programName";
	protected final static String FIELD_EPISODE_NAME = "episodeName";

	/**
	 * SQL command to retrieve schedule
	 */
	public final static String SQL_SCHEDULE = "SELECT p.startTime as " + FIELD_START_TIME + ", p.endTime as " + FIELD_END_TIME
			+ ",displayName as " + FIELD_CHANNEL_NAME + " , seriesNum as " + FIELD_SERIES_NUM + ",episodeNum as " + FIELD_EPISODE_NUM
			+ ", programName, episodeName as " + FIELD_EPISODE_NAME
			+ " FROM mptvdb.schedule s JOIN mptvdb.channel c on s.idChannel=c.idChannel "
			+ "JOIN mptvdb.program p on s.idChannel=p.idChannel and s.startTime=p.startTime and s.endTime=p.endTime "
			+ "WHERE p.endTime > CURRENT_DATE ORDER BY p.startTime ASC";

	protected String startTime = null;
	protected String endTime = null;
	protected String channelName = null;
	protected String seriesNum = null;
	protected String episodeNum = null;
	protected String programName = null;
	protected String episodeName = null;

	public Schedule(ResultSet rs) throws SQLException {
		this.startTime = rs.getString(FIELD_START_TIME);
		this.endTime = rs.getString(FIELD_END_TIME);
		this.channelName = rs.getString(FIELD_CHANNEL_NAME);
		this.seriesNum = rs.getString(FIELD_SERIES_NUM);
		this.episodeNum = rs.getString(FIELD_EPISODE_NUM);
		this.programName = rs.getString(FIELD_PROGRAM_NAME);
		this.episodeName = rs.getString(FIELD_EPISODE_NAME);
	}

	public Schedule() {
		super();
	}

	/**
	 * @return the sqlSchedule
	 */
	public static String getSqlSchedule() {
		return SQL_SCHEDULE;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @return the seriesNum
	 */
	public String getSeriesNum() {
		if (seriesNum != null && !"".equalsIgnoreCase(seriesNum.trim())) {
			int seriesNumInt = Integer.parseInt(seriesNum);
			seriesNumInt = 100 + seriesNumInt;
			return ("" + seriesNumInt).substring(1);
		}
		return seriesNum;
	}

	/**
	 * @return the episodeNum
	 */
	public String getEpisodeNum() {
		if (episodeNum != null && !"".equalsIgnoreCase(episodeNum)) {
			int episodeNumInt = Integer.parseInt(episodeNum);
			episodeNumInt = 100 + episodeNumInt;
			return ("" + episodeNumInt).substring(1);
		}
		return episodeNum;
	}

	/**
	 * @return the programName
	 */
	public String getProgramName() {
		return programName;
	}

	/**
	 * @return the episodeName
	 */
	public String getEpisodeName() {
		return episodeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Schedule [startTime=" + startTime + ", endTime=" + endTime + ", channelName=" + channelName + ", seriesNum=" + seriesNum
				+ ", episodeNum=" + episodeNum + ", programName=" + programName + ", episodeName=" + episodeName + "]";
	}
}
