import com.pauquette.appointments.model.AppointmentsDAO;

public class Driver {
	private AppointmentsDAO dao;
	private String searchForText;
	private String description;

	private String inputDate;

	public Driver() {
		dao=AppointmentsDAO.getInstance();
		if (dao.checkPopulated()) {
			// NOOP - The database has some existing tables
		} else {
			// First time initialization of an empty database
			dao.initializeDataBase();
		}
	}

	public String getDescription() {
		return description;
	}

	public String getInputDate() {
		return inputDate;
	}
	public String getSearchForText() {
		return searchForText;
	}
	
	/* Returns JSON string for searchResults */
	public Object getSearchResult() {
		if (getSearchForText()==null||getSearchForText().isEmpty()) {
			return dao.toJson(dao.getAllAppointments());
		} else {
		    return dao.toJson(dao.getAppointmentsContaining(getSearchForText()));
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	
	public void setSearchForText(String searchForText) {
		this.searchForText = searchForText;
	}
	
	public void createAppointment() {
		dao.createAppointment(getDescription(), getInputDate(), 60);
	}
}