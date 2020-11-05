import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
	private Scanner in;

	public Menu() {
		in = new Scanner(System.in);
	}
	
	private int getIntFromInput(int min, int max) {
		int i = 0; 
		boolean ok = false; 
		while (!ok) { 
			i = getInt(); 
			if (i >= min && i <= max) { 
				ok = true;
			} else { 
				System.out.println("Enter a number within the range: ");
			}
		}
		
		return i;
	}
	
	private int getInt() { 
		int i = 0; 
		boolean ok = false; 
		do { 
			try { 
				i = in.nextInt();
				ok = true;
			} catch (InputMismatchException ex) { 
				System.out.println("Please enter a valid number: "); 
				in.nextLine(); 
			}
		} while (!ok);
		
		return i;
	}
	
	private ReservationDate getDateFromInput(String inOrOut, LocalDate checkInTime) {
		boolean checkIn = false,checkOut = false;
		if (inOrOut.equals("checkin")) checkIn = true; 
		if (inOrOut.equals("checkout")) checkOut = true; 
		LocalDate today = LocalDate.now();
		if (checkIn) { 
			System.out.println("Enter check-in date after " + new ReservationDate(today));
		} else if (checkOut) { 
			System.out.println("Enter check-out date before " + new ReservationDate(checkInTime.plusDays(30)));
		}
		ReservationDate d = null;
		boolean ok = false;
		while (!ok) {
			String date = in.next();	
			
			try {
				d = new ReservationDate(date);
				
				if (checkIn) { 
					if (d.getDate().isBefore(today)) System.out.print("Check-in can not be before current time... Enter date again: "); 
					else ok = true; 
					
				} else if (checkOut) { 
					if (d.getDate().isAfter(checkInTime.plusDays(30)) || (d.getDate().isBefore(checkInTime))) System.out.print("Check-out can not be over 30 days after, or before check-in ... Enter date again: "); 
					else ok = true;		
					
				} else { 
					ok = true;
				}
				
			} catch (DateTimeException e) {
				System.out.print("Date Not Valid... Try entering it again: ");

			} catch (NumberFormatException e) {
				System.out.println("Please enter the date in format dd/mm/yyyy... Try entering it again: ");
			}
		}
	
		return d;
	}

	public void run(ReservationSystem system) throws IOException {
		system.loadHotelData();
		system.loadReservationData();
		boolean more = true; 
		while(more) {
		
		HashMap<String, String[]> Methods = new HashMap<String, String[]>();
		Methods.put("Customer", new String[]{"Reservation", "Cancellation", "Quit"});
		Methods.put("Hotel Desk Administrator", new String[]{"Reservation", "Cancellation", "Check-In", "Check-Out", "Quit"});
		Methods.put("Supervisor", new String[]{"Reservation", "Cancellation", "Check-In", "Check-Out", "Apply Discount", "View Data Analytics", "Quit"});
		
	
		System.out.println("Login: (1)Customer (2)Hotel Desk Administrator (3)Supervisor (4)Quit");
		String user = "";
		int input = getIntFromInput(1,4);
		if(input == 1) user = "Customer";
		if(input == 2) user = "Hotel Desk Administrator";
		if(input == 3) user = "Supervisor";
		if(input == 4) break;
		
		String[] Commands = Methods.get(user);
		int position = 1;
		for(int i = 0; i < Commands.length; i++) {
			System.out.print("(" + position + ")" + Commands[i] + " ");
			position++;
		}
		System.out.println();
				
		String chosenCommand = Commands[getIntFromInput(1,Commands.length) - 1];
		
		if(chosenCommand.equals("Reservation")) {
			ArrayList<ReservedRoom> chosenRooms = new ArrayList<>();
			double totalCost = 0;
			
			LocalDate time = LocalDate.now(); 
			ReservationDate checkIn = getDateFromInput("checkin", time);			
			ReservationDate checkOut = getDateFromInput("checkout", checkIn.getDate());
			
			System.out.println("How many rooms would you like to reserve?");
			int noOfRooms = in.nextInt();
			
			for(int i = 1; i < noOfRooms + 1; i++) {
				System.out.println("How many adults for room " + i + "? (min: " + system.getMinAdults() + ") " + "(max: " + system.getMaxAdults() + ")");
				int noOfAdults = in.nextInt();
				System.out.println("How many children for room " + i + "? (min: " + system.getMinChildren() + ") " + "(max: " + system.getMaxChildren() + ")");
				int noOfChildren = in.nextInt();
				System.out.println("Choose a room (" + i + "/" + noOfRooms + "):" );
				Room chosenRoom = system.getRoomChoices(noOfAdults, noOfChildren, checkIn, checkOut);
				double price = system.getRoomPrice(chosenRoom, checkIn, checkOut);
				totalCost += price;
				
				System.out.println("Would you like breakfast included for €" + 7 * checkIn.getNoOfNights(checkOut));
				System.out.println("(1)Yes (2)No");
				boolean breakfastIncluded = false;
				if(getIntFromInput(1,2) == 1) breakfastIncluded = true;
				
				chosenRooms.add(new ReservedRoom(chosenRoom, noOfAdults, noOfChildren, price, breakfastIncluded));
			}
			
			System.out.println("Enter first name:");
			String name = in.next();
			
			System.out.println("Enter last name:");
			name += " " + in.next();
			
			System.out.println("Enter reservation type: (1)Standard (2)Advanced Purchased");
			int reservationType = in.nextInt();
			
			Bill bill = new Bill(totalCost);
			System.out.println("Total cost is: " + totalCost);
			System.out.println("A deposit of: " + totalCost * 0.20 + " is required to reserve the room");
			System.out.println("(1)Confirm (2)Cancel");
			if(getIntFromInput(1,2) == 1) {
				
				if(reservationType == 1) 
					system.addReservation(new StandardReservation(name, chosenRooms, checkIn, checkOut, bill));
				else 
					system.addReservation(new AdvancedPurchaseReservation(name, chosenRooms, checkIn, checkOut, bill));
				System.out.println("Reservation succesfully made!");
			}
		} 
		
		else if(chosenCommand.equals("Cancellation")) {
			System.out.println("Enter Reservation Number");
			Reservation currentReservation = system.getReservation(getInt());
			while (currentReservation == null) {
				in.next();
				System.out.println("Reservation not found, try again");
			}
			currentReservation.cancel();
		}
		
		else if(chosenCommand.equals("Check-In")) {
			System.out.println("Enter Reservation Number");
			
			Reservation currentReservation = system.getReservation(getInt());
			while (currentReservation == null) {
				in.next();
				System.out.println("Reservation not found, try again");
			}
			currentReservation.checkIn();
		}
		
		else if(chosenCommand.equals("Check-Out")) {
			System.out.println("Enter Reservation Number");
			Reservation currentReservation = system.getReservation(getInt());
			while (currentReservation == null) {
				in.next();
				System.out.println("Reservation not found, try again");
			}
			currentReservation.checkOut();
		}
		
		else if(chosenCommand.equals("Apply Discount")) {
			System.out.println("Enter Discount");
			Double discount = in.nextDouble();
			System.out.println("Enter Reservation Number");
			Reservation currentReservation = system.getReservation(getInt());
			while (currentReservation == null) {
				in.next();
				System.out.println("Reservation not found, try again");
			}
			currentReservation.getBill().applyDiscount(discount);
		}
		
		else if(chosenCommand.equals("View Data Analytics")) {
			System.out.println("Choose time frame: (1)Daily (2)Weekly (3)Monthly (4)Specific period");
			int inp = getIntFromInput(1,4);
			if (inp == 1) { 
				System.out.println("Enter Reservation Date: "); 
				ReservationDate date = getDateFromInput("other",null); 
				system.dailyAnalytics(date);
				System.out.println("1");
			} else if (inp == 2) { 
				System.out.println("Enter the start of the week date: "); 
				ReservationDate date = getDateFromInput("other",null); 
				system.weeklyAnalytics(date);
			} else if (inp == 3) { 
				System.out.println("Enter the month (1 - 12): "); 
				int month = getIntFromInput(1,12); 
				system.monthlyAnalytics(month);
			} else if (inp == 4) { 
				System.out.println("Enter start date: "); 
				ReservationDate dateS = getDateFromInput("other",null); 
				System.out.println("Enter end date: "); 
				ReservationDate dateE = getDateFromInput("other", null); 
				system.intervalAnalytics(dateS, dateE);
			}
		}
		else if(chosenCommand.equals("Quit")) {
			more = false;
		}
	}
	}
}
