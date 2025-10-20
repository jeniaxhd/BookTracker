package sk.upjs.paz.entity;

import sk.upjs.paz.enums.*;

import java.time.LocalDate;

public record Status (
        long id,
        Book book,
        User user,
        Bookstate state,
        int pagesRead,
        LocalDate lastTimeRead
) {
    public Status {
        Bookstate finalState = state;

        if (pagesRead > 0) {
            if (state != Bookstate.FINISHED && state != Bookstate.ABANDONED) {
                finalState = Bookstate.READING;
            }
        }

        state = finalState;
    }
}
