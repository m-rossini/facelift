
-- table: WEB_USER
create table WEB_USER (
	USER_ID			integer 		NOT NULL,
	SECURITY_ID		varchar2(128)	NOT NULL,
	USER_EMAIL		varchar2(512)	NOT NULL,
	USER_NAME		varchar2(512)			,
	LOGIN_TRIES		integer 		NOT NULL
);

alter table WEB_USER add constraint WEB_USER_PK
      primary key (USER_ID);
      
alter table WEB_USER add constraint WEB_USER_UK1
      unique (SECURITY_ID);

alter table WEB_USER add constraint WEB_USER_UK2
      unique (USER_EMAIL);
      
create index WEB_USER_IDX1 on WEB_USER (USER_NAME);

create sequence USER_SEQUENCE start with 1;
