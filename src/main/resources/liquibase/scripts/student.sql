-- liquibase formatted sql

-- changeset atikhomirov:1

CREATE TABLE student(
  id    BIGSERIAL,
  name TEXT,
  age INTEGER,
  course INTEGER,
  faculty_id BIGINT
)
-- changeset atikhomirov:2
CREATE INDEX student_name_index ON student (name);
