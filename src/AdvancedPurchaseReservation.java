import java.io.IOException;
import java.util.ArrayList;

//5% discount on total price
public class AdvancedPurchaseReservation extends Reservation {
	
	public AdvancedPurchaseReservation(String ReservationName, ArrayList<ReservedRoom> chosenRooms, ReservationDate checkIn, ReservationDate checkOut, Bill bill) throws IOException {
		super(ReservationName, chosenRooms, checkIn, checkOut, bill, "Advanced Purchase");
	}
	
	public AdvancedPurchaseReservation(int ReservationNumber, String ReservationName, ArrayList<ReservedRoom> chosenRooms, ReservationDate checkIn, ReservationDate checkOut, Bill bill) throws IOException{
		super(ReservationNumber, ReservationName, chosenRooms, checkIn, checkOut, bill, "Advanced Purchase");
		super.bill.applyDiscount(0.05);
	}
	
	@Override
	public void cancel() throws IOException {
		//don't issue refund
		super.cancel();
	}

}
