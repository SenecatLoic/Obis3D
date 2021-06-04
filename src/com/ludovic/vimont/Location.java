package com.ludovic.vimont;

public final class Location {
	private final String name;
	private final double latitude;
	private final double longitude;
	
	public Location(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String name() {
		return name;
	}

	public double lat() {
		return latitude;
	}

	public double lng() {
		return longitude;
	}

	@Override
	public String toString() {
		return "Location [name=" + name + ", latitude=" + latitude + ", longitude=" + longitude +  "]";
	}
}