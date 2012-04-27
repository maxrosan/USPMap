package dataresource;

import java.util.ArrayList;

import android.content.res.Resources;
import android.util.Log;
import br.usp.ime.uspmap.R;

public class ItemPositionMapper {

	private static ItemPositionMapper instance = null;
	private Resources res;
	
	private ItemPositionMapper() {

	}
	
	public void setResources(Resources resources) {
		res = resources;
	}
	
	public static ItemPositionMapper getInstance() {
		if (instance == null) {
			instance = new ItemPositionMapper();
		}
		
		return instance;
	}
	
	public ArrayList<ItemPosition> getPositions() {
		
		ArrayList<ItemPosition> ret = new ArrayList<ItemPosition>();
		
		if (res != null) {
			String[] strs = res.getStringArray(R.array.positions);
			int i;
			
			for (i = 0; i < strs.length; i++) {
				String[] reg = strs[i].split(";");
				ItemPosition item = new ItemPosition();
				java.lang.reflect.Field field;
				try {
					field = R.drawable.class.getField(reg[1]);
					int drawableId = field.getInt(null);
					
					item.setName(reg[0]);
					item.setIcon(drawableId);
					item.setLatitude(Float.parseFloat(reg[2]));
					item.setLongitude(Float.parseFloat(reg[3]));
					
					ret.add(item);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("error", "ItemPositionMapper: " + reg[1] + ":Security Exception");
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("error", "ItemPositionMapper:" + reg[1] + ": No such field");
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("error", "ItemPositionMapper: " + reg[1] + ": Illegal Argument Exception");
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("error", "ItemPositionMapper: " + reg[1] + ": Illegal Acess Exception");
				}

			}
		}
		
		return ret;
	}
	
}
