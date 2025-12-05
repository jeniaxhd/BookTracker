package sk.upjs.paz.service;

import sk.upjs.paz.entity.Country;

import java.util.List;
import java.util.Optional;

public interface CountryService {
    void add(Country country);

    void update(Country country);

    void delete(Long id);

    Optional<Country> getById(Long id);

    List<Country> getAll();

    Optional<Country> getByName(String name);
}
