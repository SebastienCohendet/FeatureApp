<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
include("DBConnect.php");

 //GETTING THE SENT PROJECT_ID FROM POST, and related table ids
 if(isset($_POST["project_id"])) { 
	 $arr=$_POST["myHttpData"];
	 $decarr = json_decode($arr,true); 
	 $project_id = $decarr[0];
 }
 else{
 	 $project_id = 1;
 }
 
 if(isset($_POST['project'])){
 
 //GETTING PROJECT TABLE INFO
 $project_query = "SELECT * FROM project";
 $project_result = pg_query($project_query);
 $project_resultarr = array();
 $count_project = 0;
 while ($row = pg_fetch_row($project_result)) {
 	 $project_resultarr[$count_project] = array('project_id' => $row[0], 'project_name' => $row[1], 'gpsgeom_id' => $row[2]);
 	 $count_project++;
}

  //GETTING GPSGEOM TABLE INFO
 $gpsgeom_query = "SELECT DISTINCT ON (g.gpsGeom_id) * FROM gpsgeom g INNER JOIN element e ON e.gpsGeom_id = g.gpsGeom_id
 INNER JOIN composed c ON c.photo_id = e.photo_id";
 $gpsgeom_result = pg_query($gpsgeom_query);
 $gpsgeom_resultarr = array();
 $count_gpsgeom = 0;
 while ($row = pg_fetch_row($gpsgeom_result)) {
 	 $gpsgeom_resultarr[$count_gpsgeom] = array('gpsGeom_id' => $row[0], 'gpsGeom_the_geom' => $row[1]);
 	 $count_gpsgeom++;
}
 }
 
 //GETTING PROJECT TABLE INFO
 $project_query = "SELECT * FROM project WHERE project_id = ".$project_id;
 $project_result = pg_query($project_query);
 $project_resultarr = array();
 $count_project = 0;
 while ($row = pg_fetch_row($project_result)) {
 	 $project_resultarr[$count_project] = array('project_id' => $row[0], 'project_name' => $row[1], 'gpsgeom_id' => $row[2]);
 	 $count_project++;
}
 
 // GETTIN COMPOSED TABLE INFO	
 $composed_query = "SELECT * FROM composed WHERE project_id = ".$project_id;
 $composed_result = pg_query($composed_query);
 $composed_resultarr = array();
 $count_composed = 0;
 while ($row = pg_fetch_row($composed_result)){
 	 $composed_resultarr[$count_composed] = array('project_id' => $row[0], 'photo_id' => $row[1]);
 	 $count_composed++;
 }
 
 //GETTING PHOTO TABLE INFO
 $photo_query = "SELECT * FROM photo p INNER JOIN composed c ON c.photo_id = p.photo_id WHERE c.project_id = ".$project_id;
 $photo_result = pg_query($photo_query);
 $photo_resultarr = array();
 $count_photo = 0;
 while ($row = pg_fetch_row($photo_result)) {
 	 $photo_resultarr[$count_photo] = array('photo_id' => $row[0], 'photo_description' => $row[1], 'photo_author' => $row[2], 'photo_url' => $row[3], 'gpsGeom_id' => $row[4]);
 	 $count_photo++;
 }
 
 //GETTIN ELEMENT TABLE INFO
 $element_query = "SELECT * FROM element e INNER JOIN composed c on c.photo_id = e.photo_id WHERE c.project_id = ".$project_id;
 $element_result = pg_query($element_query);
 $element_resultarr = array();
 $count_element = 0;
 while ($row = pg_fetch_row($element_result)){
 	 $element_resultarr[$count_element] = array('element_id' => $row[0], 'photo_id' => $row[1] , 'material_id' => $row[2] , 'gpsGeom_id' => $row[3] , 'pixelGeom_id' => $row[4] , 'elementType_id' => $row[5] , 'element_color' => $row[6]);
 	 $count_element++;
 }
 
  //GETTING GPSGEOM TABLE INFO
 $gpsgeom_query = "SELECT DISTINCT ON (g.gpsGeom_id) * FROM gpsgeom g INNER JOIN element e ON e.gpsGeom_id = g.gpsGeom_id
 INNER JOIN composed c ON c.photo_id = e.photo_id
 WHERE c.project_id = ".$project_id;
 $gpsgeom_result = pg_query($gpsgeom_query);
 $gpsgeom_resultarr = array();
 $count_gpsgeom = 0;
 while ($row = pg_fetch_row($gpsgeom_result)) {
 	 $gpsgeom_resultarr[$count_gpsgeom] = array('gpsGeom_id' => $row[0], 'gpsGeom_the_geom' => $row[1]);
 	 $count_gpsgeom++;
}
 
 //GETTING PIXELGEOM TABLE INFO
 $pixelgeom_query = "SELECT DISTINCT ON (pi.pixelGeom_id)* FROM pixelgeom pi INNER JOIN element e ON e.pixelGeom_id = pi.pixelGeom_id 
 INNER JOIN composed c ON c.photo_id = e.photo_id
 WHERE c.project_id = ".$project_id;
 $pixelgeom_result = pg_query($pixelgeom_query);
 $pixelgeom_resultarr = array();
 $count_pixelgeom = 0;
 while ($row = pg_fetch_row($pixelgeom_result)){
 	 $pixelgeom_resultarr[$count_pixelgeom] = array('pixelGeom_id' => $row[0], 'pixelGeom_the_geom' => $row[1]);
 	 $count_pixelgeom++;
 }
 
 //GETTING ELEMENTTYPE TABLE INFO
 $elementtype_query = "SELECT distinct on (elt.elementType_id) * FROM elementtype elt INNER JOIN element e ON e.elementType_id = elt.elementType_id 
 INNER JOIN composed c ON c.photo_id = e.photo_id
 WHERE c.project_id = ".$project_id;
 $elementtype_result = pg_query($elementtype_query);
 $elementtype_resultarr = array();
 $count_elementtype = 0;
 while ($row = pg_fetch_row($elementtype_result)){
 	 $elementtype_resultarr[$count_elementtype] = array('elementType_id' => $row[0], 'elementType_name' => $row[1]);
 	 $count_elementtype++;
 } 
 
 
 //GETTING MATERIAL TABLE INFO
 $material_query = "SELECT distinct on (m.material_id) * FROM material m INNER JOIN element e ON e.material_id = m.material_id
 INNER JOIN composed c ON c.photo_id = e.photo_id
 WHERE c.project_id = ".$project_id;
 $material_result = pg_query($material_query);
 $material_resultarr = array();
 $count_material = 0;
 while ($row = pg_fetch_row($material_result)){
 	 $material_resultarr[$count_material] = array('material_id' => $row[0], 'material_name' => $row[1]);
 	 $count_material++;
 }
 
 //PUTING ALL THE INFO IN ONE JSON
 
 $globaljSon = array();
 $globaljSon[0] = array('Project' => $project_resultarr);
 $globaljSon[1] = array('Photo' => $photo_resultarr);
 $globaljSon[2] = array('Composed' => $composed_resultarr);
 $globaljSon[3] = array('GpsGeom' => $gpsgeom_resultarr);
 $globaljSon[4] = array('Element' => $element_resultarr);
 $globaljSon[5] = array('PixelGeom' => $pixelgeom_resultarr);
 $globaljSon[6] = array('ElementType' => $elementtype_resultarr);
 $globaljSon[7] = array('Material' => $material_resultarr);
 
 $finaljSon = json_encode($globaljSon);
 
echo $finaljSon;
 
 ?>
