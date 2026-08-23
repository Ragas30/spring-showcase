-- ================================================
-- V4: Create missing code sequences
-- V1 was skipped because the database was baselined,
-- so these sequences were never created.
-- ================================================

CREATE SEQUENCE IF NOT EXISTS emp_code_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS dept_code_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS pos_code_seq START WITH 1 INCREMENT BY 1;

-- Sync each sequence past any codes already in the data

SELECT setval('emp_code_seq',
    GREATEST(
        COALESCE((SELECT MAX(CAST(REGEXP_REPLACE(employee_code, '\D', '', 'g') AS BIGINT))
                  FROM employees
                  WHERE employee_code LIKE 'EMP%'), 0) + 1,
        1),
    false);

SELECT setval('dept_code_seq',
    GREATEST(
        COALESCE((SELECT MAX(CAST(REGEXP_REPLACE(department_code, '\D', '', 'g') AS BIGINT))
                  FROM departments
                  WHERE department_code LIKE 'DEPT%'), 0) + 1,
        1),
    false);

SELECT setval('pos_code_seq',
    GREATEST(
        COALESCE((SELECT MAX(CAST(REGEXP_REPLACE(position_code, '\D', '', 'g') AS BIGINT))
                  FROM positions
                  WHERE position_code LIKE 'POS%'), 0) + 1,
        1),
    false);
