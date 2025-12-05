package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.CountryDao;
import sk.upjs.paz.entity.Country;
import sk.upjs.paz.service.CountryService;

import java.util.List;
import java.util.Optional;

public class CountryServiceImpl implements CountryService {

    private final CountryDao countryDao;

    public CountryServiceImpl(CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @Override
    public void add(Country country) {
        if (country == null)
            throw new IllegalArgumentException("Country cannot be null");

        if (country.name() == null || country.name().isBlank())
            throw new IllegalArgumentException("Country name cannot be empty");

        countryDao.add(country);
    }

    @Override
    public void update(Country country) {
        if (country == null)
            throw new IllegalArgumentException("Country cannot be null");

        if (country.id() == null)
            throw new IllegalArgumentException("Country ID cannot be null");

        countryDao.update(country);
    }

    @Override
    public void delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Country ID cannot be null");

        countryDao.delete(id);
    }

    @Override
    public Optional<Country> getById(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Country ID cannot be null");

        return countryDao.getById(id);
    }

    @Override
    public List<Country> getAll() {
        return countryDao.getAll();
    }

    @Override
    public Optional<Country> getByName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");

        return countryDao.getByName(name);
    }
}
