-- Create tables
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX idx_users_username ON users (username);

CREATE TABLE roles
(
    id        SERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_roles
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE refresh_tokens
(
    id          SERIAL PRIMARY KEY,
    token       VARCHAR(255),
    user_id     INT NOT NULL,
    expiry_date TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE products
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL UNIQUE,
    price    float        NOT NULL,
    quantity integer      NOT NULL
);

CREATE UNIQUE INDEX idx_products_name ON products (name);

INSERT INTO roles (role_name)
VALUES ('ADMIN'),
       ('USER'),
       ('PRODUCT_MANAGER');

INSERT INTO users(username, password)
-- bcrypt hash of admin123
VALUES ('admin', '$2a$12$HdF7ckVhyOUJ7w5hGohPf.iylz3N6zdM1XeTuK.IBHm9hlQNkJjVG');
-- bcrypt hash of pm123
INSERT INTO users(username, password)
VALUES ('product_manager', '$2a$12$LTKxbcSzhg77glgA9Zje2ek1PjLI2vBZ2UOZPBkfOTR84cJvgCJxe');

-- Grant roles to users
-- admin -> ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.role_name = 'ADMIN'
WHERE u.username = 'admin';

-- product_manager -> PRODUCT_MANAGER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.role_name = 'PRODUCT_MANAGER'
WHERE u.username = 'product_manager';