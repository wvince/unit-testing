
create table email_outbox (id int8 not null, body varchar(255), recipient_email varchar(255), status varchar(255), subject varchar(255), version int8, primary key (id));