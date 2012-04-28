package br.usp.ime.uspmap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class CircularOverlay extends Overlay {

	private ArrayList<GeoPoint> points;
	
	public CircularOverlay(String name, Resources res) {
		//R.array.circular1_caminho
		
		points = new ArrayList<GeoPoint>();
		
		try {
			Field f = R.array.class.getField(name);
			int id = f.getInt(null);
			String[] arr = res.getStringArray(id);
			
			for (int i = 0; i < arr.length; i++) {
				String pos[] = arr[i].split(";");
				Log.i("pos", pos[0] + ":" + pos[1]);
				float lat = Float.parseFloat(pos[0]);
				float lon = Float.parseFloat(pos[1]);
				GeoPoint point = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
				points.add(point);
			}
			
		} catch (SecurityException e) {
			e.printStackTrace();
			Log.e("error", name + ": Security Exception");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			Log.e("error", name + ": No such field");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Log.e("error", name + ": Illegal argument");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Log.e("error", name + ": Illegal access");
		}
		
	}
	
	@Override
	 public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		 
		super.draw(canvas, mapv, shadow);
		
		Paint paint = new Paint();
		
		paint.setDither(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(4);
		paint.setAlpha(50);
		
		Iterator<GeoPoint> it = points.iterator();;
		Path path = new Path();
		Projection proj = mapv.getProjection();
		
		Point start, stop;
		
		start = new Point();
		stop = new Point();
		
		if (it.hasNext()) {
			GeoPoint point = it.next();
			proj.toPixels(point, start);
			path.moveTo(start.x, start.y);
		}
		
		while (it.hasNext()) {
			GeoPoint point = it.next();
			proj.toPixels(point, stop);
			canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
			start.x = stop.x;
			start.y = stop.y;
		}
		
		
	 }
	
}
