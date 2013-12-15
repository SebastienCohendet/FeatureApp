<?php 
ini_set('display_errors', 'On');
error_reporting(E_ALL);
include("DBConnect.php");

if(isset($_POST["myHttpData"])) { //if there is data to import in the database
	 $arr=$_POST["myHttpData"];
	 $decarr = json_decode($arr,true); 

	 $count = count($decarr);
	 
	 $values = array();
	 $update_values = array();
	 $sql='BEGIN;
	 ';
	 
	 //we parse every table passed in the json
	 for ($x=0;$x<$count;$x++){
		$newrec = $decarr[$x];
		foreach($newrec as $tableName => $table) {
		
			foreach($table as $colomnName => $colomn){
				//init the value to put in sql query
				$nbValue=0;
				$allColomnName='';
				$setInfo='';
				$allColomn="";
				foreach($colomn as $colomnName => $colomn){
					if($colomnName != "registredInLocal" && $colomnName != "selected" && $colomnName != "linkedElement" && $colomnName != "linkedPixelGeom" && $colomnName !="photo_urltemp" && $colomnName !="photo_urlTemp"){
						$nbValue++;
						if($colomn =="")
							$colomn=" ";
						$allColomnName .= $colomnName.', ';
						
					if(is_int($colomn))
						$allColomn .= ''.$colomn.', ';
					else{
						$colomn=pg_escape_string($colomn);
						$allColomn .= '\''.$colomn.'\', ';
					}
						
						$setInfo .= $colomnName." = nv.".$colomnName.", ";

					}
				}
				
			if($nbValue!=0) {
			$allColomn = substr($allColomn, 0,-2);
			$allColomnName = substr($allColomnName, 0, -2);
			$setInfo = substr($setInfo, 0, -2);
			$sql .="WITH new_values (".$allColomnName.") as (
				  values 
					 (".$allColomn.")
				),
				upsert as
				( 
					update ".$tableName." m 
						set ".$setInfo."
					FROM new_values nv
					WHERE ";
					if ($tableName=="composed")
						$sql .="m.project_id = nv.project_id AND m.photo_id = nv.photo_id";
					else
						$sql .="m.".$tableName."_id = nv.".$tableName."_id";
						
					$sql .=" RETURNING m.*
				)
				INSERT INTO ".$tableName." (".$allColomnName.")
				SELECT ".$allColomnName."
				FROM new_values
				WHERE NOT EXISTS (SELECT 1 
								  FROM upsert up 
				  WHERE ";
				  if ($tableName=="composed")
						$sql .="up.project_id = new_values.project_id AND up.photo_id = new_values.photo_id);
						";
					else
						$sql .="up.".$tableName."_id = new_values.".$tableName."_id);
						";
				}
			}
		}		
	 }
	$sql .="COMMIT;";
	$bool = pg_query($conn,$sql);
	
	 
	 $fp = fopen('testSqlite.txt', 'w+');
	
	fwrite($fp, $sql);
	fclose($fp);
	
	if ($bool) //success of the request
		echo "OK";
	else
		echo pg_last_error($conn);

	
}
else {
	echo 'Post manquant';
}
pg_close($conn);
?>


