INSERT INTO AUTHORS (INITIALS, LASTNAME) VALUES ('A.S.', 'Pushkin'), ('D.A.', 'Rubina');
INSERT INTO BOOKS (NAME, AUTHOR_ID) VALUES ('Regular adventure novel', 1), ('Regular romance novel', 2), ('Romance-adventure novel', 2);
INSERT INTO GENRES (NAME) VALUES ('Adventure'), ('Romance');
INSERT INTO BOOKS_GENRES (BOOK_ID, GENRE_ID) VALUES (1, 1), (2, 2), (3, 1), (3, 2);