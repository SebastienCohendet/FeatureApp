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

CREATE FUNCTION merge_gpsgeom(key INTEGER, the_geom TEXT, address TEXT) RETURNS VOID AS
$$
BEGIN
    LOOP
        -- first try to update the key
        UPDATE gpsgeom SET gpsgeom_the_geom = the_geom, gpsgeom_address = address WHERE gpsgeom_id = key;
        IF found THEN
            RETURN;
        END IF;
        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO gpsgeom VALUES (key, the_geom, address);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
            -- Do nothing, and loop to try the UPDATE again.
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

CREATE SEQUENCE project_id_seq;
CREATE TABLE Project
(
	project_id INTEGER  DEFAULT nextval('project_id_seq') PRIMARY KEY,
	project_name VARCHAR(60),
	gpsGeom_id INTEGER references GpsGeom(gpsGeom_id)
);

CREATE FUNCTION merge_project(key INTEGER, name VARCHAR(60), gpsGeom_id_val INTEGER) RETURNS VOID AS
$$
BEGIN
    LOOP
        -- first try to update the key
        UPDATE project SET project_name = name, gpsGeom_id = gpsGeom_id_val WHERE project_id = key;
        IF found THEN
            RETURN;
        END IF;
        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO project VALUES (key, name, gpsGeom_id_val);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
            -- Do nothing, and loop to try the UPDATE again.
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

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

CREATE FUNCTION merge_photo(key INTEGER, description TEXT, author VARCHAR(60), url TEXT, gpsGeom_id_val INTEGER) RETURNS VOID AS
$$
BEGIN
    LOOP
        -- first try to update the key
        UPDATE photo SET photo_description = description, photo_author = author, photo_url = url, gpsGeom_id = gpsGeom_id_val WHERE photo_id = key;
        IF found THEN
            RETURN;
        END IF;
        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO photo VALUES (key, description, author, url, gpsGeom_id_val);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
            -- Do nothing, and loop to try the UPDATE again.
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

CREATE TABLE Composed
(
	project_id INTEGER references Project(project_id),
	photo_id INTEGER references Photo(photo_id),
	PRIMARY KEY(project_id, photo_id) 
);

CREATE FUNCTION merge_composed(project_id_val INTEGER, photo_id_val INTEGER) RETURNS VOID AS
$$
BEGIN
        -- try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO photo VALUES (project_id_val, photo_id_val);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
             RETURN;
        END;
END;
$$
LANGUAGE plpgsql;

CREATE SEQUENCE pixelGeom_id_seq;
CREATE TABLE PixelGeom
(
	pixelGeom_id INTEGER DEFAULT nextval('pixelGeom_id_seq') PRIMARY KEY,
	pixelGeom_the_geom GEOMETRY
);

CREATE FUNCTION merge_pixelgeom(key INTEGER, the_geom TEXT) RETURNS VOID AS
$$
BEGIN
    LOOP
        -- first try to update the key
        UPDATE pixelgeom SET pixelgeom_the_geom = the_geom WHERE pixelgeom_id = key;
        IF found THEN
            RETURN;
        END IF;
        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO pixelgeom VALUES (key, the_geom);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
            -- Do nothing, and loop to try the UPDATE again.
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

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

CREATE FUNCTION merge_element(key INTEGER, photo_id_val INTEGER, material_id_val INTEGER, gpsGeom_id_val INTEGER, pixelGeom_id_val INTEGER,
								elementType_id_val INTEGER,	color VARCHAR(10)) 
								RETURNS VOID AS
$$
BEGIN
    LOOP
        -- first try to update the key
        UPDATE element SET photo_id = photo_id_val,	material_id = material_id_val, gpsGeom_id = gpsGeom_id_val,
		pixelGeom_id = pixelGeom_id_val, elementType_id = elementType_id_val, element_color = color WHERE element_id = key;
        IF found THEN
            RETURN;
        END IF;
        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO element VALUES (key, photo_id_val, material_id_val, gpsGeom_id_val, pixelGeom_id_val,elementType_id_val, color);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
            -- Do nothing, and loop to try the UPDATE again.
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;


