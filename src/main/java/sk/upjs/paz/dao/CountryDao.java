package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Country;

import java.util.List;
import java.util.Optional;

public interface CountryDao {

    void add(Country country);

    void update(Country country);

    void delete(Long id);


    List<Country> getAll();

    Optional<Country> getById(Long id);

    Optional<Country> getByName(String name);
}
