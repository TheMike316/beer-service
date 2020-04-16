package org.brewery.beerservice.service;

import org.brewery.beerservice.domain.Beer;
import org.brewery.beerservice.domain.mapper.BeerMapper;
import org.brewery.beerservice.repository.BeerRepository;
import org.brewery.common.model.BeerDto;
import org.brewery.common.model.BeerList;
import org.brewery.common.model.BeerStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BeerServiceImplTest {

    @Mock
    BeerRepository repository;


    @Mock
    BeerMapper mapper;

    @InjectMocks
    BeerServiceImpl service;

    UUID id;
    String upc = "0631234200036";

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
                .upc(upc)
                .price(BigDecimal.valueOf(6.99))
                .quantityToBrew(200)
                .minOnHand(25)
                .build();

        dto = BeerDto.builder()
                .id(id)
                .version(1L)
                .beerName("Testy McGuffin")
                .beerStyle(BeerStyle.ALE)
                .upc(upc)
                .price(BigDecimal.valueOf(6.99))
                .quantityOnHand(10000)
                .build();

        given(mapper.beerToBeerDto(entity)).willReturn(dto);
        given(mapper.beerToBeerDtoWithInventoryData(entity)).willReturn(dto);
        given(mapper.beerDtoToBeer(dto)).willReturn(entity);
    }

    @Test
    void getById() {
        given(repository.findById(any())).willReturn(Optional.of(entity));

        var actual = service.getById(id, false);

        assertDto(actual);

        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).beerToBeerDto(any());
    }

    @Test
    void getByIdWithInventory() {
        given(repository.findById(any())).willReturn(Optional.of(entity));

        var actual = service.getById(id, true);

        assertDto(actual);

        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).beerToBeerDtoWithInventoryData(any());
    }

    @Test
    void getByIdFail() {
        given(repository.findById(any())).willReturn(Optional.empty());

        var e = Assertions.assertThrows(ResponseStatusException.class, () -> service.getById(id, false));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }


    @Test
    void getByUpc() {
        given(repository.findByUpc(anyString())).willReturn(Optional.of(entity));

        var actual = service.getByUpc(upc, false);

        assertDto(actual);

        verify(repository, times(1)).findByUpc(upc);
        verify(mapper, times(1)).beerToBeerDto(any());
    }

    @Test
    void getByUpcWithInventory() {
        given(repository.findByUpc(anyString())).willReturn(Optional.of(entity));

        var actual = service.getByUpc(upc, true);

        assertDto(actual);

        verify(repository, times(1)).findByUpc(upc);
        verify(mapper, times(1)).beerToBeerDtoWithInventoryData(any());
    }


    @Test
    void saveNewBeer() {
        given(repository.save(any())).willReturn(entity);

        var actual = service.saveNewBeer(dto);

        assertDto(actual);

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
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }


    @Test
    void deleteById() {
        service.deleteById(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void getList() {
        var pageRequest = PageRequest.of(0, 10);
        var expected = new BeerList(Collections.singletonList(dto));
        var beerPage = mockBeerPage(pageRequest);

        given(repository.findAll(any(Pageable.class))).willReturn(beerPage);

        var actual = service.getList(null, null, pageRequest, false);

        Assertions.assertNotNull(actual);
        assertEquals(expected.getContent().get(0).getId(), actual.getContent().get(0).getId());
        assertEquals(expected.getContent().get(0).getBeerName(), actual.getContent().get(0).getBeerName());
        assertEquals(expected.getContent().get(0).getBeerStyle(), actual.getContent().get(0).getBeerStyle());
        assertEquals(expected.getContent().get(0).getUpc(), actual.getContent().get(0).getUpc());

        verify(repository, times(1)).findAll(any(Pageable.class));
        verify(repository, times(0)).findAllByBeerNameAndBeerStyle(anyString(), anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerStyle(anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerName(anyString(), any(Pageable.class));
        verify(mapper, times(actual.getNumberOfElements())).beerToBeerDto(any());
    }

    @Test
    void getListByBeerName() {
        var pageRequest = PageRequest.of(0, 10);
        var expected = new BeerList(Collections.singletonList(dto));
        var beerPage = mockBeerPage(pageRequest);

        given(repository.findAllByBeerName(anyString(), any(Pageable.class))).willReturn(beerPage);

        var actual = service.getList(dto.getBeerName(), null, pageRequest, false);

        Assertions.assertNotNull(actual);
        assertEquals(expected.getContent().get(0).getId(), actual.getContent().get(0).getId());
        assertEquals(expected.getContent().get(0).getBeerName(), actual.getContent().get(0).getBeerName());
        assertEquals(expected.getContent().get(0).getBeerStyle(), actual.getContent().get(0).getBeerStyle());
        assertEquals(expected.getContent().get(0).getUpc(), actual.getContent().get(0).getUpc());

        verify(repository, times(1)).findAllByBeerName(anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerNameAndBeerStyle(anyString(), anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerStyle(anyString(), any(Pageable.class));
        verify(repository, times(0)).findAll(any(Pageable.class));
        verify(mapper, times(actual.getNumberOfElements())).beerToBeerDto(any());
    }

    @Test
    void getListByBeerStyle() {
        var pageRequest = PageRequest.of(0, 10);
        var expected = new BeerList(Collections.singletonList(dto));
        var beerPage = mockBeerPage(pageRequest);

        given(repository.findAllByBeerStyle(anyString(), any(Pageable.class))).willReturn(beerPage);

        var actual = service.getList(null, dto.getBeerStyle().name(), pageRequest, false);

        Assertions.assertNotNull(actual);
        assertEquals(expected.getContent().get(0).getId(), actual.getContent().get(0).getId());
        assertEquals(expected.getContent().get(0).getBeerName(), actual.getContent().get(0).getBeerName());
        assertEquals(expected.getContent().get(0).getBeerStyle(), actual.getContent().get(0).getBeerStyle());
        assertEquals(expected.getContent().get(0).getUpc(), actual.getContent().get(0).getUpc());

        verify(repository, times(1)).findAllByBeerStyle(anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerNameAndBeerStyle(anyString(), anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerName(anyString(), any(Pageable.class));
        verify(repository, times(0)).findAll(any(Pageable.class));
        verify(mapper, times(actual.getNumberOfElements())).beerToBeerDto(any());
    }

    @Test
    void getListByBeerNameAndBeerStyle() {
        var pageRequest = PageRequest.of(0, 10);
        var expected = new BeerList(Collections.singletonList(dto));
        var beerPage = mockBeerPage(pageRequest);

        given(repository.findAllByBeerNameAndBeerStyle(anyString(), anyString(), any(Pageable.class))).willReturn(beerPage);

        var actual = service.getList(dto.getBeerName(), dto.getBeerStyle().name(), pageRequest, false);

        Assertions.assertNotNull(actual);
        assertEquals(expected.getContent().get(0).getId(), actual.getContent().get(0).getId());
        assertEquals(expected.getContent().get(0).getBeerName(), actual.getContent().get(0).getBeerName());
        assertEquals(expected.getContent().get(0).getBeerStyle(), actual.getContent().get(0).getBeerStyle());
        assertEquals(expected.getContent().get(0).getUpc(), actual.getContent().get(0).getUpc());

        verify(repository, times(1)).findAllByBeerNameAndBeerStyle(anyString(), anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerStyle(anyString(), any(Pageable.class));
        verify(repository, times(0)).findAllByBeerName(anyString(), any(Pageable.class));
        verify(repository, times(0)).findAll(any(Pageable.class));
        verify(mapper, times(actual.getNumberOfElements())).beerToBeerDto(any());
    }

    @Test
    void getListWithInventory() {

        var pageRequest = PageRequest.of(0, 10);
        var beerPage = mockBeerPage(pageRequest);

        given(repository.findAll(any(Pageable.class))).willReturn(beerPage);

        var actual = service.getList(null, null, pageRequest, true);

        Assertions.assertNotNull(actual);

        verify(repository, times(1)).findAll(any(Pageable.class));
        verify(mapper, times(actual.getNumberOfElements())).beerToBeerDtoWithInventoryData(any());
    }

    private void assertDto(BeerDto actual) {
        Assertions.assertNotNull(actual);
        assertEquals(entity.getId(), actual.getId());
        assertEquals(entity.getVersion(), actual.getVersion());
        assertEquals(entity.getBeerName(), actual.getBeerName());
        assertEquals(entity.getBeerStyle(), actual.getBeerStyle().toString());
        assertEquals(entity.getUpc(), actual.getUpc());
        assertEquals(entity.getPrice(), actual.getPrice());
    }

    private Page<Beer> mockBeerPage(PageRequest pageRequest) {
        var beerPage = mock(Page.class);
        given(beerPage.getContent()).willReturn(Collections.singletonList(entity));
        given(beerPage.getPageable()).willReturn(pageRequest);
        given(beerPage.getTotalElements()).willReturn(1L);
        return beerPage;
    }

}