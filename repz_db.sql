create table if not exists discord_users
(
    id            bigint       not null
        primary key,
    player_name   varchar(255) null,
    iwa_client_id varchar(255) null,
    d_user_id     varchar(255) not null,
    constraint d_user_id
        unique (d_user_id)
);

create table if not exists hibernate_sequence
(
    next_val bigint null
);

create table if not exists hibernate_sequences
(
    sequence_name varchar(255) not null
        primary key,
    next_val      bigint       null
);

create table if not exists player_tracks
(
    id          bigint       not null
        primary key,
    client_id   varchar(255) not null,
    join_date   datetime     null,
    leave_date  datetime     null,
    player_name varchar(255) null,
    server_id   varchar(255) not null,
    tracker_id  varchar(255) null
);

create index client_id
    on player_tracks (client_id);

create index server_id
    on player_tracks (server_id);

create table if not exists server_tracks
(
    id               bigint       not null
        primary key,
    date             datetime     null,
    map_name         varchar(255) not null,
    max_player_count int          null,
    percentage       double       null,
    player_count     int          null,
    server_id        varchar(255) not null
);

create index server_id
    on server_tracks (server_id, date);

create table if not exists servers
(
    id          bigint       not null
        primary key,
    game        varchar(255) null,
    server_name varchar(255) not null,
    port        int          null,
    server_id   varchar(255) not null,
    constraint server_id
        unique (server_id)
);