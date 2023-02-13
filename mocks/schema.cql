
DROP KEYSPACE IF EXISTS poc_users;
CREATE KEYSPACE poc_users WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

use poc_users;

// User credentials, keyed by email address so we can authenticate
// Separated from user in case auth is external (Google, Facebook, etc...)
CREATE TABLE user_credentials (
   email text,
   password text,
   userid uuid,
   PRIMARY KEY (email)
);

// Basic entity table for a user
// UUID for userid to link to auth system
CREATE TABLE users (
   userid uuid,
   firstname varchar,
   lastname varchar,
   email text,
   created_date timestamp,
   PRIMARY KEY (userid)
);

// Entity table to store refresh tokens
CREATE TABLE user_login_tokens (
   userid uuid,
   token uuid,
   created_date timestamp,
   token_data text,
   PRIMARY KEY (userid, token)
) WITH CLUSTERING ORDER BY (added_date DESC);
