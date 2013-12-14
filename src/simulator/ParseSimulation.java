package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ParseSimulation {

	private TrafficEnums[] enums;
	private int timeIncrement;
	private ArrayList<String> firstStops;
	private ArrayList<String> secondStops;
	private ArrayList<String> thirdStops;
	
	private ArrayList<Date> inducedStops;
	
	public ParseSimulation()
	{
		setEnums(new TrafficEnums[]{TrafficEnums.NORMAL,TrafficEnums.NORMAL,TrafficEnums.NORMAL,
				TrafficEnums.NORMAL,TrafficEnums.NORMAL,TrafficEnums.NORMAL,TrafficEnums.NORMAL,
				TrafficEnums.TRAFFIC_LIGHT, TrafficEnums.TRAFFIC_LIGHT, TrafficEnums.TRAFFIC_JAM
				});
		induceError();
		firstStops = new ArrayList<String>();
		secondStops = new ArrayList<String>();
		thirdStops = new ArrayList<String>();
		
		inducedStops = new ArrayList<Date>();
	}
	
	public void induceError()
	{
		Random nature = new Random();
		int enumsIndex = nature.nextInt(10);
		
		switch (enumsIndex) {
		case 7:
		case 8:
			setTimeIncrement(2);
			break;
		case 9:
			setTimeIncrement(10);
			break;
		default:
			setTimeIncrement(0);
			break;
		}	
	}

	public int getTimeIncrement() {
		return timeIncrement;
	}

	public void setTimeIncrement(int timeIncrement) {
		this.timeIncrement = timeIncrement;
	}
	
	public void parseBusInfo(String file) throws ParseException
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			SimpleDateFormat tTR = new SimpleDateFormat("hh:mm:ss a");
			Calendar cal = Calendar.getInstance();
			while ((line = reader.readLine()) != null)
			{
				String[] parsedLine = line.split(" ");
				String firstStop = parsedLine[1] + " " + parsedLine[2].split(",")[0];
				cal.setTime(tTR.parse(firstStop));
				cal.add(Calendar.MINUTE, getTimeIncrement());
				Date inducedFirstStop = cal.getTime();
				firstStops.add(firstStop);
				inducedStops.add(inducedFirstStop);
				
				String secondStop = parsedLine[3] + " " + parsedLine[4].split(",")[0];
				cal.setTime(tTR.parse(secondStop));
				cal.add(Calendar.MINUTE, getTimeIncrement());
				Date inducedSecondStop = cal.getTime();
				secondStops.add(secondStop);
				inducedStops.add(inducedSecondStop);
				
				String thirdStop = parsedLine[5] + " " + parsedLine[6].split(",")[0];
				cal.setTime(tTR.parse(thirdStop));
				cal.add(Calendar.MINUTE, getTimeIncrement());
				Date inducedThirdStop = cal.getTime();
				thirdStops.add(thirdStop);
				inducedStops.add(inducedThirdStop);
				
			}
			reader.close();			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TrafficEnums[] getEnums() {
		return enums;
	}

	public void setEnums(TrafficEnums[] enums) {
		this.enums = enums;
	}
	
	public void startSimulation(String[] user1, String[] user2, String[] user3) throws InterruptedException, ParseException
	{
		BusUser firstUser = new BusUser(user1);
		BusUser secondUser = new BusUser(user2);
		BusUser thirdUser = new BusUser(user3);

		while (true)
		{
				SimpleDateFormat tTR = new SimpleDateFormat("hh:mm:ss a");
				//user1
				System.out.println("User at bus stop 1 sent a request. ");
				simulate(tTR, firstUser, user1, firstStops);
//				Thread.sleep(1000);
				//user2
				System.out.println("User at bus stop 2 sent a request. ");
				simulate(tTR, secondUser, user2, secondStops);
//				Thread.sleep(1000);
				//user3
				System.out.println("User at bus stop 3 sent a request. ");
				simulate(tTR, thirdUser, user3, thirdStops);
//				Thread.sleep(1000);
				return;
		}
	}
	
	public Date findNextTime(String timeToCompare, ArrayList<String> stops) throws ParseException
	{
		SimpleDateFormat tTR = new SimpleDateFormat("hh:mm:ss a");
		
		Date userRequest = tTR.parse(timeToCompare);
		Date stopsDate = tTR.parse(stops.get(0));
		for (int i = 0; i < stops.size() - 1; i++)
		{
			stopsDate = tTR.parse(stops.get(i));
			Date nextStopsDate = tTR.parse(stops.get(i + 1));
			if (userRequest.compareTo(stopsDate) < 0)
			{
				return stopsDate;
			}
			else if ((userRequest.compareTo(stopsDate) > 0) && (userRequest.compareTo(nextStopsDate) < 0))
			{
				return nextStopsDate;
			}
		}
		return null;
	}
	
	public void simulate(SimpleDateFormat tTR, BusUser user, String[] requests, ArrayList<String> stopsList) throws ParseException
	{
		Calendar cal = Calendar.getInstance();

		for (int i = 0; i < requests.length; i++)
		{
			String timeToRequest = user.askForInformation(i);	
			Date compareTime = findNextTime(timeToRequest, stopsList);
			if (compareTime != null)
			{
				if (getTimeIncrement() == 2)
				{
					cal.setTime(compareTime);
					cal.add(Calendar.MINUTE, 2);
					Date inducedFirstStop = cal.getTime();
					System.out.println("Bus is at a traffic light, bus will get there at " +
					tTR.format(inducedFirstStop) + " but BT4U says: " + tTR.format(compareTime) + " leads to an imprecision of atleast 2 minutes.");
					System.out.println("When the user doesn't see the bus by: " + tTR.format(compareTime) + ", BT4U will incorrectly show the time for the next scheduled arrival at: " + tTR.format(findNextTime(tTR.format(inducedFirstStop), stopsList)) + "\r\n");
				}
				else if (getTimeIncrement() == 10)
				{
					cal.setTime(compareTime);
					cal.add(Calendar.MINUTE, 10);
					Date inducedFirstStop = cal.getTime();
					System.out.println("Bus is at a traffic jam, bus will get there at " +
							tTR.format(inducedFirstStop) + " but BT4U says: " + tTR.format(compareTime)
							+ " leads to an accuracy imprecision of atleast 10 minutes");
					System.out.println("When the user doesn't see the bus by: " + tTR.format(compareTime) + ", BT4U will incorrectly show the time for the next scheduled arrival at: " + tTR.format(findNextTime(tTR.format(inducedFirstStop), stopsList)) + "\r\n");

				}
				else
				{
					System.out.println("Bus encountered no obstacles, bus will get to the bus stop at: " + tTR.format(compareTime).toString());
				}
			}
			else
			{
				;
			}
		}
		
	}
}
