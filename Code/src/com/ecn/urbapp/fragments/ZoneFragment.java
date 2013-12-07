package com.ecn.urbapp.fragments;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.db.Element;
import com.ecn.urbapp.db.PixelGeom;
import com.ecn.urbapp.dialogs.TopologyExceptionDialogFragment;
import com.ecn.urbapp.utils.ConvertGeom;
import com.ecn.urbapp.zones.BitmapLoader;
import com.ecn.urbapp.zones.DrawZoneView;
import com.ecn.urbapp.zones.UtilCharacteristicsZone;
import com.ecn.urbapp.zones.Zone;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.io.ParseException;

/**
 * @author	COHENDET Sébastien
 * 			DAVID Nicolas
 * 			GUILBART Gabriel
 * 			PALOMINOS Sylvain
 * 			PARTY Jules
 * 			RAMBEAU Merwan
 * 
 * ZoneFragment class
 * 
 * This is the fragment used to define the different zones.
 * 			
 */

public class ZoneFragment extends Fragment implements OnClickListener, OnTouchListener{
	/**
	 * Field defining the radius tolerance on touch
	 */
	private int TOUCH_RADIUS_TOLERANCE = 30;//only for catching points in edit mode
	
	/**
	 * Constant field defining the reference height to correct the size of point for the zone creation
	 */
	private final int REFERENCE_HEIGHT = 600;
	
	/**
	 * Constant field defining the reference width to correct the size of point for the zone creation
	 */
	private final int REFERENCE_WIDTH = 1200;
	
	/**
	 * Field defining the actual sate of definition of zones
	 */
	private int state;

	/**
	 * Button cancel
	 */
	private Button cancel;
	/**
	 * Button help
	 */
	private Button help;
	/**
	 * Button back
	 */
	private Button back;
	/**
	 * Button validate
	 */
	private Button validate;
	/**
	 * Button delete
	 */
	private Button delete;
	

	/**
	 * Image displayed
	 */
	private ImageView myImage;
	
	/**
	 * Matrix for displaying
	 */
	private Matrix matrix;
	
	/**
	 * Temporary zone for edition
	 */
	private Zone zoneCache ; 
	

	/**
	 * Temporary pixelGeom for edition
	 */
	private PixelGeom geomCache;
	
	/**
	 * Zone selected
	 */
	private Zone zone;
	
	/**
	 * Point selected
	 */
	private Point selected;
	private DrawZoneView drawzoneview;
	private int imageHeight; private int imageWidth;
	
	/**
	 * Constant value
	 */
	private final int IMAGE_CREATION = 2;
	/**
	 * Constant value
	 */
	private final int IMAGE_EDITION = 3;
	/**
	 * Constant value
	 */
	private final int IMAGE_SELECTION= 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public void onClick(View v) {
		switch(state){
		
		case IMAGE_SELECTION:
			break;
		case IMAGE_CREATION:
			switch(v.getId()){
			case R.id.zone_button_back:
				zone.getPoints().remove(zone.getPoints().lastElement());
				refreshCreate();
				break;
			case R.id.zone_button_cancel:
				state = IMAGE_SELECTION;
				exitAction();
				break;
			case R.id.zone_button_help:
				break;
			case R.id.zone_button_validate:
				ArrayList<PixelGeom> lpg = new ArrayList<PixelGeom>();
				for (PixelGeom pg : MainActivity.pixelGeom) {
					lpg.add(pg);
				}
				ArrayList<Element> le = new ArrayList<Element>();
				for (Element elt : MainActivity.element) {
					le.add(elt);
				}
				try {
					PixelGeom pg = new PixelGeom();
					pg.setPixelGeom_the_geom((new Zone(zone)).getPolygon().toText());
					UtilCharacteristicsZone.addInMainActivityZones(pg, null);
					exitAction();
				} catch(TopologyException e) {
					MainActivity.pixelGeom = lpg;
					MainActivity.element = le;
					TopologyExceptionDialogFragment diag = new TopologyExceptionDialogFragment();
					diag.show(getFragmentManager(), "TopologyExceptionDialogFragment");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				state = IMAGE_SELECTION;
				exitAction();
				break;
			}
			break;
		case IMAGE_EDITION:
			switch(v.getId()){
			case R.id.zone_button_delete:
				if(selected.x!=0 || selected.y!=0){
					zone.deletePoint(selected);
					selected.set(0,0);//no selected point anymore
					myImage.invalidate();//refresh image
				}
				else{
					int pos;
					for(pos=0; pos<MainActivity.pixelGeom.size(); pos++){
						if(MainActivity.pixelGeom.get(pos).getPixelGeomId()==geomCache.getPixelGeomId()){
							MainActivity.pixelGeom.remove(pos);
						}
					}
					state = IMAGE_SELECTION;
					exitAction();
				}
				break;
			case R.id.zone_button_back:
				break;
			case R.id.zone_button_cancel:
				if(zoneCache != null){
					//if user is coming from CreateZone there is no original to save
				}
					//MainActivity.zones.add(new Zone(zoneCache));//save original
					/*PixelGeom pgeom = new PixelGeom();
					pgeom.setPixelGeomId(MainActivity.pixelGeom.size());
					pgeom.setPixelGeom_the_geom(ConvertGeom.ZoneToPixelGeom(zoneCache));
					
					MainActivity.pixelGeom.add(pgeom);*/
	            exitAction();
				state = IMAGE_SELECTION;
				break;
			case R.id.zone_button_help:
				break;
			case R.id.zone_button_validate:
				if(!zone.getPoints().isEmpty()){
					ArrayList<PixelGeom> lpg = new ArrayList<PixelGeom>();
					for (PixelGeom pg : MainActivity.pixelGeom) {
						lpg.add(pg);
					}
					ArrayList<Element> le = new ArrayList<Element>();
					for (Element elt : MainActivity.element) {
						le.add(elt);
					}
					try {
						//MainActivity.zones.remove(zoneCache); //delete original
						MainActivity.pixelGeom.remove(geomCache);
						PixelGeom pg = new PixelGeom();
						pg.setPixelGeom_the_geom((new Zone(zone)).getPolygon().toText());
						UtilCharacteristicsZone.addInMainActivityZones(pg, null);
						exitAction();
					} catch(TopologyException e) {
						MainActivity.pixelGeom = lpg;
						MainActivity.element = le;
						TopologyExceptionDialogFragment diag = new TopologyExceptionDialogFragment();
						diag.show(getFragmentManager(), "TopologyExceptionDialogFragment");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				state = IMAGE_SELECTION;
				exitAction();
				break;
			}
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.layout_zone, null);

		back = (Button) v.findViewById(R.id.zone_button_back);
		help = (Button) v.findViewById(R.id.zone_button_help);
		cancel = (Button) v.findViewById(R.id.zone_button_cancel);
		validate = (Button) v.findViewById(R.id.zone_button_validate);
		delete = (Button) v.findViewById(R.id.zone_button_delete);

		back.setOnClickListener(this);
		cancel.setOnClickListener(this);
		validate.setOnClickListener(this);
		delete.setOnClickListener(this);
		
		validate.setEnabled(false);
		back.setEnabled(false);
		cancel.setEnabled(false);
		help.setEnabled(true);
		delete.setEnabled(false);

		zone = new Zone(); zoneCache = new Zone(); selected = new Point(0,0); 

		myImage = (ImageView) v.findViewById(R.id.image_zone);
		
		drawzoneview = new DrawZoneView(zone, selected) ;

		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

		Drawable[] drawables = {
			new BitmapDrawable(
				getResources(),
				BitmapLoader.decodeSampledBitmapFromFile(
						Environment.getExternalStorageDirectory()+"/featureapp/"+MainActivity.photo.getPhoto_url(), metrics.widthPixels, metrics.heightPixels - 174)), drawzoneview
				};
				
		myImage.setImageDrawable(new LayerDrawable(drawables));
		myImage.setOnTouchListener(this);
		
		SetCharactFragment scf = (SetCharactFragment)getFragmentManager().findFragmentById(R.id.fragmentCaract);
		
		
		return v;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		state=1;
		
		imageHeight = myImage.getDrawable().getIntrinsicHeight(); 
		imageWidth = myImage.getDrawable().getIntrinsicWidth();
		
		float ratioW =((float)REFERENCE_WIDTH/imageWidth);
		float ratioH =((float)REFERENCE_HEIGHT/imageHeight);
		float ratio = ratioW < ratioH ? ratioW : ratioH ;
			
		drawzoneview.setRatio(ratio);
		TOUCH_RADIUS_TOLERANCE/=ratio;
	}
	
	/**
	 * Common action to do on exit (cancel or validation)
	 */
	private void exitAction(){
        drawzoneview.onZonePage();
		validate.setEnabled(false);
		back.setEnabled(false);
		cancel.setEnabled(false);
		help.setEnabled(true);
		delete.setEnabled(false);
		
		zone.setZone(new Zone());
		zoneCache.setZone(new Zone());
		selected.set(0,0);
		drawzoneview.setIntersections(new Vector<Point>());
		myImage.invalidate();
	}
	
	/** Declarations for create zone **/
	/*private OnTouchListener imageCreateTouchListener = new ImageView.OnTouchListener() {
		Point selected;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				Log.d("Move","Action Down");
				selected = new Point(0, 0);
				Point touch = getTouchedPoint(event);
				for(Point p : zone.getPoints()){//is the touched point a normal point ?
					float dx=Math.abs(p.x-touch.x);
					float dy=Math.abs(p.y-touch.y);
					if((dx*dx+dy*dy)<TOUCH_RADIUS_TOLERANCE*TOUCH_RADIUS_TOLERANCE){//10 radius tolerance
						selected.set(p.x,p.y);
					}
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				Log.d("Move","Action Up");
				if(selected.x==0 && selected.y==0){
					zone.addPoint2(getTouchedPoint(event));				
				}
				else{
					Point touch = getTouchedPoint(event);
					if (! zone.updatePoint(selected, touch)){//Is it a normal point ?
						zone.updateMiddle(selected, touch);	//If not it's a "middle" point, and it's upgraded to normal
						//TODO transfer to zone
					}
					selected.set(0, 0);//No selected point anymore
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				Log.d("Move","Action Move");
				if(selected.x!=0 || selected.y!=0){
					Point touch = getTouchedPoint(event);
					zone.updatePoint(selected, touch);
					selected.set(touch.x,touch.y);
				}
			}
			refreshCreate();//display new point, refresh buttons' availabilities	
			return true;
		}
	};*/
	
	public Point getTouchedPoint(MotionEvent event){
		float[] coord = {event.getX(),event.getY()};//get touched point coord

		getMatrix();
		matrix.mapPoints(coord);//apply matrix transformation on points coord
		int pointX = (int)coord[0]; int pointY = (int)coord[1];
		Log.d("Touch","x:"+pointX+" y:"+pointY);
		if(pointX<0){
			pointX=0;
		}else{
			if(pointX>imageWidth){
				pointX=imageWidth;
			}
		}
		if(pointY<0){
			pointY=0;
		}else{
			if(pointY>imageHeight){
				pointY=imageHeight;
			}
		}
		return(new Point(pointX,pointY));
	}
	
	public void getMatrix(){
		matrix = new Matrix();
		myImage.getImageMatrix().invert(matrix);
	}
	
	public void refreshCreate(){
		Vector<Point> points = zone.getPoints();
		if(! points.isEmpty()){
			back.setEnabled(false);
			if(points.size()>1){							
				validate.setEnabled(false);
				back.setEnabled(true);
				if(points.size()>2){
					validate.setEnabled(true);
					if(points.size()>3){
						Log.d("Intersect","enough points to test !");
						Vector<Point> intersections = new Vector<Point>(zone.isSelfIntersecting());
						if(!intersections.isEmpty()){
							validate.setEnabled(false);
						}
						drawzoneview.setIntersections(intersections);
					}
				}
			}
		}
		myImage.invalidate();
	}
    public void refreshEdit(){
		Vector<Point> points = zone.getPoints();
		if(points.size()<3){
			validate.setEnabled(false);
		}
		else{
			if(points.size()>2){
				validate.setEnabled(true);
				if(points.size()>3){
					Vector<Point> intersections = new Vector<Point>(zone.isSelfIntersecting());
					if(!intersections.isEmpty()){
						validate.setEnabled(false);
					}
					drawzoneview.setIntersections(intersections);
				}
			}
		}
		myImage.invalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
			switch(state){
			case IMAGE_CREATION:
	    		/*getMatrix();
				zone.addPoint(getTouchedPoint(event));
				refreshCreate();
				break;*/
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					Log.d("Move","Action Down");
					selected = new Point(0, 0);
					Point touch = getTouchedPoint(event);
					for(Point p : zone.getPoints()){//is the touched point a normal point ?
						float dx=Math.abs(p.x-touch.x);
						float dy=Math.abs(p.y-touch.y);
						if((dx*dx+dy*dy)<TOUCH_RADIUS_TOLERANCE*TOUCH_RADIUS_TOLERANCE){//10 radius tolerance
							selected.set(p.x,p.y);
						}
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.d("Move","Action Up");
					if(selected.x==0 && selected.y==0){
						zone.addPoint2(getTouchedPoint(event));				
					}
					else{
						Point touch = getTouchedPoint(event);
						if (! zone.updatePoint(selected, touch)){//Is it a normal point ?
							zone.updateMiddle(selected, touch);	//If not it's a "middle" point, and it's upgraded to normal
							//TODO transfer to zone
						}
						selected.set(0, 0);//No selected point anymore
					}
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					Log.d("Move","Action Move");
					if(selected.x!=0 || selected.y!=0){
						Point touch = getTouchedPoint(event);
						zone.updatePoint(selected, touch);
						selected.set(touch.x,touch.y);
					}
				}
				refreshCreate();//display new point, refresh buttons' availabilities	
				break;
			case IMAGE_EDITION:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getMatrix();
					Point touch = getTouchedPoint(event);
					
					Vector<Zone> zones = new Vector<Zone>();
					for(PixelGeom pg: MainActivity.pixelGeom){
						zones.add(ConvertGeom.pixelGeomToZone(pg));
					}
				//If one of its points was, it's a point move
					if(selected.x != 0 && selected.y != 0){
						if (! zone.updatePoint(selected, touch)){//Is it a normal point ?
							zone.updateMiddle(selected, touch);	//If not it's a "middle" point, and it's upgraded to normal
							//TODO transfer to zone
						}
						selected.set(0, 0);//No selected point anymore
					}
					//If none of its points, is the touch point a normal or middle point ?
					else{
						for(Point p : zone.getPoints()){//is the touched point a normal point ?
							float dx=Math.abs(p.x-touch.x);
							float dy=Math.abs(p.y-touch.y);
							if((dx*dx+dy*dy)<TOUCH_RADIUS_TOLERANCE*TOUCH_RADIUS_TOLERANCE){//10 radius tolerance
								selected.set(p.x,p.y);
							}
						}
						if(selected.x == 0 && selected.y == 0){//is the touched point a middle point ?
							for(Point p : zone.getMiddles()){
								float dx=Math.abs(p.x-touch.x);
								float dy=Math.abs(p.y-touch.y);
								if((dx*dx+dy*dy)<TOUCH_RADIUS_TOLERANCE*TOUCH_RADIUS_TOLERANCE){
									selected.set(p.x,p.y);
								}
							}
						}
					}
				}
				refreshEdit();
				break;
				
			case IMAGE_SELECTION:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getMatrix();
					Point touch = getTouchedPoint(event);
					
					Vector<Zone> zones = new Vector<Zone>();
					for(PixelGeom pg: MainActivity.pixelGeom){
						zones.add(ConvertGeom.pixelGeomToZone(pg));
					}
					boolean flag=false;
					Zone z=null;
					for(Zone test : zones){
						if(test.containPoint(touch)){
							flag=true;
							z=test;
							break;
						}
					}
					if(flag){
						zoneCache = z;
						zone.setZone(z);
	
						for(int i=0; i<MainActivity.pixelGeom.size(); i++){
							if(MainActivity.pixelGeom.get(i).getPixelGeom_the_geom().equals(zoneCache.getPolygon().toString())){
								geomCache = MainActivity.pixelGeom.get(i);
								MainActivity.pixelGeom.get(i).selected=true;
							}
						}
						state = IMAGE_EDITION;
						validate.setEnabled(true);
						back.setEnabled(false);
						cancel.setEnabled(true);
						help.setEnabled(true);
						delete.setEnabled(true);
						refreshEdit();
					}
					else{
			    		getMatrix();
						zone.addPoint(getTouchedPoint(event));
						refreshCreate();
						state = IMAGE_CREATION;
						validate.setEnabled(false);
						back.setEnabled(false);
						cancel.setEnabled(true);
						help.setEnabled(true);
					}
				break;
			}
				
		}
		return true;
	}

}