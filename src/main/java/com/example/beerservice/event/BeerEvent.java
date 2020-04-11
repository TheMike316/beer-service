package com.example.beerservice.event;

import com.example.beerservice.web.model.BeerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerEvent {

    private BeerDto beerDto;
}
