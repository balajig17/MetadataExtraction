package main.java.com.c_m;

public class Place 
{
	private String name;
	private double longitude;
	private double lattitude;
	
	public Place(String name, double longitude, double lattitude)
	{
		this.name = name;
		this.longitude = longitude;
		this.lattitude = lattitude;
	}
	
	public String toString() { 
	    return "("+this.name + "," + this.longitude + "," + this.lattitude + ")";
	}
	
	public String toJSON(int locationCount)
	{
		
		String json="\"loc"+String.valueOf(locationCount)+"\":";
		json+="\""+this.name+"\"";
		json+=",";
		json+="\"lat"+String.valueOf(locationCount)+"\":";
		json+=String.valueOf(this.lattitude);
		json+=",";
		json+="\"long"+String.valueOf(locationCount)+"\":";
		json+=String.valueOf(this.longitude);
		return json;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLattitude() {
		return lattitude;
	}

	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}
}
