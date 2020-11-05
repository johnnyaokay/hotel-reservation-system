
public class ReservedRoom {
	private Room room;
	private int noOfAdults;
	private int noOfChildren;
	private double price;
	private Boolean breakfastIncluded = false;
	
	public ReservedRoom(Room room, int noOfAdults, int noOfChildren, double price, boolean breakfastIncluded) {
		this.room = room;
		this.noOfAdults = noOfAdults;
		this.noOfChildren = noOfChildren;
		this.price = price;
		this.breakfastIncluded = breakfastIncluded;
	}

	public Room getRoom() {
		return room;
	}

	public Boolean getBreakfastIncluded() {
		return breakfastIncluded;
	}

	public int getNoOfAdults() {
		return noOfAdults;
	}

	public int getNoOfChildren() {
		return noOfChildren;
	}

	public double getPrice() {
		return price;
	}
	
	
}
	
