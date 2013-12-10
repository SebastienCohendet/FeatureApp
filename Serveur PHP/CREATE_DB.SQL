CREATE DATABASE "URBAPP"
	WITH OWNER = postgres
	TEMPLATE=template_postgis
	ENCODING = 'UTF8';

\connect URBAPP


CREATE SEQUENCE gpsGeom_id_seq;
CREATE TABLE GpsGeom
(
	gpsGeom_id INTEGER DEFAULT nextval('gpsGeom_id_seq') PRIMARY KEY,
	gpsGeom_the_geom GEOMETRY,
	gpsGeom_address TEXT
);

CREATE SEQUENCE project_id_seq;
CREATE TABLE Project
(
	project_id INTEGER  DEFAULT nextval('project_id_seq') PRIMARY KEY,
	project_name VARCHAR(60),
	gpsGeom_id INTEGER references GpsGeom(gpsGeom_id)
);

CREATE SEQUENCE photo_id_seq;
CREATE TABLE Photo
(
	photo_id INTEGER DEFAULT nextval('photo_id_seq') PRIMARY KEY,
	photo_description TEXT,
	photo_author VARCHAR(60),
	photo_url TEXT,
	gpsGeom_id INTEGER references GpsGeom(gpsGeom_id),
	photo_nbrPoint INTEGER,
	photo_derniereModif DATE
);

CREATE TABLE Composed
(
	project_id INTEGER references Project(project_id),
	photo_id INTEGER references Photo(photo_id),
	PRIMARY KEY(project_id, photo_id) 
);

CREATE SEQUENCE pixelGeom_id_seq;
CREATE TABLE PixelGeom
(
	pixelGeom_id INTEGER DEFAULT nextval('pixelGeom_id_seq') PRIMARY KEY,
	pixelGeom_the_geom GEOMETRY
);

CREATE SEQUENCE material_id_seq;
CREATE TABLE Material
(
	material_id INTEGER DEFAULT nextval('material_id_seq') PRIMARY KEY,
	material_name VARCHAR(90)
);

CREATE SEQUENCE elementType_id_seq;
CREATE TABLE ElementType
(
	elementType_id INTEGER DEFAULT nextval('elementType_id_seq') PRIMARY KEY,
	elementType_name VARCHAR(90)
);

CREATE SEQUENCE element_id_seq;
CREATE TABLE Element
(
	element_id INTEGER DEFAULT nextval('element_id_seq') PRIMARY KEY,
	photo_id INTEGER references Photo(photo_id),
	material_id INTEGER references Material(material_id),
	gpsGeom_id INTEGER references GpsGeom(gpsGeom_id),
	pixelGeom_id INTEGER references PixelGeom(pixelGeom_id),
	elementType_id INTEGER references ElementType(elementType_id),
	element_color VARCHAR(10)
);

