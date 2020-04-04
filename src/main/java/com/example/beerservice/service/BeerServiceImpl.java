package com.example.beerservice.service;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.domain.mapper.BeerMapper;
import com.example.beerservice.repository.BeerRepository;
import com.example.beerservice.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public BeerDto getById(UUID beerId) {
        return beerRepository.findById(beerId).map(beerMapper::beerToBeerDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find beer with id: " + beerId));
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        var beer = beerMapper.beerDtoToBeer(beerDto);
        if (beer == null)
            throw new IllegalArgumentException("The given dto is null!");

        Beer savedBeer = beerRepository.save(beer);
        return beerMapper.beerToBeerDto(savedBeer);
    }

    @Override
    public void updateBeer(UUID beerId, BeerDto beerDto) {
        var beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find beer with id: " + beerId));

        BeanUtils.copyProperties(beerDto, beer, "id", "version", "createdDate", "lastModifiedDate", "quantityOnHand");

        beer.setLastModifiedDate(Timestamp.from(Instant.now()));

        beerRepository.save(beer);
    }

    @Override
    public void deleteById(UUID beerId) {
        beerRepository.deleteById(beerId);
    }
}
