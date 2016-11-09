# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table aura (
  id                            bigserial not null,
  name                          varchar(255),
  admin_token                   varchar(255),
  user_token                    varchar(255),
  constraint pk_aura primary key (id)
);
create sequence aura_seq;

create table metric (
  id                            bigserial not null,
  name                          varchar(255),
  redirect_address              varchar(255),
  aura_id                       bigint,
  constraint pk_metric primary key (id)
);
create sequence metric_seq;

create table metric_display (
  id                            bigserial not null,
  name                          varchar(255),
  aura_id                       bigint,
  metric_id                     bigint,
  group_category                boolean,
  group_user                    boolean,
  plot                          varchar(255),
  time_frame                    varchar(255),
  constraint pk_metric_display primary key (id)
);
create sequence metric_display_seq;

create table metric_entry (
  id                            bigserial not null,
  category                      varchar(255),
  date                          timestamp,
  value                         integer,
  metric_id                     bigint,
  profile_id                    bigint,
  constraint pk_metric_entry primary key (id)
);
create sequence metric_entry_seq;

create table profile (
  id                            bigserial not null,
  name                          varchar(255),
  register_date                 timestamp,
  constraint pk_profile primary key (id)
);
create sequence profile_seq;

alter table metric add constraint fk_metric_aura_id foreign key (aura_id) references aura (id) on delete restrict on update restrict;
create index ix_metric_aura_id on metric (aura_id);

alter table metric_display add constraint fk_metric_display_aura_id foreign key (aura_id) references aura (id) on delete restrict on update restrict;
create index ix_metric_display_aura_id on metric_display (aura_id);

alter table metric_display add constraint fk_metric_display_metric_id foreign key (metric_id) references metric (id) on delete restrict on update restrict;
create index ix_metric_display_metric_id on metric_display (metric_id);

alter table metric_entry add constraint fk_metric_entry_metric_id foreign key (metric_id) references metric (id) on delete restrict on update restrict;
create index ix_metric_entry_metric_id on metric_entry (metric_id);

alter table metric_entry add constraint fk_metric_entry_profile_id foreign key (profile_id) references profile (id) on delete restrict on update restrict;
create index ix_metric_entry_profile_id on metric_entry (profile_id);


# --- !Downs

alter table metric drop constraint if exists fk_metric_aura_id;
drop index if exists ix_metric_aura_id;

alter table metric_display drop constraint if exists fk_metric_display_aura_id;
drop index if exists ix_metric_display_aura_id;

alter table metric_display drop constraint if exists fk_metric_display_metric_id;
drop index if exists ix_metric_display_metric_id;

alter table metric_entry drop constraint if exists fk_metric_entry_metric_id;
drop index if exists ix_metric_entry_metric_id;

alter table metric_entry drop constraint if exists fk_metric_entry_profile_id;
drop index if exists ix_metric_entry_profile_id;

drop table if exists aura;
drop sequence if exists aura_seq;

drop table if exists metric;
drop sequence if exists metric_seq;

drop table if exists metric_display;
drop sequence if exists metric_display_seq;

drop table if exists metric_entry;
drop sequence if exists metric_entry_seq;

drop table if exists profile;
drop sequence if exists profile_seq;

