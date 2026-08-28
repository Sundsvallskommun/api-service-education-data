CREATE TABLE event_category_staging (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    education_event_id     VARCHAR(64) NOT NULL,
    direction_id           VARCHAR(64) NOT NULL,
CONSTRAINT uq_event_category_staging UNIQUE (education_event_id, direction_id),
    INDEX idx_event_category_staging_event (education_event_id)
) ENGINE=InnoDB;