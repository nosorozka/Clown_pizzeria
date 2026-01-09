-- -----------------------------------------------------
-- Initial Data
-- -----------------------------------------------------

-- Insert roles (ENUM values)
INSERT INTO `mydb`.`roles` (`name`) VALUES 
('ROLE_USER'), 
('ROLE_ADMIN'),
('ROLE_COOK'),
('ROLE_COURIER');

-- Insert sizes
INSERT INTO `mydb`.`size` (`name`, `price`) VALUES 
('Malá', 0.00),
('Stredná', 2.50),
('Veľká', 5.00);

-- Insert tags
INSERT INTO `mydb`.`tags` (`name`) VALUES 
('Vegetariánska'),
('Ostrá'),
('Obľúbená'),
('Novinka'),
('Klasická');

-- Insert sample ingredients
INSERT INTO `mydb`.`ingredients` (`name`, `price`) VALUES 
('Mozzarella', 1.50),
('Paradajková omáčka', 0.50),
('Pepperoni', 2.00),
('Šampiňóny', 1.00),
('Olivy', 1.00),
('Cibuľa', 0.50),
('Paprika', 1.00),
('Šunka', 2.00),
('Ananás', 1.50),
('Slanina', 2.50);

-- Insert sample pizzas
INSERT INTO `mydb`.`pizza` (`name`, `pizza_price`, `description`,`image_path`) VALUES

('Capricciosa', 10.99, 'Tradičná pizza so šunkou, šampiňónmi, mozzarellou a paradajkovou omáčkou', 'capricciosa.webp'),

('Carbonara', 11.49, 'Krémová pizza inšpirovaná carbonara s pancettou, syrom a jemnou smotanovou omáčkou', 'carbonara-pizza.webp'),

('Delicatezza Rustica', 12.49, 'Rustikálna pizza s prosciuttom, sušenými paradajkami, rukolou a parmezánom', 'delicatezza-rustica.webp'),

('Diavola Piccante', 11.99, 'Pikantná pizza s feferónkami, salámou a výraznou paradajkovou omáčkou', 'diavola-piccante.webp'),

('Funghi', 10.49, 'Jednoduchá a obľúbená pizza so šampiňónmi a mozzarellou', 'funghi.webp'),

('Funghi al Panna', 10.99, 'Pizza so šampiňónmi na jemnej smotanovej omáčke s mozzarellou', 'funghi-al-panna.webp'),

('Gluten Free Primavera', 12.99, 'Bezlepková pizza so sezónnou zeleninou, ľahkou omáčkou a mozzarellou', 'gluten-free-primavera.webp'),

('Havajská', 11.99, 'Kombinácia šunky a ananásu na paradajkovom základe s mozzarellou', 'hawaii-classic.webp'),

('La Crema Bianca', 11.79, 'Biela pizza so smotanovou omáčkou, syrom mozzarella a jemným cesnakom', 'la-crema-bianca.webp'),

('Margherita Classica', 9.99, 'Klasická talianska pizza s paradajkovou omáčkou, mozzarellou a bazalkou', 'margherita-classica.webp')
;

-- Link pizzas with ingredients
INSERT INTO `mydb`.`pizza_has_ingredients` (`pizza_id`, `ingredients_id`) VALUES 
(1, 1), (1, 2),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 8), (3, 9),
(4, 1), (4, 2), (4, 4), (4, 5), (4, 6), (4, 7),
(5, 1), (5, 2), (5, 3), (5, 8), (5, 10);

-- Link pizzas with sizes (all pizzas available in all sizes)
INSERT INTO `mydb`.`pizza_has_size` (`pizza_id`, `size_id`) VALUES 
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 3),
(4, 1), (4, 2), (4, 3),
(5, 1), (5, 2), (5, 3);

-- Link pizzas with tags
INSERT INTO `mydb`.`pizza_has_tags` (`pizza_id`, `tags_id`) VALUES 
(1, 1), (1, 5),
(2, 3), (2, 5),
(3, 4),
(4, 1),
(5, 2), (5, 3);

-- Insert staff users (password: Admin123! - BCrypt encoded)
INSERT INTO `mydb`.`users` (`first_name`, `last_name`, `email`, `password`, `created_at`, `updated_at`, `roles_id`) VALUES 
('Admin', 'Používateľ', 'admin@pizza.com', '$2a$10$VxgvANRs8lhXIJ4t11mR9evYy8FyTfjXYX8r.DDSxCh9nZOWSsE5G', CURDATE(), CURDATE(), 2),
('Kuchár', 'Hlavný', 'kuchar@pizza.com', '$2a$10$VxgvANRs8lhXIJ4t11mR9evYy8FyTfjXYX8r.DDSxCh9nZOWSsE5G', CURDATE(), CURDATE(), 3),
('Kuriér', 'Rýchly', 'kurier@pizza.com', '$2a$10$VxgvANRs8lhXIJ4t11mR9evYy8FyTfjXYX8r.DDSxCh9nZOWSsE5G', CURDATE(), CURDATE(), 4);
