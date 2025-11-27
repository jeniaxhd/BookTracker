package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Country;

import java.util.List;
import java.util.Optional;

public interface CountryDao {
    List<Country> getAll();

    Optional<Country> getById(Long id);

    Optional<Country> getByName(String name);
}
