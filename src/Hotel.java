import java.util.ArrayList;
public class Hotel {
	private String hotelType;
	private ArrayList<Room> Rooms = new ArrayList<>();
	
	
	public Hotel(String hotelType) {
		this.hotelType = hotelType;
	}
	
	public ArrayList<Room> getRooms(){
		return Rooms;
	}
	
	public String getHotelType() {
		return hotelType;
	}
	
	public void addRoom(String roomType, int noOfRooms, int minAdultOccupancy, int maxAdultOccupancy, int minChildOccupancy, int maxChildOccupancy, double[] rates) {
		Rooms.add(new Room(hotelType, roomType, noOfRooms, minAdultOccupancy, maxAdultOccupancy, minChildOccupancy, maxChildOccupancy, rates));
		
	}
	
	public String toString() {
		return hotelType;
	}
}
