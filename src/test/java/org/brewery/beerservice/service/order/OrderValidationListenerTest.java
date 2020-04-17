package org.brewery.beerservice.service.order;

import org.brewery.beerservice.config.JmsConfig;
import org.brewery.beerservice.repository.BeerRepository;
import org.brewery.common.model.BeerOrderDto;
import org.brewery.common.model.BeerOrderLineDto;
import org.brewery.common.model.event.ValidateBeerOrderRequest;
import org.brewery.common.model.event.ValidateBeerOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OrderValidationListenerTest {

    static final String UPC_1 = "1234";
    static final String UPC_2 = "4567";
    static final String UPC_3 = "8910";

    @Mock
    JmsTemplate jmsTemplate;

    @Mock
    BeerRepository beerRepository;

    @InjectMocks
    OrderValidationListener listener;

    UUID id = UUID.randomUUID();

    BeerOrderDto dto;

    ValidateBeerOrderRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        var orderLines = Arrays.asList(
                BeerOrderLineDto.builder()
                        .upc(UPC_1)
                        .build(),
                BeerOrderLineDto.builder()
                        .upc(UPC_2)
                        .build(),
                BeerOrderLineDto.builder()
                        .upc(UPC_3)
                        .build()
        );

        dto = BeerOrderDto.builder()
                .id(id)
                .beerOrderLines(orderLines)
                .build();

        request = new ValidateBeerOrderRequest(dto);
    }

    @Test
    void validateOrderSuccess() {
        given(beerRepository.existsByUpc(UPC_1)).willReturn(true);
        given(beerRepository.existsByUpc(UPC_2)).willReturn(true);
        given(beerRepository.existsByUpc(UPC_3)).willReturn(true);

        listener.validateOrder(request);

        var expectedResponse = ValidateBeerOrderResponse.builder().isValid(true).beerOrderId(dto.getId()).build();

        verify(beerRepository, times(3)).existsByUpc(anyString());
        verify(jmsTemplate, times(1)).convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, expectedResponse);
    }

    @Test
    void validateOrderFailure() {
        given(beerRepository.existsByUpc(UPC_1)).willReturn(true);
        given(beerRepository.existsByUpc(UPC_2)).willReturn(false);
        given(beerRepository.existsByUpc(UPC_3)).willReturn(true);

        listener.validateOrder(request);

        var expectedResponse = ValidateBeerOrderResponse.builder().isValid(false).beerOrderId(dto.getId()).build();

        verify(beerRepository, times(2)).existsByUpc(anyString());
        verify(jmsTemplate, times(1)).convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, expectedResponse);
    }
}