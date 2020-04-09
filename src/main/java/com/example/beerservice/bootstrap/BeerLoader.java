package com.example.beerservice.bootstrap;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Profile({"!staging", "!production"})
public class BeerLoader implements InitializingBean {

    private static final String BEER_1_UPC = "0631234200036";
    private static final String BEER_2_UPC = "0631234300019";
    private static final String BEER_3_UPC = "0083783375213";

    private final BeerRepository beerRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        loadBeers();
    }

    private void loadBeers() {
        if (beerRepository.count() > 0) {
            return;
        }
        var beers = Arrays.asList(
                Beer.builder()
                        .beerName("Mango Bobs")
                        .beerStyle("IPA")
                        .minOnHand(12)
                        .quantityToBrew(200)
                        .upc(BEER_1_UPC)
                        .price(new BigDecimal("12.95"))
                        .version(1L)
                        .build(),
                Beer.builder()
                        .beerName("Galaxy Cat")
                        .beerStyle("PALE_ALE")
                        .minOnHand(12)
                        .quantityToBrew(200)
                        .upc(BEER_2_UPC)
                        .price(new BigDecimal("12.95"))
                        .version(1L)
                        .build(),
                Beer.builder()
                        .beerName("Pinball Porter")
                        .beerStyle("PORTER")
                        .minOnHand(12)
                        .quantityToBrew(200)
                        .upc(BEER_3_UPC)
                        .price(new BigDecimal("12.95"))
                        .version(1L)
                        .build()
        );

        beerRepository.saveAll(beers);
    }
}
