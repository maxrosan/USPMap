package br.usp.ime.uspmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class USPMapActivity extends MapActivity {
    /** Called when the activity is first created. */
	private MapController       mc;
	private LocationManager     localMan;
	private MapLocationListener mapLocalListener;
	private Location            myLocation;
	private List<Overlay>       mListOverlay;
	private MapView             mapView;
	private USPMapOverlay       mMe = null;
	private Runnable            zoomChecker;
	private Handler             handler;
	private int                 lastZoom;
	private CircularOverlay     circ1 = null, circ2 = null;
	private boolean             GPSon = false;
	private Runnable            gpsChecker = null;
	
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
        
        Log.i("USPMap", "Setting local listener...[0]");
        
        localMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        mapLocalListener = new MapLocationListener();
        mapLocalListener.setMapOverlay(this);
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
        			drawPlaces();
        			drawMe();
        		}

        		handler.removeCallbacks(zoomChecker);
        		handler.postDelayed(zoomChecker, 500);
        	}

		};
		
        //XXX: It is necessary to check if the GPS is enabled
    }
    
    private void turnGPSOn() {
    	GPSon = true;
    	localMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, mapLocalListener);
    	mapView.invalidate();
    }
    
    private void turnGPSOff() {
    	GPSon = false;
    	localMan.removeUpdates(mapLocalListener);
    	if (mMe != null) {
    		mListOverlay.remove(mMe);
    		mapView.invalidate();
    	}
    }
    
    synchronized public void drawPlaces() {
		lastZoom = mapView.getZoomLevel();
		
		ItemPositionMapper mapper = ItemPositionMapper.getInstance();
		mapper.setResources(getResources());
		
		Iterator<Overlay> itRem = mListOverlay.iterator();
		while (itRem.hasNext()) {
			Overlay o = itRem.next();
			if (o != mMe && !(o instanceof CircularOverlay)) {
				itRem.remove();
			}
		}

		ArrayList<ItemPosition> arrayItens = mapper.getPositions();
		Iterator<ItemPosition> it = arrayItens.iterator();
		while (it.hasNext()) {
			ItemPosition item = it.next();
			Drawable draw = getResources().getDrawable(item.getIcon());
			if (lastZoom <= 16) {
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
		
		mapView.invalidate();
		
    }
    
    synchronized public void drawMe() {
    	
    	if (GPSon) {

    		GeoPoint point = new GeoPoint((int)(myLocation.getLatitude() * 1E6), 
    				(int)(myLocation.getLongitude() * 1E6));

    		if (mMe != null) {
    			mListOverlay.remove(mMe);
    		}    	

    		Drawable drawMe = getResources().getDrawable(R.drawable.ic_launcher);
    		if (lastZoom <= 17) {
    			BitmapDrawable bd = ((BitmapDrawable) drawMe);
    			int dstWidth = bd.getBitmap().getWidth() >> 1;
    		int dstHeight = bd.getBitmap().getHeight() >> 1;
    		drawMe = new BitmapDrawable(Bitmap.createScaledBitmap(bd.getBitmap(), dstWidth, dstHeight, true));
    		}
    		mMe = new USPMapOverlay(drawMe);
    		OverlayItem oitem = new OverlayItem(point, "Me!", "");
    		mMe.addOverlay(oitem);

    		mListOverlay.add(mMe);
    		
    		mapView.invalidate();
    	}
    
    }
    
    public void changeMyLocation(Location local) {
    	myLocation = local;
    	
    	Log.i("USPMap", "local changed");
 
    	GeoPoint point = new GeoPoint((int)(local.getLatitude() * 1E6), 
    			(int)(local.getLongitude() * 1E6));
    	
    	mc.animateTo(point);
    	drawMe();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menus_item, menu);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		
		if (GPSon) {
			menu.findItem(R.id.ativargps).setTitle("Desligar GPS");
		} else {
			menu.findItem(R.id.ativargps).setTitle("Ativar GPS");
		}
		
		return true;
		
	}
	
	public void startGPS() {
		if (gpsChecker == null) {
			gpsChecker = new Runnable() {

				private Location localNet = null;

				public void run() {

					Location local = localMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

					if (local == null) {

						if (localNet == null) {
							localNet = localMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							if (localNet != null) {
								drawPlaces();
								changeMyLocation(localNet);
							}
						}

						handler.removeCallbacks(gpsChecker);
						handler.postDelayed(gpsChecker, 500);
					} else {
						localNet = null;
						drawPlaces();
						changeMyLocation(local);
					}
				}
			};
		}

		handler.postDelayed(gpsChecker, 0);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.circ1:
			if (circ1 == null) {
				circ1 = new CircularOverlay("circular1_caminho", getResources());
				circ1.setColor(Color.BLUE);
				mListOverlay.add(circ1);
				
				drawPlaces();
				drawMe();
				mapView.invalidate();
			}
			break;
		case R.id.circ2:
			if (circ2 == null) {
				circ2 = new CircularOverlay("circular2_caminho", getResources());
				circ2.setColor(Color.GREEN);
				mListOverlay.add(circ2);
				
				drawPlaces();
				drawMe();
				mapView.invalidate();
			}
			break;
			
		case R.id.remover:
			if (circ1 != null) {
				mListOverlay.remove(circ1);
				circ1 = null;
			}
			if (circ2 != null) {
				mListOverlay.remove(circ2);
				circ2 = null;
			}
			mapView.invalidate();
			break;
			
		case R.id.ativargps:
			if (!localMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("GPS está desativado. Deseja ativá-lo?")
					.setCancelable(false)
					.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					})
					.setNegativeButton("Não", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
				AlertDialog alert = builder.create();
				alert.show();
			} else if (!GPSon) { 
				turnGPSOn();
				startGPS();				
			} else {
				turnGPSOff();
			}
			break;

		}
		
		return super.onOptionsItemSelected(item);
	}
	
}