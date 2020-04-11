package com.example.beerservice.service.brewing;

import com.example.beerservice.config.JmsConfig;
import com.example.beerservice.domain.Beer;
import com.example.beerservice.event.BrewBeerEvent;
import com.example.beerservice.event.NewInventoryEvent;
import com.example.beerservice.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrewingListener {

    private final BeerRepository repository;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.BEER_REQUEST_QUEUE)
    public void listen(BrewBeerEvent event) {
        var beerDto = event.getBeerDto();

        //simulate brewing; in an actual production system, a lot more stuff would happen, but we'll keep it
        //simple for demonstration purposes
        var quantityToBrew = repository.findById(beerDto.getId()).map(Beer::getQuantityToBrew).orElseThrow();

        log.info("Finished brewing beer for Beer {}", beerDto.getId());
        beerDto.setQuantityOnHand(quantityToBrew);

        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, new NewInventoryEvent(beerDto));
    }
}
