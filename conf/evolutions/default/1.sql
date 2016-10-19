# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table aura (
  id                            bigint not null,
  name                          varchar(255),
  constraint pk_aura primary key (id)
);
create sequence aura_seq;

create table metric (
  id                            bigint not null,
  name                          varchar(255),
  plot_type                     varchar(255),
  aura_id                       bigint,
  constraint pk_metric primary key (id)
);
create sequence metric_seq;

create table metric_entry (
  id                            bigint not null,
  name                          varchar(255),
  date                          timestamp,
  value                         integer,
  metric_id                     bigint,
  constraint pk_metric_entry primary key (id)
);
create sequence metric_entry_seq;

alter table metric add constraint fk_metric_aura_id foreign key (aura_id) references aura (id) on delete restrict on update restrict;
create index ix_metric_aura_id on metric (aura_id);

alter table metric_entry add constraint fk_metric_entry_metric_id foreign key (metric_id) references metric (id) on delete restrict on update restrict;
create index ix_metric_entry_metric_id on metric_entry (metric_id);


# --- !Downs

alter table metric drop constraint if exists fk_metric_aura_id;
drop index if exists ix_metric_aura_id;

alter table metric_entry drop constraint if exists fk_metric_entry_metric_id;
drop index if exists ix_metric_entry_metric_id;

drop table if exists aura;
drop sequence if exists aura_seq;

drop table if exists metric;
drop sequence if exists metric_seq;

drop table if exists metric_entry;
drop sequence if exists metric_entry_seq;

