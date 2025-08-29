
SET FOREIGN_KEY_CHECKS=0;

-- =================================================================
-- DEPARTMENT Table
-- Stores information about different departments.
-- =================================================================

DROP TABLE IF EXISTS `DEPARTMENT`;

-- Create the DEPARTMENT table
CREATE TABLE `DEPARTMENT` (
  `DEPARTMENT_ID` INT NOT NULL,
  `DEPARTMENT_NAME` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`DEPARTMENT_ID`)
);

-- Insert data into the DEPARTMENT table
INSERT INTO `DEPARTMENT` (`DEPARTMENT_ID`, `DEPARTMENT_NAME`) VALUES
(1, 'HR'),
(2, 'Finance'),
(3, 'Engineering'),
(4, 'Sales'),
(5, 'Marketing'),
(6, 'IT');

DROP TABLE IF EXISTS `EMPLOYEE`;

-- Create the EMPLOYEE table with a foreign key to DEPARTMENT
CREATE TABLE `EMPLOYEE` (
  `EMP_ID` INT NOT NULL,
  `FIRST_NAME` VARCHAR(255) DEFAULT NULL,
  `LAST_NAME` VARCHAR(255) DEFAULT NULL,
  `DOB` DATE DEFAULT NULL,
  `GENDER` VARCHAR(10) DEFAULT NULL,
  `DEPARTMENT` INT DEFAULT NULL,
  PRIMARY KEY (`EMP_ID`),
  KEY `FK_DEPARTMENT` (`DEPARTMENT`),
  CONSTRAINT `FK_DEPARTMENT` FOREIGN KEY (`DEPARTMENT`) REFERENCES `DEPARTMENT` (`DEPARTMENT_ID`)
);

-- Insert data into the EMPLOYEE table
INSERT INTO `EMPLOYEE` (`EMP_ID`, `FIRST_NAME`, `LAST_NAME`, `DOB`, `GENDER`, `DEPARTMENT`) VALUES
(1, 'John', 'Williams', '1980-05-15', 'Male', 3),
(2, 'Sarah', 'Johnson', '1990-07-20', 'Female', 2),
(3, 'Michael', 'Smith', '1985-02-10', 'Male', 3),
(4, 'Emily', 'Brown', '1992-11-30', 'Female', 4),
(5, 'David', 'Jones', '1988-09-05', 'Male', 5),
(6, 'Olivia', 'Davis', '1995-04-12', 'Female', 1),
(7, 'James', 'Wilson', '1983-03-25', 'Male', 6),
(8, 'Sophia', 'Anderson', '1991-08-17', 'Female', 4),
(9, 'Liam', 'Miller', '1979-12-01', 'Male', 1),
(10, 'Emma', 'Taylor', '1993-06-28', 'Female', 5);

DROP TABLE IF EXISTS `PAYMENTS`;

-- Create the PAYMENTS table with a foreign key to EMPLOYEE
CREATE TABLE `PAYMENTS` (
  `PAYMENT_ID` INT NOT NULL,
  `EMP_ID` INT DEFAULT NULL,
  `AMOUNT` DECIMAL(10, 2) DEFAULT NULL,
  `PAYMENT_TIME` DATETIME DEFAULT NULL,
  PRIMARY KEY (`PAYMENT_ID`),
  KEY `FK_EMPLOYEE` (`EMP_ID`),
  CONSTRAINT `FK_EMPLOYEE` FOREIGN KEY (`EMP_ID`) REFERENCES `EMPLOYEE` (`EMP_ID`)
);

-- Insert data into the PAYMENTS table
INSERT INTO `PAYMENTS` (`PAYMENT_ID`, `EMP_ID`, `AMOUNT`, `PAYMENT_TIME`) VALUES
(1, 2, 65784.00, '2025-01-01 13:44:12'),
(2, 4, 62736.00, '2025-01-06 18:36:37'),
(3, 1, 69437.00, '2025-01-01 10:19:21'),
(4, 3, 67183.00, '2025-01-02 17:21:57'),
(5, 2, 66273.00, '2025-02-01 11:49:15'),
(6, 5, 71475.00, '2025-01-01 07:24:14'),
(7, 1, 70837.00, '2025-02-03 19:11:31'),
(8, 6, 69628.00, '2025-01-02 10:41:15'),
(9, 4, 71876.00, '2025-02-01 12:16:47'),
(10, 3, 70098.00, '2025-02-03 10:11:17'),
(11, 6, 67827.00, '2025-02-02 19:21:27'),
(12, 5, 69871.00, '2025-02-05 17:54:17'),
(13, 2, 72984.00, '2025-03-05 09:37:35'),
(14, 1, 67982.00, '2025-03-01 06:09:51'),
(15, 6, 70198.00, '2025-03-02 10:34:35'),
(16, 4, 74998.00, '2025-03-02 09:27:26');

-- Re-enable foreign key checks after data insertion is complete
SET FOREIGN_KEY_CHECKS=1;


SELECT 
    e1.EMP_ID,
    e1.FIRST_NAME,
    e1.LAST_NAME,
    d.DEPARTMENT_NAME,
    COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
FROM EMPLOYEE e1
JOIN DEPARTMENT d 
    ON e1.DEPARTMENT = d.DEPARTMENT_ID
LEFT JOIN EMPLOYEE e2
    ON e1.DEPARTMENT = e2.DEPARTMENT
   AND e2.DOB > e1.DOB  -- younger employees have later DOB
GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME
ORDER BY e1.EMP_ID DESC;
