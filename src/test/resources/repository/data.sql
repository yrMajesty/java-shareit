INSERT INTO users (name, email) values ('Mike', 'mike@mail.ru');
INSERT INTO users (name, email) values ('Tom', 'tom@mail.ru');
INSERT INTO users (name, email) values ('Sam', 'sam@mail.ru');

INSERT INTO items (name, description, is_available, owner_id) VALUES
            ('Book', 'Interesting book', true, 1);

INSERT INTO requests (description, requestor_id, date_created) VALUES
            ('i need tv box', 3, '2023-06-11T01:13:30');
INSERT INTO requests (description, requestor_id, date_created) VALUES
            ('i need book', 2, '2023-06-10T10:13:30');

INSERT INTO items (name, description, is_available, owner_id, request_id) VALUES
            ('TV box', 'cool tv box', true, 2, 1);

INSERT INTO comments (text, item_id, author_id, created) VALUES
    ('super interesting good book', 1, 3, '2023-06-25T10:10:30');

