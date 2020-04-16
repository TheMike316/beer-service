package org.brewery.beerservice.service.brewing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.beerservice.config.JmsConfig;
import org.brewery.beerservice.domain.mapper.BeerMapper;
import org.brewery.beerservice.repository.BeerRepository;
import org.brewery.beerservice.service.inventory.BeerInventoryService;
import org.brewery.common.model.event.BrewBeerEvent;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService {
    private final BeerRepository repository;
    private final BeerInventoryService inventoryService;
    private final BeerMapper mapper;
    private final JmsTemplate jmsTemplate;


    @Scheduled(fixedRate = 5000) //every 5 seconds
    public void checkForLowInventory() {
        repository.findAll().forEach(beer -> {
            var inventory = inventoryService.getOnHandInventory(beer.getId());

            log.info("Inventory of Beer [{}]: {}; minimum required on hand: {} ",
                    beer.getId(), inventory, beer.getMinOnHand());

            if (beer.getMinOnHand() >= inventory) {
                log.info("Inventory of beer [" + beer.getId() + "] has reached the minimum! Brewing new beer");
                jmsTemplate.convertAndSend(JmsConfig.BEER_REQUEST_QUEUE, new BrewBeerEvent(mapper.beerToBeerDto(beer)));
            }
        });
    }
}
