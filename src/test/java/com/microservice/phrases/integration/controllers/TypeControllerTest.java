package com.microservice.phrases.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.phrases.Type;
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
public class TypeControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<String> invalidParamsMessages = new ArrayList<>();

    private List<String> typeMessages = new ArrayList<>();

    private Type invalidType = new Type();

    private Type type1;
    private Type type2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(wac).build();
        createDummyTypes();
        setInvalidType();
        setInvalidTypeParamsMessages();
        setTypeMessages();
    }

    private void createDummyTypes(){
        type1 = new Type("TYPE1", new Date());
        type2 = new Type("TYPE2", new Date());
    }

    /**
     * Type attributes with random and invalid number of characters
     * name = 21 characters
     */
    private void setInvalidType() {
        invalidType.setName("fwmflkftupdcqnkdsytuc");
    }

    private void setInvalidTypeParamsMessages() {
        invalidParamsMessages.add("The name field must have between 1 and 20 characters");
    }

    private void setTypeMessages() {
        typeMessages.add("The field name must not be empty");
        typeMessages.add("The field name must have between 1 and 20 characters");
    }

    @Test
    public void a_index() throws Exception {
        mockMvc.perform(get("/types")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$.[0].name", is("Inspiradora")));
    }

    /* BEGIN SHOW typeController method tests */

    @Test
    public void show_withProperId() throws Exception {
        mockMvc.perform(get("/types/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("Inspiradora")));
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/types/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        mockMvc.perform(get("/types/{id}", anyLong()))
                .andExpect(status().isNotFound());
    }

    /* END SHOW typeController method tests */

    /* BEGIN CREATE typeController method tests */

    @Test
    public void create_withProperType() throws Exception {
        mockMvc.perform(post("/types")
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.type.name", is("TYPE1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));
    }

    @Test
    public void create_whenTypeIsEmpty() throws Exception {
        mockMvc.perform(post("/types")
                .content(objectMapper.writeValueAsString(new Type()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(typeMessages.get(0))));
    }

    @Test
    public void create_whenTypeHasInvalidParams() throws Exception {
        mockMvc.perform(post("/types")
                .content(objectMapper.writeValueAsString(invalidType))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(typeMessages.get(1))));
    }

    /* END CREATE typeController method tests */

    /* BEGIN UPDATE typeController method tests */

    @Test
    public void update_withProperTypeAndId() throws Exception {
        mockMvc.perform(put("/types/{id}", 1)
                .content(objectMapper.writeValueAsString(type2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.type.name", is("TYPE2")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));
    }

    @Test
    public void update_whenTypeIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/types/{id}", "randomString")
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenTypeIsEmpty_AndProperId() throws Exception {
        mockMvc.perform(put("/types/{id}", 1)
                .content(objectMapper.writeValueAsString(new Type()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(typeMessages.get(0))));
    }

    @Test
    public void update_whenTypeIsInvalid_AndProperId() throws Exception {
        mockMvc.perform(put("/types/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidType))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(typeMessages.get(1))));
    }

    @Test
    public void update_whenTypeIsNotFound() throws Exception {
        mockMvc.perform(put("/types/{id}", 0)
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END UPDATE typeController method tests */

    /* BEGIN DELETE typeController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/types/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));
    }

    @Test
    public void delete_withInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/types/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    /* END DELETE typeController method tests */
}
