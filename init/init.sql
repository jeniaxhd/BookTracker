-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema booktracker
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema booktracker
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `booktracker` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `booktracker` ;

-- -----------------------------------------------------
-- Table `country`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `country` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `country_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `author`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `author` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `country` VARCHAR(50) NULL DEFAULT NULL,
  `bio` TEXT NULL DEFAULT NULL,
  `country_id` BIGINT UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_author_country1_idx` (`country_id` ASC) VISIBLE,
  CONSTRAINT `fk_author_country1`
    FOREIGN KEY (`country_id`)
    REFERENCES `country` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `book` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `year` YEAR NULL DEFAULT NULL,
  `pages` INT NULL DEFAULT NULL,
  `cover_path` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `book_has_author`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `book_has_author` (
  `book_id` BIGINT UNSIGNED NOT NULL,
  `author_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`author_id`, `book_id`),
  INDEX `fk_Book_has_author_author1_idx` (`author_id` ASC) VISIBLE,
  INDEX `fk_Book_has_author_Book1_idx` (`book_id` ASC) VISIBLE,
  CONSTRAINT `fk_Book_has_author_author1`
    FOREIGN KEY (`author_id`)
    REFERENCES `author` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_Book_has_author_Book1`
    FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `genre`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `genre` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 42
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `book_has_genre`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `book_has_genre` (
  `book_id` BIGINT UNSIGNED NOT NULL,
  `genre_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`book_id`, `genre_id`),
  INDEX `fk_book_has_genre_genre` (`genre_id` ASC) VISIBLE,
  CONSTRAINT `fk_book_has_genre_book`
    FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_book_has_genre_genre`
    FOREIGN KEY (`genre_id`)
    REFERENCES `genre` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `mail` VARCHAR(100) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `readBooks` INT UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `mail_UNIQUE` (`mail` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `readingsession`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `readingsession` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `start` DATETIME NULL DEFAULT NULL,
  `duration` INT UNSIGNED NULL DEFAULT NULL,
  `endPage` INT UNSIGNED NULL DEFAULT '0',
  `lastTimeRead` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `book_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_readingSession_Book1_idx` (`book_id` ASC) VISIBLE,
  INDEX `fk_readingSession_User1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_readingSession_Book1`
    FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_readingSession_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `review` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `rating` INT UNSIGNED NOT NULL,
  `comment` TEXT NULL DEFAULT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `book_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Review_Book1_idx` (`book_id` ASC) VISIBLE,
  INDEX `fk_Review_User1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_Review_Book1`
    FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`),
  CONSTRAINT `fk_Review_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `user_has_book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_has_book` (
  `user_id` BIGINT UNSIGNED NOT NULL,
  `book_id` BIGINT UNSIGNED NOT NULL,
  `bookstate` ENUM('READING', 'FINISHED', 'WANT_TO_READ', 'ABANDONED', 'NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
  PRIMARY KEY (`user_id`, `book_id`),
  INDEX `fk_user_has_book_book1_idx` (`book_id` ASC) VISIBLE,
  INDEX `fk_user_has_book_user1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_has_book_book1`
    FOREIGN KEY (`book_id`)
    REFERENCES `book` (`id`),
  CONSTRAINT `fk_user_has_book_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
