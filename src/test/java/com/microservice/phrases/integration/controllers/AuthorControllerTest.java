package com.microservice.phrases.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.phrases.Author;
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
public class AuthorControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Author author1;
    private Author author2;

    private List<String> authorMessages = new ArrayList<>();

    private Author invalidAuthor = new Author();

    private List<String> invalidParamsMessages = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(wac).build();
        createDummyAuthors();
        setInvalidAuthor();
        setInvalidAuthorParamsMessages();
        setAuthorMessages();
    }

    private void createDummyAuthors(){
        author1 = new Author("AUTHOR1", new Date());
        author2 = new Author("AUTHOR2", new Date());
    }

    /**
     * Author attributes with random and invalid number of characters
     * name = 101 characters
     */
    private void setInvalidAuthor() {
        invalidAuthor.setName("rumtabvtxikoxopgdbdhhekzvdsjvyaneluscptdjmirtmeiekzexhgyfqvrceoiglrygplmvgejgwmxfrqngzfyfxumqecwxsbdu");
    }

    private void setInvalidAuthorParamsMessages() {
        invalidParamsMessages.add("The name field must have between 1 and 100 characters");
    }

    private void setAuthorMessages() {
        authorMessages.add("The field name must not be empty");
        authorMessages.add("The field name must have between 1 and 100 characters");
    }

    @Test
    public void a_index() throws Exception {
        mockMvc.perform(get("/authors")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(46)))
                .andExpect(jsonPath("$.[0].name", is("Zig Ziglar")));
    }

    /* BEGIN SHOW authorController method tests */

    @Test
    public void show_withProperId() throws Exception {
        mockMvc.perform(get("/authors/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("Zig Ziglar")));
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/authors/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        mockMvc.perform(get("/authors/{id}", anyLong()))
                .andExpect(status().isNotFound());
    }

    /* END SHOW authorController method tests */

    /* BEGIN CREATE authorController method tests */

    @Test
    public void create_withProperAuthor() throws Exception {
        mockMvc.perform(post("/authors")
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.author").exists())
                .andExpect(jsonPath("$.author.name", is("AUTHOR1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));
    }

    @Test
    public void create_whenAuthorIsEmpty() throws Exception {
        mockMvc.perform(post("/authors")
                .content(objectMapper.writeValueAsString(new Author()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(authorMessages.get(0))));
    }

    @Test
    public void create_whenAuthorHasInvalidParams() throws Exception {
        mockMvc.perform(post("/authors")
                .content(objectMapper.writeValueAsString(invalidAuthor))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(authorMessages.get(1))));
    }

    /* END CREATE authorController method tests */

    /* BEGIN UPDATE authorController method tests */

    @Test
    public void update_withProperAuthorAndId() throws Exception {
        mockMvc.perform(put("/authors/{id}", 1)
                .content(objectMapper.writeValueAsString(author2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.author").exists())
                .andExpect(jsonPath("$.author.name", is("AUTHOR2")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));
    }

    @Test
    public void update_whenAuthorIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/authors/{id}", "randomString")
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenAuthorIsEmpty_AndProperId() throws Exception {
        mockMvc.perform(put("/authors/{id}", 1)
                .content(objectMapper.writeValueAsString(new Author()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(authorMessages.get(0))));
    }

    @Test
    public void update_whenAuthorIsInvalid_AndProperId() throws Exception {
        mockMvc.perform(put("/authors/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidAuthor))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(authorMessages.get(1))));
    }

    @Test
    public void update_whenAuthorIsNotFound() throws Exception {
        mockMvc.perform(put("/authors/{id}", anyLong())
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END UPDATE authorController method tests */

    /* BEGIN DELETE authorController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));
    }

    @Test
    public void delete_withInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    /* END DELETE authorController method tests */
}
