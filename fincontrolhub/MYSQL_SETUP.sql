-- MySQL Setup Script for Finance Dashboard Application
-- Run this in your MySQL client or cmd

-- Create the database
CREATE DATABASE IF NOT EXISTS finance_db;

-- Use the database
USE finance_db;

-- Tables will be created automatically by Hibernate DDL-auto: update
-- But you can optionally create them manually for better control

-- Create User table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create FinancialRecord table
CREATE TABLE IF NOT EXISTS financial_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    date DATE NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create default admin user (password: admin123 - BCrypt encoded)
INSERT INTO user (username, password, active, role) VALUES 
('admin', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy3SAMM', true, 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;

-- Show tables to verify creation
SHOW TABLES;

-- Display created tables structure
DESCRIBE user;
DESCRIBE financial_record;
