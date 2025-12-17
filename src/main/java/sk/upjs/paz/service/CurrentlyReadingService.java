package sk.upjs.paz.service;

import sk.upjs.paz.ui.dto.ActiveBookCard;

import java.util.List;

public interface CurrentlyReadingService {
    List<ActiveBookCard> listActiveBooks(long userId);
}
