package ox.augmented;

import ox.augmented.data.DataSource;

public class Poi {

	private String name;
	private Double latitude;
	private Double longitude;
	private String info;
	private DataSource dataSource;
	
	public Poi(String name, Double latitude, Double longitude, String info, DataSource dataSource) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.info = info;		
		this.dataSource = dataSource;
		if(dataSource != null) {
			dataSource.setAnchor(this);
			info = dataSource.getCurrentText();
		}
	}
	
	public Poi(String name, Double latitude, Double longitude, String info) {
		this(name, latitude, longitude, info, null);
	}
	
	public void setDataSource(DataSource dataSource) {
		dataSource.setAnchor(this);
		this.dataSource = dataSource;
	}

	public String getName() {
		return name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getInfo() {
		if(dataSource != null) {
			info = dataSource.getCurrentText();
		}
		return info;
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
}
