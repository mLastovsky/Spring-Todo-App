CREATE TABLE todos
(
    id               BIGSERIAL PRIMARY KEY,
    description      TEXT        NOT NULL,
    status           VARCHAR(20) NOT NULL     DEFAULT 'PENDING',
    user_id          BIGINT      NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_todo_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
