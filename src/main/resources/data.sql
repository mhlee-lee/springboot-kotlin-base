INSERT INTO "my_table1" ("name", "age")
VALUES ('Alice', 31),
       ('Bob', 27);

INSERT INTO "address_scopes" ("id", "vpc_id", "status", "address_type", "created_at", "updated_at")
VALUES ('scope-1', 'vpc-001', 1, "ATEN0001", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('scope-2', 'vpc-002', 0, "ATEN0002", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
