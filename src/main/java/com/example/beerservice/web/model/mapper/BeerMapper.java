package com.example.beerservice.web.model.mapper;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.web.model.BeerDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
@DecoratedWith(BeerMapperDecorator.class)
public interface BeerMapper {

    BeerDto beerToBeerDto(Beer beer);

    BeerDto beerToBeerDtoWithInventoryData(Beer beer);

    Beer beerDtoToBeer(BeerDto beerDto);

}
