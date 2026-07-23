CREATE TABLE "discord_user"
(
    id         VARCHAR(255) NOT NULL,
    money      BIGINT       NOT NULL,
    last_daily TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_user PRIMARY KEY (id)
);