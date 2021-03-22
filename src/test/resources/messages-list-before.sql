delete from message;

insert into message(id, text, tag, user_id) values
(1, 'first', 'my-tag', 1),
(2, 'second', 'more', 1),
(3, 'third', 'my-tag', 1),
(4, 'fourth', 'another', 2);

-- как бы указываем хайбернету с какого айдишника дальше стартовать
-- следующая запись которую он произведет будет под индексом 10
alter sequence hibernate_sequence restart with 10;