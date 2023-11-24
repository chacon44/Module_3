-- certificate table--
create table IF NOT EXISTS certificates(
    id serial primary key not null,
    name varchar(255) not null,
    description varchar(255) not null, --the text can have up to 255 characters, to avoid unlimited lenghts of TEXT type
    price decimal(8,2) not null, --the number can have up to eight digits including two decimals
    duration int not null,
    create_date varchar(255) not null,
    last_update_date varchar(255) not null
);

create table IF NOT EXISTS tag(
    id serial primary key not null,
    name varchar(255)
);

CREATE TABLE IF NOT EXISTS certificates_tag (
    certificate_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (certificate_id, tag_id),
    FOREIGN KEY (certificate_id) REFERENCES certificates(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO certificates (certificate_name, description, price, duration, create_date, last_update_date) VALUES ('certificate0', 'description0', 3.0, 0, NOW(), NOW());
INSERT INTO certificates (certificate_name, description, price, duration, create_date, last_update_date) VALUES ('certificate1', 'description1', 3.1, 1, NOW(), NOW());
INSERT INTO certificates (certificate_name, description, price, duration, create_date, last_update_date) VALUES ('certificate2', 'description2', 3.2, 2, NOW(), NOW());

INSERT INTO tag (tag_name) VALUES ('tag0' );
INSERT INTO tag (tag_name) VALUES ('tag1');
INSERT INTO tag (tag_name) VALUES ('tag2');

INSERT INTO certificates_tag (certificate_id, tag_id) VALUES (1, 1);
INSERT INTO certificates_tag (certificate_id, tag_id) VALUES (2, 2);
INSERT INTO certificates_tag (certificate_id, tag_id) VALUES (3, 3);
