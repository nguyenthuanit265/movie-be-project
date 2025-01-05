-- db_spring_security.roles definition

CREATE TABLE `roles`
(
    `id`    bigint NOT NULL AUTO_INCREMENT,
    `code`  varchar(255) DEFAULT NULL,
    `label` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- db_spring_security.users definition

CREATE TABLE `users`
(
    `id`       bigint NOT NULL AUTO_INCREMENT,
    `email`    varchar(255) DEFAULT NULL,
    `name`     varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- db_spring_security.users_roles definition

CREATE TABLE `users_roles`
(
    `role_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`role_id`, `user_id`),
    KEY       `FK2o0jvgh89lemvvo17cbqvdxaa` (`user_id`),
    CONSTRAINT `FK2o0jvgh89lemvvo17cbqvdxaa` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `FKj6m8fwv7oqv74fcehir1a9ffy` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;