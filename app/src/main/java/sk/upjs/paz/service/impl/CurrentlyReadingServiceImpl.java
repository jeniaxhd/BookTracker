package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.CurrentlyReadingDao;
import sk.upjs.paz.ui.dto.ActiveBookCard;
import sk.upjs.paz.service.CurrentlyReadingService;

import java.util.List;

public class CurrentlyReadingServiceImpl implements CurrentlyReadingService {

    private final CurrentlyReadingDao dao;

    public CurrentlyReadingServiceImpl(CurrentlyReadingDao dao) {
        this.dao = dao;
    }

    @Override
    public List<ActiveBookCard> listActiveBooks(long userId) {
        if (userId <= 0) throw new IllegalArgumentException("userId must be > 0");
        return dao.listActiveBooks(userId);
    }
}
