INSERT INTO AUTHORS (INITIALS, LASTNAME) VALUES ('�.�.', '������'), ('�.�', '������');
INSERT INTO BOOKS (NAME, AUTHOR_ID) VALUES ('������� ��������������� �����', 1), ('������� �������� �����', 2), ('�������-��������������� �����', 2);
INSERT INTO GENRES (NAME) VALUES ('�����������'), ('������');
INSERT INTO BOOKS_GENRES (BOOK_ID, GENRE_ID) VALUES (1, 1), (2, 2), (3, 1), (3, 2);