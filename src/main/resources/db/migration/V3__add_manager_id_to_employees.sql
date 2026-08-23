-- ================================================
-- V3: Add manager_id self-referencing FK to employees
-- ================================================

ALTER TABLE employees
    ADD COLUMN manager_id BIGINT;

ALTER TABLE employees
    ADD CONSTRAINT fk_employee_manager
    FOREIGN KEY (manager_id) REFERENCES employees(id);

CREATE INDEX idx_employee_manager_id ON employees(manager_id);
