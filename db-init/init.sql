-- Создание схемы
CREATE SCHEMA IF NOT EXISTS cringearium;

-- Таблица user_role
CREATE TABLE IF NOT EXISTS cringearium.user_role (
    user_role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(45) NOT NULL,
    description TEXT NOT NULL
);

-- Таблица user_account
CREATE TABLE IF NOT EXISTS cringearium.user_account (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_pic oid,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    user_role_id INT NOT NULL,
    CONSTRAINT fk_user_user_role FOREIGN KEY (user_role_id)
        REFERENCES cringearium.user_role (user_role_id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- Таблица chat
CREATE TABLE IF NOT EXISTS cringearium.chat (
    chat_id SERIAL PRIMARY KEY,
    chat_name VARCHAR(45) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблица user_chat
CREATE TABLE IF NOT EXISTS cringearium.user_chat (
    chat_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (chat_id, user_id),
    CONSTRAINT fk_user_chat_chat FOREIGN KEY (chat_id)
        REFERENCES cringearium.chat (chat_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_chat_user FOREIGN KEY (user_id)
        REFERENCES cringearium.user_account (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица message
CREATE TABLE IF NOT EXISTS cringearium.message (
    message_id SERIAL PRIMARY KEY,
    content oid NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT now(),
    user_id INT,
    chat_id INT NOT NULL,
    is_ai_response BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_message_user FOREIGN KEY (user_id)
        REFERENCES cringearium.user_account (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_message_chat FOREIGN KEY (chat_id)
        REFERENCES cringearium.chat (chat_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица course
CREATE TABLE IF NOT EXISTS cringearium.course (
    course_id SERIAL PRIMARY KEY,
    course_name VARCHAR(45) NOT NULL,
    course_theme VARCHAR(45) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    price INT NOT NULL,
    course_description TEXT,
    course_image oid
);

-- Таблица course_data
CREATE TABLE IF NOT EXISTS cringearium.course_data (
    course_data_id SERIAL PRIMARY KEY,
    content oid NOT NULL,
    course_id INT NOT NULL,
    CONSTRAINT fk_course_data_course FOREIGN KEY (course_id)
        REFERENCES cringearium.course (course_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица order_table
CREATE TABLE IF NOT EXISTS cringearium.order_table (
    order_id SERIAL PRIMARY KEY,
    status VARCHAR(45) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    course_id INT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT fk_order_course FOREIGN KEY (course_id)
        REFERENCES cringearium.course (course_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id)
        REFERENCES cringearium.user_account (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица user_course
CREATE TABLE IF NOT EXISTS cringearium.user_course (
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    PRIMARY KEY (user_id, course_id),
    CONSTRAINT fk_user_course_user FOREIGN KEY (user_id)
        REFERENCES cringearium.user_account (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_course_course FOREIGN KEY (course_id)
        REFERENCES cringearium.course (course_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);



INSERT INTO cringearium.user_role (role_name, description) VALUES
    ('Admin', 'Администратор системы'),
    ('Curator', 'Куратор курсов'),
    ('Student', 'Студент, рядовой пользователь');

INSERT INTO cringearium.user_account (username, email, password_hash, user_role_id) VALUES
    ('admin1', 'admin@gmail.com', '$2a$10$YTUdWtqQXwD0Vtbec56c/OthCcvN6iGLbeN5xQHv71WdbO9L9AM5G', 1);

INSERT INTO cringearium.course (course_name, course_theme, price, course_description) VALUES
    ('C++. Крещение огнем', 'Программирование', 4990, 'F в чат всем, кто запишется'),
    ('Окей, летс GO', 'Программирование', 3990, 'Хочешь быть модным? Учи GO'),
    ('Введение в мемологию', 'Мемы', 4200, 'Докажите всему миру, что вы никогда в жизни не трогали траву!'),
    ('Курс Java', 'Программирование', 5000, 'Для тех, кому нечего терять'),
    ('Python для хомячков', 'Программирование', 4990, 'Введение в самый нормисный язык программирования'),
    ('Хроника итальянского брейнрота', 'Мемы', 4990, 'Бомбини курсини'),
    ('Пособие по борьбе с кибербуллингом', 'Психология', 3990, 'Гайд о том, как выключить компьютер');

