-- ================================================
-- V1: Initial Schema for Employee Management System
-- ================================================

-- Sequences for auto-generated codes
CREATE SEQUENCE emp_code_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE dept_code_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE pos_code_seq START WITH 1 INCREMENT BY 1;

-- ================================================
-- Users Table
-- ================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- ================================================
-- Departments Table
-- ================================================
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    department_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- ================================================
-- Positions Table
-- ================================================
CREATE TABLE positions (
    id BIGSERIAL PRIMARY KEY,
    position_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    department_id BIGINT REFERENCES departments(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- ================================================
-- Employees Table
-- ================================================
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE,
    phone_number VARCHAR(30),
    gender VARCHAR(10),
    birth_date DATE,
    hire_date DATE,
    status VARCHAR(20),
    department_id BIGINT REFERENCES departments(id),
    position_id BIGINT REFERENCES positions(id),
    photo_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Indexes for employees
CREATE INDEX idx_employee_full_name ON employees(full_name);
CREATE INDEX idx_employee_employee_code ON employees(employee_code);
CREATE INDEX idx_employee_email ON employees(email);
CREATE INDEX idx_employee_gender ON employees(gender);
CREATE INDEX idx_employee_status ON employees(status);

-- ================================================
-- Audit Logs Table
-- ================================================
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_values TEXT,
    new_values TEXT,
    performed_by VARCHAR(100),
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_performed_at ON audit_logs(performed_at);
CREATE INDEX idx_audit_action ON audit_logs(action);
