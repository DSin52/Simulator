package simulator;

import java.text.ParseException;
public class Simulator {
	
	public static void main (String[] args) throws ParseException
	{
		ParseSimulation sim = new ParseSimulation();
		
		sim.parseBusInfo("Stop_Times.csv");
		
		try {
			sim.startSimulation(new String[] {"07:00:00 AM", "09:04:00 PM", "10:10:43 PM"}, new String[] {"07:01:23 AM", "11:15:00 AM", "08:30:13 AM"}, new String[] {"07:02:00 AM", "03:15:00 PM", "10:45:00 AM"});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
