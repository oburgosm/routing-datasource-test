/**
 * Author:  oburgosm
 * Created: May 18, 2018
 */
CREATE TABLE Product (
    id bigint auto_increment,
    name varchar(255),
    PRIMARY KEY  (id),
    UNIQUE KEY uq_name(name)
);

