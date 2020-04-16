package org.brewery.common.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brewery.common.model.BeerDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerEvent {

    private BeerDto beerDto;
}
