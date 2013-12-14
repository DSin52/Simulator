package simulator;

public class BusUser {

	private String[] timings;
	
	public BusUser(String[] timings)
	{
		this.timings = timings;
	}
	
	public String askForInformation(int i)
	{
		return timings[i];
	}
	
}
