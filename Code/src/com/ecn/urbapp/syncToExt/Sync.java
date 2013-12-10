package com.ecn.urbapp.syncToExt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.db.GpsGeom;
import com.ecn.urbapp.db.Photo;
import com.ecn.urbapp.db.Project;
import com.google.android.gms.internal.n;
import com.google.gson.Gson;

public class Sync
{
	/**
	 * MaxId and timestamp
	 */
	public static HashMap<String, Integer> maxId = new HashMap<String, Integer>();
	
	/**
	 * Contains all the projects on server
	 */
	public static List<Project> refreshedValues = new ArrayList<Project>();
	
	/**
	 * Contains all the relative photos on server of a project type
	 */
	public static List<Photo> refreshedValuesPhoto;
	
	/**
	 * Contains all the GpsGeom from Server
	 */
	public static List<GpsGeom> allGpsGeom = new ArrayList<GpsGeom>();

	
	/**
	 * Launch the sync to external DB (export mode)
	 * @return Boolean if success of not
	 */
	public Boolean doSyncToExt()
	{
		Boolean success = false;
		
			try
			{
				new BackTaskExportToExt().execute();
				success = true;
			}
			catch (Exception e)
			{
			}
		
		return success;
	}
	
	/**
	 * 
	 * @return Boolean if success of not
	 */
	public boolean getProjectsFromExt()
	{
		Boolean success = false;
			try
			{
				BackTaskImportProject BaProjectSync = new BackTaskImportProject();
				BaProjectSync.execute().get();
				success = true;
			}
			catch (Exception e)
			{
				
			}
		
		return success;
	}
	
	/**
	 * 
	 * @return Boolean if success of not
	 */
	public boolean getPhotosFromExt(long project_id)
	{
		Boolean success = false;
		refreshedValuesPhoto = new ArrayList<Photo>();
			try
			{
				BackTaskImportPhoto BaPhotoSync = new BackTaskImportPhoto(project_id);
				BaPhotoSync.execute().get();
				success = true;
			}
			catch (Exception e)
			{
				
			}
		
		return success;
	}
	/**
	 * Get the max id of each critical tables in external DB AND get current timestamp from database
	 * @return Hashmap of all max id
	 */
	public static HashMap<String, Integer> getMaxId() {
		try {
			maxId = new BackTastMaxId().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxId;
	}

	
	
	/**
	 * The additional threat to upload data to the server
	 * @author Sebastien
	 *
	 */
	public class BackTaskExportToExt extends AsyncTask<Void, String, String> {
		
		private Context mContext;
		
		/**
		 * Constructor
		 * @param json
		 */
		public BackTaskExportToExt(){
			super();			
			this.mContext = MainActivity.baseContext;
		}

		/**
		 * Pre Execution orders
		 */
		protected void onPreExecute(){
			
			super.onPreExecute();
			Toast.makeText(MainActivity.baseContext,  "Début de l'exportation", Toast.LENGTH_SHORT).show();
		}

		/**
		 * The transformation in json and the sending to server
		 */
		protected String doInBackground(Void... params) { 
			Gson gson = new Gson();
			String dataJson = gson.toJson(MainActivity.gpsGeom);
			String jSonComplete = "[{\"gpsgeom\":"+dataJson+"},";

			dataJson = gson.toJson(MainActivity.pixelGeom);
			jSonComplete += "{\"pixelgeom\":"+dataJson+"},";

			ArrayList<Photo> photos = new ArrayList<Photo>();
			photos.add(MainActivity.photo);
			dataJson = gson.toJson(photos);
			jSonComplete += "{\"photo\":"+dataJson+"},";

			dataJson = gson.toJson(MainActivity.project);
			jSonComplete += "{\"project\":"+dataJson+"},";

			dataJson = gson.toJson(MainActivity.composed);
			jSonComplete += "{\"composed\":"+dataJson+"},";

			dataJson = gson.toJson(MainActivity.element);
			jSonComplete += "{\"element\":"+dataJson+"}]";

			/**
			 * File upload request
			 */
			File mImage = new File(Environment.getExternalStorageDirectory(), "featureapp/"+MainActivity.photo.getPhoto_url());
			
			//TODO make the upload only when necessary !
			doFileUpload(mImage);

			return postData(jSonComplete);
		}
	 

		/**
		 * The posting method to server
		 * @param param the json data to send
		 * @return the string of the server response
		 */
	    public String postData(String param) {
		    HttpClient httpclient = new DefaultHttpClient();
		    // specify the URL you want to post to
		    HttpPost httppost = new HttpPost(MainActivity.serverURL+"registerDB.php");
		    try {
			    // create a list to store HTTP variables and their values
			    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			    // add an HTTP variable and value pair
			    nameValuePairs.add(new BasicNameValuePair("myHttpData", param));
			    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			    // send the variable and value, in other words post, to the URL
			    HttpResponse response = httpclient.execute(httppost);
			    
			    StringBuilder sb = new StringBuilder();
			    try {
			    	BufferedReader reader = 
			    			new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
			    	String line = null;

			    	while ((line = reader.readLine()) != null) {
			    		sb.append(line);
			    	}
			    }
			    catch (IOException e) { e.printStackTrace(); }
			    catch (Exception e) { e.printStackTrace(); }
			    
			    return sb.toString();
				
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } ;
	        //TODO catch the nulpointer case
	        return null;
	    }

	    private boolean doFileUpload(File file) {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;

            String pathToOurFile = file.getPath();
            String urlServer = MainActivity.serverURL+"uploadImage.php";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            // log path to our file
            Log.d("DFHUPLOAD", pathToOurFile);

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;
            int sentBytes = 0;
            long fileSize = file.length();

            // log filesize
            String files= String.valueOf(fileSize);
            String buffers= String.valueOf(maxBufferSize);
            Log.d("DFHUPLOAD",files);
            Log.d("DFHUPLOAD",buffers);

            try
            {
                    FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

                    URL url = new URL(urlServer);
                    connection = (HttpURLConnection) url.openConnection();

                    // Allow Inputs & Outputs
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    // Enable POST method
                    connection.setRequestMethod("POST");

                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                    outputStream = new DataOutputStream( connection.getOutputStream() );
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0)
                    {
                            outputStream.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            sentBytes += bufferSize;
                            //for progress bar publishProgress((int)(sentBytes * 100 / fileSize));

                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    connection.getResponseCode();

                    connection.getResponseMessage();


                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    try {
                            int responseCode = connection.getResponseCode();
                            return responseCode == 200;
                    } catch (IOException ioex) {
                            Log.e("DFHUPLOAD", "Upload file failed: " + ioex.getMessage(), ioex);
                            return false;
                    } catch (Exception e) {
                            Log.e("DFHUPLOAD", "Upload file failed: " + e.getMessage(), e);
                            return false;
                    }
            }
            catch (Exception ex)
            {
                    String msg= ex.getMessage();
                    Log.d("DFHUPLOAD", msg);
            }
            return true;
    }
		/**
		 * The things to execute after the backTask 
		 */
	    protected void onPostExecute(String result) {	
	    	if (result.equals("OK")){
	    		Toast.makeText(mContext, "Synchronisation avec la base : SUCCES", Toast.LENGTH_SHORT).show();
	    	}
	    	else {
		        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
	    	}
	    }
	    

	}

	/**
	 * The additional threat to get the Max id of each tables on server AND the current timestamp for synchronise purpose
	 * @author Sebastien
	 *
	 */
	public static class BackTastMaxId extends AsyncTask<Void, HashMap<String, Integer>, HashMap<String, Integer>> {
		
		private Context mContext;
		
		/**
		 * Constructor
		 * @param json
		 */
		public BackTastMaxId(){
			super();			
			this.mContext = MainActivity.baseContext;
		}

		/**
		 * Pre Execution orders
		 */
		protected void onPreExecute(){
			
			super.onPreExecute();
			Toast.makeText(MainActivity.baseContext,  "Début du get Id", Toast.LENGTH_SHORT).show();
		}

		/**
		 * Ask the server and transform them to HashMap
		 */
		protected HashMap<String, Integer> doInBackground(Void... params) { 
			
			String JSON = getData();
			 try {
			    	JSONObject jObj = new JSONObject(JSON); 
			    	HashMap<String, Integer> maxID = new HashMap<String, Integer>();

			    	maxID.put("Photo", jObj.getInt("photo"));
			    	maxID.put("GpsGeom", jObj.getInt("gpsgeom"));
			    	maxID.put("Element", jObj.getInt("element"));
			    	maxID.put("PixelGeom", jObj.getInt("pixelgeom"));
			    	maxID.put("Project", jObj.getInt("project"));
			    	maxID.put("date", jObj.getInt("date"));
			    	return maxID;
			    	
			        } catch (JSONException e) {
			           Log.e("JSON Parser", "Error parsing data " + e.toString());
			           return (HashMap<String, Integer>) null;
			        }  
		}
	 

		/**
		 * The request method to server
		 * @return the string of the server response
		 */
	    public String getData() {
		    HttpClient httpclient = new DefaultHttpClient();
		    // specify the URL you want to post to
		    HttpPost httppost = new HttpPost(MainActivity.serverURL+"maxID.php");
		    try {
			    // send the variable and value, in other words post, to the URL
			    HttpResponse response = httpclient.execute(httppost);
			    
			    StringBuilder sb = new StringBuilder();
			    try {
			    	BufferedReader reader = 
			    			new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
			    	String line = null;

			    	while ((line = reader.readLine()) != null) {
			    		sb.append(line);
			    	}
			    }
			    catch (IOException e) { e.printStackTrace(); }
			    catch (Exception e) { e.printStackTrace(); }
			    
			    return sb.toString();
				
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } ;
	        return "error";
	    }

		
		/**
		 * The things to execute after the backTask 
		 */
	    protected void onPostExecute(HashMap<String, Integer> result) {	
	    	if (!result.isEmpty()){
	    		//TODO change the message so not to be in debug mode :)
	    		Toast.makeText(mContext, result.toString(), Toast.LENGTH_SHORT).show();
	    	}
	    	else {
		        Toast.makeText(mContext, "Erreur dans la communication avec le serveur", Toast.LENGTH_SHORT).show();
	    	}
	    }
	}
	
	
	/**
	 * The additional threat to get projects and gpsgeom data from server
	 * @author Sebastien
	 *
	 */
	public static class BackTaskImportProject extends AsyncTask<Void, Void, Void> {
			
		private Context mContext;
			
		/**
		 * To get the informations of project and gpsGeom for all the projects on external DB server
		 * @param refreshedValues Project info
		 * @param allGpsGeom GpsGeom info
		 */		public BackTaskImportProject(){			
			this.mContext = MainActivity.baseContext;
		}

		/**
		 * Pre Execution orders
		 */
		protected void onPreExecute(){
			super.onPreExecute();
			Toast.makeText(MainActivity.baseContext,  "Début de la synchro", Toast.LENGTH_SHORT).show();
		}

		/**
		 * Ask the server and save project and gpsGeom on the var
		 * @return 
		 */
		protected Void doInBackground(Void... params) { 

			String JSON = getData();
			try {
				JSONArray jArr = new JSONArray(JSON); 
				refreshedValues = new ArrayList<Project>();
				allGpsGeom = new ArrayList<GpsGeom>();

				JSONObject projects = jArr.getJSONObject(0);
				JSONArray projectsInner = projects.getJSONArray("Project");
				JSONObject gpsGeom = jArr.getJSONObject(1);
				JSONArray gpsGeomInner = gpsGeom.getJSONArray("GpsGeom");

				for(int i=0;i<projectsInner.length();i++)
				{
					JSONObject project = projectsInner.getJSONObject(i);
					long project_id = project.getLong("project_id");
					String project_name = project.getString("project_name");
					long gpsgeom_id = project.getLong("gpsGeom_id");
					
					Project projectEnCours = new Project();
					projectEnCours.setProjectId(project_id);
					projectEnCours.setProjectName(project_name);
					projectEnCours.setGpsGeom_id(gpsgeom_id);
					
					refreshedValues.add(projectEnCours);
				}
				for(int i=0;i<gpsGeomInner.length();i++)
				{
					JSONObject gpsgeom = gpsGeomInner.getJSONObject(i);
					long gpsGeom_id = gpsgeom.getLong("gpsGeom_id");
					String gpsGeom_the_geom = gpsgeom.getString("gpsGeom_the_geom");
					
					GpsGeom gpsGeomEnCours = new GpsGeom();
					gpsGeomEnCours.setGpsGeomId(gpsGeom_id);
					gpsGeomEnCours.setGpsGeomCoord(gpsGeom_the_geom);
					
					allGpsGeom.add(gpsGeomEnCours);
				}
                 
             } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
             }
			return null;
		}
	 

		/**
		 * The request method to server
		 * @return the string of the server response
		 */
	    public String getData() {
		    HttpClient httpclient = new DefaultHttpClient();
		    // specify the URL you want to post to
		    HttpPost httppost = new HttpPost(MainActivity.serverURL+"import.php");
		    try {
		    	
		    	// create a list to store HTTP variables and their values
			    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			    // add an HTTP variable and value pair
			    nameValuePairs.add(new BasicNameValuePair("project", "all"));
			    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			    // send the variable and value, in other words post, to the URL
			    HttpResponse response = httpclient.execute(httppost);
			    
			    StringBuilder sb = new StringBuilder();
			    try {
			    	BufferedReader reader = 
			    			new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
			    	String line = null;

			    	while ((line = reader.readLine()) != null) {
			    		sb.append(line);
			    	}
			    }
			    catch (IOException e) { e.printStackTrace(); }
			    catch (Exception e) { e.printStackTrace(); }
			    
			    return sb.toString();
				
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } ;
	        return "error";
	    }

	}
	
	/**
	 * The additional threat to get projects and gpsgeom data from server
	 * @author Sebastien
	 *
	 */
	public static class BackTaskImportPhoto extends AsyncTask<Void, Void, Void> {
			
		private Context mContext;
		private long project_id = 1;		
		
		/**
		 * Default constructor
		 */
		public BackTaskImportPhoto(long project_id){			
			this.mContext = MainActivity.baseContext;
			this.project_id = project_id;
		}

		/**
		 * Pre Execution orders
		 */
		protected void onPreExecute(){
			super.onPreExecute();
			Toast.makeText(MainActivity.baseContext,  "Début de la synchro", Toast.LENGTH_SHORT).show();
		}

		/**
		 * Ask the server and save all data on the specific var
		 * @return 
		 */
		protected Void doInBackground(Void... params) { 

			String JSON = getData();
			try {
				JSONArray jArr = new JSONArray(JSON); 
				refreshedValues = new ArrayList<Project>();
				allGpsGeom = new ArrayList<GpsGeom>();

				JSONObject projects = jArr.getJSONObject(0);
				JSONArray projectsInner = projects.getJSONArray("Project");

				for(int i=0;i<projectsInner.length();i++)
				{
					JSONObject project = projectsInner.getJSONObject(i);
					long project_id = project.getLong("project_id");
					String project_name = project.getString("project_name");
					long gpsgeom_id = project.getLong("gpsgeom_id");
					
					Project projectEnCours = new Project();
					projectEnCours.setProjectId(project_id);
					projectEnCours.setProjectName(project_name);
					projectEnCours.setGpsGeom_id(gpsgeom_id);
					
					refreshedValues.add(projectEnCours);
				}
				
				JSONObject photos = jArr.getJSONObject(1);
				JSONArray photoInner = photos.getJSONArray("Photo");

				for(int i=0;i<photoInner.length();i++)
				{
					JSONObject photo = photoInner.getJSONObject(i);
					long photo_id = photo.getLong("photo_id");
					String photo_descript = photo.getString("photo_description");
					String photo_url = photo.getString("photo_url");
					String photo_author = photo.getString("photo_author");
					long gpsgeom_id = photo.getLong("gpsgeom_id");
					int photo_nbr = photo.getInt("photo_nbrPoint");
					String photo_date = photo.getString("photo_date");
					
					Photo photoEnCours = new Photo();
					photoEnCours.setPhoto_id(photo_id);
					photoEnCours.setPhoto_author(photo_author);
					photoEnCours.setPhoto_description(photo_descript);
					photoEnCours.setPhoto_url(photo_url);
					photoEnCours.setGpsGeom_id(gpsgeom_id);
					photoEnCours.setPhoto_nbrPoints(photo_nbr);
					photoEnCours.setPhoto_derniereModif(photo_date);
					
					refreshedValuesPhoto.add(photoEnCours);
				}
				
				JSONObject gpsGeom = jArr.getJSONObject(3);
				JSONArray gpsGeomInner = gpsGeom.getJSONArray("GpsGeom");
				
				for(int i=0;i<gpsGeomInner.length();i++)
				{
					JSONObject gpsgeom = gpsGeomInner.getJSONObject(i);
					long gpsGeom_id = gpsgeom.getLong("gpsGeom_id");
					String gpsGeom_the_geom = gpsgeom.getString("gpsGeom_the_geom");
					
					GpsGeom gpsGeomEnCours = new GpsGeom();
					gpsGeomEnCours.setGpsGeomId(gpsGeom_id);
					gpsGeomEnCours.setGpsGeomCoord(gpsGeom_the_geom);
					
					allGpsGeom.add(gpsGeomEnCours);
				}
				
				
                 
             } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
             }
			return null;
		}
	 

		/**
		 * The request method to server
		 * @return the string of the server response
		 */
	    public String getData() {
		    HttpClient httpclient = new DefaultHttpClient();
		    // specify the URL you want to post to
		    HttpPost httppost = new HttpPost(MainActivity.serverURL+"import.php");
		    try {
		    	
		    	// create a list to store HTTP variables and their values
			    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			    // add an HTTP variable and value pair
			    nameValuePairs.add(new BasicNameValuePair("project_id", String.valueOf(project_id)));
			    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			    // send the variable and value, in other words post, to the URL
			    HttpResponse response = httpclient.execute(httppost);
			    
			    StringBuilder sb = new StringBuilder();
			    try {
			    	BufferedReader reader = 
			    			new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
			    	String line = null;

			    	while ((line = reader.readLine()) != null) {
			    		sb.append(line);
			    	}
			    }
			    catch (IOException e) { e.printStackTrace(); }
			    catch (Exception e) { e.printStackTrace(); }
			    
			    return sb.toString();
				
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } ;
	        return "error";
	    }

	}
}