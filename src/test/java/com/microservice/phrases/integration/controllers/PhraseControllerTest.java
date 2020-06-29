package com.microservice.phrases.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.phrases.Phrase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Pruebas de integración utilizando base de datos en memoria(H2)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Configuracion de base de datos h2, tomando propiedades de yml test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@IfProfileValue(name="test-groups", value="integration")
public class PhraseControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    private TestRestTemplate restTemplate;

    private Phrase phrase1;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Phrase invalidPhrase = new Phrase();
    private List<String> phraseMessages = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(wac).build();
        phrase1 = new Phrase("phrase1 body test", null, null, null, 99L, new Date());
        setInvalidPhrase();
        setEmptyPhraseMessages();
    }

    /**
     * Phrase attributes with random and invalid number of characters
     * body = 201 characters
     */
    private void setInvalidPhrase() {
        invalidPhrase.setBody("iaUiupA7Q2bhhfRvBp3jA9zOe2l7fyAzyuwEZqr3NpYng9Z9Ggx4cbqMV3keCcV2qFnzw6bjCHQzzqFx72bPeFS36ZxInNzR2nztROnQEu4FCgGWXUx1QgWjPZn3bt0EcgXSpEtMqrChtC3gfpRPgqpRtoG6xQLlQK1K9Cmjyp2PqWJrlfQdCcwjxvgKgV4cXCYuRFfLv");
    }

    private void setEmptyPhraseMessages() {
        phraseMessages.add("The field body must not be empty");
        phraseMessages.add("The field body must have between 1 and 200 characters");
    }

    @Test
    public void a_index() throws Exception { // Se nombra a_ ya que el orden de ejecucion de los tests es de nombre ascendente, la otra forma sería utilizar Junit 5
        mockMvc.perform(get("/phrases")
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
               // .andExpect(jsonPath("$", hasSize(51))) // Cantidad de frases en import.sql, el valor varía dependiendo de las pruebas que se ejecuten(author,images,types) eliminan en cascada
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].body", is("Si puedes soñarlo, puedes lograrlo")))
                .andExpect(jsonPath("$.[0].author").exists())
                .andExpect(jsonPath("$.[0].type").exists())
                .andExpect(jsonPath("$.[0].likesCounter", is(121)));
    }
/*

    // Creación de frase utilizando una base de datos en memoria(h2) y testrest template
    @Test
    public void create_PhraseProperObject() {
        ResponseEntity<String> response = restTemplate.postForEntity("/phrases", phrase1, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertEquals(true, response.getBody().contains("phrase1"));
    }
*/
    /* BEGIN SHOW phraseController method tests */

    @Test
    public void show_withProperId() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/phrases/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();

        Assert.assertEquals(200, result.getResponse().getStatus());
        Assert.assertEquals(true, result.getResponse().getContentAsString().contains("Si puedes soñarlo, puedes lograrlo"));
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/phrases/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        mockMvc.perform(get("/phrases/{id}", 99))
                .andExpect(status().isNotFound());
    }

    /* END SHOW phraseController method tests */

    /* BEGIN CREATE phraseController method tests */

    @Test
    public void create_withProperPhrase() throws Exception {
        mockMvc.perform(post("/phrases")
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phrase").exists())
                .andExpect(jsonPath("$.phrase.body", is("phrase1 body test")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));
    }

    @Test
    public void create_whenPhraseIsEmpty() throws Exception {
        mockMvc.perform(post("/phrases")
                .content(objectMapper.writeValueAsString(new Phrase()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(phraseMessages.get(0))));
    }

    @Test
    public void create_whenPhraseHasInvalidParams() throws Exception {
        mockMvc.perform(post("/phrases")
                .content(objectMapper.writeValueAsString(invalidPhrase))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(phraseMessages.get(1))));
    }

    /* END CREATE phraseController method tests */

    /* BEGIN UPDATE phraseController method tests */

    @Test
    public void update_withProperPhraseAndId() throws Exception {
        mockMvc.perform(put("/phrases/{id}", 1)
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phrase").exists())
                .andExpect(jsonPath("$.phrase.body", is("phrase1 body test")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));
    }

    @Test
    public void update_whenPhraseIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/phrases/{id}", "randomString")
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenPhraseIsEmpty_AndProperId() throws Exception {
        mockMvc.perform(put("/phrases/{id}", 1)
                .content(objectMapper.writeValueAsString(new Phrase()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(phraseMessages.get(0))));
    }

    @Test
    public void update_whenPhraseIsInvalid_AndProperId() throws Exception {
        mockMvc.perform(put("/phrases/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidPhrase))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem(phraseMessages.get(1))));
    }

    @Test
    public void update_whenPhraseIsNotFound() throws Exception {
        mockMvc.perform(put("/phrases/{id}", 0)
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END UPDATE phraseController method tests */

    /* BEGIN DELETE phraseController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/phrases/{id}", 50))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));
    }

    @Test
    public void delete_withInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/phrases/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    /* END DELETE phraseController method tests */
}
