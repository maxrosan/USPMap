package br.usp.ime.uspmap;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MapLocationListener implements LocationListener {
	
	USPMapActivity mapOverlay = null;
	
	public void setMapOverlay(USPMapActivity mo) {
		assert(mo != null);
		mapOverlay = mo;
	}

	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		assert(mapOverlay != null);
		mapOverlay.changeMyLocation(arg0);
		Log.i("USPMap", "location changed");
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Log.i("USPMap", "proved enabled: " + arg0);
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		Log.i("USPMap", "Location status changed");
	}

}
