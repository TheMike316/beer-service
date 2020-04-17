package org.brewery.common.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brewery.common.model.BeerOrderDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateBeerOrderRequest {

    private BeerOrderDto beerOrderDto;
}
