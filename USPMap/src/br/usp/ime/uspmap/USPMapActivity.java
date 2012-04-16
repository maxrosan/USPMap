package br.usp.ime.uspmap;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class USPMapActivity extends MapActivity {
    /** Called when the activity is first created. */
	private MapController       mc;
	private LocationManager     localMan;
	private MapLocationListener mapLocalListener;
	private Location            myLocation;
	private List<Overlay>       mListOverlay;
	private MapView             mapView;
	USPMapOverlay               mMe = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setBuiltInZoomControls(true);
        
        mc = mapView.getController();
        
        Log.i("debug", "Setting local listener...[0]");
        
        localMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        mapLocalListener = new MapLocationListener();
        mapLocalListener.setMapOverlay(this);
        localMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mapLocalListener);
        mc.setZoom(17);
        
        Resources res = getResources();
        
        mListOverlay = mapView.getOverlays();
        
        Location localInitial = new Location("initialLocal");
        localInitial.setLatitude(Double.parseDouble(res.getString(R.string.latitude_inital)));
        localInitial.setLongitude(Double.parseDouble(res.getString(R.string.longitude_initial)));
        changeMyLocation(localInitial);
        
        
        //XXX: It is necessary to check if the GPS is enabled
    }
    
    public void changeMyLocation(Location local) {
    	myLocation = local;
 
    	GeoPoint point = new GeoPoint((int)(local.getLatitude() * 1E6), 
    			(int)(local.getLongitude() * 1E6));
    	
    	if (mMe != null) {
    		mListOverlay.remove(mMe);
    	}
    	
        Drawable drawMe = getResources().getDrawable(R.drawable.ic_launcher);
        mMe = new USPMapOverlay(drawMe);
        OverlayItem oitem = new OverlayItem(point, "Me!", "");
        mMe.addOverlay(oitem);
        
        mListOverlay.add(mMe);
    	
    	mc.animateTo(point);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}