package com.example.beerservice.bootstrap;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

import java.math.BigDecimal;
import java.util.Arrays;

// not needed; data will be initialized with the data.sql script, as we need to set specific UUIDs, because
// they need to be the same in the other services
//@Component
@RequiredArgsConstructor
//@Profile({"!staging", "!production"})
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
                        .build(),
                Beer.builder()
                        .beerName("Stiegl")
                        .beerStyle("Lager")
                        .minOnHand(15)
                        .quantityToBrew(250)
                        .upc(BEER_2_UPC)
                        .price(new BigDecimal("12.95"))
                        .build(),
                Beer.builder()
                        .beerName("Kaiser")
                        .beerStyle("Lager")
                        .minOnHand(15)
                        .quantityToBrew(250)
                        .upc(BEER_3_UPC)
                        .price(new BigDecimal("12.95"))
                        .build()
        );

        beerRepository.saveAll(beers);
    }
}
