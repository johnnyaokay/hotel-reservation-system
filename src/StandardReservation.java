import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class StandardReservation extends Reservation {
	
	public StandardReservation(String ReservationName, ArrayList<ReservedRoom> chosenRooms, ReservationDate checkIn, ReservationDate checkOut, Bill bill) throws IOException{
		super(ReservationName, chosenRooms, checkIn, checkOut, bill, "Standard");
	}
	
	public StandardReservation(int ReservationNumber, String ReservationName, ArrayList<ReservedRoom> chosenRooms, ReservationDate checkIn, ReservationDate checkOut, Bill bill) throws IOException{
		super(ReservationNumber, ReservationName, chosenRooms, checkIn, checkOut, bill, "Standard");
	}
	
	@Override
	public void cancel() throws IOException {
		//if cancelled 24 hours within checkInDate
		//or if no show booking then cancel
			//don't issue refund
		
		//date, rnumber, rtype, action
		super.cancel();
		if (LocalDate.now().isBefore(checkInDate.getDate())) { 
			super.bill.refund();
		}
	}

}
