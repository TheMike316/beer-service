package com.example.beerservice.service;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.domain.mapper.BeerMapper;
import com.example.beerservice.repository.BeerRepository;
import com.example.beerservice.web.model.BeerDto;
import com.example.beerservice.web.model.BeerStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class BeerServiceImplTest {

    @Mock
    BeerRepository repository;


    @Mock
    BeerMapper mapper;

    @InjectMocks
    BeerServiceImpl service;

    UUID id;

    Beer entity;

    BeerDto dto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        entity = Beer.builder()
                .id(id)
                .version(1L)
                .beerName("Testy McGuffin")
                .beerStyle(BeerStyle.ALE.toString())
                .upc("0631234200036")
                .price(BigDecimal.valueOf(6.99))
                .quantityToBrew(200)
                .minOnHand(25)
                .build();

        dto = BeerDto.builder()
                .id(id)
                .version(1L)
                .beerName("Testy McGuffin")
                .beerStyle(BeerStyle.ALE)
                .upc("0631234200036")
                .price(BigDecimal.valueOf(6.99))
                .quantityOnHand(10000)
                .build();

        given(mapper.beerToBeerDto(entity)).willReturn(dto);
        given(mapper.beerDtoToBeer(dto)).willReturn(entity);
    }

    @Test
    void getById() {
        given(repository.findById(any())).willReturn(Optional.of(entity));

        var actual = service.getById(id);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(entity.getId(), actual.getId());
        Assertions.assertEquals(entity.getVersion(), actual.getVersion());
        Assertions.assertEquals(entity.getBeerName(), actual.getBeerName());
        Assertions.assertEquals(entity.getBeerStyle(), actual.getBeerStyle().toString());
        Assertions.assertEquals(entity.getUpc(), actual.getUpc());
        Assertions.assertEquals(entity.getPrice(), actual.getPrice());

        verify(repository, times(1)).findById(id);
    }

    @Test
    void getByIdFail() {
        given(repository.findById(any())).willReturn(Optional.empty());

        var e = Assertions.assertThrows(ResponseStatusException.class, () -> service.getById(id));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    @Test
    void saveNewBeer() {
        given(repository.save(any())).willReturn(entity);

        var actual = service.saveNewBeer(dto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(entity.getId(), actual.getId());
        Assertions.assertEquals(entity.getVersion(), actual.getVersion());
        Assertions.assertEquals(entity.getBeerName(), actual.getBeerName());
        Assertions.assertEquals(entity.getBeerStyle(), actual.getBeerStyle().toString());
        Assertions.assertEquals(entity.getUpc(), actual.getUpc());
        Assertions.assertEquals(entity.getPrice(), actual.getPrice());

        verify(repository, times(1)).save(any());
    }

    @Test
    void updateBeer() {
        given(repository.findById(any())).willReturn(Optional.of(entity));

        service.updateBeer(id, dto);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any());
    }


    @Test
    void updateFail() {
        given(repository.findById(any())).willReturn(Optional.empty());

        var e = Assertions.assertThrows(ResponseStatusException.class, () -> service.updateBeer(id, dto));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }


    @Test
    void deleteById() {
        service.deleteById(id);

        verify(repository, times(1)).deleteById(id);
    }
}