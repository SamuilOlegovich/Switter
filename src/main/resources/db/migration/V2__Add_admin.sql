-- используем для добавления в пустую базу юзера админа иначе ничего н сможем делать
insert into usr (id, username, password, active)
    values (1, 'admin', '123', true);

insert into user_role (user_id, roles)
    values (1, 'USER'), (1, 'ADMIN');

insert into usr (id, username, password, active)
    values (2, '0', '0', true);

insert into user_role (user_id, roles)
    values (2, 'USER'), (2, 'ADMIN');