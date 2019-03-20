CREATE TABLE  IF NOT EXISTS user(
	id INTEGER GENERATED BY DEFAULT AS IDENTITY(start with 1,increment by 1),
    name varchar(128),
    gender INTEGER,
    age INTEGER,
    birthday date,
    degree INTEGER,
    cellphone varchar(128),
    email varchar(128),
    hobby INTEGER,
    intro varchar(256),
    lastUpdTime timestamp,
    PRIMARY KEY (id)
);