package edu.comp.myBus.containers;

public class Stop {
	public String code;
    public String stop_id;
    public String stop_lat;
    public String stop_lon;
    public String stop_name;
    public Stop(){}
    public Stop(String stop_id, String code, String stop_lat, String stop_lon, String stop_name){
    	this.stop_id=stop_id;
    	this.code = code;
    	this.stop_lat=stop_lat;
    	this.stop_lon=stop_lon;
    	this.stop_name = stop_name;
    }
    
}
