package com.pauquette.appointments.model;

import java.time.LocalDateTime;

public class Appointment {
	private Integer appointment_id;
	private String description;
	private LocalDateTime when;
	private Integer duration_in_minutes;
	public String display() {
		StringBuilder sb=new StringBuilder();
		sb.append(description);
		sb.append(when.toString());
		sb.append(duration_in_minutes);
		return sb.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Appointment other = (Appointment) obj;
		if (appointment_id == null) {
			if (other.appointment_id != null)
				return false;
		} else if (!appointment_id.equals(other.appointment_id))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (duration_in_minutes == null) {
			if (other.duration_in_minutes != null)
				return false;
		} else if (!duration_in_minutes.equals(other.duration_in_minutes))
			return false;
		if (when == null) {
			if (other.when != null)
				return false;
		} else if (!when.equals(other.when))
			return false;
		return true;
	}
	
	public Integer getAppointment_id() {
		return appointment_id;
	}
	public String getDescription() {
		return description;
	}
	public Integer getDuration_in_minutes() {
		return duration_in_minutes;
	}
	public LocalDateTime getWhen() {
		return when;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appointment_id == null) ? 0 : appointment_id.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((duration_in_minutes == null) ? 0 : duration_in_minutes.hashCode());
		result = prime * result + ((when == null) ? 0 : when.hashCode());
		return result;
	}
	public void setAppointment_id(Integer appointment_id) {
		this.appointment_id = appointment_id;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDuration_in_minutes(Integer duration_in_minutes) {
		this.duration_in_minutes = duration_in_minutes;
	}
	public void setWhen(LocalDateTime when) {
		this.when = when;
	}

}
