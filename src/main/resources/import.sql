INSERT INTO types (name, created_at) VALUES('Inspiradora', NOW());
INSERT INTO types (name, created_at) VALUES('Sabias', NOW());

INSERT INTO images (name, created_at) VALUES('Imagen1', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen2', NOW());

INSERT INTO authors (name, created_at) VALUES('Zig Ziglar', NOW());
INSERT INTO authors (name, created_at) VALUES('Robert H. Schuller', NOW());
INSERT INTO authors (name, created_at) VALUES('William Feather', NOW());
INSERT INTO authors (name, created_at) VALUES('Oscar Wilde', NOW());
INSERT INTO authors (name, created_at) VALUES('Benjamin Franklin', NOW());

INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('Si puedes soñarlo, puedes lograrlo', '1', '1', '1', 121 ,NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('¿Qué grandes cosas intentarías si supieras que no vas a fracasar?', '2', '1', '2', 423, NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('El éxito parece ser en buena parte cuestión de perseverar después de que otros hayan abandonado', '3', '1', '1', 211, NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('Amarse a uno mismo es el comienzo de un romance de por vida', '4', '2', '1', 201, NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('No escondas tus talentos que se hicieron para usarlos, ¿qué es un reloj de sol en la sombra?', '5', '2', '2', 152, NOW());
