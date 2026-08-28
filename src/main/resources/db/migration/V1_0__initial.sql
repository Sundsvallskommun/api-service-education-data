CREATE TABLE susa_education_event_page_raw (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    json_body              LONGBLOB NOT NULL,
    page                   INTEGER(6) NOT NULL,
    date_collected         DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE susa_education_info_page_raw (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    json_body              LONGBLOB NOT NULL,
    page                   INTEGER(6) NOT NULL,
    date_collected         DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE susa_education_provider_page_raw (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    json_body              LONGBLOB NOT NULL,
    page                   INTEGER(6) NOT NULL,
    date_collected         DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE education_event (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    title                  VARCHAR(255),
    education_event_id     VARCHAR(64),
    education_info_id      VARCHAR(64),
    education_provider_id  VARCHAR(64),
    city                   VARCHAR(100),
    municipality_id        VARCHAR(6),
    course_post_url        VARCHAR(255),
    seats                  INT,
    currency_type          VARCHAR(10),
    cost                   DECIMAL(10,2),
    lecture_type           VARCHAR(32),
    study_pace             VARCHAR(10),
    language               VARCHAR(32),
    start_date             DATE,
    end_date               DATE,
    application_start_date DATE,
    application_end_date   DATE,
    created_at             DATE,
    cancelled                BOOLEAN,
    INDEX idx_education_event_edu_id (education_event_id),
    INDEX idx_education_event_info_id (education_info_id)
) ENGINE=InnoDB;

CREATE TABLE education_info (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    education_info_id      VARCHAR(64),
    title                  VARCHAR(255),
    code                   VARCHAR(32),
    school_type            VARCHAR(32),
    education_type         VARCHAR(100),
    description            TEXT,
    education_eligibility  TEXT,
    recommended_prior_knowledge TEXT,
    credit_type            VARCHAR(10),
    credits                DOUBLE(10,2),
    duration               VARCHAR(10),
    result_is_degree       BOOLEAN,
    expires                DATETIME,
    created_at             DATE
) ENGINE=InnoDB;

CREATE TABLE reference_category (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    category_id            VARCHAR(64) NOT NULL,
    category_name          VARCHAR(50),
    direction_id           VARCHAR(64) NOT NULL,
    direction_name         VARCHAR(50),
CONSTRAINT uq_reference_category_direction UNIQUE (category_id, direction_id),
    INDEX idx_reference_category_direction (direction_id)
) ENGINE=InnoDB;

CREATE TABLE event_category (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    education_event_id     VARCHAR(64) NOT NULL,
    direction_id           VARCHAR(64) NOT NULL,
    updated_at             DATETIME,
CONSTRAINT uq_event_category UNIQUE (education_event_id, direction_id),
    INDEX idx_event_category_event (education_event_id),
    INDEX idx_event_category_direction (direction_id)
) ENGINE=InnoDB;

CREATE TABLE gy_program_category (
    id                     VARCHAR(36) NOT NULL PRIMARY KEY,
    program_code           VARCHAR(64) NOT NULL,
    program_name           VARCHAR(100),
    category               VARCHAR(32),
    vocational             BOOLEAN,
CONSTRAINT uq_gy_program_code UNIQUE (program_code)
) ENGINE=InnoDB;

CREATE TABLE education_info_subject (
    education_info_id      VARCHAR(36) NOT NULL,
    subject_type           VARCHAR(32),
    subject_code           VARCHAR(32),
CONSTRAINT fk_education_info_subject
    FOREIGN KEY (education_info_id) REFERENCES education_info (id),
    INDEX idx_education_info_subject (education_info_id)
) ENGINE=InnoDB;

CREATE TABLE education_info_degree (
    education_info_id      VARCHAR(36) NOT NULL,
    degree                 VARCHAR(255),
CONSTRAINT fk_education_info_degree
    FOREIGN KEY (education_info_id) REFERENCES education_info (id),
    INDEX idx_education_info_degree (education_info_id)
) ENGINE=InnoDB;