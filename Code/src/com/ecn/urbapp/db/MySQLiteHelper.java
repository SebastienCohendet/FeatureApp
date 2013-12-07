package com.ecn.urbapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

		/**
		 * declaration of tables
		 */
	//TODO Adddescription for javadoc
		public static final String TABLE_PROJECT = "Project";
		public static final String TABLE_GPSGEOM = "GpsGeom";
		public static final String TABLE_PIXELGEOM = "PixelGeom";
		public static final String TABLE_PHOTO = "Photo";
		public static final String TABLE_MATERIAL = "Material";
		public static final String TABLE_ELEMENTTYPE = "ElementType";
		public static final String TABLE_COMPOSED = "Composed";
		public static final String TABLE_ELEMENT = "Element";

		
		//creation of tables getters

		//TODO Adddescription for javadoc
		public static String getTableProject() {
			return TABLE_PROJECT;
		}


		//TODO Adddescription for javadoc
		public static String getTablePixelgeom() {
			return TABLE_PIXELGEOM;
		}


		//TODO Adddescription for javadoc
		public static String getTablePhoto() {
			return TABLE_PHOTO;
		}


		//TODO Adddescription for javadoc
		public static String getTableMaterial() {
			return TABLE_MATERIAL;
		}


		//TODO Adddescription for javadoc
		public static String getTableElementtype() {
			return TABLE_ELEMENTTYPE;
		}


		//TODO Adddescription for javadoc
		public static String getTableComposed() {
			return TABLE_COMPOSED;
		}


		//TODO Adddescription for javadoc
		public static String getTableElement() {
			return TABLE_ELEMENT;
		}


		//TODO Adddescription for javadoc
		public static String getTableGpsgeom() {
			return TABLE_GPSGEOM;
		}

		//TODO Adddescription for javadoc
		//creation of columns
		
		public static final String COLUMN_PROJECTID = "project_id";
		public static final String COLUMN_PROJECTNAME = "project_name";
		//PROJECTGPSGEOM refers to GPSGEOM
		
		public static final String COLUMN_GPSGEOMID = "gpsGeom_id";
		public static final String COLUMN_GPSGEOMCOORD = "gpsGeom_the_geom";
		
		public static final String COLUMN_PIXELGEOMID = "pixelGeom_id";
		public static final String COLUMN_PIXELGEOMCOORD = "pixelGeom_the_geom";
		
		public static final String COLUMN_PHOTOID = "photo_id";
		public static final String COLUMN_PHOTODESCRIPTION = "photo_description";
		public static final String COLUMN_PHOTOAUTHOR = "photo_author";
		public static final String COLUMN_PHOTOURL = "photo_url";
		public static final String COLUMN_PHOTOADRESSE = "photo_adresse";
		public static final String COLUMN_PHOTONBRPOINTS = "photo_nbrPoints";
		public static final String COLUMN_PHOTODERNIEREMODIF = "photo_derniereModif";
		//PHOTOGPSGEOM refers to GPSGEOM
		
		public static final String COLUMN_MATERIALID = "material_id";
		public static final String COLUMN_MATERIALNAME = "material_name";
		
		public static final String COLUMN_ELEMENTTYPEID = "elementType_id";
		public static final String COLUMN_ELEMENTTYPENAME = "elementType_name";
		
		public static final String COLUMN_ELEMENTID = "element_id";
		public static final String COLUMN_ELEMENTCOLOR = "element_color";
		
		public static final String DATABASE_NAME = "local3.db";
		public static final int DATABASE_VERSION = 3;

		//TODO Adddescription for javadoc
		// query to create the database
		private static final String 
					DATABASE_CREATE = 		
						"create table "
						+ TABLE_GPSGEOM + " (" 
						+ COLUMN_GPSGEOMID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ COLUMN_GPSGEOMCOORD + " text not null" 
						+"); "
		;
		//TODO Adddescription for javadoc
		private static final String 
				DATABASE_CREATE2 = 	
					"create table "
					+ TABLE_PIXELGEOM + " (" 
					+ COLUMN_PIXELGEOMID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_PIXELGEOMCOORD + " text not null" 
					+"); "
		;
		//TODO Adddescription for javadoc
		private static final String 
				DATABASE_CREATE3 = 	
				    "create table "
					    + TABLE_PROJECT + " (" 
					    + COLUMN_PROJECTID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					    + COLUMN_PROJECTNAME + " text not null, " 
					    + COLUMN_GPSGEOMID + " INTEGER, " 
					    + "FOREIGN KEY("+ COLUMN_GPSGEOMID +") REFERENCES "+TABLE_GPSGEOM+" ("+COLUMN_GPSGEOMID+")"
					    +");"
		;
		//TODO Adddescription for javadoc
		private static final String 
				DATABASE_CREATE4 = 	
				    "create table "
					    + TABLE_PHOTO + " (" 
					    + COLUMN_PHOTOID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					    + COLUMN_PHOTODESCRIPTION + " text not null, " 
					    + COLUMN_PHOTOAUTHOR + " text not null, " 
					    + COLUMN_PHOTOURL + " text not null, " 
					    + COLUMN_PHOTOADRESSE+ " text not null, " 
					    + COLUMN_PHOTONBRPOINTS + " INTEGER, " 
					    + COLUMN_PHOTODERNIEREMODIF + " DATETIME, "
					    + COLUMN_GPSGEOMID + " INTEGER, " 
					    + "FOREIGN KEY("+ COLUMN_GPSGEOMID +") REFERENCES "+TABLE_GPSGEOM+" ("+COLUMN_GPSGEOMID+")"
					    +");"
		;
		private static final String 
				DATABASE_CREATE5 = 	
				    "create table "
					    + TABLE_MATERIAL + " (" 
					    + COLUMN_MATERIALID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					    + COLUMN_MATERIALNAME + " text not null " 
					    +");"
	    ;
		private static final String 
				DATABASE_CREATE6 = 	
				    "create table "
					    + TABLE_ELEMENTTYPE + " (" 
					    + COLUMN_ELEMENTTYPEID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					    + COLUMN_ELEMENTTYPENAME + " text not null " 
					    +");"
		;
		
		private static final String 
				DATABASE_CREATE7 = 	
				    "create table "
					    + TABLE_COMPOSED + " (" 
					    + COLUMN_PROJECTID   + " INTEGER, "
					    + COLUMN_PHOTOID + " INTEGER, " 
					    +" PRIMARY KEY ( "+COLUMN_PROJECTID+", "+COLUMN_PHOTOID+" ),"
					    + " FOREIGN KEY( "+ COLUMN_PROJECTID +" ) REFERENCES "+TABLE_PROJECT+" ( "+COLUMN_PROJECTID+" ),"
					    + " FOREIGN KEY( "+ COLUMN_PHOTOID +" ) REFERENCES "+TABLE_PHOTO+" ( "+COLUMN_PHOTOID+" )"
					    +");"
		;
		
		private static final String 
				DATABASE_CREATE8 = 	
				    "create table "
					    + TABLE_ELEMENT + " (" 
					    + COLUMN_ELEMENTID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					    + COLUMN_PHOTOID + " INTEGER, " 
					    + COLUMN_MATERIALID + " INTEGER, " 
					    + COLUMN_GPSGEOMID + " INTEGER, " 
					    + COLUMN_PIXELGEOMID + " INTEGER, " 
					    + COLUMN_ELEMENTTYPEID + " INTEGER, " 
					    + COLUMN_ELEMENTCOLOR + " text not null, "
					    + "FOREIGN KEY("+ COLUMN_PHOTOID +") REFERENCES "+TABLE_PHOTO+" ("+COLUMN_PHOTOID+"),"
					    + "FOREIGN KEY("+ COLUMN_MATERIALID +") REFERENCES "+TABLE_MATERIAL+" ("+COLUMN_MATERIALID+"),"
					    + "FOREIGN KEY("+ COLUMN_GPSGEOMID +") REFERENCES "+TABLE_GPSGEOM+" ("+COLUMN_GPSGEOMID+"),"
					    + "FOREIGN KEY("+ COLUMN_PIXELGEOMID +") REFERENCES "+TABLE_PIXELGEOM+" ("+COLUMN_PIXELGEOMID+"),"
					    + "FOREIGN KEY("+ COLUMN_ELEMENTTYPEID +") REFERENCES "+TABLE_ELEMENTTYPE+" ("+COLUMN_ELEMENTTYPEID+")"
					    +");"
		;
			     

		//TODO Adddescription for javadoc
		//constructor
		public MySQLiteHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		

		//TODO Adddescription for javadoc
		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(getDatabaseCreate());	
			database.execSQL(getDatabaseCreate2());
			database.execSQL(getDatabaseCreate3());
			database.execSQL(getDatabaseCreate4());
			database.execSQL(getDatabaseCreate5());	
			database.execSQL(getDatabaseCreate6());
			database.execSQL(getDatabaseCreate7());
			database.execSQL(getDatabaseCreate8());
		}


		//TODO Adddescription for javadoc
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(MySQLiteHelper.class.getName(), 
			"Upgrading database from version"+oldVersion+" to "+newVersion+", which will destroy all your data");
			db.execSQL(
					"DROP TABLE IF EXISTS" 
							+ TABLE_PROJECT +", "
							+ TABLE_GPSGEOM +", "
							+ TABLE_PIXELGEOM +", "
							+ TABLE_PHOTO 
							+"; "
					);
			onCreate(db);		
		}


		//TODO Adddescription for javadoc
		public static String getDatabaseCreate() {
			return DATABASE_CREATE;
		}
		public static String getDatabaseCreate2() {
			return DATABASE_CREATE2;
		}
		public static String getDatabaseCreate3() {
			return DATABASE_CREATE3;
		}
		public static String getDatabaseCreate4() {
			return DATABASE_CREATE4;
		}
		public static String getDatabaseCreate5() {
			return DATABASE_CREATE5;
		}
		public static String getDatabaseCreate6() {
			return DATABASE_CREATE6;
		}
		public static String getDatabaseCreate7() {
			return DATABASE_CREATE7;
		}
		public static String getDatabaseCreate8() {
			return DATABASE_CREATE8;
		}
		
}
