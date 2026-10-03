-- liquibase formatted sql

-- changeset atikhomirov:1

CREATE TABLE avatar(
  id    BIGSERIAL,
  "data" OID,
  file_path TEXT,
  file_size BIGINT,
  media_type TEXT,
  student_id BIGINT
)

