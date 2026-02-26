package sk.upjs.paz.entity;

public class Author {

    private Long id;
    private String name;
    private String country;
    private String bio;
    private Country countryObj;

    public Author() {
    }

    public Author(Long id, String name, String country, String bio, Country countryObj) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.bio = bio;
        this.countryObj = countryObj;
    }

    public Author(String name, String country, String bio, Country countryObj) {
        this.name = name;
        this.country = country;
        this.bio = bio;
        this.countryObj = countryObj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Country getCountryObj() {
        return countryObj;
    }

    public void setCountryObj(Country countryObj) {
        this.countryObj = countryObj;
    }

    @Override
    public String toString() {
        return name;
    }
}
