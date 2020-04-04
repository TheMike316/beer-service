package com.example.beerservice.repository;

import com.example.beerservice.web.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository repository;

    PageRequest pageRequest = PageRequest.of(0, 10);

    @Test
    void findAllByBeerName() {
        String beerName = "Galaxy Cat";
        var actual = repository.findAllByBeerName(beerName, pageRequest);

        assertNotNull(actual);
        assert actual.getTotalElements() > 0;
        assert actual.getContent().stream().allMatch(b -> beerName.equals(b.getBeerName()));
    }

    @Test
    void findAllByBeerStyle() {
        var beerStyle = BeerStyle.PORTER;
        var actual = repository.findAllByBeerStyle(beerStyle.name(), pageRequest);

        assertNotNull(actual);
        assert actual.getTotalElements() > 0;
        assert actual.getContent().stream().allMatch(b -> beerStyle.name().equals(b.getBeerStyle()));
    }

    @Test
    void findAllByBeerNameAndBeerStyle() {
        var beerStyle = BeerStyle.IPA;
        var beerName = "Mango Bobs";

        var actual = repository.findAllByBeerNameAndBeerStyle(beerName, beerStyle.name(), pageRequest);

        assertNotNull(actual);
        assert actual.getTotalElements() > 0;
        assert actual.getContent().stream().allMatch(b -> beerName.equals(b.getBeerName()) && beerStyle.name().equals(b.getBeerStyle()));

    }
}