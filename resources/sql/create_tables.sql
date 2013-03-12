-- create users table
create table users (
  id integer not null auto_increment,
  email varchar(255) not null,
  password varchar(255) not null,
  -- contraints
  primary key(id)
) DEFAULT CHARSET=utf8;

-- create threads table
create table threads (
  id integer not null auto_increment,
  user_id integer not null,
  title text not null,
  stime datetime not null,
  -- constraints
  foreign key(user_id) references users(id),
  primary key(id)
) DEFAULT CHARSET=utf8;

-- create posts table
create table posts (
  id integer not null auto_increment,
  type char not null,
  body text not null,
  stime datetime not null,
  user_id integer,
  thread_id integer,
  -- constraints
  foreign key(user_id)references user(id),
  foreign key(thread_id) references thread(id),
  primary key(id)
) DEFAULT CHARSET=utf8;

-- create tags table
create table tags (
  id integer not null auto_increment,
  name varchar(255) not null,
  primary key(id)
) DEFAULT CHARSET=utf8;

