set search_path = public, popchat;

-- TRAIT
create table if not exists trait
(
    trait_id    text primary key            not null,
    name        text                        not null,
    description text                        not null,
    rarity      varchar(8) default 'COMMON' not null
        constraint rarity_options check ( rarity in ('COMMON', 'UNCOMMON', 'RARE', 'MYTHICAL') )
);

comment on table trait is
    'Special traits commonly associated with a user account';

-- USER

create table if not exists user_profile
(
    user_id       varchar(26) primary key         not null,
    display_name  varchar(20)                     not null
        constraint check_min_length check ( length(display_name) >= 4 ),
    avatar        bytea,
    status        varchar(150),
    description   varchar(500),
    pronouns      varchar(30) default 'they/them' not null,
    title         varchar(10),
    banned        bool        default false       not null,
    creation_time timestamptz default now()       not null,
    update_time   timestamptz
        constraint check_time check ( update_time > creation_time ),
    deleted       bool        default false       not null
);

comment on table user_profile is
    'Publicly listed profile data provided by users.';

create table if not exists user_trait
(
    user_id       varchar(26)               not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    trait_id      text                      not null
        constraint fk_trait
            references trait (trait_id)
            on delete cascade,
    creation_time timestamptz default now() not null,
    unique (user_id, trait_id)
);

comment on table user_trait is
    'A trait that a given user account has earned or been given.';

create table if not exists user_credentials
(
    user_id       varchar(26) unique not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    password_hash text               not null
);

comment on table user_credentials is
    'An account''s associated password hash, which is used for logging into an account.';

create table if not exists user_block
(
    user_id         varchar(26) not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    blocked_user_id varchar(26) not null
        constraint fk_blocked_user_profile
            references user_profile (user_id)
            on delete cascade,
    unique (user_id, blocked_user_id)
);

comment on table user_block is
    'A one-way connection between users that represents a contact block between them.';

create table if not exists user_session
(
    user_id         varchar(26) unique        not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    token           varchar(100)              not null
        constraint token_exact_size check ( length(token) = 50 ),
    creation_time   timestamptz default now() not null,
    expiration_time timestamptz default now() + (1 || ' week')::interval
        constraint check_time check ( expiration_time > creation_time )
);

comment on table user_session is
    'Represents a logged in user''s session.';

-- CHANNEL

create table if not exists channel
(
    channel_id    varchar(26) primary key   not null,
    name          varchar(200)              not null
        constraint name_not_empty check ( (name = '') is false ),
    channel_type  varchar(10) default 'PRIVATE'
        constraint channel_type_options check ( channel_type in ('PRIVATE', 'GROUP', 'PUBLIC') ),
    creation_time timestamptz default now() not null,
    update_time   timestamptz
        constraint check_time check ( update_time > creation_time ),
    deleted       bool        default false not null
);

comment on table channel is
    'A communication channel used for sending messages.';

create table if not exists channel_moderator
(
    channel_id    varchar(26)               not null
        constraint fk_channel
            references channel (channel_id)
            on delete cascade,
    user_id       varchar(26)               not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    creation_time timestamptz default now() not null,
    unique (channel_id, user_id)
);

comment on table channel_moderator is
    'A user that has extended power within a channel to maintain order.';

create table if not exists user_channel
(
    user_id    varchar(26)                not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    channel_id varchar(26)                not null
        references channel (channel_id)
            on delete cascade,
    status     varchar(10) default 'OPEN' not null
        constraint status_options check ( status in ('OPEN', 'BLOCKED') ),
    unique (user_id, channel_id)
);

comment on table user_channel is
    'A representation of a user''s association to a given channel.';

create table if not exists user_connection
(
    user_id           varchar(26) not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    connected_user_id varchar(26) not null
        constraint fk_connected_user_profile
            references user_profile (user_id)
            on delete cascade,
    channel_id        varchar(26) not null
        references channel (channel_id)
            on delete cascade,
    unique (user_id, connected_user_id),
    constraint lexicographic_order check ( user_id < connected_user_id )
);

comment on table user_connection is
    'Represents a communication contact with another user.';

-- MESSAGE

create table message
(
    message_id    varchar(26) primary key   not null,
    channel_id    varchar(26)               not null
        constraint fk_channel
            references channel (channel_id)
            on delete cascade,
    text          varchar(2000)
        constraint text_not_empty check ( (text = '') is false ),
    creation_time timestamptz default now() not null,
    update_time   timestamptz
        constraint check_time check ( update_time > creation_time ),
    deleted       bool        default false not null
);

comment on table message is
    'Represents a single message that can be sent within a given channel.';

create table message_attachment
(
    message_id varchar(26) not null
        constraint fk_message
            references message (message_id)
            on delete cascade,
    data       bytea       not null
);

comment on table message_attachment is
    'Represents an attachment that can be associated with a given message.';

-- ACTION

create table if not exists server_moderator_action
(
    user_id         varchar(26)                not null
        constraint fk_user_profile
            references user_profile (user_id)
            on delete cascade,
    action          varchar(10) default 'WARN' not null
        constraint action_options check ( action in ('WARN', 'KICK', 'TEMPBAN', 'BAN') ),
    reason          varchar(500),
    creation_time   timestamptz default now()  not null,
    expiration_time timestamptz
        constraint check_time check ( expiration_time > creation_time )
);

comment on table server_moderator_action is
    'An action taken against a user for breaking rules that applies across the entire application.';

create table if not exists channel_moderator_action
(
    channel_id varchar(26) not null
        constraint fk_channel
            references channel (channel_id)
            on delete cascade
) inherits (server_moderator_action);

comment on table channel_moderator_action is
    'An action taken against a user for breaking rules that applies to a single channel.';
