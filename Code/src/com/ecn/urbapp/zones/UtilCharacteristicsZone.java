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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.db.Element;
import com.ecn.urbapp.db.ElementType;
import com.ecn.urbapp.db.Material;
import com.ecn.urbapp.db.PixelGeom;
import com.ecn.urbapp.utils.GetId;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

//TODO only dsplay databased material and not those put into ressources

/**
 * This class regroup all the zones linked to a photo
 * 
 * @author patrick, Jules Party
 * 
 */
public final class UtilCharacteristicsZone {

	private static GeometryFactory gf = new GeometryFactory();
	private static WKTReader wktr = new WKTReader(gf);

	/**
	 * Set the type of all the selected zones
	 *
	 * @param type
	 *            the type to set
	 */
	public static void setTypeForSelectedZones(String type) {
		for (Element e : getAllSelectedZones()) {
			if(type==null){
				e.setElementType_id(0);
			}
			else{
				for(ElementType et : MainActivity.elementType){
					if(et.getElementType_name().equals(type)){
						e.setElementType_id(et.getElementType_id());
					}
				}
			}
		}
	}

	/**
	 * Set the material of all the selected zones
	 *
	 * @param material
	 *            the material to set
	 */
	public static void setMaterialForSelectedZones(String material) {
		for (Element e : getAllSelectedZones()) {
			if(material==null){
				e.setMaterial_id(0);
			}
			else{
				for(Material m : MainActivity.material){
					if(m.getMaterial_name().equals(material)){
						e.setMaterial_id(m.getMaterial_id());
					}
				}
			}
		}
	}

	/**
	 * Set the color of all the selected zones
	 *
	 * @param color
	 *            the color to set
	 */
	public static void setColorForSelectedZones(int color) {
		for (Element e : getAllSelectedZones()) {
			e.setElement_color(""+color);
		}
	}

	/**
	 * Return the color of all the selected zones as an int (or 0 when the zones
	 * have not the same color)
	 * 
	 * @return the color as an int
	 */
	public static Integer getColorForSelectedZones() {
		Vector<Element> element = getAllSelectedZones();
		if (element != null && !element.isEmpty()) {
			int color=0;
			if(element.get(0).getElement_color()!=null){
				color = Integer.parseInt(element.get(0).getElement_color());
			}
			for (Element e : element) {
				if(element.get(0).getElement_color()!=null){
					if (Integer.parseInt(e.getElement_color()) != color) {
						color = 0;
					}
				}
			}
			return color;
		} else {
			return 0;
		}
	}

	/**
	 * This method return the position (in the list) of the first zone that
	 * contains the point in parameter and -1 if no zone is appropriate
	 * 
	 * @param point
	 * @return the number of the smallest zone that contains the point and -1
	 *         otherwise
	 */
	public static int isInsideZone(Point point) {
		int result = -1;
		for (int i = 0; i < MainActivity.pixelGeom.size(); i++) {
			Coordinate coord = new Coordinate(point.x, point.y);
			com.vividsolutions.jts.geom.Point geomPoint = gf.createPoint(coord);
			try {
				if (geomPoint.within(gf.createPolygon(gf.createLinearRing(((Polygon) wktr.read(MainActivity.pixelGeom.get(i).getPixelGeom_the_geom())).getExteriorRing().getCoordinates()), null))) {
					if (result == -1) {
						result = i;
					} else if (gf.createPolygon(gf.createLinearRing(((Polygon) wktr.read(MainActivity.pixelGeom.get(i).getPixelGeom_the_geom())).getExteriorRing().getCoordinates()), null).getArea()
							< gf.createPolygon(gf.createLinearRing(((Polygon) wktr.read(MainActivity.pixelGeom.get(result).getPixelGeom_the_geom())).getExteriorRing().getCoordinates()), null).getArea()) {
						result = i;
					}
				}
			} catch (ParseException e) {
			}
		}
		return result;
	}

	/**
	 * Unselect all the zones
	 */
	public static void unselectAll() {
		for (int i = 0; i < MainActivity.pixelGeom.size(); i++) {
			MainActivity.pixelGeom.get(i).selected = false;
		}
	}

	/**
	 * Select the zone whose number is in parameter (if the number is positive)
	 * and all the linked zones
	 * 
	 * @param zoneNumber
	 *            the number of the zone to select
	 */
	public static void select(int zoneNumber) {
		if (zoneNumber >= 0) {
			MainActivity.pixelGeom.get(zoneNumber).selected = !MainActivity.pixelGeom.get(zoneNumber).selected;
		} else {
			unselectAll();
		}
	}

	/**
	 * Return a vector with all the selected zones
	 * 
	 * @return vector with all the selected zones
	 */
	public static Vector<Element> getAllSelectedZones() {
		Vector<Element> selectedElements = new Vector<Element>();
		for(PixelGeom pg: MainActivity.pixelGeom){
			if(pg.selected){
				for(Element e : MainActivity.element){
					if(e.getPixelGeom_id()==pg.getPixelGeomId()){
						selectedElements.add(e);
					}
				}
			}
		}
		return selectedElements;
	}

	/**
	 * This method return a HashMap with two keys :  types and materials,
	 * and whose values are HashMaps using the kind of materials (or types) as keys and the
	 * percentage of presence of these materials (or types) along the selected zones as values.
	 * 
	 * @param res
	 */
	public static Map<String, HashMap<String, Float>> getStatsForSelectedZones(Resources res) {
		Vector<Element> selectedZones = getAllSelectedZones();
		if (selectedZones.isEmpty()) {
			selectedZones = new Vector<Element>(MainActivity.element);
		}
		float totalArea = 0f;
		HashMap<String, Float> types = new HashMap<String, Float>();
		HashMap<String, Float> materials = new HashMap<String, Float>();
		for (Element e : selectedZones) {
			PixelGeom pg = new PixelGeom();;
			for(PixelGeom g : MainActivity.pixelGeom){
				if(g.getPixelGeomId()==e.getPixelGeom_id()){
					pg = g;
				}
			}
			float pgArea;
			try {
				pgArea = (float) wktr.read(pg.getPixelGeom_the_geom()).getArea();
			} catch (ParseException e1) {
				pgArea = 0;
			}
			String type="";
			for(ElementType et : MainActivity.elementType){
				if(et.getElementType_id()==e.getElementType_id()){
					type=et.getElementType_name();
				}
			}
			Float currentArea = types.get(type);
			if (currentArea != null) {
				types.put(type, currentArea + pgArea);
			} else {
				types.put(type, pgArea);
			}
			String material="";
			for(Material  m : MainActivity.material){
				if(m.getMaterial_id()==e.getMaterial_id()){
					material=m.getMaterial_name();
				}
			}
			currentArea = materials.get(material);
			if (currentArea != null) {
				materials.put(material, currentArea + pgArea);
			} else {
				materials.put(material, pgArea);
			}
			totalArea += pgArea;
			
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

	/**
	 * This method add the PixelGeom in parameter to the list of PixelGeom from
	 * MainActivity. If the PixelGeom intersects with existing PixelGeoms, the
	 * method calculates the intersections between those PixelGeoms and add them
	 * so that no PixelGeom covers another one. The method also add the Element
	 * linked to the PixelGeom. This element will have the same characteristics
	 * that the Element in parameter, unless the PixelGeom intersects an
	 * existing zones already characterized.
	 * 
	 * @param pixelGeom
	 *            the PixelGeom to add
	 * @param ref
	 *            the Element defining the characteristics of the PixelGeom
	 * @throws TopologyException
	 *             when the JTS library does not manage to compute the
	 *             intersection
	 * @throws ParseException
	 *             when a PixelGeom cannot be interpreted into a Geometry
	 */
	public static void addInMainActivityZones(PixelGeom pixelGeom, Element ref)
			throws TopologyException, ParseException {
		List<Long> pixelGeomIdToRemove = new ArrayList<Long>();
		List<PixelGeom> pixelGeomToAdd = new ArrayList<PixelGeom>();
		Map<PixelGeom, Element> linkedElements = new HashMap<PixelGeom, Element>();
		Geometry geom = null;
		for (PixelGeom oldPixelGeom : MainActivity.pixelGeom) {
			if (wktr.read(pixelGeom.getPixelGeom_the_geom()).contains(
					wktr.read(oldPixelGeom.getPixelGeom_the_geom()))) {
				if (geom == null) {
					geom = gf.createGeometry(wktr.read(
							oldPixelGeom.getPixelGeom_the_geom()));
				} else {
					geom = geom.union(wktr.read(
							oldPixelGeom.getPixelGeom_the_geom()));
				}
			}
		}
		if (geom != null) {
			if (geom instanceof GeometryCollection) {
				GeometryCollection geomColl = (GeometryCollection) geom;
				for (int i = 0; i < geomColl.getNumGeometries(); i++) {
					if (geomColl.getGeometryN(i) instanceof Polygon) {
						PixelGeom pg = new PixelGeom();
						Polygon poly = intPolygon((Polygon) geomColl.getGeometryN(i));
						poly = intPolygon(poly);
						pg.setPixelGeom_the_geom(poly.toText());
						pixelGeom = createHole(pixelGeom, pg);
					}
				}
			} else if (geom instanceof Polygon) {
				PixelGeom pg = new PixelGeom();
				Polygon poly = intPolygon((Polygon) geom);
				pg.setPixelGeom_the_geom(poly.toText());
				pixelGeom = createHole(pixelGeom, pg);
			}
		}
		for (PixelGeom oldPixelGeom : MainActivity.pixelGeom) {
			if (wktr.read(pixelGeom.getPixelGeom_the_geom()).within(wktr.read(oldPixelGeom.getPixelGeom_the_geom()))) {
				pixelGeomIdToRemove.add(oldPixelGeom.getPixelGeomId());
				oldPixelGeom = createHole(oldPixelGeom, pixelGeom);
				pixelGeomToAdd.add(oldPixelGeom);
				pixelGeomToAdd.add(pixelGeom);
				break;
			} else if (wktr.read(pixelGeom.getPixelGeom_the_geom()).intersects(wktr.read(oldPixelGeom.getPixelGeom_the_geom()))
					&& !wktr.read(pixelGeom.getPixelGeom_the_geom()).touches(wktr.read(oldPixelGeom.getPixelGeom_the_geom()))) {
				pixelGeomIdToRemove.add(oldPixelGeom.getPixelGeomId());
				Element elt = null;
				for (Element element : MainActivity.element) {
					if (element.getPixelGeom_id() == oldPixelGeom.getPixelGeomId()) {
						//elt = element;
						elt = new Element();
						elt.setElement_color(element.getElement_color());
						elt.setElementType_id(element.getElementType_id());
						elt.setMaterial_id(element.getMaterial_id());
					}
				}
				geom = wktr.read(pixelGeom.getPixelGeom_the_geom()).intersection(wktr.read(oldPixelGeom.getPixelGeom_the_geom()));
				for (PixelGeom pg : getPixelGeomsFromGeom(geom)) {
					linkedElements.put(pg, elt);
					pixelGeomToAdd.add(pg);
				}
				geom = wktr.read(pixelGeom.getPixelGeom_the_geom()).difference(wktr.read(oldPixelGeom.getPixelGeom_the_geom()));
				for (PixelGeom pg : getPixelGeomsFromGeom(geom)) {
					linkedElements.put(pg, ref);
					pixelGeomToAdd.add(pg);
				}
				geom = wktr.read(oldPixelGeom.getPixelGeom_the_geom()).difference(wktr.read(pixelGeom.getPixelGeom_the_geom()));
				for (PixelGeom pg : getPixelGeomsFromGeom(geom)) {
					linkedElements.put(pg, elt);
					pixelGeomToAdd.add(pg);
				}
				break;
			}
		}
		if (pixelGeomToAdd.isEmpty()) {
			addPixelGeom(pixelGeom, ref);
		} else {
			for (Long pgeomId : pixelGeomIdToRemove) {
				Element elt = null;
				for (Element element : MainActivity.element) {
					if (element.getPixelGeom_id() == pgeomId) {
						elt = element;
					}
				}
				PixelGeom pgeom = null;
				for (PixelGeom pg : MainActivity.pixelGeom) {
					if (pg.getPixelGeomId() == pgeomId) {
						pgeom = pg;
					}
				}
				MainActivity.pixelGeom.remove(pgeom);
				MainActivity.element.remove(elt);
			}
			for (PixelGeom pg : pixelGeomToAdd) {
				addInMainActivityZones(pg, linkedElements.get(pg));
			}
		}
	}
	
	/**
	 * This method add the PixelGeom in parameter to the list of PixelGeom from
	 * MainActivity. The method also add the Element linked to the PixelGeom.
	 * This element will have the same characteristics that the Element in
	 * parameter. This method should be used only if the PixelGeom does not
	 * intersect any existing PixelGeom.
	 * 
	 * @param pixelGeom
	 *            the PixelGeom to add
	 * @param ref
	 *            the Element defining the characteristics of the PixelGeom
	 */
	private static void addPixelGeom(PixelGeom pgeom, Element elt) {
		pgeom.setPixelGeomId(GetId.PixelGeom());
		Element element = new Element();
		element.setElement_id(GetId.Element());
		element.setPhoto_id(MainActivity.photo.getPhoto_id());
		element.setPixelGeom_id(pgeom.getPixelGeomId());
		element.setGpsGeom_id(MainActivity.photo.getGpsGeom_id());
		if (elt == null) {
			element.setElement_color("" + Color.RED);
		} else {
			element.setElement_color(elt.getElement_color());
			element.setElementType_id(elt.getElementType_id());
			element.setMaterial_id(elt.getMaterial_id());
		}
		MainActivity.element.add(element);
		MainActivity.pixelGeom.add(pgeom);
	}

	/**
	 * Transform the Geometry in parameter into a list of the Polygons composing
	 * the Geometry, possible LineString or Point are ignored. The Polygons are
	 * then converted into PixelGeoms and returned.
	 * 
	 * @param geom
	 *            the Geometry to convert into PixelGeom(s)
	 * @return the equivalent PixelGeom 
	 */
	private static List<PixelGeom> getPixelGeomsFromGeom(Geometry geom) {
		List<PixelGeom> result = new ArrayList<PixelGeom>();
		if (geom instanceof GeometryCollection) {
			GeometryCollection geomColl = (GeometryCollection) geom;
			for (int i = 0; i < geomColl.getNumGeometries(); i++) {
				result.addAll(getPixelGeomsFromGeom(geomColl.getGeometryN(i)));
			}
		} else if (geom instanceof Polygon) {
			PixelGeom pg = new PixelGeom();
			Polygon poly = intPolygon((Polygon) geom);
			pg.setPixelGeom_the_geom(poly.toText());
			result.add(pg);
		}
		return result;
	}

	 private static Polygon intPolygon(Polygon geom) {
		Coordinate[] coords = geom.getExteriorRing().getCoordinates();
		int dim = coords.length;
		for (int i = 0; i < dim; i++) {
			coords[i] = new Coordinate((int) coords[i].x, (int) coords[i].y);
		}
		LinearRing shell = gf.createLinearRing(coords);
		LinearRing[] holes = new LinearRing[geom.getNumInteriorRing()];
		for (int j = 0; j < geom.getNumInteriorRing(); j++) {
			coords = geom.getInteriorRingN(j).getCoordinates();
			dim = coords.length;
			for (int i = 0; i < dim; i++) {
				coords[i] = new Coordinate((int) coords[i].x, (int) coords[i].y);
			}
			holes[j] = gf.createLinearRing(coords);
		}
		return gf.createPolygon(shell, holes);
	}

	/**
	 * Return the PixelGeom pgeomShell in parameter with a new hole defined by
	 * the PixelGeom pgeomHole in parameter. The method does not checked that
	 * the hole can be added.
	 * 
	 * @param pgeomShell
	 *            the PixelGeom to pierce
	 * @param pgeomHole
	 *            define the edge of the hole
	 * @return the pierced PixelGeom
	 * @throws ParseException
	 *             if a PixelGeom cannot be interpreted into a Geometry
	 */
	public static PixelGeom createHole(PixelGeom pgeomShell, PixelGeom pgeomHole)
			throws ParseException {
		Polygon polyShell = (Polygon) wktr.read(pgeomShell.getPixelGeom_the_geom());
		Polygon polyHole = (Polygon) wktr.read(pgeomHole.getPixelGeom_the_geom());
		LinearRing shell = gf.createLinearRing(polyShell.getExteriorRing()
				.getCoordinates());
		int nbrHoles = polyShell.getNumInteriorRing();
		LinearRing[] holes = new LinearRing[nbrHoles + 1];
		for (int i = 0; i < nbrHoles; i++) {
			holes[i] = gf.createLinearRing(polyShell.getInteriorRingN(i).getCoordinates());
		}
		Coordinate[] coordinates = polyHole.getExteriorRing().getCoordinates();
		LinearRing lr = gf.createLinearRing(coordinates);
		if ((coordinates[0].x - coordinates[1].x) * (coordinates[2].y - coordinates[0].y)
				-  (coordinates[0].y - coordinates[1].y) * (coordinates[2].x - coordinates[0].x) > 0) {
			lr = gf.createLinearRing(lr.reverse().getCoordinates());
		}
		holes[nbrHoles] = lr;
		PixelGeom result = new PixelGeom();
		Polygon poly = intPolygon(gf.createPolygon(shell, holes));
		result.setPixelGeom_the_geom(poly.toText());
		return result;
	}
}
