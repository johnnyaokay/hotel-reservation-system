import java.io.IOException;

/**
 * This program simulates a vending machine.
 */
public class RunSystem {
	public static void main(String[] args) throws IOException {
		ReservationSystem system = new ReservationSystem();
		Menu systemInterface = new Menu();
		systemInterface.run(system);
	}
}
