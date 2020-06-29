package com.microservice.phrases.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.phrases.Image;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Pruebas de integración utilizando base de datos en memoria(H2)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Configuracion de base de datos h2, tomando propiedades de yml test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@IfProfileValue(name="test-groups", value="integration")
public class ImageControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> imageMessages = new ArrayList<>();
    private Image invalidImage = new Image();

    private Image image1;
    private Image image2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(wac).build();
        createDummyImages();
        setInvalidImage();
        setInvalidImageParamsMessages();
        setImageMessages();
    }

    private void createDummyImages(){
        image1 = new Image("IMAGE1", new Date());
        image2 = new Image("IMAGE2", new Date());
    }

    /**
     * Image attributes with random and invalid number of characters
     * name = 21 characters
     */
    private void setInvalidImage() {
        invalidImage.setName("fwmflkftupdcqnkdsytuc");
    }

    private void setInvalidImageParamsMessages() {
        invalidParamsMessages.add("The name field must have between 1 and 20 characters");
    }

    private void setImageMessages() {
        imageMessages.add("The field name must not be empty");
        imageMessages.add("The field name must have between 1 and 20 characters");
    }


    @Test
    public void a_index() throws Exception {
        mockMvc.perform(get("/images")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(11)))
                .andExpect(jsonPath("$.[0].name", is("Imagen1")));
    }

    /* BEGIN SHOW imageController method tests */

    @Test
    public void show_withProperId() throws Exception {
         mockMvc.perform(get("/images/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("Imagen1")));
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/images/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        mockMvc.perform(get("/images/{id}", anyLong()))
                .andExpect(status().isNotFound());
    }

    /* END SHOW imageController method tests */

    /* BEGIN CREATE imageController method tests */

    @Test
    public void create_withProperImage() throws Exception {
        mockMvc.perform(post("/images")
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.image").exists())
                .andExpect(jsonPath("$.image.name", is("IMAGE1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));
    }

    @Test
    public void create_whenImageIsEmpty() throws Exception {
        mockMvc.perform(post("/images")
                .content(objectMapper.writeValueAsString(new Image()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(imageMessages.get(0))));
    }

    @Test
    public void create_whenImageHasInvalidParams() throws Exception {
        mockMvc.perform(post("/images")
                .content(objectMapper.writeValueAsString(invalidImage))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(imageMessages.get(1))));
    }

    /* END CREATE imageController method tests */

    /* BEGIN UPDATE imageController method tests */

    @Test
    public void update_withProperImageAndId() throws Exception {
        mockMvc.perform(put("/images/{id}", 1)
                .content(objectMapper.writeValueAsString(image2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.image").exists())
                .andExpect(jsonPath("$.image.name", is("IMAGE2")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));
    }

    @Test
    public void update_whenImageIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/images/{id}", "randomString")
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenImageIsEmpty_AndProperId() throws Exception {
        mockMvc.perform(put("/images/{id}", 1)
                .content(objectMapper.writeValueAsString(new Image()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(imageMessages.get(0))));
    }

    @Test
    public void update_whenImageIsInvalid_AndProperId() throws Exception {
        mockMvc.perform(put("/images/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidImage))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(imageMessages.get(1))));
    }

    @Test
    public void update_whenImageIsNotFound() throws Exception {
        mockMvc.perform(put("/images/{id}", 0)
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END UPDATE imageController method tests */

    /* BEGIN DELETE imageController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/images/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/images/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    /* END DELETE imageController method tests */
}
