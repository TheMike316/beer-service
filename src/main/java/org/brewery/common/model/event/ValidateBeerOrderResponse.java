package org.brewery.common.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateBeerOrderResponse {

    private boolean isValid;
    private UUID beerOrderId;
}
