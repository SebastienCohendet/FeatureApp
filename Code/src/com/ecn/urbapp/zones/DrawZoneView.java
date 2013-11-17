/*--------------------------------------------------------------------

Copyright Jonathan Cozzo and Patrick Rannou (22/03/2013)

This software is an Android application whose purpose is to select 
and characterize zones on a photography (type, material, color...).

This software is governed by the CeCILL license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

-----------------------------------------------------------------------*/

package com.ecn.urbapp.zones;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * This class is used to draw the zones on the image
 * 
 * @author patrick
 * 
 */
//la méthode draw dessine en fonction des informations dont elle dispose, qui sont apportées via les différents constructeur
public class DrawZoneView extends Drawable {
	private Zone zone; private Vector<Zone> zones; private PointF selected; private Vector<PointF> intersections;
	private boolean edit; private boolean create; Paint paintLastPoint; Paint paintFirstPoint;

	public DrawZoneView() {
		super();
	}
	
	public DrawZoneView(Vector<Zone> zones) {
		super();
		this.zones = zones; zone = new Zone();
	}
	
	public DrawZoneView(Vector<Zone> zones, Zone zone) {
		super();
		this.zone = zone; this.zones = zones; 
	}
	
	public DrawZoneView(Vector<Zone> zones, Zone zone, PointF selected) {
		super();
		this.zone = zone; this.zones = zones; this.selected = selected; this.intersections = new Vector<PointF>();
		this.edit = false; this.create = false;
	}
	
	public void setIntersections(Vector<PointF> intersections){
		this.intersections = intersections;
	}
	
	public void onCreateMode(){
		create = true;
		edit = false;
	}
	
	public void onEditMode(){
		create = false;
		edit = true;
	}
	
	public void onZonePage(){
		create = false;
		edit = false;
	}
	
	/**
	 * This method is called to draw the zones
	 */
	@Override
	public void draw(Canvas canvas) {
		Paint paintNormal = new Paint();
		paintNormal.setColor(Color.RED);
		paintNormal.setStyle(Paint.Style.FILL);
		paintNormal.setAlpha(255);
		
		if(create){
			paintLastPoint = new Paint();
			paintLastPoint.setColor(Color.MAGENTA);
			paintLastPoint.setStyle(Paint.Style.FILL);
			paintLastPoint.setAlpha(255);
			
			paintFirstPoint = new Paint();
			paintFirstPoint.setColor(Color.YELLOW);
			paintFirstPoint.setStyle(Paint.Style.FILL);
			paintFirstPoint.setAlpha(255);
		}else{
			paintLastPoint = new Paint(paintNormal);
			paintFirstPoint = new Paint(paintNormal);
		}
		
		Paint paintIntersections = new Paint();
		paintIntersections.setColor(Color.BLUE);
		paintIntersections.setStyle(Paint.Style.STROKE);
		paintIntersections.setAlpha(255);
		
		Paint paintFillZone = new Paint();
		paintFillZone.setColor(Color.BLUE);
		paintFillZone.setStyle(Paint.Style.FILL);
		paintFillZone.setAlpha(50);
		
		Vector<PointF> points = zone.getPoints();
		Vector<PointF> middles = zone.getMiddles();
		
		if(! points.isEmpty()){
			canvas.drawCircle(points.get(0).x, points.get(0).y, 2, paintLastPoint);
			if(points.size()>1){
				canvas.drawCircle(points.lastElement().x, points.lastElement().y, 2, paintFirstPoint);
				canvas.drawLine(points.get(points.size()-2).x, points.get(points.size()-2).y, points.lastElement().x, points.lastElement().y, paintNormal);
				if(edit){
					canvas.drawCircle(middles.get(0).x, middles.get(0).y, 1, paintNormal);
				}
				if(points.size()>2){
					for(int i=1; i<points.size()-1; i++){
						canvas.drawCircle(points.get(i).x, points.get(i).y, 2, paintNormal);
						canvas.drawLine(points.get(i-1).x, points.get(i-1).y, points.get(i).x, points.get(i).y, paintNormal);
						if(edit){
							canvas.drawCircle(middles.get(i).x, middles.get(i).y, 1, paintNormal);
						}
					}
					if(edit){
						canvas.drawCircle(middles.lastElement().x, middles.lastElement().y, 1, paintFirstPoint);
					}
					canvas.drawLine(points.get(points.size()-1).x, points.get(points.size()-1).y, points.get(0).x, points.get(0).y, paintFirstPoint);
					try{if(selected.x != 0 && selected.y != 0){
						canvas.drawCircle(selected.x, selected.y, 5, paintNormal);
					}}catch(Exception e){}
				}
			}
		}
		try{Log.d("Intersection",intersections.toString());}catch(Exception e){}
		if(! intersections.isEmpty()){
			for(int i=0;i<intersections.size();i=i+2){
				canvas.drawCircle(intersections.get(i).x, intersections.get(i).y, 2, paintIntersections);
				canvas.drawLine(intersections.get(i).x, intersections.get(i).y, intersections.get(i+1).x, intersections.get(i+1).y, paintIntersections);
				canvas.drawCircle(intersections.get(i+1).x, intersections.get(i+1).y, 2, paintIntersections);
			}
		}
		
		// Create a closed path for the polygon
			if(!zones.isEmpty()){
				for(Zone polygon : zones){
					Path polyPath = new Path();
					Vector<PointF> points2 = polygon.getPoints();
					polyPath.moveTo(points2.get(0).x, points2.get(0).y);			
					for(int i=1; i<points2.size(); i++){
						polyPath.lineTo(points2.get(i).x, points2.get(i).y);
					}

					// Draw the polygon
					canvas.drawPath(polyPath, paintFillZone);
				}
			}
	}


	@Override
	public void setAlpha(int arg0) {
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}
}
