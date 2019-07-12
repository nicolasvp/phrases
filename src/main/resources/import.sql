INSERT INTO types (name, created_at) VALUES('Inspiradora', NOW());
INSERT INTO types (name, created_at) VALUES('Sabias', NOW());
INSERT INTO types (name, created_at) VALUES('Amor', NOW());
INSERT INTO types (name, created_at) VALUES('Paz', NOW());
INSERT INTO types (name, created_at) VALUES('Filosóficas', NOW());

INSERT INTO images (name, created_at) VALUES('Imagen1', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen2', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen3', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen4', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen5', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen6', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen7', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen8', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen9', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen10', NOW());
INSERT INTO images (name, created_at) VALUES('Imagen11', NOW());

INSERT INTO authors (name, created_at) VALUES('Zig Ziglar', NOW());
INSERT INTO authors (name, created_at) VALUES('Robert H. Schuller', NOW());
INSERT INTO authors (name, created_at) VALUES('William Feather', NOW());
INSERT INTO authors (name, created_at) VALUES('Oscar Wilde', NOW());
INSERT INTO authors (name, created_at) VALUES('Benjamin Franklin', NOW());
INSERT INTO authors (name, created_at) VALUES('Aristóteles', NOW());
INSERT INTO authors (name, created_at) VALUES('Albert Camus', NOW());
INSERT INTO authors (name, created_at) VALUES('Buda', NOW());
INSERT INTO authors (name, created_at) VALUES('Francisco de Asís', NOW());
INSERT INTO authors (name, created_at) VALUES('Tales de Mileto', NOW());
INSERT INTO authors (name, created_at) VALUES('Francis Bacon', NOW());

INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('Si puedes soñarlo, puedes lograrlo', '1', '1', '1', 121 ,NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('¿Qué grandes cosas intentarías si supieras que no vas a fracasar?', '2', '1', '2', 423, NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('El éxito parece ser en buena parte cuestión de perseverar después de que otros hayan abandonado', '3', '1', '3', 211, NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('Amarse a uno mismo es el comienzo de un romance de por vida', '4', '2', '4', 201, NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('No escondas tus talentos que se hicieron para usarlos, ¿qué es un reloj de sol en la sombra?', '5', '2', '5', 152, NOW());

INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('El amor se compone de una sola alma que habita en dos cuerpos', '6', '3', '6', 141 ,NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('No ser amados es una simple desventura; la verdadera desgracia es no amar', '7', '3', '7', 511 ,NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('Más que mil palabras inútiles, vale una sola que otorgue paz', '8', '4', '8', 271 ,NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('Que la paz que anuncian con sus palabras esté primero en sus corazones', '9', '4', '9', 211 ,NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('La cosa más difícil es conocernos a nosotros mismos; la más fácil es hablar mal de los demás', '10', '5', '10', 137 ,NOW());
INSERT INTO phrases (body, author_id, type_id, image_id, likes_counter, created_at) VALUES('El conocimiento es poder', '11', '5', '11', 674 ,NOW());




