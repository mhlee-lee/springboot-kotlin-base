DROP TABLE IF EXISTS my_table1;
DROP TABLE IF EXISTS address_scopes;

CREATE TABLE IF NOT EXISTS my_table1
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
),
    age INTEGER
    );

CREATE TABLE IF NOT EXISTS address_scopes
(
    id
    VARCHAR
(
    255
) PRIMARY KEY,
    vpc_id VARCHAR
(
    255
) NOT NULL,
    status INTEGER,
    address_type CHAR
(
    8
),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );
