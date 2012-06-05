package edu.comp.myBus.containers;
//this object is used for getStopTimesByStop and getDeparturesByStop, hence two different contructors.
public class Bus {
	String arrival_time;
	String departure_time;//not used by getDeparturesByStop
	String route_id;
	String trip_headsign;
	
	//These two variables are not used by getStopTimesByStop
	public String headsign;
	public String expected_mins;
	//-------------------
	
	public Bus(String arrival_time, String departure_time, String route_id,
			String trip_headsign) {
		this.arrival_time = arrival_time;
		this.departure_time = departure_time;
		this.route_id = route_id;
		this.trip_headsign = trip_headsign;
	}
	public Bus(String arrival_time, String route_id,
			String trip_headsign, String headsign, String expected_mins) {
		this.arrival_time = arrival_time;
		this.route_id = route_id;
		this.trip_headsign = trip_headsign;
		this.headsign=headsign;
		this.expected_mins=expected_mins;
	}
	
}
