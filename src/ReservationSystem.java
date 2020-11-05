import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ReservationSystem {
	private Scanner in = new Scanner(System.in);
	private ArrayList<Hotel> Hotels = new ArrayList<>();
	private ArrayList<Reservation> Reservations = new ArrayList<>();
	double[] roomStats = new double[3];
	HashMap <String, double[]> data = new HashMap<>();
	private int minAdults;
	private int maxAdults;
	private int minChildren;
	private int maxChildren;
	
	
	public Reservation getReservation(int ReservationNumber){
		for(int i = 0; i < Reservations.size(); i++) {
			if(ReservationNumber == Reservations.get(i).getReservationNumber()) {
				return Reservations.get(i);
			}
		}
		return null;
	}

	public int getMinAdults() {
		return minAdults;
	}
	
	public int getMaxAdults() {
		return maxAdults;
	}
	
	public int getMinChildren() {
		return minChildren;
	}
	
	public int getMaxChildren() {
		return maxChildren;
	}
	
	public ArrayList<Hotel> getHotels(){
		return Hotels;
	}
	
	public Room getRoomByName(String name) {
		for(int i = 0; i < Hotels.size(); i++) {
			ArrayList<Room> hotelRooms = Hotels.get(i).getRooms();
			for(int j = 0; j < hotelRooms.size(); j++) {
				Room room =  hotelRooms.get(j);
				if(name.equals(room.getRoomType())){
					return room;
				}
			}
		}
		return null;
	}
	
	public double getRoomPrice(Room chosenRoom, ReservationDate checkIn, ReservationDate checkOut) {
		int noOfNights = checkIn.getNoOfNights(checkOut);
		double[] rates = chosenRoom.getRates();
		int currentDay = checkIn.getWeekday();
		double price = 0;
		for(int j = 0; j < noOfNights; j++) {
			price += rates[currentDay];
			currentDay++;
			if(currentDay == 7) currentDay = 1;
		}
		return price;
	}

	//only display if room is available for check-in/check-out days
	public Room getRoomChoices(int noOfAdults, int noOfChildren, ReservationDate checkIn, ReservationDate checkOut) {
		HashMap<Integer, Room> roomIndexes = new HashMap<Integer, Room>();
		int roomCount = 0;
		System.out.printf("No. %-7s %-17s %s\n", "Hotel", "Room Type", "Cost");
		System.out.println("-------------------------------------");
		for(int i = 0; i < Hotels.size(); i++) {
			ArrayList<Room> hotelRooms = Hotels.get(i).getRooms();
			for(int j = 0; j < hotelRooms.size(); j++) {
				Room room =  hotelRooms.get(j);
				if(room.RoomsAvailable() && room.canOccupy(noOfAdults, noOfChildren)){
					roomCount++;
					System.out.printf("%2d) %-25s %.2f\n",roomCount, room,getRoomPrice(room, checkIn, checkOut));
					roomIndexes.put(roomCount, room);
				}
			}
		}
		int command = in.nextInt();
		return roomIndexes.get(command);
	}
	
	public void addReservation(Reservation reservation) {
		Reservations.add(reservation);
	}
	
	public void loadHotelData() throws IOException {
		//Hotel type                  = 0
		//Room type                   = 1
		//Number of Rooms             = 2
		//Occupancy Min Adult + Child = 3
		//Occupancy Max Adult + Child = 4
		//Rates Monday-Sunday         = 5-11
		
		
		String fileIn = "src/l4hotels.csv";
	    String line = null;
	    int lineNumber = 1;
	    Hotel currentHotel = null;
	    
		FileReader fileReader = new FileReader(fileIn);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);

	    while ((line = bufferedReader.readLine()) != null) {
	    	if(lineNumber > 2) {
	    		String[] values = line.split(",");
	    		String[] minOccupancies = values[3].split("\\+");
	    		String[] maxOccupancies = values[4].split("\\+");
	    		
	    		int minAdultOccupancy = Integer.parseInt(minOccupancies[0]);
    			int maxAdultOccupancy = Integer.parseInt(maxOccupancies[0]);
    			int minChildOccupancy = Integer.parseInt(minOccupancies[1]);
    			int maxChildOccupancy = Integer.parseInt(maxOccupancies[1]);
    			double[] rates = new double[7];
    			for(int i = 5; i < values.length; i++) {
    			    rates[i - 5] = Double.parseDouble(values[i]);
    			}
    			
	    		if(!values[0].equals("")) {
	    			currentHotel = new Hotel(values[0]);
	    			Hotels.add(currentHotel);
	    		}
	    		currentHotel.addRoom(values[1], Integer.parseInt(values[2]), minAdultOccupancy, maxAdultOccupancy, minChildOccupancy, maxChildOccupancy, rates);
	    		
	    		if(minAdultOccupancy > minAdults) minAdults = minAdultOccupancy;
	    		if(maxAdultOccupancy > maxAdults) maxAdults = maxAdultOccupancy;
	    		if(minChildOccupancy > minChildren) minChildren = minChildOccupancy;
	    		if(maxChildOccupancy > maxChildren) maxChildren = maxChildOccupancy;
	    	}
	        lineNumber++;
	    }
	    bufferedReader.close();
	}
	
	public void updateHotelStaysAndCancellations() throws IOException{
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader("src/Hotel Stay & Cancellation Information.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("Error. File Not Found"); 
		} 
		
		int lineNumber = 1;  
		String line = null; 
		
		while ((line = bufferedReader.readLine()) != null) { 
			if (lineNumber > 1) { 
				String[] values = line.split(",");
				if(!values[0].equals("")) {
					Reservation currentReservation = getReservation(Integer.parseInt(values[1]));
					if(currentReservation != null) {
						currentReservation.setState(values[5]);
					}
				}
			}
			lineNumber++;
		}
		bufferedReader.close();
	}
	
	public void loadReservationData() throws IOException {
		//Date = 0
		//Reservation Number = 1
		//Reservation Name   = 2
		//Reservation Type   = 3
		//Check-In Date      = 4
		//Nights             = 5 
		//Number Of Rooms    = 6
		//Rooms              = 7
		//Occupancy          = 8
		//Breakfast Included = 9
		//Total              = 10
		//Deposit            = 11
		
		
		String fileIn = "src/Reservation Information.csv";
	    String line = null;
	    int lineNumber = 1;
	    Reservation currentReservation = null;
	    ReservationDate checkInDate = null;
	    ReservationDate checkOutDate = null;
	    
		FileReader fileReader = new FileReader(fileIn);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);

	    while ((line = bufferedReader.readLine()) != null) {
	    	if(lineNumber > 1) {
	    		
	    		String[] values = line.split(",");
	    		if(values[0].equals("") && !values[7].equals("")) {
	    			boolean breakfastIncluded = Boolean.parseBoolean(values[9]);
	    			String[] Occupancy = values[8].split("\\+");
		    		int noOfAdults = Integer.parseInt(Occupancy[0]);
		    		int noOfChildren = Integer.parseInt(Occupancy[1]);
		    		Room room = getRoomByName(values[7]);
		    		double price = getRoomPrice(room, checkInDate, checkOutDate);
	    			currentReservation.addRoom(new ReservedRoom(room, noOfAdults, noOfChildren, price, breakfastIncluded));
	    		} else {
		    		int ReservationNumber = Integer.parseInt(values[1]);
		    		String ReservationName = values[2];
		    		String ReservationType = values[3];
		    		int noOfNights = Integer.parseInt(values[5]);
		    		int noOfRooms = Integer.parseInt(values[6]);
		    		checkInDate = new ReservationDate(values[4]);
		    		checkOutDate = new ReservationDate(checkInDate.getDate().plusDays(noOfNights));		
		    		boolean breakfastIncluded = Boolean.parseBoolean(values[9]);
		    		double total = Double.parseDouble(values[10]);
		    		double deposit = Double.parseDouble(values[11]);
		    		Bill bill = new Bill(total);
		    	
		    		ArrayList<ReservedRoom> Rooms = new ArrayList<>();
	    			String[] Occupancy = values[8].split("\\+");
		    		int noOfAdults = Integer.parseInt(Occupancy[0]);
		    		int noOfChildren = Integer.parseInt(Occupancy[1]);
		    		Room room = getRoomByName(values[7]);
		    		room.takeRoom();
		    		double price = getRoomPrice(room, checkInDate, checkOutDate);
		    		Rooms.add(new ReservedRoom(room, noOfAdults, noOfChildren, price, breakfastIncluded));
		    		
		    		if (ReservationType.equals("Standard")) {
		    			currentReservation = new StandardReservation(ReservationNumber, ReservationName, Rooms, checkInDate, checkOutDate, bill);
						Reservations.add(currentReservation);
		    		} else {
						currentReservation = new AdvancedPurchaseReservation(ReservationNumber, ReservationName, Rooms, checkInDate, checkOutDate, bill);
						Reservations.add(currentReservation);
					}
	    		}
	    	}
	        lineNumber++;
	    }
	    updateHotelStaysAndCancellations();
	    bufferedReader.close();
	}
	
	private void writeToCSV(String period) throws IOException {
		double totalOccupancyFigures = 0;
		double totalCancellations = 0;
		double totalCompanyIncome = 0;
		
		File newFile = new File("Analytics" + System.currentTimeMillis());
		FileWriter csvWriter = new FileWriter(newFile + ".csv");
		csvWriter.append("Analytics for: " + period);
		for(int i = 0; i < Hotels.size(); i++) {
			ArrayList<Room> hotelRooms = Hotels.get(i).getRooms();
			csvWriter.append("Hotel," + Hotels.get(i).getHotelType() + "\n");
			csvWriter.append("Room, Occupancy Figures, Cancellations, Income\n");
			int totalHotelIncome = 0;
			for(int j = 0; j < hotelRooms.size(); j++) {
				String room =  hotelRooms.get(j).getRoomType();
				if(data.containsKey(room)) {
					double stats[] = data.get(room);
					totalOccupancyFigures += stats[0];
					totalCancellations += stats[1];
					totalCompanyIncome += stats[2];
					totalHotelIncome += stats[2];
					csvWriter.append(room + "," + stats[0] + "," + stats[1] + "," + stats[2] + "\n");
				} else {
					csvWriter.append(room + "," + 0 + "," + 0 + "," + 0 + "\n");
				}
			}
			csvWriter.append("Total Hotel Income," + totalHotelIncome + "\n" + "\n");
		}
		
		csvWriter.append("Total Occupancy Figures," + totalOccupancyFigures + "\n");
		csvWriter.append("Total Cancellations," + totalCancellations + "\n");
		csvWriter.append("Total Company Income," + totalCompanyIncome + "\n");
		csvWriter.flush();
		csvWriter.close();
	}
	
	public void weeklyAnalytics(ReservationDate start) throws IOException { 
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader("src/Hotel Stay & Cancellation Information.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("Error. File Not Found"); 
		} 
		
		int lineNumber = 1;  
		String line = null; 
		String currentAction = "";
		
		while ((line = bufferedReader.readLine()) != null) { 
			if (lineNumber > 1) { 
				String[] values = line.split(",");
				if(values[0].equals("") && !values[7].equals("")) {
					String roomName = values[2];
					String action = currentAction;
					if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
					double[] stats = data.get(roomName);
					if(action.equals("Cancelled")) {
						stats[1]++;
					}
					if(action.equals("Check-In")) {
						stats[0]++;
					}
					stats[2] += Double.parseDouble(values[4]);
				}
				ReservationDate d = new ReservationDate (values[0]); 
			
				if (d.getDate().isAfter( start.getDate().minusDays(1)) && d.getDate().isBefore(start.getDate().plusDays(7))) { // its the requested day
					String roomName = values[2];
					int noOfRooms = Integer.parseInt(values[3]);
					String action = values[5];
					currentAction = action;
					double total = Double.parseDouble(values[4]);
					if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
					double[] stats = data.get(roomName);
					if(action.equals("Cancelled")) {
						stats[1]++;
					}
					if(action.equals("Check-In")) {
						stats[0]++;
					}
					stats[2] = total;
				}
			}
			lineNumber++;
		}
		writeToCSV("from " + start.toString() + " to " + new ReservationDate(start.getDate().plusDays(6)).toString());
		bufferedReader.close();
	}
	
	public void monthlyAnalytics(int month) throws IOException { 
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader("src/Hotel Stay & Cancellation Information.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("Error. File Not Found"); 
		} 
		
		int lineNumber = 1;  
		String line = null; 
		String currentAction = "";
		
		while ((line = bufferedReader.readLine()) != null) { 
			if (lineNumber > 1) { 
				String[] values = line.split(",");
				if(values[0].equals("") && !values[2].equals("")) {
					String roomName = values[2];
					String action = currentAction;
					if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
					double[] stats = data.get(roomName);
					if(action.equals("Cancelled")) {
						stats[1]++;
					}
					if(action.equals("Check-In")) {
						stats[0]++;
					}
					stats[2] += Double.parseDouble(values[4]);
				}
				
				String roomName = values[2];
				int noOfRooms = Integer.parseInt(values[3]);
				String action = values[5];
				currentAction = action;
				double price = Double.parseDouble(values[4]);
				if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
				double[] stats = data.get(roomName);
				if(action.equals("Cancelled")) {
					stats[1]++;
				}
				if(action.equals("Check-In")) {
					stats[0]++;
				}
				stats[2] += price;
			}
			lineNumber++;
		}
		writeToCSV(Month.of(month).name());
		bufferedReader.close();
	}
	
	public void dailyAnalytics(ReservationDate date) throws IOException { 
		BufferedReader bufferedReader = null;
		System.out.println("1");
		try {
			bufferedReader = new BufferedReader(new FileReader("src/Hotel Stay & Cancellation Information.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("Error. File Not Found"); 
		} 
		System.out.println("2");
		int lineNumber = 1;  
		String line = null;
		String currentAction = "";
		System.out.println("3");
		while ((line = bufferedReader.readLine()) != null) { 
			System.out.println("4");
			if (lineNumber > 1) { 
				String[] values = line.split(",");
				if(values[0].equals("") && !values[2].equals("")) {
					String roomName = values[2];
					String action = currentAction;
					if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
					double[] stats = data.get(roomName);
					if(action.equals("Cancelled")) {
						stats[1]++;
					}
					if(action.equals("Check-In")) {
						stats[0]++;
					}
					stats[2] += Double.parseDouble(values[4]);
				} else {
				
					ReservationDate d = new ReservationDate (values[0]); 
					if (d.equals(date)) { // its the requested day
						String roomName = values[2];
						int noOfRooms = Integer.parseInt(values[3]);
						String action = values[5];
						currentAction = action;
						double price = Double.parseDouble(values[4]);
						if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
						double[] stats = data.get(roomName);
						if(action.equals("Cancelled")) {
							stats[1]++;
						}
						if(action.equals("Check-In")) {
							stats[0]++;
						}
						stats[2] += price;
					}
				}
			}
			lineNumber++;
		}
		System.out.println("5");
		writeToCSV(date.toString());
		bufferedReader.close();
		System.out.println("5");
	}
	
	public void intervalAnalytics(ReservationDate start, ReservationDate end) throws IOException { 
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader("src/Hotel Stay & Cancellation Information.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("Error. File Not Found"); 
		} 
		
		int lineNumber = 1;  
		String line = null; 
		String currentAction = "";
		
		while ((line = bufferedReader.readLine()) != null) { 
			if (lineNumber > 1) { 
				String[] values = line.split(",");
				if(values[0].equals("") && !values[2].equals("")) {
					String roomName = values[2];
					String action = currentAction;
					if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
					double[] stats = data.get(roomName);
					if(action.equals("Cancelled")) {
						stats[1]++;
					}
					if(action.equals("Check-In")) {
						stats[0]++;
					}
					stats[2] += Double.parseDouble(values[4]);
				}
				
				ReservationDate d = new ReservationDate (values[0]); 
			
				if (d.getDate().isAfter( start.getDate().minusDays(1)) && d.getDate().isBefore(end.getDate().plusDays(1))) { // its the requested day
					String roomName = values[2];
					int noOfRooms = Integer.parseInt(values[3]);
					String action = values[5];
					currentAction = action;
					double price = Double.parseDouble(values[4]);
					if(!data.containsKey(roomName)) data.put(roomName, new double[] {0,0,0});
					double[] stats = data.get(roomName);
					if(action.equals("Cancelled")) {
						stats[1]++;
					}
					if(action.equals("Check-In")) {
						stats[0]++;
					}
					stats[2] += price;
				}
			}
			lineNumber++;
		}
		writeToCSV("from " + start.toString() + " to " + end.toString());
		bufferedReader.close();
	}
}
