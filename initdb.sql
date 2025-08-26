-- Create tables
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE roles
(
    id        SERIAL PRIMARY KEY,
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

CREATE TABLE refresh_tokens
(
    id          SERIAL PRIMARY KEY,
    token       VARCHAR(255),
    user_id     INT NOT NULL,
    expiry_date TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

WITH new_roles AS (
INSERT
INTO roles (role_name)
VALUES ('ADMIN'), ('USER')
    RETURNING id, role_name
    ), new_admin AS (
INSERT
INTO users (username, password)
-- bcrypt hash of "admin123"
VALUES ('admin', '$2a$12$HdF7ckVhyOUJ7w5hGohPf.iylz3N6zdM1XeTuK.IBHm9hlQNkJjVG')
    RETURNING id
    ), new_user AS (
INSERT
INTO users (username, password)
-- bcrypt hash of "user123"
VALUES ('user', '$2a$12$NWl0F5Y.5BuBtVe4Uo/B.ueKDk02EI1U240PPWYM/GHKUwVlkbMH6')
    RETURNING id
    )
INSERT
INTO user_roles (user_id, role_id)
SELECT na.id, nr.id
FROM new_admin na
         JOIN new_roles nr ON nr.role_name = 'ADMIN'

UNION ALL

SELECT nu.id, nr.id
FROM new_user nu
         JOIN new_roles nr ON nr.role_name = 'USER';
