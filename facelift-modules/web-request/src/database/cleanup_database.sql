

CREATE INDEX proc_request_outfile_idx_1 ON proc_request_outfile ( trail_id );

CREATE INDEX proc_request_trail_idx_1 ON proc_request_trail ( request_id );


DROP FUNCTION cleanup_processing_request(reqId INT8);

CREATE TABLE tmp_procrequests_id ( id INT8 );

CREATE TABLE tmp_trails_id ( id INT8 );







CREATE OR REPLACE FUNCTION cleanup_web_request(reqId INT8) RETURNS VARCHAR AS $$
DECLARE
	nbr_rec INTEGER;
BEGIN

	ALTER TABLE web_request_requests DROP CONSTRAINT fk82d7b13f6b880d74;
	ALTER TABLE proc_request_trail   DROP CONSTRAINT fk8e37eaed1f20a4eb;
	ALTER TABLE proc_request_infile  DROP CONSTRAINT fk25ca0bfa1f20a4eb;
	ALTER TABLE proc_request_info    DROP CONSTRAINT fka9baaec71f20a4eb;

	TRUNCATE TABLE tmp_procrequests_id;
	TRUNCATE TABLE tmp_trails_id;

	INSERT INTO tmp_procrequests_id (
		 SELECT proc_request_id 
		 FROM web_request_requests 
		 WHERE web_request_id = reqId );

	INSERT INTO tmp_trails_id (
		 SELECT trail_id 
		 FROM proc_request_trail
		 WHERE request_id IN ( SELECT id FROM tmp_procrequests_id ) );
	RAISE INFO 'inserted int temp tables';
		
	DELETE FROM proc_request_infile WHERE request_id IN ( SELECT id FROM tmp_procrequests_id );
	GET DIAGNOSTICS nbr_rec = ROW_COUNT;
	RAISE INFO 'removed % rows from from proc_request_infile ', nbr_rec;
	
	DELETE FROM proc_request_info WHERE request_id IN ( SELECT id FROM tmp_procrequests_id );	
	GET DIAGNOSTICS nbr_rec = ROW_COUNT;
	RAISE INFO 'removed % rows from from proc_request_infile ', nbr_rec;
	
	DELETE FROM proc_outfile_attrs WHERE file_id IN 
			( SELECT file_id FROM tmp_trails_id
			  JOIN proc_request_outfile ON proc_request_outfile.trail_id = tmp_trails_id.id );
	GET DIAGNOSTICS nbr_rec = ROW_COUNT;
	RAISE INFO 'removed % rows from from proc_outfile_attrs ', nbr_rec;

	DELETE FROM proc_request_outfile WHERE trail_id IN ( SELECT id FROM tmp_trails_id );
	GET DIAGNOSTICS nbr_rec = ROW_COUNT;
	RAISE INFO 'removed % rows from from proc_request_outfile ', nbr_rec;
	
	DELETE FROM proc_request_trail WHERE request_id IN ( SELECT id FROM tmp_procrequests_id );
	GET DIAGNOSTICS nbr_rec = ROW_COUNT;
	RAISE INFO 'removed % rows from from proc_request_trail ', nbr_rec;


	DELETE FROM web_request_requests WHERE web_request_id = reqId;
	GET DIAGNOSTICS nbr_rec = ROW_COUNT;
	RAISE INFO 'removed % rows from from web_request_requests ', nbr_rec;
	
	DELETE FROM proc_request WHERE request_id IN ( SELECT id FROM tmp_procrequests_id );
	GET DIAGNOSTICS nbr_rec = ROW_COUNT;
	RAISE INFO 'removed % rows from from proc_request ', nbr_rec;	
	
	DELETE FROM web_bundlefile WHERE web_request_id = reqId;
	DELETE FROM web_notification WHERE web_request_id = reqId;
	DELETE FROM web_request_counter WHERE request_id = reqId;
	DELETE FROM web_request_info WHERE request_id = reqId;
	
	DELETE FROM web_request WHERE request_id = reqId;
	RAISE INFO 'removed web_request... restoring constraints';
	
	ALTER TABLE web_request_requests ADD CONSTRAINT fk82d7b13f6b880d74 
	      FOREIGN KEY (proc_request_id) REFERENCES proc_request(request_id)
	      DEFERRABLE INITIALLY DEFERRED;

	ALTER TABLE proc_request_trail ADD CONSTRAINT fk8e37eaed1f20a4eb
	      FOREIGN KEY (request_id) REFERENCES proc_request(request_id)
	      DEFERRABLE INITIALLY DEFERRED;
	
	ALTER TABLE proc_request_infile ADD CONSTRAINT fk25ca0bfa1f20a4eb
	      FOREIGN KEY (request_id) REFERENCES proc_request(request_id)
	      DEFERRABLE INITIALLY DEFERRED;
	      
	ALTER TABLE proc_request_info ADD CONSTRAINT fka9baaec71f20a4eb
	      FOREIGN KEY (request_id) REFERENCES proc_request(request_id)
	      DEFERRABLE INITIALLY DEFERRED;
	
	RAISE INFO 'done. Reindexing tables';
	-- reindexing web tables
	REINDEX TABLE web_bundlefile;
	REINDEX TABLE web_request_info;
	REINDEX TABLE web_request_requests;
	
	-- reindexing tables from request_base 
	REINDEX TABLE proc_request_infile;
	REINDEX TABLE proc_request_info;
	REINDEX TABLE proc_request_trail;
	REINDEX TABLE proc_request_outfile;
	REINDEX TABLE proc_outfile_attrs;
	REINDEX TABLE proc_request;				

	RETURN 'REMOVED ID : ' || reqId::text;
END;

$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION cleanup_web_request_by_date(nbrDays INTEGER) RETURNS varchar AS $$

DECLARE
	requestInfo RECORD;
	resultString VARCHAR;
BEGIN
	resultString := 'REMOVED IDs : ';
	FOR requestInfo IN SELECT * FROM web_request WHERE start_date < current_date - nbrDays LOOP
		PERFORM cleanup_web_request(requestInfo.request_id);
		resultString := resultString || ' ' || requestInfo.request_id::text || ',';
	END LOOP;
	
	RETURN substr(resultString,1,length(resultString)-1);	
END;

$$ LANGUAGE plpgsql;



