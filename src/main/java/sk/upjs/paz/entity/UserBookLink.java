package sk.upjs.paz.entity;

import sk.upjs.paz.enums.BookState;

public record UserBookLink(long userId, long bookId, BookState state) {}
