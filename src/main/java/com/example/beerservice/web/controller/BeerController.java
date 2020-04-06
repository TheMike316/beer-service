package com.example.beerservice.web.controller;

import com.example.beerservice.service.BeerService;
import com.example.beerservice.web.model.BeerDto;
import com.example.beerservice.web.model.BeerList;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/beer")
public class BeerController {

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "25";

    private final BeerService beerService;

    @GetMapping
    public BeerList listBeers(@RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUMBER) Integer pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
                              @RequestParam(value = "beerName", required = false) String beerName,
                              @RequestParam(value = "beerStyle", required = false) String beerStyle,
                              @RequestParam(value = "showInventoryOnHand", defaultValue = "false") boolean showInventoryOnHand) {
        return beerService.getList(beerName, beerStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);
    }

    @GetMapping("/{beerId}")
    public BeerDto getById(@NotNull @PathVariable UUID beerId,
                           @RequestParam(value = "showInventoryOnHand", defaultValue = "false") boolean showInventoryOnHand) {
        return beerService.getById(beerId, showInventoryOnHand);
    }

    @GetMapping("/upc/{upc}")
    public BeerDto getByUpc(@NotNull @PathVariable String upc,
                            @RequestParam(value = "showInventoryOnHand", defaultValue = "false") boolean showInventoryOnHand) {
        return beerService.getByUpc(upc, showInventoryOnHand);
    }

    @PostMapping
    public ResponseEntity<Void> createNewBeer(@Valid @RequestBody BeerDto beerDto) {
        BeerDto savedBeer = beerService.saveNewBeer(beerDto);

        HttpHeaders headers = new HttpHeaders();
//        TODO create actual url for resource
//        headers.setLocation("/api/v1/beer/" + savedBeer.getId());
        headers.add("location", "/api/v1/beer/" + savedBeer.getId());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{beerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBeer(@NotNull @PathVariable UUID beerId, @Valid @RequestBody BeerDto beerDto) {
        beerService.updateBeer(beerId, beerDto);
    }

    @DeleteMapping("/{beerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBeer(@NotNull @PathVariable UUID beerId) {
        beerService.deleteById(beerId);
    }
}
