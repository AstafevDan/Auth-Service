INSERT INTO users (id, username, email, password, email_verified)
VALUES (1, 'user1', 'test1@email.com', '$2a$12$ZUsg0JSPL9EpfDcZqnZBZeBwAHsQ748m8pAaEyu5eFjm810GiGpWC', false),
       (2, 'user2', 'test2@email.com', '$2a$12$f.boifqJcCmoXakrbcnBQOkFZYIiT1n06loIB7RmmCnWXli4f3lEe', true),
       (3, 'user3', 'test3@email.com', '$2a$12$mrXOL1lBnkSPqx0.se6ZuuFBD37lN4FDTxDbwGD6CYjBslky3u.x6', false);
SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));