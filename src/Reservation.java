import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Reservation {
	private static int lastReservationNumber = 1;
	
	protected int ReservationNumber;
	private String ReservationName;
	protected String ReservationType;
	private ArrayList<ReservedRoom> reservedRooms = new ArrayList<>();
	protected Bill bill;
	protected ReservationDate checkInDate;
	private ReservationDate checkOutDate;
	private String state;

	public Reservation(String ReservationName, ArrayList<ReservedRoom> chosenRooms, ReservationDate checkIn, ReservationDate checkOut, Bill bill, String ReservationType) throws IOException{
		lastReservationNumber++;
		this.ReservationNumber = lastReservationNumber;
		this.ReservationName = ReservationName;
		this.reservedRooms = chosenRooms;
		this.checkInDate = checkIn;
		this.checkOutDate = checkOut;
		this.bill = bill;
		this.ReservationType = ReservationType;
		writeToCSV(); //change this to try catch
	}
	
	public Reservation(int ReservationNumber, String ReservationName, ArrayList<ReservedRoom> chosenRooms, ReservationDate checkIn, ReservationDate checkOut, Bill bill, String ReservationType) throws IOException{
		this.ReservationNumber = ReservationNumber;
		this.ReservationName = ReservationName;
		this.reservedRooms = chosenRooms;
		this.checkInDate = checkIn;
		this.checkOutDate = checkOut;
		this.bill = bill;
		this.ReservationType = ReservationType;
		lastReservationNumber = ReservationNumber;
	}
	
	public int getReservationNumber() {
		return ReservationNumber;
	}
	
	public Bill getBill() {
		return bill;
	}
	
	public void addRoom(ReservedRoom room) {
		reservedRooms.add(room);
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public void writeToCSV() throws IOException {
		//Use Date class or method to count days because this will error for months and that
		int noOfNights = checkInDate.getNoOfNights(checkOutDate);
		
		FileWriter csvWriter = new FileWriter("src/Reservation Information.csv", true);
		csvWriter.append(new ReservationDate(LocalDate.now()) + "," + ReservationNumber + "," + ReservationName + "," + ReservationType + "," + checkInDate + "," + noOfNights + "," + reservedRooms.size() + ",");
		for(int i = 0; i < reservedRooms.size(); i++) {
			if(i > 0) csvWriter.append(",,,,,,,");
			ReservedRoom currentRoom = reservedRooms.get(i);
			String occupancy = currentRoom.getNoOfAdults() + "+" + currentRoom.getNoOfChildren();
			csvWriter.append(currentRoom.getRoom().getRoomType() + "," + occupancy + "," + currentRoom.getBreakfastIncluded());
			if(i == 0) csvWriter.append("," + bill.getTotal() + "," + bill.getDeposit());
			csvWriter.append("\n");
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	public void checkIn() throws IOException {
		if(state.equals("")) {
			state = "Check-In";
			FileWriter csvWriter = new FileWriter("src/Hotel Stay & Cancellation Information.csv", true);
			csvWriter.append(new ReservationDate(LocalDate.now()) + "," + ReservationNumber + ",");
			for(int i = 0; i < reservedRooms.size(); i++) {
				ReservedRoom currentRoom = reservedRooms.get(i);
				if(i > 0) csvWriter.append(",," + currentRoom.getRoom().getRoomType() + ",," + currentRoom.getPrice());			
				if(i == 0) csvWriter.append(currentRoom.getRoom().getRoomType() + "," + reservedRooms.size() + "," + currentRoom.getPrice() + "," + "Check-In");
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		} else {
			System.out.println("Unable to Check-In. User has already checked-in or the reservation has been cancelled. ");
		}
	}
	
	public void checkOut() throws IOException {
		if(state.equals("Check-In")) {
			state = "Check-Out";
			FileWriter csvWriter = new FileWriter("src/Hotel Stay & Cancellation Information.csv", true);
			csvWriter.append(new ReservationDate(LocalDate.now()) + "," + ReservationNumber + ",");
			for(int i = 0; i < reservedRooms.size(); i++) {
				ReservedRoom currentRoom = reservedRooms.get(i);
				if(i > 0) csvWriter.append(",," + currentRoom.getRoom().getRoomType() + ",," + currentRoom.getPrice());			
				if(i == 0) csvWriter.append(currentRoom.getRoom().getRoomType() + "," + reservedRooms.size() + "," + currentRoom.getPrice() + "," + "Check-Out");
				csvWriter.append("\n");
			}
		csvWriter.flush();
		csvWriter.close();
		}
		else {
			System.out.println("Unable to Check-Out. User has already checked-out or the reservation has been cancelled. ");
		}
	}
	
	public void cancel() throws IOException {
		if(state.equals(null)) {
			FileWriter csvWriter = new FileWriter("src/Hotel Stay & Cancellation Information.csv", true);
			csvWriter.append(new ReservationDate(LocalDate.now()) + "," + ReservationNumber + ",");
			for(int i = 0; i < reservedRooms.size(); i++) {
				ReservedRoom currentRoom = reservedRooms.get(i);
				if(i > 0) csvWriter.append(",," + currentRoom.getRoom().getRoomType() + ",," + currentRoom.getPrice());			
				if(i == 0) csvWriter.append(currentRoom.getRoom().getRoomType() + "," + reservedRooms.size() + "," + currentRoom.getPrice() + "," + "Cancellation");
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		} else {
			System.out.println("Unable to Cancel. User has already checked-in or checked-out. ");
		}
	}
}
