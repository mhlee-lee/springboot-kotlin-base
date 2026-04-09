INSERT INTO "my_table1" ("name", "age")
VALUES ('Alice', 31),
       ('Bob', 27);

INSERT INTO "address_scopes" ("id", "vpc_id", "status", "created_at", "updated_at")
VALUES ('scope-1', 'vpc-001', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('scope-2', 'vpc-002', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
