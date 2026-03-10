TRUNCATE TABLE payment_cards RESTART IDENTITY;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

INSERT INTO users (name, surname, birth_date, email, active, created_at, updated_at)
VALUES
('Ivan', 'Ivanov', '1990-05-15', 'ivan@example.com', true, NOW(), NOW()),
('Maria', 'Petrova', '1992-08-22', 'maria@example.com', true, NOW(),NOW()),
('Alexey', 'Sidorov', '1985-12-01', 'alex@example.com', false, NOW(),NOW()),
('Elena', 'Kuznetsova', '1995-03-10', 'elena@example.com', true, NOW(),NOW()),
('Dmitry', 'Smirnov', '1988-07-30', 'dmitry@example.com', true, NOW(),NOW()),
('Olga', 'Popova', '1993-11-25', 'olga@example.com', true, NOW(), NOW()),
('Sergey', 'Volkov', '1980-01-05', 'sergey@example.com', true, NOW(),NOW()),
('Anna', 'Sokolova', '1998-09-14', 'anna@example.com', true, NOW(),NOW()),
('Pavel', 'Morozov', '1987-04-20', 'pavel@example.com', false, NOW(),NOW()),
('Natalia', 'Novikova', '1991-06-18', 'natalia@example.com', true, NOW(),NOW());
INSERT INTO payment_cards (user_id, number, holder, expiration_date, active, created_at, updated_at)
VALUES
(1, '4242424242424242', 'IVAN IVANOV', '2025-12-01', true, NOW(), NOW()),
(1, '5555444433332222', 'IVAN IVANOV', '2026-06-01', true, NOW(), NOW()),
(2, '1111222233334444', 'MARIA PETROVA', '2024-10-01', true, NOW(), NOW()),
(4, '9999888877776666', 'ELENA KUZNETSOVA', '2027-01-01', true, NOW(), NOW()),
(4, '1234123412341234', 'ELENA KUZNETSOVA', '2025-05-01', false, NOW(), NOW()),
(4, '8888777755554444', 'ELENA KUZNETSOVA', '2028-08-01', true, NOW(), NOW()),
(5, '4444555566667777', 'DMITRY SMIRNOV', '2026-03-01', true, NOW(), NOW()),
(7, '3333222211110000', 'SERGEY VOLKOV', '2025-09-01', true, NOW(), NOW()),
(8, '7777888899990000', 'ANNA SOKOLOVA', '2026-11-01', true, NOW(), NOW()),
(10, '2222333344445555', 'NATALIA NOVIKOVA', '2025-12-01', true, NOW(), NOW());