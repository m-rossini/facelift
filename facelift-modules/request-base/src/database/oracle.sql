
-- table: PROC_REQUEST
create table PROC_REQUEST (
	REQUEST_ID 			integer			NOT NULL,
	REQUEST_LABEL    	varchar2(256)			,
	LATEST_STATUS   	integer			NOT NULL
);

alter table PROC_REQUEST add constraint PROC_REQUEST_PK 
      primary key (REQUEST_ID);

create index PROC_REQUEST_IDX1 on PROC_REQUEST (REQUEST_LABEL);

create sequence PROC_REQUEST_SEQUENCE start with 1;


-- table: PROC_REQUEST_INFO
create table PROC_REQUEST_INFO (
	REQUEST_ID      	integer			NOT NULL,
	INFO_KEY			varchar2(64)	NOT NULL,
	INFO_VALUE			varchar2(256)				
);

alter table PROC_REQUEST_INFO add constraint PROC_REQUEST_INFO_PK 
      primary key (REQUEST_ID, INFO_KEY);

alter table PROC_REQUEST_INFO add constraint PROC_REQUEST_INFO_FK1
      foreign key (REQUEST_ID) references PROC_REQUEST (REQUEST_ID);


-- table: PROC_REQUEST_INFILE
create table PROC_REQUEST_INFILE (
	FILENAME			varchar2(512)	NOT NULL,
	REQUEST_ID			integer			NOT NULL
);

alter table PROC_REQUEST_INFILE add constraint PROC_REQUEST_INFILE_PK
      primary key  (FILENAME, REQUEST_ID);

alter table PROC_REQUEST_INFILE add constraint PROC_REQUEST_INFILE_FK1
      foreign key (REQUEST_ID) references PROC_REQUEST (REQUEST_ID);

create index PROC_REQUEST_INFILE_IDX1 on PROC_REQUEST_INFILE (REQUEST_ID);


-- table: PROC_REQUEST_TRAIL
create table PROC_REQUEST_TRAIL (
	TRAIL_ID			integer			NOT NULL,
	REQUEST_ID			integer			NOT NULL,
	STATUS_ID			integer			NOT NULL,
	TRAIL_DATETIME		date			NOT NULL,
	TRAIL_MESSAGE		varchar2(512)			
);

alter table PROC_REQUEST_TRAIL add constraint PROC_REQUEST_TRAIL_PK
      primary key  (TRAIL_ID);

alter table PROC_REQUEST_TRAIL add constraint PROC_REQUEST_TRAIL_FK1
      foreign key (REQUEST_ID) references PROC_REQUEST (REQUEST_ID);
      
create index PROC_REQUEST_TRAIL_IDX1 on PROC_REQUEST_TRAIL (REQUEST_ID);      

create sequence PROC_TRAIL_SEQUENCE start with 1;


-- table: PROC_REQUEST_OUTFILE
create table PROC_REQUEST_OUTFILE (
	FILE_ID				integer			NOT NULL,
	TRAIL_ID			integer			NOT NULL,
	FILENAME			varchar2(512)	NOT NULL
);

alter table PROC_REQUEST_OUTFILE add constraint PROC_REQUEST_OUTFILE_PK
      primary key  (FILE_ID);

alter table PROC_REQUEST_OUTFILE add constraint PROC_REQUEST_OUTFILE_FK1
      foreign key (TRAIL_ID) references PROC_REQUEST_TRAIL (TRAIL_ID);
      
create index PROC_REQUEST_OUTFILE_IDX1 on PROC_REQUEST_OUTFILE (TRAIL_ID);

create index PROC_REQUEST_OUTFILE_IDX2 on PROC_REQUEST_OUTFILE (FILENAME);      

create sequence OUTFILE_SEQUENCE start with 1;


-- table: PROC_OUTFILE_ATTRS
create table PROC_OUTFILE_ATTRS (
	FILE_ID				integer			NOT NULL,
	ATTR_KEY			varchar2(64)	NOT NULL,
	ATTR_VALUE			varchar2(256)			
);

alter table PROC_OUTFILE_ATTRS add constraint PROC_OUTFILE_ATTRS_PK
      primary key (FILE_ID, ATTR_KEY);

alter table PROC_OUTFILE_ATTRS add constraint PROC_REQUEST_ATTRS_FK1
      foreign key (FILE_ID) references PROC_REQUEST_OUTFILE (FILE_ID);
