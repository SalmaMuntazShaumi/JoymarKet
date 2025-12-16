package model_entity;

public class Admin extends User {
	private String emergencyContact;

	public Admin() {
		super();
		this.setRole("admin");
	}

	public Admin(String idUser, String fullName, String email, String password, String phone, String address,
			String gender, String emergencyContact) {
		super(idUser, fullName, email, password, phone, address, "admin", gender);
		this.emergencyContact = emergencyContact;
	}

	public String getEmergencyContact() {
		return emergencyContact;
	}

	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}
}