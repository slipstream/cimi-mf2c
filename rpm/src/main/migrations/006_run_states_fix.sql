-- Target versions: pre v2.2.4
-- SHOULD be executed ONLY ONCE when upgrading to SlipStream v2.2.4

UPDATE RUN SET STATE = 6 WHERE STATE = 8;
UPDATE RUN SET STATE = 20 WHERE STATE = 9;
UPDATE RUN SET STATE = 9 WHERE STATE = 7;
UPDATE RUN SET STATE = 7 WHERE STATE = 20;
UPDATE RUN SET STATE = 8 WHERE STATE = 10 OR STATE = 11 OR STATE = 12 OR STATE = 13;
UPDATE RUN SET STATE = 4 WHERE STATE = 14;