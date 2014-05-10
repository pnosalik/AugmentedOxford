package ox.augmented.model;

import ox.augmented.data.DataSource;

public class Poi {

	private String name;
	private Double latitude;
	private Double longitude;
	private String info;
	private String dataSourceInfo = "";
	private DataSource dataSource;
	
	public Poi(String name, Double latitude, Double longitude, String info, DataSource dataSource) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.info = info;		
		this.dataSource = dataSource;
		if(dataSource != null) {
			dataSource.setAnchor(this);
			dataSourceInfo = dataSource.getCurrentText();
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
		return info;
	}
	
	public boolean hasDataSource() {
		return dataSource != null;
	}
	
	public String getDataSourceInfo() {
		if(dataSource != null) {
			dataSourceInfo = dataSource.getCurrentText();
			dataSource.next();
		}
		return dataSourceInfo;
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
}
