create schema if not exists `dlboard` ;

create table if not exists dlboard.user (
    user_id int auto_increment primary key,
    user_name varchar(100)
);

create table if not exists dlboard.message (
    message_id int auto_increment primary key,
    user_id int,
    message_content varchar(400),
    foreign key (user_id) references user (user_id)
);

