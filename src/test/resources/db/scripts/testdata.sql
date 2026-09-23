INSERT INTO education_info
(id, education_info_id, title, code, school_type, education_type, education_eligibility, credit_type, credits, created_at) VALUES
('info-1', 'i.1', 'Matematik nivå 1a', 'MATE1A00X', 'VUXGY', 'kurs',      'Grundläggande behörighet', 'vp', 100, '2026-06-07'),
('info-2', 'i.2', 'Energiingenjör',    'TEING',     'HS',    'kurspaket', 'Fysik 2, Kemi 1',          'hp', 180, '2026-06-07'),
('info-3', 'i.1', 'Matematik nivå 1a', 'MATE1A00X', 'VUXGY', 'kurs',      'Grundläggande behörighet', 'vp', 100, '2026-06-06');

INSERT INTO education_event
(id, education_event_id, education_info_id, title, city, municipality_id, lecture_type, created_at) VALUES
('event-1', 'e.1', 'i.1', NULL,             'Sundsvall', '2281', 'Distance',  '2026-06-07'),
('event-2', 'e.2', 'i.2', 'Energiingenjör', 'Härnösand', '2280', 'Distance',  '2026-06-07'),
('event-3', 'e.3', NULL,  'Utan info',      'Sundsvall', '2281', 'Classroom', '2026-06-07'),
('event-4', 'e.4', 'i.1', NULL,             'Sundsvall', '2281', 'Distance',  '2026-06-06');