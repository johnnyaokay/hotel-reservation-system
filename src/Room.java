
public class Room {
	private String hotelType;
	private String roomType;
	private int noOfRooms;
	
	private int minAdultOccupancy;
	private int maxAdultOccupancy;
	private int minChildOccupancy;
	private int maxChildOccupancy;
	private double rates[];
	
	public Room(String hotelType, String roomType, int noOfRooms, int minAdultOccupancy, int maxAdultOccupancy, int minChildOccupancy, int maxChildOccupancy, double[] rates) {
		this.hotelType = hotelType;
		this.roomType = roomType;
		this.noOfRooms = noOfRooms;
		this.maxAdultOccupancy = maxAdultOccupancy;
		this.maxAdultOccupancy = maxAdultOccupancy;
		this.minChildOccupancy = minChildOccupancy;
		this.maxChildOccupancy = maxChildOccupancy;
		this.rates = rates;
	}
	
	public Boolean canOccupy(int noOfAdults, int noOfChildren) {
		if((noOfAdults >= minAdultOccupancy) && (noOfAdults <= maxAdultOccupancy) && (noOfChildren >= minChildOccupancy) && (noOfChildren <= maxChildOccupancy)) return true;
		return false;
	}
	
	public boolean RoomsAvailable() {
		if(noOfRooms > 0) return true;
		return false;
	}
	
	public void takeRoom() {
		noOfRooms--;
	}
	
	public void makeRoomAvailable() {
		noOfRooms++;
	}
	
	public String getRoomType() {
		return roomType;
	}
	
	public double[] getRates() {
		return rates;
	}
	
	public String toString() {
		return hotelType + "| " + roomType;
	}

	public int getMinAdultOccupancy() {
		return minAdultOccupancy;
	}

	public int getMaxAdultOccupancy() {
		return maxAdultOccupancy;
	}

	public int getMinChildOccupancy() {
		return minChildOccupancy;
	}

	public int getMaxChildOccupancy() {
		return maxChildOccupancy;
	}
}
