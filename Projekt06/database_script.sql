-- Скрипт создания базы данных для проекта кредитного калькулятора
-- MariaDB/MySQL

CREATE DATABASE IF NOT EXISTS simpledb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE simpledb;

-- Таблица для хранения результатов кредитных расчетов
CREATE TABLE IF NOT EXISTS loan_result (
    idloan_result INT AUTO_INCREMENT PRIMARY KEY,
    kwota DECIMAL(15,2) NOT NULL,
    okres DECIMAL(10,2) NOT NULL,
    procent DECIMAL(5,2) NOT NULL,
    result DECIMAL(15,2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_created_at (created_at),
    INDEX idx_kwota (kwota)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

