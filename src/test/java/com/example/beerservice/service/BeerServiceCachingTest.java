package com.example.beerservice.service;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.repository.BeerRepository;
import com.example.beerservice.service.inventory.BeerInventoryService;
import com.example.beerservice.web.model.BeerDto;
import com.example.beerservice.web.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BeerServiceCachingTest {

    @MockBean
    BeerRepository repository;

    @MockBean
    BeerInventoryService beerInventoryService;

    @Autowired
    BeerService service;

    UUID id = UUID.randomUUID();

    Beer entity = Beer.builder()
            .id(id)
            .version(1L)
            .beerName("Testy McGuffin")
            .beerStyle(BeerStyle.ALE.toString())
            .upc("0631234200036")
            .price(BigDecimal.valueOf(6.99))
            .quantityToBrew(200)
            .minOnHand(25)
            .build();

    BeerDto dto = BeerDto.builder()
            .id(id)
            .version(1L)
            .beerName("Testy McGuffin")
            .beerStyle(BeerStyle.ALE)
            .upc("0631234200036")
            .price(BigDecimal.valueOf(6.99))
            .quantityOnHand(10000)
            .build();

    @Test
    void getByIdCached() {
        given(repository.findById(any())).willReturn(Optional.of(entity));

        var dtoUncached = service.getById(id, false);

        var dtoCached = service.getById(id, false);

        //repo should be accessed only once
        verify(repository, times(1)).findById(id);
        assertEquals(dtoUncached, dtoCached);
    }

    @Test
    void getByIdUncached() {
        given(repository.findById(any())).willReturn(Optional.of(entity));
        given(beerInventoryService.getOnHandInventory(any())).willReturn(100);

        var dtoUncached = service.getById(id, true);

        var dtoCached = service.getById(id, true);

        verify(repository, times(2)).findById(id);
        assertEquals(dtoUncached, dtoCached);
    }

    @Test
    void testEvictAfterUpdate() {
        given(repository.findById(any())).willReturn(Optional.of(entity));

        service.getById(id, false);

        service.updateBeer(id, dto);

        service.getById(id, false);

        // the service should not use the cache the second time, as the dto for the id should have been evicted by updating it
        // 3 instead of 2, because updateBeer also calls findById once
        verify(repository, times(3)).findById(id);
    }

    @Test
    void getListCached() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Beer> beerPage = mockBeerPage(pageRequest);
        given(repository.findAll(any(Pageable.class))).willReturn(beerPage);

        service.getList(null, null, pageRequest, false);

        service.getList(null, null, pageRequest, false);

        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getListUncached() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Beer> beerPage = mockBeerPage(pageRequest);
        given(repository.findAll(any(Pageable.class))).willReturn(beerPage);
        given(beerInventoryService.getOnHandInventory(any())).willReturn(100);

        service.getList(null, null, pageRequest, true);

        service.getList(null, null, pageRequest, true);

        verify(repository, times(2)).findAll(any(Pageable.class));
    }

    @Test
    void clearCacheAfterSave() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Beer> beerPage = mockBeerPage(pageRequest);
        given(repository.findAll(any(Pageable.class))).willReturn(beerPage);

        service.getList(null, null, pageRequest, false);

        service.saveNewBeer(dto);

        service.getList(null, null, pageRequest, false);

        // service should not use the cache the second time, as it should have been invalidated by saving a new beer
        verify(repository, times(2)).findAll(any(Pageable.class));
    }

    @Test
    void clearCacheAfterDelete() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Beer> beerPage = mockBeerPage(pageRequest);
        given(repository.findAll(any(Pageable.class))).willReturn(beerPage);

        service.getList(null, null, pageRequest, false);

        service.deleteById(id);

        service.getList(null, null, pageRequest, false);

        // service should not use the cache the second time, as it should have been invalidated by deleting a beer
        verify(repository, times(2)).findAll(any(Pageable.class));
    }

    private Page<Beer> mockBeerPage(PageRequest pageRequest) {
        var beerPage = mock(Page.class);
        given(beerPage.getContent()).willReturn(Collections.singletonList(entity));
        given(beerPage.getPageable()).willReturn(pageRequest);
        given(beerPage.getTotalElements()).willReturn(1L);
        return beerPage;
    }
}
