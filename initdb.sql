-- Create tables
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE roles
(
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(255)
);

CREATE TABLE user_roles
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

WITH
    new_roles AS (
INSERT INTO roles (role_name)
VALUES ('ADMIN'), ('USER')
    RETURNING id, role_name
    ),
    new_admin AS (
INSERT INTO users (username, password)
VALUES ('admin', 'admin')
    RETURNING id
    ),
    new_user AS (
INSERT INTO users (username, password)
VALUES ('user', 'user')
    RETURNING id
    )
INSERT INTO user_roles (user_id, role_id)
SELECT na.id, nr.id
FROM new_admin na
         JOIN new_roles nr ON nr.role_name = 'ADMIN'

UNION ALL

SELECT nu.id, nr.id
FROM new_user nu
         JOIN new_roles nr ON nr.role_name = 'USER';
