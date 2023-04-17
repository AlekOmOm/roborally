/* Need to switch off FK check for MySQL since there are crosswise FK references */
SET FOREIGN_KEY_CHECKS = 0;;

CREATE TABLE IF NOT EXISTS Game (
    gameID int NOT NULL UNIQUE AUTO_INCREMENT,

    name varchar(255),
    currentPlayer tinyint NULL,
    phase varchar(255),
    step tinyint,


    PRIMARY KEY (gameID),
    FOREIGN KEY (gameID, currentPlayer) REFERENCES Player(gameID, playerID)
    );;

CREATE TABLE IF NOT EXISTS Player (
    gameID int NOT NULL,
    playerID tinyint NOT NULL,

    name varchar(255),
    colour varchar(31),

    positionX int,
    positionY int,
    heading tinyint,
    forder int,
    last_checkpoint int,

    PRIMARY KEY (gameID, playerID),
    FOREIGN KEY (gameID) REFERENCES Game(gameID)
    );;

create table if not exists playerHand (
    playerID tinyint not null,
    gameID int not null,
    card0 varchar(255),
    card1 varchar(255),
    card2 varchar(255),
    card3 varchar(255),
    card4 varchar(255),
    card5 varchar(255),
    card6 varchar(255),
    card7 varchar(255)

    );;

create table if not exists playerRegister (
    playerID tinyint not null,
    gameID int not null,

    card0 varchar(255),
    card1 varchar(255),
    card2 varchar(255),
    card3 varchar(255),
    card4 varchar(255)
    );;

SET FOREIGN_KEY_CHECKS = 1;;