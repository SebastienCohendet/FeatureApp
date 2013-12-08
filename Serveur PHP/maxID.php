<?php
include("DBConnect.php");

$maxPhoto = pg_query($conn,"SELECT max(photo_id) FROM photo;");
$maxProject = pg_query($conn,"SELECT max(project_id) FROM project;");
$maxGPS= pg_query($conn,"SELECT max(gpsgeom_id) FROM gpsgeom;");
$maxPixel = pg_query($conn,"SELECT max(pixelgeom_id) FROM pixelgeom;");
$maxElement = pg_query($conn,"SELECT max(element_id) FROM element;");
$date = pg_query($conn, "current_date");

$JSONArray = array();
$maxPhoto = pg_fetch_row($maxPhoto);
  $JSONArray['photo'] = $maxPhoto['0'];

$maxProject = pg_fetch_row($maxProject);
  $JSONArray['project'] = $maxProject['0'];

$maxGPS = pg_fetch_row($maxGPS);
  $JSONArray['gpsgeom'] = $maxPhoto['0'];

$maxPixel = pg_fetch_row($maxPixel);
  $JSONArray['pixelgeom'] = $maxPixel['0'];

$maxElement = pg_fetch_row($maxElement);
  $JSONArray['element'] = $maxElement['0'];
  
 $dateTime = pg_fetch_row($dateTime);
  $JSONArray['date'] = $dateTime['0'];

	$resultatsJSON = json_encode($JSONArray);
	echo $resultatsJSON;
?>