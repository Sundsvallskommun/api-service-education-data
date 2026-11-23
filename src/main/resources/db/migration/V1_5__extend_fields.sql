ALTER TABLE education_event
    ADD COLUMN study_site       VARCHAR(255),
    ADD COLUMN start_type       VARCHAR(2),
    ADD COLUMN contractor       VARCHAR(255);
ALTER TABLE education_info
    ADD COLUMN duration_unit    VARCHAR(16);
