-- Database Setup Script for Web Calendar
-- Run this script to create the database

-- Create the database (run this as postgres superuser)
CREATE DATABASE webcalendar;

-- Connect to the database
\c webcalendar;

-- The tables will be automatically created by Spring Boot JPA
-- when the application starts with spring.jpa.hibernate.ddl-auto=update

-- Verify the database is ready
SELECT version();
