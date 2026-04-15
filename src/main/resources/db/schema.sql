CREATE TABLE IF NOT EXISTS samples
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    age INTEGER NOT NULL
    );
