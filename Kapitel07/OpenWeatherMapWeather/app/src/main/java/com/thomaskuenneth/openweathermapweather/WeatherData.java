package com.thomaskuenneth.openweathermapweather;

public class WeatherData {

	public String name;
	public String description;
	public String icon;
	public Double temp;
	
	public WeatherData(String name, String description,
					   String icon, Double temp) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.temp = temp;
	}
}
