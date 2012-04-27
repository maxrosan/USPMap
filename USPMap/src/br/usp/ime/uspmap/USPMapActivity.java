package br.usp.ime.uspmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import dataresource.ItemPosition;
import dataresource.ItemPositionMapper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
	Runnable                    zoomChecker;
	Handler                     handler;
	int                         lastZoom;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setBuiltInZoomControls(true);
        
        mc = mapView.getController();
        
        handler = new Handler();
        
        Log.i("debug", "Setting local listener...[0]");
        
        localMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        mapLocalListener = new MapLocationListener();
        mapLocalListener.setMapOverlay(this);
        localMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mapLocalListener);
        mc.setZoom(18);
        
        Resources res = getResources();
        
        mListOverlay = mapView.getOverlays();
        
        Location localInitial = new Location("initialLocal");
        localInitial.setLatitude(Double.parseDouble(res.getString(R.string.latitude_inital)));
        localInitial.setLongitude(Double.parseDouble(res.getString(R.string.longitude_initial)));
        changeMyLocation(localInitial);
                 
        lastZoom = 0;
        zoomChecker = new Runnable() {

        	public void run() {
        		// TODO Auto-generated method stub

        		//Log.e("zoom", "z = " + mapView.getZoomLevel());

        		if (lastZoom != mapView.getZoomLevel()) {

        			lastZoom = mapView.getZoomLevel();

        			ItemPositionMapper mapper = ItemPositionMapper.getInstance();
        			mapper.setResources(getResources());
        			
        			Iterator<Overlay> itRem = mListOverlay.iterator();
        			while (itRem.hasNext()) {
        				Overlay o = itRem.next();
        				if (o != mMe) {
        					itRem.remove();
        				}
        			}

        			ArrayList<ItemPosition> arrayItens = mapper.getPositions();
        			Iterator<ItemPosition> it = arrayItens.iterator();
        			while (it.hasNext()) {
        				ItemPosition item = it.next();
        				Drawable draw = getResources().getDrawable(item.getIcon());
        				if (lastZoom <= 17) {
        					BitmapDrawable bd = ((BitmapDrawable) draw);
        					int dstWidth = bd.getBitmap().getWidth() >> 1;
        					int dstHeight = bd.getBitmap().getHeight() >> 1;
        					draw = new BitmapDrawable(Bitmap.createScaledBitmap(bd.getBitmap(), dstWidth, dstHeight, true));
        				}
        				USPMapOverlay newOL = new USPMapOverlay(draw);
        				GeoPoint point = new GeoPoint((int)(item.getLatitude() * 1E6), 
        						(int)(item.getLongitude() * 1E6));
        				OverlayItem oItem = new OverlayItem(point, item.getName(), "");
        				newOL.addOverlay(oItem);
        				mListOverlay.add(newOL);
        			}								
        		}

        		handler.removeCallbacks(zoomChecker);
        		handler.postDelayed(zoomChecker, 500);
        	}

		};
		
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
	
	protected void onResume() {
		super.onResume();
		handler.postDelayed(zoomChecker, 500);
	}
	
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(zoomChecker);
	}
}