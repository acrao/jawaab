-- create users table
create table users (
  id integer not null auto_increment,
  email varchar(255) not null,
  password varchar(255) not null,
  name varchar(255) not null,
  handle varchar(255) not null,
  -- contraints
  primary key(id)
) DEFAULT CHARSET=utf8;

-- create posts table
create table posts (
  id integer not null auto_increment,
  type char not null,
  title text null,
  body text not null,
  stime datetime not null,
  user_id integer,
  parent integer,
  -- constraints
  foreign key(user_id)references user(id),
  primary key(id)
) DEFAULT CHARSET=utf8;

-- create tags table
create table tags (
  id integer not null auto_increment,
  name varchar(255) not null,
  -- constraints
  primary key(id)
) DEFAULT CHARSET=utf8;

-- create post_tags table
create table post_tags (
  post_id integer not null,
  tag_id integer not null,
  -- constraints
  foreign key(post_id) references posts (id),
  foreign key(tag_id) references tags(id)
) DEFAULT CHARSET=utf8;

-- create post_votes table
create table post_votes (
  post_id integer not null,
  user_id integer not null,
  -- constraints
  foreign key(post_id) references posts(id),
  foreign key(user_id) references users(id)
) DEFAULT CHARSET=utf8;

