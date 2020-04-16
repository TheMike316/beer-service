package org.brewery.beerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.brewery.common.model.event.NewInventoryEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;

@Configuration
public class JmsConfig {

    public static final String BEER_REQUEST_QUEUE = "brewing-request";
    public static final String NEW_INVENTORY_QUEUE = "new-inventory";

    @Bean
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);

        var typeMap = new HashMap<String, Class<?>>();
        typeMap.put("new-inventory-event", NewInventoryEvent.class);
        converter.setTypeIdMappings(typeMap);

        return converter;
    }
}
