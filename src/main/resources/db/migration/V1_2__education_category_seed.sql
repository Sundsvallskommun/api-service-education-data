

-- GYMNASIE UTBILDNINGAR
INSERT INTO gy_program_category (id, program_code, program_name, category, vocational) VALUES
-- Yrkesprogram (Gy25)
(UUID(), 'BA', 'Bygg- och anläggningsprogrammet',       'Bygg och anläggning',              true),
(UUID(), 'BF', 'Barn- och fritidsprogrammet',           'Pedagogik och socialt arbete',     true),
(UUID(), 'EE', 'El- och energiprogrammet',              'El och energi',                    true),
(UUID(), 'FR', 'Frisör- och stylistprogrammet',         'Hantverk och skönhet',             true),
(UUID(), 'FS', 'Försäljnings- och serviceprogrammet',   'Handel och ekonomi',               true),
(UUID(), 'FT', 'Fordons- och transportprogrammet',      'Fordon och transport',             true),
(UUID(), 'HT', 'Hotell- och turismprogrammet',          'Hotell, restaurang och turism',    true),
(UUID(), 'IN', 'Industritekniska programmet',           'Industri och teknik',              true),
(UUID(), 'NB', 'Naturbruksprogrammet',                  'Naturbruk',                        true),
(UUID(), 'RL', 'Restaurang- och livsmedelsprogrammet',  'Hotell, restaurang och turism',    true),
(UUID(), 'VF', 'VVS- och fastighetsprogrammet',         'VVS och fastighet',                true),
(UUID(), 'VO', 'Vård- och omsorgsprogrammet',           'Vård och omsorg',                  true),
-- Högskoleförberedande program (Gy25)
(UUID(), 'EK', 'Ekonomiprogrammet',                     'Ekonomi, juridik och företagande', false),
(UUID(), 'ES', 'Estetiska programmet',                  'Konst, musik och media',           false),
(UUID(), 'HU', 'Humanistiska programmet',               'Språk, kultur och historia',       false),
(UUID(), 'NA', 'Naturvetenskapsprogrammet',             'Naturvetenskap',                   false),
(UUID(), 'SA', 'Samhällsvetenskapsprogrammet',          'Samhällsvetenskap',                false),
(UUID(), 'TE', 'Teknikprogrammet',                      'Teknik och IT',                    false)