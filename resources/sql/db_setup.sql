-- run as root
create user 'jawaab_app'@'localhost' identified by 'prashan';
create database jawaab;
grant all on jawaab.* to 'jawaab_app'@'localhost';