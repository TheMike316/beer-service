package com.example.beerservice.service;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.repository.BeerRepository;
import com.example.beerservice.web.model.BeerDto;
import com.example.beerservice.web.model.BeerList;
import com.example.beerservice.web.model.mapper.BeerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public BeerDto getById(UUID beerId, boolean showInventoryOnHand) {
        return beerRepository.findById(beerId)
                .map(beer -> showInventoryOnHand ?
                        beerMapper.beerToBeerDtoWithInventoryData(beer) : beerMapper.beerToBeerDto(beer))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Could not find beer with id: " + beerId));
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

    @Override
    public BeerList getList(String beerName, String beerStyle, PageRequest pageRequest, boolean showInventoryOnHand) {

        Page<Beer> beerPage;
        if (StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
            beerPage = beerRepository.findAll(pageRequest);

        } else if (!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
            beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);

        } else if (StringUtils.isEmpty(beerName)) {
            beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);

        } else {
            beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
        }

        List<BeerDto> content = beerPage.getContent().stream()
                .map(beer -> showInventoryOnHand ?
                        beerMapper.beerToBeerDtoWithInventoryData(beer) : beerMapper.beerToBeerDto(beer))
                .collect(Collectors.toList());

        return new BeerList(
                content,
                PageRequest.of(beerPage.getPageable().getPageNumber(), beerPage.getPageable().getPageSize()),
                beerPage.getTotalElements()
        );
    }
}
