
public class Bill {
	private double total;
	private double deposit;
	private boolean billPaid = false;
	
	public Bill(double total) {
		this.total = total;
		this.deposit = total * 0.20;
	}
	
	public double getTotal() {
		return total;
	}
	
	public double getDeposit() {
		return total * 0.20;
	}
	
	public void applyDiscount(double percentage) {
		total = total - (total * percentage);
	}
	
	public void refund() {
		total = deposit;
	}
}
