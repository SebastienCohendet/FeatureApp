<?php 

include("DBConnect.php");

if(isset($_POST["myHttpData"])) { //if there is data to import in the database
	 $arr=$_POST["myHttpData"];
	 $decarr = json_decode($arr,true); 
	 
	 $count = count($decarr);
	 
	 $values = array();
	 $update_values = array();
	 $sql='';
	 
	 //we parse every table passed in the json
	 for ($x=0;$x<$count;$x++){
		
		$newrec = $decarr[$x];
		foreach($newrec as $tableName => $table) {
		
			//init the value to put in sql query
			$nbValue=0;
			$allColomnName='';
			$setInfo='';
			$allColomn="";
			foreach($table as $colomnName => $colomn){
				if (!is_array($colomn)){ //if it is not an array (so was not a listArray before (//photo))
					if($colomnName != "registredInLocal"){
						$nbValue++;
						if($colomn =="")
							$colomn=" ";
						$allColomnName .= $colomnName.', ';
						
						if(is_int($colomn))
							$allColomn .= ''.$colomn.', ';
						else
							$allColomn .= '\''.$colomn.'\', ';
							
						$setInfo .= $colomnName." = nv.".$colomnName.", ";
					}
				}
				else {
					foreach($colomn as $colomnName => $colomn){
						if($colomnName != "registredInLocal"){
							$nbValue++;
							if($colomn =="")
								$colomn=" ";
							$allColomnName .= $colomnName.', ';
							
						if(is_int($colomn))
							$allColomn .= ''.$colomn.', ';
						else
							$allColomn .= '\''.$colomn.'\', ';
							
							$setInfo .= $colomnName." = nv.".$colomnName.", ";

						}
					}
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
	
	pg_query($conn,$sql);
	
	 
	 $fp = fopen('testSqlite.txt', 'w+');
	
	fwrite($fp, $sql);
	fclose($fp);
	echo "OK";

	
}
else {
	$retour = pg_query($conn,"SELECT * FROM Comments");

	$resultats=array('comments' => array()); 
	
	While($ligne = pg_fetch_assoc($retour)) { 
		foreach($ligne as $cle => $valeur){ 
			$tableau[$cle] = $valeur; 
		}
		$resultats['comments'][]=$tableau; 
	}
	
	$resultatsJSON = json_encode($resultats);

	print($resultatsJSON); pg_close($conn); 
}
?>

