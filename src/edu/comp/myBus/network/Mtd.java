package edu.comp.myBus.network;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.comp.myBus.containers.Bus;
import edu.comp.myBus.containers.Stop;

public class Mtd {

	//wrapper methods
	public static Bus[] getDeparturesByStop(String stop_id){
		String param = "&stop_id="+stop_id;
		String xml = mtdRequest("GetDeparturesByStop",param);
		return parseDepartures(xml);
	}
	public static Stop[] getStopsBySearch(String query){
		String param = "&query="+URLEncoder.encode(query);
		String xml = mtdRequest("GetStopsBySearch",param);
		return parseStops(xml);
	}
	
	private static String mtdRequest(String request,String param) {
		//make web request
		final String key = "key=b7718c6e195e45a68df1cd8bd09d24e0";
		String urlString = "http://developer.cumtd.com/api/v2.1/xml/"+request+"?";
		urlString += key;
		urlString += param;
		String output;
		HttpURLConnection urlConnection;
		try {
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		try {
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			output = convertStreamToString(in);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			urlConnection.disconnect();
		}
		
		return output;
	}
	
	//parse xml data returned by mtd web service
	private static Bus[] parseDepartures(String xml){
		Document doc = Jsoup.parse(xml);
		Elements data = doc.getElementsByTag("departure");
		int size = data.size();
		Bus[] buses = new Bus[size];
		for(int i = 0; i<size;i++){
			Element dep = data.get(i);
			String expected_mins = dep.attr("expected_mins");
			String expected = dep.attr("expected");
			String headsign = dep.attr("headsign");
			dep=dep.child(1);
			String trip_headsign = dep.attr("trip_headsign");
			String route_id = dep.attr("route_id");
			buses[i]=new Bus(expected,route_id,trip_headsign,headsign,expected_mins);
		}
		return buses;
	}
	private static Stop[] parseStops(String xml){
		Document doc = Jsoup.parse(xml);
		Elements data = doc.getElementsByTag("stop_point");
		int size = data.size();
		Stop[] stops = new Stop[size];
		for(int i = 0; i<size;i++){
			Element stop = data.get(i);
			String stop_id = stop.attr("stop_id");
			String code = stop.attr("code");
			String stop_lat = stop.attr("stop_lat");
			String stop_lon = stop.attr("stop_lon");
			String stop_name = stop.attr("stop_name");
			stops[i]= new Stop(stop_id,code,stop_lat,stop_lon,stop_name);
		}
		return stops;
	}
	
	private static String convertStreamToString(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}
}
