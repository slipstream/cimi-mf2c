-- Target versions: pre v2.4.1
-- SHOULD be executed when upgrading SlipStream to >= 2.4.1

SET AUTOCOMMIT FALSE;

-- START TRANSACTION;

ALTER TABLE Node ADD MAXPROVISIONINGFAILURES INTEGER DEFAULT 0;

COMMIT;