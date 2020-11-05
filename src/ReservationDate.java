import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationDate {
	private LocalDate date;
	private int day;
	private int month;
	private int year;

	public ReservationDate(String line) {
		String values[] = line.split("/");
		day = Integer.parseInt(values[0]);
		month = Integer.parseInt(values[1]);
		year = Integer.parseInt(values[2]);
		date = LocalDate.of(year, month, day);
	}
	
	public ReservationDate(LocalDate d) {
		day = d.getDayOfMonth();
		month = d.getMonthValue();
		year = d.getYear();
		date = LocalDate.of(year, month, day);
	}
	
	public LocalDate getDate() {
		return this.date;
	}
	
	public int getWeekday() {
		return this.date.getDayOfWeek().getValue() - 1;
	}
	
	public int getNoOfNights(ReservationDate d) {
		return (int) date.until(d.getDate(), ChronoUnit.DAYS);
	}
	
	public boolean equals(ReservationDate d) {
		if(this.toString().equals(d.toString())) return true;
		return false;
	}
	
	public String toString() {
		return day + "/" + month + "/" + year;
	}
}