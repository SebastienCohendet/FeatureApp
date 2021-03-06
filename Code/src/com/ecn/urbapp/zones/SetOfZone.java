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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.ecn.urbapp.R;

import android.content.res.Resources;
import android.graphics.Point;

/**
 * This class regroup all the zones linked to a photo
 * 
 * @author patrick
 * 
 */
public class SetOfZone {

	/** List of all the zones */
	protected Vector<Zone> zones;

	/**
	 * Last type chosen by the user for a zone (used to remember the state of
	 * the material choice dialog during a change of screen orientation)
	 */
	public int type;

	/**
	 * Constructor of a new empty SetOfpoints
	 */
	public SetOfZone() {
		zones = new Vector<Zone>();
	}
	/**
	 * Constructor of a copy SetOfpoints
	 */
	public SetOfZone(Vector<Zone> z) {
		zones = z;
	}

	/**
	 * Set the type of all the selected zones
	 * 
	 * @param zone
	 *            the zone
	 * @param type
	 *            the type to set
	 */
	public void setTypeForSelectedZones(String type) {
		for (Zone zone : getAllSelectedZones()) {
			zone.setType(type);
		}
	}

	/**
	 * Set the material of all the selected zones
	 * 
	 * @param zone
	 *            the zone
	 * @param material
	 *            the material to set
	 */
	public void setMaterialForSelectedZones(String material) {
		for (Zone zone : getAllSelectedZones()) {
			zone.setMaterial(material);
		}
	}

	/**
	 * Set the color of all the selected zones
	 * 
	 * @param zone
	 *            the zone
	 * @param color
	 *            the color to set
	 */
	public void setColorForSelectedZones(int color) {
		for (Zone zone : getAllSelectedZones()) {
			zone.setColor(color);
		}
	}

	/**
	 * Return the color of all the selected zones as an int (or 0 when the zones
	 * have not the same color)
	 * 
	 * @return the color as an int
	 */
	public Integer getColorForSelectedZones() {
		Vector<Zone> zones = getAllSelectedZones();
		if (zones != null && !zones.isEmpty()) {
			int color = zones.get(0).getColor();
			for (Zone zone : zones) {
				if (zone.getColor() != color) {
					color = 0;
				}
			}
			return color;
		} else {
			return 0;
		}
	}

	/**
	 * This method add the point to the last unfinished zone
	 * 
	 * @param point
	 *            the point to add
	 * @param accuracy
	 *            the maximum distance (in pixel) from the first point of the
	 *            zone that the user need to touch to finish to zone
	 */
	public void addPoint(Point point, float accuracy) {
		// Get the last zone (there is at least an empty one)
		Zone lastpoints = zones.get(zones.size() - 1);
		// Finish the zone if the touch point is near the first point of the
		// zone (check if there is at least one point before)
		if ((lastpoints.points.size() != 0)
				&& ((Math.abs(lastpoints.points.get(0).x - point.x) < accuracy) && (Math
						.abs(lastpoints.points.get(0).y - point.y) < accuracy))) {
			// Add the first point to complete the polygon
			lastpoints.addPoint(lastpoints.points.get(0));
			lastpoints.finished = true;
		} else {
			// Add the point if the zone is not finished
			lastpoints.addPoint(point);
		}
	}

	/**
	 * Getter for zones
	 * 
	 * @return zones
	 */
	public Vector<Zone> getZones() {
		return this.zones;
	}

	/**
	 * Add an empty zone to the list of zone (called when the user touch the add
	 * zone button)
	 */
	public void addEmpty() {
		this.zones.add(new Zone());
	}

	/**
	 * This method return the position (in the list) of the first zone that
	 * contains the point in parameter and -1 if no zone is appropriate
	 * 
	 * @param point
	 * @return the number of the smallest zone that contains the point and -1
	 *         otherwise
	 */
	public int isInsideZone(Point point) {
		int result = -1;
		for (int i = 0; i < zones.size(); i++) {
			if (zones.get(i).containPoint(point)) {
				if (result == -1) {
					result = i;
				} else if (zones.get(i).area() < zones.get(result)
						.area()) {
					result = i;
				}
			}
		}
		return result;
	}

	/**
	 * Delete the zone in parameter from the SetOfZone
	 * 
	 * @param zone
	 *            the zone to delete
	 */
	public void delete(Zone zone) {
		zones.remove(zone);
	}

	/**
	 * Unselect all the zones
	 */
	public void unselectAll() {
		for (int i = 0; i < zones.size(); i++) {
			zones.get(i).selected = false;
		}
	}

	/**
	 * Select the zone whose number is in parameter (if the number is positive)
	 * and all the linked zones
	 * 
	 * @param zoneNumber
	 *            the number of the zone to select
	 */
	public void select(int zoneNumber) {
		if (zoneNumber >= 0) {
			zones.get(zoneNumber).selected = !zones.get(zoneNumber).selected;
		} else {
			unselectAll();
		}
	}

	/**
	 * Return a vector with all the selected zones
	 * 
	 * @return vector with all the selected zones
	 */
	public Vector<Zone> getAllSelectedZones() {
		Vector<Zone> selectedZonesNumbers = new Vector<Zone>();
		for (int i = 0; i < zones.size(); i++) {
			if (zones.get(i).selected) {
				selectedZonesNumbers.add(zones.get(i));
			}
		}
		return selectedZonesNumbers;
	}

	/**
	 * This method return a HashMap with two keys :  types and materials,
	 * and whose values are HashMaps using the kind of materials (or types) as keys and the
	 * percentage of presence of these materials (or types) along the selected zones as values.
	 * 
	 * @param res
	 */
	public Map<String, HashMap<String, Float>> getStatsForSelectedZones(
			Resources res) {
		Vector<Zone> selectedZones = getAllSelectedZones();
		if (selectedZones.isEmpty()) {
			selectedZones = zones;
		}
		float totalArea = 0f;
		HashMap<String, Float> types = new HashMap<String, Float>();
		HashMap<String, Float> materials = new HashMap<String, Float>();
		for (Zone zone : selectedZones) {
			String type = zone.getTypeToText(res);
			Float currentArea = types.get(type);
			if (currentArea != null) {
				types.put(type, currentArea + zone.area());
			} else {
				types.put(type, zone.area());
			}
			String material = zone.getMaterialToText(res);
			currentArea = materials.get(material);
			if (currentArea != null) {
				materials.put(material, currentArea + zone.area());
			} else {
				materials.put(material, zone.area());
			}
			totalArea += zone.area();
		}
		for (String key : materials.keySet()) {
			materials.put(key, materials.get(key) / totalArea);
		}

		for (String key : types.keySet()) {
			types.put(key, types.get(key) / totalArea);
		}
		HashMap<String, HashMap<String, Float>> summary = new HashMap<String, HashMap<String, Float>>();
		summary.put(res.getString(R.string.type), types);
		summary.put(res.getString(R.string.materials), materials);
		return summary;
	}

}
