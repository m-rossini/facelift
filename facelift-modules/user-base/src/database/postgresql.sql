
-- table: WEB_USER
create table WEB_USER (
	USER_ID			int8	 		NOT NULL,
	SECURITY_ID		varchar(128)	NOT NULL,
	USER_EMAIL		varchar(512)	NOT NULL,
	USER_NAME		varchar(512)			,
	LOGIN_TRIES		int4	 		NOT NULL
);

alter table WEB_USER add constraint WEB_USER_PK
      primary key (USER_ID);
      
alter table WEB_USER add constraint WEB_USER_UK1
      unique (SECURITY_ID);

alter table WEB_USER add constraint WEB_USER_UK2
      unique (USER_EMAIL);
      
create index WEB_USER_IDX1 on WEB_USER (USER_NAME);

create sequence USER_SEQUENCE start with 1;
