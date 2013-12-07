package com.ecn.urbapp.db;

import java.util.ArrayList;
import java.util.List;

import com.ecn.urbapp.activities.MainActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalDataSource {
	
	//Database fields
	//TODO Adddescription for javadoc
	private SQLiteDatabase database;
	
	public SQLiteDatabase getDatabase() {
		return database;
	}


	//TODO Adddescription for javadoc
	private MySQLiteHelper dbHelper;

	//TODO Adddescription for javadoc
	public MySQLiteHelper getDbHelper() {
		return dbHelper;
	}


	//TODO Adddescription for javadoc
	private String[] allColumnsProject = {MySQLiteHelper.COLUMN_PROJECTID, MySQLiteHelper.COLUMN_PROJECTNAME, MySQLiteHelper.COLUMN_GPSGEOMID};
	private String[] allColumnsPhoto = {MySQLiteHelper.COLUMN_PHOTOID, MySQLiteHelper.COLUMN_PHOTODESCRIPTION, MySQLiteHelper.COLUMN_PHOTOAUTHOR, MySQLiteHelper.COLUMN_PHOTOURL, MySQLiteHelper.COLUMN_GPSGEOMID};
	private String[] allColumnsGpsGeom = {MySQLiteHelper.COLUMN_GPSGEOMID, MySQLiteHelper.COLUMN_GPSGEOMCOORD};
	private String[] allColumnsPixelGeom = {MySQLiteHelper.COLUMN_PIXELGEOMID, MySQLiteHelper.COLUMN_PIXELGEOMCOORD};
	private String[] allColumnsMaterial = {MySQLiteHelper.COLUMN_MATERIALID, MySQLiteHelper.COLUMN_MATERIALNAME};
	private String[] allColumnsElementType = {MySQLiteHelper.COLUMN_ELEMENTTYPEID, MySQLiteHelper.COLUMN_ELEMENTTYPENAME};
	private String[] allColumnsComposed = {MySQLiteHelper.COLUMN_PROJECTID, MySQLiteHelper.COLUMN_PHOTOID};
	private String[] allColumnsElement = {MySQLiteHelper.COLUMN_ELEMENTID, MySQLiteHelper.COLUMN_PHOTOID,MySQLiteHelper.COLUMN_MATERIALID, MySQLiteHelper.COLUMN_GPSGEOMID,MySQLiteHelper.COLUMN_PIXELGEOMID, MySQLiteHelper.COLUMN_ELEMENTTYPEID, MySQLiteHelper.COLUMN_ELEMENTCOLOR};
	
	
	//constructor
	public LocalDataSource(Context context){
		dbHelper = new MySQLiteHelper(context);
	}

	/**
	 * Open and close database
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	//TODO Adddescription for javadoc
	public void close(){
		dbHelper.close();
	}

	//TODO Adddescription for javadoc
	/**
	 * creating a new project in the database
	 * @param str
	 * @return
	 */
	public Project createProject (String str){
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_PROJECTNAME, str);
		long insertId = database.insert(MySQLiteHelper.TABLE_PROJECT, null, values);
		//TODO check the utily of autoincrement
		Cursor cursor = 
				database.query(
						MySQLiteHelper.TABLE_PROJECT,
						allColumnsProject,
						MySQLiteHelper.COLUMN_PROJECTID+" = "+insertId,
						null, null, null, null);
		cursor.moveToFirst();
		Project newProject = cursorToProject(cursor);//method at the end of the class
		cursor.close();
		return newProject;
	}
	


	//TODO Adddescription for javadoc
	//surcharge
	public Project createProject (long id, String str){
        Boolean exist = existProjectWithId(id);
        
        if(exist == true){
        	Project existProject = getProjectWithId(id);
        	Project updatedProject = updateProject(id, existProject, str);
            return updatedProject;
        }
        else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_PROJECTID, id);
            values.put(MySQLiteHelper.COLUMN_PROJECTNAME, str);
            long insertId = database.insert(MySQLiteHelper.TABLE_PROJECT, null,
                    values);
            Cursor cursor = database.query(MySQLiteHelper.TABLE_PROJECT,
                    allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Project p2 = cursorToProject(cursor);
            cursor.close();
            return p2;
        }
    }

	//TODO Adddescription for javadoc
	public Project updateProject(Long id, Project project, String descr){
        ContentValues values = new ContentValues();
 
        values.put(MySQLiteHelper.COLUMN_PROJECTNAME, descr);
 
        database.update(MySQLiteHelper.TABLE_PROJECT, values, MySQLiteHelper.COLUMN_PROJECTID + " = " +project.getProjectId(), null);
 
        return getProjectWithId(project.getProjectId());
    }

	//TODO Adddescription for javadoc
    public Project getProjectWithId(Long id){
        Cursor c = database.query(MySQLiteHelper.TABLE_PROJECT, allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = \"" + id +"\"", null, null, null, null);
        c.moveToFirst();
        Project p1 = cursorToProject(c);
        c.close();
        return p1;
    }

	//TODO Adddescription for javadoc
    public Boolean existProjectWithId(Long id){
        Cursor c = database.query(MySQLiteHelper.TABLE_PROJECT, allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = \"" + id +"\"", null, null, null, null);
        if(c.getCount()>0){
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }
	
	

	//TODO Adddescription for javadoc
	public void deleteProject(Project p1){
		long id = p1.getProjectId();
		System.out.println("Project deleted with id: "+ id);
		database.delete(MySQLiteHelper.TABLE_PROJECT, MySQLiteHelper.COLUMN_PROJECTID+" = "+ id, null);
	}
	
	/**
	 * query to get project information
	 * 
	 */
	private static final String
		GETALLPROJECTS = 
			"SELECT * FROM "
			+ MySQLiteHelper.TABLE_PROJECT 
			+ " INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM 
			+" ON "+MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_GPSGEOMID+"="+MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMID
			+";"
		;
	
	
	

	/**
	 * query to get project information
	 * 
	 */
	private static final String
		GETALLGPSGEOM = 
		"SELECT * FROM "
		+ MySQLiteHelper.TABLE_GPSGEOM 
		+";"
		;
	
	
	/**
	 * query to get photo informations
	 * 
	 */
	private static final String
		GETALLPHOTOS = 
			"SELECT * FROM "
			+ MySQLiteHelper.TABLE_PHOTO 
			+ " INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM 
			+" ON "+MySQLiteHelper.TABLE_PHOTO+"."+MySQLiteHelper.COLUMN_GPSGEOMID+"="+MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMID
			+";"
		;

	/**
	 * query to get the biggest photo_id from local db
	 * 
	 */
	private static final String
		GETMAXPHOTOID = 
			"SELECT "+MySQLiteHelper.TABLE_PHOTO+"."+MySQLiteHelper.COLUMN_PHOTOID+" FROM "
			+ MySQLiteHelper.TABLE_PHOTO 
			+" ORDER BY DESC LIMIT 1 ;"
		;
	
	
	//TODO Adddescription for javadoc
	private static final String
	GETPHOTOLINK = 
		"SELECT "+MySQLiteHelper.TABLE_PHOTO+".*, "+MySQLiteHelper.TABLE_GPSGEOM+".* FROM ("
		+ MySQLiteHelper.TABLE_PHOTO 
		+ " INNER JOIN " + MySQLiteHelper.TABLE_COMPOSED
		+" ON "+MySQLiteHelper.TABLE_PHOTO+"."+MySQLiteHelper.COLUMN_PHOTOID+"="+MySQLiteHelper.TABLE_COMPOSED+"."+MySQLiteHelper.COLUMN_PHOTOID
		+ ") INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM 
		+" ON "+MySQLiteHelper.TABLE_PHOTO+"."+MySQLiteHelper.COLUMN_GPSGEOMID+"="+MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMID
		+" WHERE "+MySQLiteHelper.TABLE_COMPOSED+"."+MySQLiteHelper.COLUMN_PROJECTID+" = "
		//need to add the project id and the ";" in the method argument
	;
	


	
	
	/**
	 * execution of the query
	 * @return
	 */
	public List<Project> getAllProjects(){
		List<Project> projectsList = new ArrayList<Project>();
		
		Cursor cursor = database.rawQuery(GETALLPROJECTS,null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Project p1 = cursorToProject(cursor);
			projectsList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		return projectsList;
	}

	
	/**
	 * execution of the query
	 * @return
	 */
	public List<GpsGeom> getAllGpsGeom(){
		List<GpsGeom> gpsGeomList = new ArrayList<GpsGeom>();
		
		Cursor cursor = database.rawQuery(GETALLGPSGEOM,null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			GpsGeom p1 = cursorToGpsGeom(cursor);
			gpsGeomList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		return gpsGeomList;
	}
	
	


	//TODO Adddescription for javadoc
	private Project cursorToProject(Cursor cursor) {
	    Project p1 = new Project();
	    p1.setProjectId(cursor.getLong(0));
	    p1.setProjectName(cursor.getString(1));
	    p1.setGpsGeom_id(cursor.getLong(2)); 
	    //TODO créer 2 fonctions, une pour l'instanciation du projet, une pour la recopie des gpsgeom
	    try{
	    	p1.setExt_GpsGeomCoord(cursor.getString(4));
	    }
	    catch (Exception e){};
	    return p1;
	}
  
  // PHOTO METHODS
	
	

	//TODO Adddescription for javadoc
	public List<Photo> getAllPhotos(){
		List<Photo> photosList = new ArrayList<Photo>();
		
		Cursor cursor = database.rawQuery(GETALLPHOTOS,null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Photo p1 = cursorToPhoto(cursor);
			photosList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		return photosList;
	}

	
	//TODO Adddescription for javadoc
	//TODO unused in local way
	public void getmaxPhotoId(){
		long maxPhotoId = 0;
		
		Cursor cursor = database.rawQuery(GETMAXPHOTOID,null);
		
		cursor.moveToFirst();
		maxPhotoId=cursor.getLong(0);
		cursor.close();
		//MainActivity.maxPhotoIdLocal=maxPhotoId;
	}
	
	
	//TODO Adddescription for javadoc
	public List<Photo> getAllPhotolinkedtoProject(long project_id){
		List<Photo> photosList = new ArrayList<Photo>();
		
		Cursor cursor = database.rawQuery(GETPHOTOLINK+project_id+";",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Photo p1 = cursorToPhoto(cursor);
			photosList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		return photosList;
	}
	

	//TODO Adddescription for javadoc
	public void deletePhoto(Photo p1){
		long id = p1.getPhoto_id();
		System.out.println("Photo deleted with id: "+ id);
		database.delete(MySQLiteHelper.TABLE_PHOTO, MySQLiteHelper.COLUMN_PHOTOID+" = "+ id, null);
	}

	//TODO Adddescription for javadoc
	public Photo createPhoto (String descr, String author, String url){
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_PHOTODESCRIPTION, descr);
		values.put(MySQLiteHelper.COLUMN_PHOTOAUTHOR, author);
		values.put(MySQLiteHelper.COLUMN_PHOTOURL, url);
		long insertId = database.insert(MySQLiteHelper.TABLE_PHOTO, null, values);
		//TODO check the utily of autoincrement
		Cursor cursor = 
				database.query(
						MySQLiteHelper.TABLE_PHOTO,
						allColumnsPhoto,
						MySQLiteHelper.COLUMN_PHOTOID+" = "+insertId,
						null, null, null, null);
		cursor.moveToFirst();
		Photo newPhoto = cursorToPhoto(cursor);//method at the end of the class
		cursor.close();
		return newPhoto;
	}

	//TODO Adddescription for javadoc
	private Photo cursorToPhoto(Cursor cursor) {
	    Photo p1 = new Photo();
	    p1.setPhoto_id(cursor.getLong(0));
	    p1.setPhoto_description(cursor.getString(1));
	    p1.setPhoto_author(cursor.getString(2)); 
	    p1.setPhoto_url(cursor.getString(3)); 
	    p1.setGpsGeom_id(cursor.getLong(4)); 
	    //TODO créer 2 fonctions, une pour l'instanciation du projet, une pour la recopie des gpsgeom
	    try{
	    	p1.setExt_GpsGeomCoord(cursor.getString(6));
	    }
	    catch (Exception e){};
	    return p1;
		
	}
  
	public Photo  getPhotoWithID(long id)
	{
		Cursor c = database.query(MySQLiteHelper.TABLE_PHOTO, allColumnsPhoto, MySQLiteHelper.COLUMN_PHOTOID + " = \"" + id +"\"", null, null, null, null);
		c.moveToFirst();
        Photo p1 = cursorToPhoto(c);
        c.close();
        return p1;
	}
  
  // GPS GEOM METHODS

	//TODO Adddescription for javadoc
	/**
	 * create a GPSGeom in the database and update the photo tuple where photo_id = id with this gpsgeom_id
	 * @param str
	 * @param id
	 * @return
	 */
	public GpsGeom createGPSGeomToPhoto (String str, long id){
		GpsGeom gps1 = createGPSGeom(str);
		//TODO TRANSACTION
		ContentValues args = new ContentValues();
		args.put(MySQLiteHelper.COLUMN_GPSGEOMID, gps1.getGpsGeomsId());
		int d = database.update(MySQLiteHelper.TABLE_PHOTO, args, MySQLiteHelper.COLUMN_PHOTOID +"=" + id, null);
		return gps1;
	}
	
	public GpsGeom getGpsGeomWithID(long id)
	{
		Cursor c = database.query(MySQLiteHelper.TABLE_GPSGEOM, allColumnsGpsGeom, MySQLiteHelper.COLUMN_GPSGEOMID + " = \"" + id +"\"", null, null, null, null);
		c.moveToFirst();
        GpsGeom g1 = cursorToGpsGeom(c);
        c.close();
        return g1;
	}


	//TODO Adddescription for javadoc
	/**
	 * create a GPSGeom in the database and update the project tuple where project_id = id with this gpsgeom_id
	 * @param str
	 * @return
	 */
	public GpsGeom createGPSGeomToProject (String str, long id){
		GpsGeom gps1 = createGPSGeom(str);
		//TODO TRANSACTION
		ContentValues args = new ContentValues();
		args.put(MySQLiteHelper.COLUMN_GPSGEOMID, gps1.getGpsGeomsId());
		int d = database.update(MySQLiteHelper.TABLE_PROJECT, args, MySQLiteHelper.COLUMN_PROJECTID +"=" + id, null);
		
		return gps1;
	}

	//TODO Adddescription for javadoc
	/**
	 * create a GPSGeom with the gpsgeom_coord str
	 * @param str
	 * @return
	 */
	public GpsGeom createGPSGeom (String str){
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_GPSGEOMCOORD, str);
		long insertId = database.insert(MySQLiteHelper.TABLE_GPSGEOM, null, values);
		//TODO check the utily of autoincrement
		Cursor cursor = database.query(
			MySQLiteHelper.TABLE_GPSGEOM,
			allColumnsGpsGeom,
			MySQLiteHelper.COLUMN_GPSGEOMID+" = "+insertId,
			null, null, null, null);
		cursor.moveToFirst();
		GpsGeom newGpsGeom = cursorToGpsGeom(cursor);//method at the end of the class
		cursor.close();
		return newGpsGeom;
	}

	//TODO Adddescription for javadoc
	/**
	 * convert the cursor to the object gpsGeom
	 * @param cursor
	 * @return GpsGeom
	 */
	private GpsGeom cursorToGpsGeom(Cursor cursor) {
	    GpsGeom p1 = new GpsGeom();
	    p1.setGpsGeomId(cursor.getLong(0));
	    p1.setGpsGeomCoord(cursor.getString(1));
	    return p1;
	}

	//TODO Adddescription for javadoc
	// methods related to Composed.java that represents the link between Photos and projects
	public Composed createLink (long proj_id, long photo_id){
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_PROJECTID, proj_id);
		values.put(MySQLiteHelper.COLUMN_PHOTOID, photo_id);
		database.insert(MySQLiteHelper.TABLE_COMPOSED, null, values);
		//TODO check the utily of autoincrement
		Cursor cursor = database.query(
			MySQLiteHelper.TABLE_COMPOSED,
			allColumnsComposed,
			MySQLiteHelper.COLUMN_PROJECTID+" = "+proj_id +" AND "+MySQLiteHelper.COLUMN_PHOTOID+" = "+photo_id,
			null, null, null, null);
		cursor.moveToFirst();
		Composed link1 = cursorToComposed(cursor);//method at the end of the class
		cursor.close();
		return link1;
	}

	//TODO Adddescription for javadoc
	private Composed cursorToComposed(Cursor cursor) {
	    Composed link1 = new Composed();
	    link1.setProject_id(cursor.getLong(0));
	    link1.setPhoto_id(cursor.getLong(1));
	    return link1;
	}

	// OTHER METHODS TO GET LOCAL ITEMS FROM ID. ADDED FOR USE IN THE SYNC CLASS
	
	public Element getElementWithID(long id)
	{
		Cursor c = database.query(MySQLiteHelper.TABLE_ELEMENT, allColumnsElement, 
				MySQLiteHelper.COLUMN_ELEMENTID + " = \"" + id +"\"", null, null, null, null);
		c.moveToFirst();
        Element e = cursorToElement(c);
        c.close();
        return e;
	}
	
	public Element cursorToElement(Cursor cursor)
	{
		Element e = new Element();
		e.setElement_id(cursor.getLong(0));
		e.setPhoto_id(cursor.getLong(1));
		e.setMaterial_id(cursor.getLong(2));
		e.setElementType_id(cursor.getLong(3));
		e.setPixelGeom_id(cursor.getLong(4));
		e.setGpsGeom_id(cursor.getLong(5));
		e.setElement_color(cursor.getString(6));
	    return e;
	}
	
	public PixelGeom getPixelGeomWithID(long id)
	{
		Cursor c = database.query(MySQLiteHelper.TABLE_PIXELGEOM, allColumnsPixelGeom, 
				MySQLiteHelper.COLUMN_PIXELGEOMID + " = \"" + id + "\"", null, null, null, null);
		c.moveToFirst();
		PixelGeom p = cursorToPixelGeom(c);
		c.close();
		return p;
	}
	
	public PixelGeom cursorToPixelGeom(Cursor cursor)
	{
		PixelGeom p = new PixelGeom();
		p.setPixelGeomId(cursor.getLong(0));
		p.setPixelGeom_the_geom(cursor.getString(1));
		return p;
	}
	
	public Material getMaterialWithID(long id)
	{
		Cursor c = database.query(MySQLiteHelper.TABLE_MATERIAL, allColumnsMaterial,
				MySQLiteHelper.COLUMN_MATERIALID + " = \"" + id + "\"", null, null, null, null);
		c.moveToFirst();
		Material m = cursorToMaterial(c);
		c.close();
		return m;
	}
	
	public Material cursorToMaterial(Cursor cursor)
	{
		Material m = new Material();
		m.setMaterial_id(cursor.getLong(0));
		m.setMaterial_name(cursor.getString(1));
		return m;
	}
	
	public ElementType getElementTypeWithID(long id)
	{
		Cursor c = database.query(MySQLiteHelper.TABLE_ELEMENTTYPE, allColumnsElementType, 
				MySQLiteHelper.COLUMN_ELEMENTTYPEID + " = \"" + id + "\"", null, null, null, null);
		c.moveToFirst();
		ElementType e = cursorToElementType(c);
		c.close();
		return e;
	}
	
	public ElementType cursorToElementType(Cursor cursor)
	{
		ElementType e = new ElementType();
		e.setElementType_id(cursor.getLong(0));
		e.setElementType_name(cursor.getString(1));
		return e;
	}


	
	
	//METHODS FOR ELMENTS TYPE
	
	//Create ellementType in the database
	//TODO sync with the external database
	/**
	 * method that register a new type in the DB
	 */
	public void createElementTypeInDB(String str){
		boolean flag = true;
		Cursor cursor = database.rawQuery(GETALLELEMENTTYPEID,null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			if(cursor.getString(1).equals(str)){
				flag=false;
			}
			cursor.moveToNext();
		}
		if(flag){
			ContentValues values = new ContentValues(); 
			values.put(MySQLiteHelper.COLUMN_ELEMENTTYPENAME, str);
			long insertId = database.insert(MySQLiteHelper.TABLE_ELEMENTTYPE, null, values);
		}
	}
	
	
	
	
	/**
	 * sql query that counts the number of element type
	 */
	private static final String
	GETALLELEMENTTYPEID = 
		"SELECT * FROM "
		+ MySQLiteHelper.TABLE_ELEMENTTYPE 
		+";"
	;
	
	
	public void getAllElementType(){
		List<ElementType> elementTypeList = new ArrayList<ElementType>();
		
		Cursor cursor = database.rawQuery(GETALLELEMENTTYPEID,null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			ElementType p1 = cursorToElementType(cursor);
			elementTypeList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.elementType=(ArrayList<ElementType>) elementTypeList;
		Log.w("neuneu","neuneu");
		
	}
	
	//TODO method that create the 3 elements in the db
	
		
	
	//METHODS FOR MATERIALS
	
	//Create ellementType in the database
	//TODO sync with the external database
	/**
	 * method that register a new type in the DB
	 */
	public void createMaterialInDB(String str){
		boolean flag = true;
		Cursor cursor = database.rawQuery(GETALLMATERIALID,null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			if(cursor.getString(1).equals(str)){
				flag=false;
			}
			cursor.moveToNext();
		}
		if(flag){
			ContentValues values = new ContentValues(); 
			values.put(MySQLiteHelper.COLUMN_MATERIALNAME, str);
			long insertId = database.insert(MySQLiteHelper.TABLE_MATERIAL, null, values);
		}
	}
	
	
	
	
	/**
	 * sql query that counts the number of element type
	 */
	private static final String
	GETALLMATERIALID = 
		"SELECT * FROM "
		+ MySQLiteHelper.TABLE_MATERIAL 
		+";"
	;
	
	
	public void getAllMaterial(){
		List<Material> materialList = new ArrayList<Material>();
		
		Cursor cursor = database.rawQuery(GETALLMATERIALID,null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Material p1 = cursorToMaterial(cursor);
			materialList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.material=(ArrayList<Material>) materialList;
		Log.w("neuneu","neuneu");
		
	}
	
	//TODO method that create the 3 elements in the db
	

	
	
	
	
	/**
	 * get information from datasource.database to public static fields from main activity once a local project is loaded
	 */
	
	/**
	 * SQL query that select every pixelgeom link to the registred photo
	 */
	private static final String
	GETALLPIXELGEOMFROMAPHOTO = 
		"SELECT "
		+ MySQLiteHelper.TABLE_PIXELGEOM+"."+MySQLiteHelper.COLUMN_PIXELGEOMID+", "
		+ MySQLiteHelper.TABLE_PIXELGEOM+"."+MySQLiteHelper.COLUMN_PIXELGEOMCOORD
		+" FROM "
		+ MySQLiteHelper.TABLE_PIXELGEOM
		+" INNER JOIN " + MySQLiteHelper.TABLE_ELEMENT 
		+" ON " + MySQLiteHelper.TABLE_ELEMENT + "." + MySQLiteHelper.COLUMN_PIXELGEOMID +" = " + MySQLiteHelper.TABLE_PIXELGEOM + "." + MySQLiteHelper.COLUMN_PIXELGEOMID
		+" WHERE " + MySQLiteHelper.TABLE_ELEMENT + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
		//need to complete with PHOTO_ID and ";"
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity
	 */
	public void instanciateAllpixelGeom(){
		ArrayList<PixelGeom> pixelGeomList = new ArrayList<PixelGeom>();
		
		Cursor cursor = database.rawQuery(GETALLPIXELGEOMFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			PixelGeom p1 = cursorToPixelGeom(cursor);
			p1.setRegistredInLocal(true);
			pixelGeomList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.pixelGeom= pixelGeomList;		
		
	}
	
	/**
	 * SQL query that select every pixelgeom link to the registred photo
	 */
	private static final String
	GETALLELEMENTFROMAPHOTO = 
		"SELECT "
		+ "* "
		+" FROM "
		+ MySQLiteHelper.TABLE_ELEMENT
		+" WHERE " + MySQLiteHelper.TABLE_ELEMENT + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
		//need to complete with PHOTO_ID and ";"
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity
	 */
	public void instanciateAllElement(){
		ArrayList<Element> elementList = new ArrayList<Element>();
		
		Cursor cursor = database.rawQuery(GETALLELEMENTFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Element p1 = cursorToElement(cursor);
			p1.setRegistredInLocal(true);
			elementList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.element= elementList;		
		
	}
	
	/**
	 * SQL query that select every pixelgeom link to the registred photo
	 */
	private static final String
	GETALLGPSGEOMFROMAPHOTO = 
		"SELECT "
		+ MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMID+", "
		+ MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMCOORD
		+" FROM "
		+ MySQLiteHelper.TABLE_GPSGEOM
		+" INNER JOIN " + MySQLiteHelper.TABLE_PHOTO 
		+" ON " + MySQLiteHelper.TABLE_GPSGEOM + "." + MySQLiteHelper.COLUMN_GPSGEOMID +" = " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_GPSGEOMID
		+" WHERE " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
		//need to complete with PHOTO_ID and ";"
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity
	 */
	public void instanciateAllGpsGeom(){
		ArrayList<GpsGeom> gpsGeomList = new ArrayList<GpsGeom>();
		
		Cursor cursor = database.rawQuery(GETALLGPSGEOMFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			GpsGeom p1 = cursorToGpsGeom(cursor);
			p1.setRegistredInLocal(true);
			gpsGeomList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.gpsGeom= gpsGeomList;		
		
	}
	
	/**
	 * SQL query that select every pixelgeom link to the registred photo
	 */
	private static final String
	GETALLPROJECTFROMAPHOTO = 
		"SELECT "
		+ MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_PROJECTID+", "
		
		+ MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_PROJECTNAME+", "
		+ MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_GPSGEOMID
		+" FROM "
		+ MySQLiteHelper.TABLE_PROJECT
		+" INNER JOIN " + MySQLiteHelper.TABLE_COMPOSED 
		+" ON " + MySQLiteHelper.TABLE_PROJECT + "." + MySQLiteHelper.COLUMN_PROJECTID +" = " + MySQLiteHelper.TABLE_COMPOSED + "." + MySQLiteHelper.COLUMN_PROJECTID
		+" WHERE " + MySQLiteHelper.TABLE_COMPOSED + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
		//need to complete with PHOTO_ID and ";"
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity
	 */
	public void instanciateAllProject(){
		ArrayList<Project> projectList = new ArrayList<Project>();
		
		Cursor cursor = database.rawQuery(GETALLPROJECTFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Project p1 = cursorToProject(cursor);
			p1.setRegistredInLocal(true);
			projectList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.project= projectList;		
		
	}
	

	
	
	/**
	 * get information from datasource.database to public static fields photo in main activity once a local project is loaded
	 */
	
	/**
	 * SQL query that select every pixelgeom link to the registred photo
	 */
	private static final String
	GETPHOTO = 
		"SELECT * FROM "
		+ MySQLiteHelper.TABLE_PHOTO
		+" WHERE " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
		//need to complete with PHOTO_ID and ";"
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity
	 */
	public void instanciatePhoto(long id ){
		Cursor cursor = database.rawQuery(GETPHOTO + id +" ;",null);
		cursor.moveToFirst();
		Photo photoLoaded = cursorToPhoto(cursor);
		cursor.close();
		MainActivity.photo= photoLoaded;		
		
	}
	

}