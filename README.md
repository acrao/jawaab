# Jawaab

A simple FAQ website. Features include : 

 * User sign up/sign in
 * Post taggging and voting

## Usage

Db Setup
---------

Jawaab uses mysql as a datastore. In order to setup the db for first time access, you will need to run the following sql
scripts

```bash
mysql -uroot < resources/setup/db_setup.sql
mysql -ujawaab_app -p -D jawaab < resources/setup/create_tables.sql
```

Running the app
--------------

If you use cake, substitute 'lein' with 'cake' below. Everything should work fine.

```bash
lein do clean, deps, compile
lein run
`````

## License

Copyright (C) 2013 Aditya Rao

Distributed under the Eclipse Public License, the same as Clojure.

