# --- !Ups
ALTER TABLE aura ADD COLUMN template varchar(255);

# --- !Downs
ALTER TABLE aura DROP COLUMN IF EXISTS template;