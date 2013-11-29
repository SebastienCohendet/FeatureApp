package com.ecn.urbapp.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.ecn.urbapp.activities.MainActivity;

public class Photo extends DataObject  {
	

	
	//Attributes
	//TODO Adddescription for javadoc
	private long photo_id;
	private String photo_description;
	private String photo_author;
	/**
	 * attributes that declare the name of the picture for instance : img1.png
	 */
	private String photo_url;
	private long gpsGeom_id;

	
	
	
	
	//Getters
	//TODO Adddescription for javadoc
	public long getGpsGeom_id() {
		return gpsGeom_id;
	}

	//TODO Adddescription for javadoc
	public void setGpsGeom_id(long gpsGeom_id) {
		this.gpsGeom_id = gpsGeom_id;
	}

	//TODO Adddescription for javadoc
	private String Ext_GpsGeomCoord;

	//TODO Adddescription for javadoc
	public String getExt_GpsGeomCoord() {
		return Ext_GpsGeomCoord;
	}

	//TODO Adddescription for javadoc
	public long getPhoto_id() {
		return photo_id;
	}

	//TODO Adddescription for javadoc
	public String getPhoto_description() {
		return photo_description;
	}

	//TODO Adddescription for javadoc
	public String getPhoto_author() {
		return photo_author;
	}
	
	
	
	
	
	
	
	//Setters
	//TODO Adddescription for javadoc
	public void setExt_GpsGeomCoord(String ext_GpsGeomCoord) {
		Ext_GpsGeomCoord = ext_GpsGeomCoord;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	//TODO Adddescription for javadoc
	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	//TODO Adddescription for javadoc
	public void setPhoto_id(long photo_id) {
		this.photo_id = photo_id;
	}

	//TODO Adddescription for javadoc
	public void setPhoto_description(String photo_description) {
		this.photo_description = photo_description;
	}

	//TODO Adddescription for javadoc
	public void setPhoto_author(String photo_author) {
		this.photo_author = photo_author;
	}



	
	

	//Override methods
	//TODO Adddescription for javadoc
	@Override
	public String toString() {
		return "Photo [photo_id=" + photo_id + ", photo_description="
				+ photo_description + ", photo_author=" + photo_author
				+ ", photo_url=" + photo_url + ", gps_Geom_id=" + gpsGeom_id +"&" + "  position =" + this.Ext_GpsGeomCoord
				+ "]";
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long setId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void saveToLocal(LocalDataSource datasource) {
		if(!this.getRegistredInLocal()){
			Cursor cursor = datasource.getDatabase().rawQuery(GETMAXPHOTOID, null);
			cursor.moveToFirst();
			if(!cursor.isAfterLast()){
				this.setPhoto_id(this.getPhoto_id()+cursor.getLong(0));
			}
		}
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_PHOTOID, this.photo_id);
		values.put(MySQLiteHelper.COLUMN_PHOTOURL, this.photo_url);
		values.put(MySQLiteHelper.COLUMN_PHOTODESCRIPTION,this.photo_description);
		values.put(MySQLiteHelper.COLUMN_PHOTOAUTHOR, this.photo_author);
		values.put(MySQLiteHelper.COLUMN_GPSGEOMID, this.gpsGeom_id);
		MainActivity.datasource.getDatabase().insert(MySQLiteHelper.TABLE_PHOTO, null, values);		
		
	}

	/**
	 * query to get the biggest photo_id from local db
	 * 
	 */
	private static final String
		GETMAXPHOTOID = 
			"SELECT "+MySQLiteHelper.TABLE_PHOTO+"."+MySQLiteHelper.COLUMN_PHOTOID+" FROM "
			+ MySQLiteHelper.TABLE_PHOTO 
			+" ORDER BY "+MySQLiteHelper.TABLE_PHOTO+"."+MySQLiteHelper.COLUMN_PHOTOID
			+" DESC LIMIT 1 ;"
		;


}
