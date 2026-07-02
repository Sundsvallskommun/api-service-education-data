CREATE TABLE susa_education_event_raw (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    json_body              LONGTEXT NOT NULL,
    date_collected         DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE susa_education_info_raw (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    json_body              LONGTEXT NOT NULL,
    date_collected         DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE susa_education_provider_raw (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    json_body              LONGTEXT NOT NULL,
    date_collected         DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE education_event (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title                  VARCHAR(255),
    education_event_id     VARCHAR(255),
    education_info_id      VARCHAR(255),
    education_provider_id  VARCHAR(255),
    city                   VARCHAR(255),
    municipality_id        VARCHAR(255),
    course_post_url        VARCHAR(255),
    seats                  INT,
    currency_type          VARCHAR(255),
    cost                   DECIMAL(10,2),
    lecture_type           VARCHAR(255),
    study_pace             VARCHAR(255),
    language               VARCHAR(255),
    start_date             DATE,
    end_date               DATE,
    application_start_date DATE,
    application_end_date   DATE,
    created_at             DATE,
    outdated_at            DATE,
    deleted                BOOLEAN,
    INDEX idx_education_event_edu_id (education_event_id),
    INDEX idx_education_event_info_id (education_info_id)
) ENGINE=InnoDB;

CREATE TABLE education_info (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    education_info_id      VARCHAR(255),
    title                  VARCHAR(255),
    school_type            VARCHAR(255),
    education_type         VARCHAR(255),
    code                   VARCHAR(255),
    description            VARCHAR(255),
    education_eligibility  VARCHAR(255),
    recommended_prior_knowledge VARCHAR(255),
    credit_type            VARCHAR(255),
    credits                VARCHAR(255),
    duration               VARCHAR(255),
    result_is_degree       BOOLEAN,
    degree                 VARCHAR(255),
    content_url            VARCHAR(255),
    expires                DATETIME,
    student_aid_eligibility VARCHAR(255),
    subjects               VARCHAR(255),
    created_at             DATE,
    outdated_at            DATE,
    deleted                BOOLEAN
) ENGINE=InnoDB;

CREATE TABLE reference_category (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category_id            VARCHAR(255) NOT NULL,
    category_name          VARCHAR(255),
    direction_id           VARCHAR(255) NOT NULL,
    direction_name         VARCHAR(255),
CONSTRAINT uq_reference_category_direction UNIQUE (category_id, direction_id),
    INDEX idx_reference_category_direction (direction_id)
) ENGINE=InnoDB;

CREATE TABLE event_category (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    education_event_id     VARCHAR(255) NOT NULL,
    direction_id           VARCHAR(255) NOT NULL,
CONSTRAINT uq_event_category UNIQUE (education_event_id, direction_id),
    INDEX idx_event_category_event (education_event_id),
    INDEX idx_event_category_direction (direction_id)
) ENGINE=InnoDB;

CREATE TABLE gy_program_category (
    id                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    program_code           VARCHAR(255) NOT NULL,
    program_name           VARCHAR(255),
    category               VARCHAR(255),
    vocational             BOOLEAN,
CONSTRAINT uq_gy_program_code UNIQUE (program_code)
) ENGINE=InnoDB;