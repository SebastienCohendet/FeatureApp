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

package com.ecn.urbapp.utils;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.db.Element;
import com.ecn.urbapp.zones.*;

/**
 * This class is used to draw the zones on the image
 * 
 * @author patrick
 * 
 */
public class DrawImageView extends Drawable {

	/**
	 * Constructor of this class
	 * 
	 * @param zones
	 *            the set of zones the need to be drawn
	 */
	public DrawImageView() {
		super();
	}

	/**
	 * This method is called to draw the zones
	 */
	@Override
	public void draw(Canvas canvas) {

		Paint finishedPaint = new Paint();
		finishedPaint.setColor(Color.RED);
		finishedPaint.setStyle(Paint.Style.STROKE);

		Paint fillPaint = new Paint();
		fillPaint.setColor(Color.RED);
		fillPaint.setStyle(Style.FILL);
		fillPaint.setAlpha(50);

		// For all the zones
		for (int i = 0; i < MainActivity.pixelGeom.size(); i++) {
			// If the zone is not selected, only draw the lines

			Zone zone = new Zone(ConvertGeom.pixelGeomToZone(MainActivity.pixelGeom.get(i)));

			Element el=null;
			for(Element e : MainActivity.element){
				if(e.getPixelGeom_id()==MainActivity.pixelGeom.get(i).getPixelGeomId()){
					el=e;
					break;
				}
			}
			
			if (!MainActivity.pixelGeom.get(i).selected) {
				if(el!=null){
					if (Integer.parseInt(el.getElement_color()) != 0) {
						finishedPaint.setColor(Integer.parseInt(el.getElement_color()));
					}
					else{
						finishedPaint.setColor(Color.RED);
					}
				}
					// Add all the lines of the polygon
					for (int j = 0; j < zone.points
							.size() - 1; j++) {
						canvas.drawLine(
								zone.points.get(j).x,
								zone.points.get(j).y,
								zone.points.get(j + 1).x,
								zone.points.get(j + 1).y,
								finishedPaint);
					}

				// If the zone is selected, draw a filled polygon
			} else {
				// Create a closed path for the polygon
				Path polyPath = new Path();
				polyPath.moveTo(
						zone.points.get(0).x,
						zone.points.get(0).y);
				for (int j = 0; j < zone.points.size(); j++) {
					polyPath.lineTo(
							zone.points.get(j).x,
							zone.points.get(j).y);
				}
				for (int k = 0; k < zone.getPolygon().getNumInteriorRing(); k++) {
					polyPath.close();
					for (int j = 0; j < zone.getPolygon().getInteriorRingN(k).getNumPoints(); j++) {
						polyPath.lineTo(
								(int) zone.getPolygon().getInteriorRingN(k).reverse().getCoordinateN(j).x,
								(int) zone.getPolygon().getInteriorRingN(k).reverse().getCoordinateN(j).y);
					}
				}

				/*if (zone.getColor() != 0) {
					fillPaint.setColor(zone.getColor());
					fillPaint.setAlpha(50);
				}*/
				if (Integer.parseInt(el.getElement_color()) != 0) {
					fillPaint.setColor(Integer.parseInt(el.getElement_color()));
					fillPaint.setAlpha(50);
				}
				else{
					fillPaint.setColor(Color.RED);
					fillPaint.setAlpha(50);
				}
				// Draw the polygon
				canvas.drawPath(polyPath, fillPaint);
				//fillPaint.setColor(Color.RED);
				fillPaint.setAlpha(50);
			}
		}
	}

	//TODO Adddescription for javadoc
	@Override
	public void setAlpha(int arg0) {
	}

	//TODO Adddescription for javadoc
	@Override
	public void setColorFilter(ColorFilter arg0) {
	}

	//TODO Adddescription for javadoc
	@Override
	public int getOpacity() {
		return 0;
	}
}
