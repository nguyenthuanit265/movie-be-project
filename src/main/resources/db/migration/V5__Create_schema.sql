CREATE TABLE roles
(
    id    BIGINT NOT NULL AUTO_INCREMENT,
    code  VARCHAR(255),
    label VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    email    VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    name     VARCHAR(255),
    PRIMARY KEY (id)
);


CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE INDEX idx_user_email ON users (email);

CREATE INDEX idx_role_code ON roles (code);