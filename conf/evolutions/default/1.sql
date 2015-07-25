# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table score (
  id                        bigint not null,
  name                      varchar(255),
  score                     bigint,
  constraint pk_score primary key (id))
;

create sequence score_seq;




# --- !Downs

drop table if exists score cascade;

drop sequence if exists score_seq;

