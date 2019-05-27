INSERT INTO types (name, created_at) VALUES('Inspiradora', NOW());

INSERT INTO authors (name, created_at) VALUES('Zig Ziglar', NOW());
INSERT INTO authors (name, created_at) VALUES('Robert H. Schuller', NOW());
INSERT INTO authors (name, created_at) VALUES('William Feather', NOW());

INSERT INTO phrases (body, author_id, type_id, created_at) VALUES('Si puedes soñarlo, puedes lograrlo', '1', '1', NOW());
INSERT INTO phrases (body, author_id, type_id, created_at) VALUES('¿Qué grandes cosas intentarías si supieras que no vas a fracasar?', '2', '1', NOW());
INSERT INTO phrases (body, author_id, type_id, created_at) VALUES('El éxito parece ser en buena parte cuestión de perseverar después de que otros hayan abandonado', '3', '1', NOW());


INSERT INTO types (name, created_at) VALUES('Sabias', NOW());

INSERT INTO authors (name, created_at) VALUES('Oscar Wilde', NOW());
INSERT INTO authors (name, created_at) VALUES('Benjamin Franklin', NOW());

INSERT INTO phrases (body, author_id, type_id, created_at) VALUES('Amarse a uno mismo es el comienzo de un romance de por vida', '4', '2', NOW());
INSERT INTO phrases (body, author_id, type_id, created_at) VALUES('No escondas tus talentos que se hicieron para usarlos, ¿qué es un reloj de sol en la sombra?', '5', '2', NOW());
