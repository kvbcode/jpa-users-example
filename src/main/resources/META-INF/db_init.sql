create sequence users_seq start with 50;

create table users(
    id bigint not null default nextval('users_seq'),
    name varchar(255) not null,
    primary key (id)
);

create table roles(
    nameid varchar(255) primary key,
    description varchar(255) not null default ''
);

create table users_roles(
    user_id bigint not null,
    role_nameid varchar(255) not null,
    primary key (user_id, role_nameid),
    foreign key (user_id) references users (id),
    foreign key (role_nameid) references roles (nameid)    
);

insert into users (id, name) values
(1, 'Lionel C. Mathena'),
(2, 'Pierre J. Gouge'),
(3, 'Rose D. Kent'),
(4, 'Beverly S. Bingaman'),
(5, 'Daniel V. Morones'),
(6, 'Thomas E. Bynum');

insert into roles (nameid, description) values
('READ', 'Разрешено чтение'),
('WRITE', 'Разрешена запись'),
('UPLOAD', 'Разрешена загрузка'),
('INFO', 'Разрешено получение дополнительной информации'),
('STATS', 'Разрешено получение статистики');


insert into users_roles (user_id, role_nameid) values 
(1, 'READ'), (1, 'WRITE'), (1, 'UPLOAD'), (1, 'INFO'), (1, 'STATS'),
(2, 'READ'), (2, 'WRITE'), (2, 'UPLOAD'),
(3, 'READ'), (3, 'WRITE'), (3, 'UPLOAD'),
(4, 'READ'), (4, 'WRITE'), (4, 'UPLOAD'),
(5, 'INFO'), (5, 'STATS');

