package dataresource;

public class ItemPosition {
	
	private String   mName;
	private int mIcon;
	private float    mLatitude;
	private float    mLongitude;
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
		
	public void setIcon(int icon) {
		mIcon = icon;
	}
	
	public int getIcon() {
		return mIcon;
	}
	
	public void setLatitude(float latitude) {
		mLatitude = latitude;
	}
	
	public float getLatitude() {
		return mLatitude;
	}
	
	public void setLongitude(float longitude) {
		mLongitude = longitude;
	}
	
	public float getLongitude() {
		return mLongitude;
	}	
}
