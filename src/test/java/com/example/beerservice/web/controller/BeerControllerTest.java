package com.example.beerservice.web.controller;

import com.example.beerservice.service.BeerService;
import com.example.beerservice.web.model.BeerDto;
import com.example.beerservice.web.model.BeerList;
import com.example.beerservice.web.model.BeerStyle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    BeerDto beerDto;

    UUID mockId;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockId = UUID.randomUUID();

        var date = OffsetDateTime.now();

        beerDto = BeerDto.builder()
                .id(mockId)
                .version(1L)
                .createdDate(date)
                .lastModifiedDate(date)
                .beerName("Testy McGuffin")
                .beerStyle(BeerStyle.ALE)
                .upc("0631234200036")
                .price(BigDecimal.valueOf(6.99))
                .quantityOnHand(10000)
                .build();
    }

    @Test
    public void testGetList() throws Exception {
        given(beerService.getList(any(), any(), any(), anyBoolean())).willReturn(new BeerList(Collections.singletonList(beerDto)));

        mockMvc.perform(get("/api/v1/beer?pageNum=0&pageSize=10&showInventoryOnHand=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(mockId.toString()))
                .andExpect(jsonPath("$.content[0].version").value(beerDto.getVersion()))
                .andExpect(jsonPath("$.content[0].beerName").value(beerDto.getBeerName()))
                .andExpect(jsonPath("$.content[0].beerStyle").value(beerDto.getBeerStyle().toString()))
                .andExpect(jsonPath("$.content[0].upc").value(beerDto.getUpc()))
                .andExpect(jsonPath("$.content[0].price").value(beerDto.getPrice().toPlainString()))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(beerDto.getQuantityOnHand()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getListWithInventory() throws Exception {
        given(beerService.getList(any(), any(), any(), anyBoolean())).willReturn(new BeerList(Collections.singletonList(beerDto)));
        mockMvc.perform(get("/api/v1/beer?pageNum=0&pageSize=10&showInventoryOnHand=true"))
                .andExpect(status().isOk());

        verify(beerService, times(1)).getList(any(), any(), any(), eq(true));
    }

    @Test
    public void testGetById() throws Exception {

        given(beerService.getById(any(), anyBoolean())).willReturn(beerDto);

        mockMvc.perform(get("/api/v1/beer/{beerId}?showInventoryOnHand=false", mockId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockId.toString()))
                .andExpect(jsonPath("$.version").value(beerDto.getVersion()))
                .andExpect(jsonPath("$.beerName").value(beerDto.getBeerName()))
                .andExpect(jsonPath("$.beerStyle").value(beerDto.getBeerStyle().toString()))
                .andExpect(jsonPath("$.upc").value(beerDto.getUpc()))
                .andExpect(jsonPath("$.price").value(beerDto.getPrice().toPlainString()))
                .andExpect(jsonPath("$.quantityOnHand").value(beerDto.getQuantityOnHand()))
                .andDo(document("v1/beer-get",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of the desired beer")
                        ),
                        requestParameters(
                                parameterWithName("showInventoryOnHand")
                                        .description("Fetch quantity on hand data. default = false")
                        ),
                        getBeerResponseFieldsSnippet()
                ));

        verify(beerService, times(1)).getById(any(), anyBoolean());
    }

    @Test
    void getByIdWithInventory() throws Exception {
        given(beerService.getById(any(), anyBoolean())).willReturn(beerDto);

        mockMvc.perform(get("/api/v1/beer/{beerId}?showInventoryOnHand=true", mockId))
                .andExpect(status().isOk());

        verify(beerService, times(1)).getById(any(), eq(true));
    }

    private ResponseFieldsSnippet getBeerResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("id").description("ID of the object"),
                fieldWithPath("version").description("Version of the object"),
                fieldWithPath("createdDate").description("created date"),
                fieldWithPath("lastModifiedDate").description("last modified date"),
                fieldWithPath("beerName").description("Name of the beer"),
                fieldWithPath("beerStyle").description("Style of the beer"),
                fieldWithPath("upc").description("UPC of the beer"),
                fieldWithPath("price").description("Price of the beer"),
                fieldWithPath("quantityOnHand").description("Available quantity")
        );
    }

    @Test
    public void testCreateNewBeer() throws Exception {
        // fields must be null in the request
        beerDto.setId(null);
        beerDto.setCreatedDate(null);
        beerDto.setLastModifiedDate(null);
        beerDto.setVersion(null);
        var json = objectMapper.writeValueAsString(beerDto);
        // ...but we need to return a dto with a set id
        beerDto.setId(mockId);
        given(beerService.saveNewBeer(any())).willReturn(beerDto);

        mockMvc.perform(post("/api/v1/beer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("location", "/api/v1/beer/" + mockId))
                .andDo(document("v1/beer-new",
                        getBeerRequestFieldsSnippet()));

        verify(beerService, times(1)).saveNewBeer(any());
    }

    private RequestFieldsSnippet getBeerRequestFieldsSnippet() {
        var fields = new ConstrainedFields(BeerDto.class);

        return requestFields(
                fields.withPath("id").ignored(),
                fields.withPath("version").ignored(),
                fields.withPath("createdDate").ignored(),
                fields.withPath("lastModifiedDate").ignored(),
                fields.withPath("beerName").description("Name of the beer"),
                fields.withPath("beerStyle").description("Style of the beer"),
                fields.withPath("upc").description("UPC of the beer"),
                fields.withPath("price").description("Price of the beer"),
                fields.withPath("quantityOnHand").description("Available quantity")
        );
    }

    @Test
    public void testUpdateBeer() throws Exception {
        // fields must be null in the request
        beerDto.setId(null);
        beerDto.setCreatedDate(null);
        beerDto.setLastModifiedDate(null);
        beerDto.setVersion(null);
        var json = objectMapper.writeValueAsString(beerDto);

        mockMvc.perform(put("/api/v1/beer/{beerId}", mockId).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNoContent())
                .andDo(document("v1/beer-update",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of the desired beer")
                        ),
                        getBeerRequestFieldsSnippet()));

        verify(beerService, times(1)).updateBeer(any(), any());
    }

    @Test
    public void testDeleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/{beerId}", mockId))
                .andExpect(status().isNoContent())
                .andDo(document("v1/beer-delete",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of the desired beer")
                        )));

        verify(beerService, times(1)).deleteById(any());
    }

    private static class ConstrainedFields {
        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils.collectionToDelimitedString(
                    this.constraintDescriptions.descriptionsForProperty(path), ". ")));
        }
    }
}