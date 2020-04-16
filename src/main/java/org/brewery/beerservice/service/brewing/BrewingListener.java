package org.brewery.beerservice.service.brewing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.beerservice.config.JmsConfig;
import org.brewery.beerservice.domain.Beer;
import org.brewery.beerservice.repository.BeerRepository;
import org.brewery.common.model.event.BrewBeerEvent;
import org.brewery.common.model.event.NewInventoryEvent;
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
