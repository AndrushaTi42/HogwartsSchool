-- liquibase formatted sql

-- changeset atikhomirov:1

CREATE TABLE faculty(
  id    BIGSERIAL,
  name TEXT,
  color TEXT
)

-- changeset atikhomirov:2
CREATE INDEX faculty_name_color_index ON faculty (name, color);