package com.pauquette.appointments.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;

/**
 *
 * @author Bryan Pauquette-An sqlite Data Access Object for the appointment book project
 */
public class AppointmentsDAO {

	private AppointmentsDAO() {
	   gson = new Gson();
	   connect();
	}

	// Class is a singleton
	private static class LazyHolder {
		private static final AppointmentsDAO INSTANCE = new AppointmentsDAO();
	
	}

	public static AppointmentsDAO getInstance() {
		return LazyHolder.INSTANCE;
	}

	private Connection conn;
	private Statement stmt;
	private Gson gson;
	private static Logger LOGGER=Logger.getGlobal();
	
	public void disconnect() {
		try {
			if (conn != null) {
				conn.close();
				LOGGER.info("Connection to SQLite has been terminated.");
			}
		} catch (SQLException ex) {
			LOGGER.severe(ex.getMessage());
		}
	}
	/* Check for existence of main data table */
	public boolean checkPopulated() {
		boolean tableExists=true;
		String sql="select min(appointment_id) from appointments";
		LOGGER.info(sql);
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.info(e.getMessage());
			tableExists=false;
		}
	    try {
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
			tableExists=false;
		}	    
	    return tableExists;
	}
	public void createAppointmentsTable() {
		InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("./sql/create_appointments.sql");
		if (in==null) {
			throw new RuntimeException("Can't locate sql file create_appointments.sql");
		}
		String sql = null;
		try {
			sql=getSQLfromFileSystem(in);
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
		 try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	    LOGGER.info("Created table in given database..."); 		
	}
	public void createAppointment(String evilDescription,
			                      LocalDateTime when,
			                      Integer duration_in_minutes) {
	String sql="insert into appointments values(null," + wrapwithQuotes(evilDescription) + "," +
			   convertDate(when) + ","  + duration_in_minutes +" );";   
	LOGGER.info(sql);
	try {
		stmt = conn.createStatement();
	} catch (SQLException e) {
		LOGGER.severe(e.getMessage());
	}
    try {
		stmt.executeUpdate(sql);
	} catch (SQLException e) {
		LOGGER.severe(e.getMessage());
	}
		
	}
	
	public void deleteAppointment(Appointment appointment) {
		String sql="delete from appointments where appointment_id=" + appointment.getAppointment_id();
		LOGGER.info(sql);
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	    try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	public String toJson(Object in) {
		return gson.toJson(in);
	}
	
	public List<Appointment> getAppointmentsContaining(String searchParam) {
		List<Appointment> results=new ArrayList<Appointment>();
		String sql="select appointment_id,description,appointment_date_time,duration_minutes from appointments where description like '%" +checkEvil(searchParam)  +"%' order by appointment_date_time";
		LOGGER.info(sql);
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	    try {
			ResultSet resultSet=stmt.executeQuery(sql);
			
			while (resultSet.next()) {
				Integer appointment_id = resultSet.getInt("appointment_id");
				String description = resultSet.getString("description");
				String when = resultSet.getString("appointment_date_time");
				Integer duration_minutes=resultSet.getInt("duration_minutes");
				Appointment appointment=new Appointment();
				appointment.setAppointment_id(appointment_id);
				appointment.setDescription(description);
				appointment.setDuration_in_minutes(duration_minutes);
				appointment.setWhen(stringToLocalDateTime(when));
				results.add(appointment);
			}
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}	    
	    return results;
		
	}
	
	public List<Appointment> getAllAppointments() {
		List<Appointment> results=new ArrayList<Appointment>();
		String sql="select appointment_id,description,appointment_date_time,duration_minutes from appointments order by appointment_date_time";
		LOGGER.info(sql);
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	    try {
			ResultSet resultSet=stmt.executeQuery(sql);
			
			while (resultSet.next()) {
				Integer appointment_id = resultSet.getInt("appointment_id");
				String description = resultSet.getString("description");
				String when = resultSet.getString("appointment_date_time");
				Integer duration_minutes=resultSet.getInt("duration_minutes");
				Appointment appointment=new Appointment();
				appointment.setAppointment_id(appointment_id);
				appointment.setDescription(description);
				appointment.setDuration_in_minutes(duration_minutes);
				appointment.setWhen(stringToLocalDateTime(when));
				results.add(appointment);
			}
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}	    
	    return results;
		
	}
	private LocalDateTime stringToLocalDateTime(String str) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
		return LocalDateTime.parse(str, formatter);	
	}
	public String convertDate(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
		String formattedDateTime = dateTime.format(formatter);  
		return "'" + formattedDateTime +  "'";
	}
	
	
	private String wrapwithQuotes(String evilTextFromUser) {
		return "'" + checkEvil(evilTextFromUser) + "'";
	}
	// TODO: Internationalize this....  
	// This is a job for your local dweeb propeller-head.  
	// In other words not a lot of useful functionality for the amount of work 
	// it would take to accomplish this.
	// This will prevent sql injection attacks in America and England and Australia
	// Good luck accomplishing an SQL injection attack with no punctuation symbols
	// The rest of the world will have to suffer for now. 
	// This is blatant technical debt.
	public String checkEvil(String evilTextFromUser) {
		return evilTextFromUser.replaceAll("[^A-Za-z0-9 ]", "");
	}
	
	public void dropAppointmentsTable() {
		InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("./sql/drop_appointments.sql");
		if (in==null) {
			throw new RuntimeException("Can't locate sql file drop_appointments.sql");
		}
		String sql = null;
		try {
			sql=getSQLfromFileSystem(in);
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
		 try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	    LOGGER.info("Dropped table in given database..."); 		
	}
	
	private String getSQLfromFileSystem(InputStream inputStream) throws IOException {
		final int bufferSize = 4096;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(inputStream, "UTF-8");
		for (; ; ) {
		    int rsz = in.read(buffer, 0, buffer.length);
		    if (rsz < 0)
		        break;
		    out.append(buffer, 0, rsz);
		}
		return out.toString();
	}
	
	/*Performs first time initialization of the database */
	public void initializeDataBase() {
	        dropAppointmentsTable();
	        createAppointmentsTable();
	}

	public void connect() {
		conn = null;
		try {
			// db parameters
			// TODO: Get this from a properties/configuration file...
			// Blatant technical debt
			String url = "jdbc:sqlite:C:\\Apache24\\cgi-bin\\appointments.db";
			// create a connection to the database
			conn = DriverManager.getConnection(url);

			LOGGER.info("Connection to SQLite has been established.");

		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	}
	
	public void finalize() {
		disconnect();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		  test();
	}
	public static void test() {
		AppointmentsDAO dao=AppointmentsDAO.getInstance();
		// Don't do any of this unless the database is empty...
		if (dao.checkPopulated()) {
			LOGGER.info("Will not test with data in the database... Drop the appointments table to enable testing");
			return; 
		}
		dao.createAppointmentsTable();
		// Test that creating appointments works...
		dao.createAppointment("Sample Appointment 1", LocalDateTime.now(), 60);
		dao.createAppointment("Sample Appointment 2", LocalDateTime.now(), 30);
		dao.createAppointment("Sample Appointment 3", LocalDateTime.now(), 15);
		dao.createAppointment("Guacamole Fiesta time", LocalDateTime.now(), 15);
		
		// Test that getting All appointments works...
		List<Appointment> appointments=dao.getAllAppointments();
		Appointment appointmentToDelete=null;
		for (Appointment appointment : appointments) {
			LOGGER.info(appointment.display());
			if (appointment.getDescription().equalsIgnoreCase("Sample Appointment 2")) {
				appointmentToDelete=appointment;
			}
		}
		// Test that deleting a single appointment works
		dao.deleteAppointment(appointmentToDelete);
		
		List<Appointment> smallerset=dao.getAllAppointments();
		for (Appointment appointment : smallerset) {
			LOGGER.info(appointment.display());
		}
		// Test containing search functionality works
		List<Appointment> spicyAppointments=dao.getAppointmentsContaining("Guac");
		for (Appointment appointment : spicyAppointments) {
			LOGGER.info(appointment.display());
		}
		// Test JSON conversion
		LOGGER.info(dao.toJson(spicyAppointments));
	}
	public void createAppointment(String description, String inputDate, int duration_in_minutes) {
		createAppointment(description,stringToLocalDateTime(inputDate),duration_in_minutes);		
	}
}