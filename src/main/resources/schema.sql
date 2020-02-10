create table if not exists link_requests
(
    id integer not null AUTO_INCREMENT,
    uid varchar(100),
    owner_id varchar(100) not null,
    agent_id varchar(100),
    entry_date timestamp not null,
    request blob,
    last_update timestamp,
    status varchar(50),
    primary key (id),
    unique key (uid)
);

create table if not exists link_req_domains
(
    id integer not null auto_increment,
    request_id integer not null,
    domain varchar(100) not null,
    primary key (id),
    foreign key (request_id) references link_requests (id) on delete cascade on update cascade
);

create table if not exists link_req_files
(
    id integer not null auto_increment,
    request_id integer not null,
    name varchar(200) not null,
    content blob not null,
    mime_type varchar(100) not null,
    upload_date timestamp not null,
    primary key (id),
    foreign key (request_id) references link_requests (id) on delete cascade on update cascade
);

create table if not exists link_req_messages
(
    id integer not null auto_increment,
    request_id integer not null,
    sender varchar(50) not null,
    date timestamp not null,
    message varchar(2000) not null,
    primary key (id),
    foreign key (request_id) references link_requests (id) on delete cascade on update cascade
);