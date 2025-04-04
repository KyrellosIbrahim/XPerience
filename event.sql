-- create database
create database if not exists ibrahim;
use ibrahim;

-- create user for db
CREATE USER IF NOT EXISTS 'xperience_user'@'192.168.%' IDENTIFIED BY 'Strong!Password';
GRANT INSERT, SELECT  ON  ibrahim.* TO 'xperience_user'@'192.168.%';

FLUSH PRIVILEGES;

-- delete Event table if it exists to start new
DROP TABLE IF EXISTS Event;

-- create Event table
create table if not exists Event (
     id int auto_increment primary key,
     name varchar(300) not null unique CHECK (CHAR_LENGTH(name) >= 1),
     date DATE not null,
     time TIME not null,
     description text not null  CHECK (CHAR_LENGTH(description) >= 1)
);

-- insert row into table
insert into Event (name, date, time, description) VALUES
('Sample', '2025-02-19', '12:00:00', 'Just to put an event in');

select 'database created successfully' AS Message;
