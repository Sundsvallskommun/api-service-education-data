CREATE TABLE education_provider (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    education_provider_id  VARCHAR(64),
    name                   VARCHAR(255),
    url                    VARCHAR(255),
    city                   VARCHAR(100),
    municipality_id        VARCHAR(6),
    created_at             DATE,
    CONSTRAINT uq_education_provider UNIQUE (education_provider_id, created_at)
) ENGINE=InnoDB;