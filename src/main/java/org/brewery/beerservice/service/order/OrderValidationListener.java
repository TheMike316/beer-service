package org.brewery.beerservice.service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.beerservice.config.JmsConfig;
import org.brewery.beerservice.repository.BeerRepository;
import org.brewery.common.model.BeerOrderDto;
import org.brewery.common.model.BeerOrderLineDto;
import org.brewery.common.model.event.ValidateBeerOrderRequest;
import org.brewery.common.model.event.ValidateBeerOrderResponse;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderValidationListener {

    private final JmsTemplate jmsTemplate;
    private final BeerRepository beerRepository;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_REQUEST_QUEUE)
    public void validateOrder(ValidateBeerOrderRequest request) {
        BeerOrderDto dto = request.getBeerOrderDto();

        log.info("Received validate order request for order {}", dto.getId());

        var valid = dto.getBeerOrderLines().stream()
                .map(BeerOrderLineDto::getUpc)
                .allMatch(beerRepository::existsByUpc);

        var response = ValidateBeerOrderResponse.builder()
                .beerOrderId(dto.getId())
                .isValid(valid)
                .build();

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, response);

        if (valid)
            log.info("Order {} is valid. Sent response.", dto.getId());
        else
            log.warn("Order {} is NOT valid! Sent response.", dto.getId());
    }
}
