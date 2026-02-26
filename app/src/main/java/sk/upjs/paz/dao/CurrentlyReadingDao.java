package sk.upjs.paz.dao;

import sk.upjs.paz.ui.dto.ActiveBookCard;
import java.util.List;

public interface CurrentlyReadingDao {
    List<ActiveBookCard> listActiveBooks(long userId);
}
