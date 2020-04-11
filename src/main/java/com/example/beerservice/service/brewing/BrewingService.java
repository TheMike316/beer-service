package com.example.beerservice.service.brewing;

import com.example.beerservice.config.JmsConfig;
import com.example.beerservice.event.BrewBeerEvent;
import com.example.beerservice.repository.BeerRepository;
import com.example.beerservice.service.inventory.BeerInventoryService;
import com.example.beerservice.web.model.mapper.BeerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
