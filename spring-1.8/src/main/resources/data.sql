INSERT INTO AUTHORS (INITIALS, LASTNAME) VALUES ('А.С.', 'Пушкин'), ('Д.А', 'Рубина');
INSERT INTO BOOKS (NAME, AUTHOR_ID) VALUES ('Обычный приключенческий роман', 1), ('Обычный любовный роман', 2), ('Любовно-приключенческий роман', 2);
INSERT INTO GENRES (NAME) VALUES ('Приключения'), ('Любовь');
INSERT INTO BOOKS_GENRES (BOOK_ID, GENRE_ID) VALUES (1, 1), (2, 2), (3, 1), (3, 2);