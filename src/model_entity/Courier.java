package model_entity;

public class Courier extends User {
	private String vehicleType;
	private String vehiclePlate;

	public Courier() {
		super();
		this.setRole("courier");
	}

	public Courier(String idUser, String fullName, String email, String password, String phone, String address,
			String gender, String vehicleType, String vehiclePlate) {
		super(idUser, fullName, email, password, phone, address, "courier", gender);
		this.vehicleType = vehicleType;
		this.vehiclePlate = vehiclePlate;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getVehiclePlate() {
		return vehiclePlate;
	}

	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}
}