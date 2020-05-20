package com.microservice.phrases.integration.controllers;

import com.microservice.phrases.controllers.PhraseController;
import com.microservice.phrases.models.dao.IPhraseDao;
import com.microservice.phrases.models.services.IPhraseService;
import com.microservices.commons.models.entity.phrases.Author;
import com.microservices.commons.models.entity.phrases.Image;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.microservices.commons.models.entity.phrases.Type;
import com.microservices.commons.models.services.IUtilService;
import javafx.application.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
@ActiveProfiles("test") // Configuracion de base de datos h2
public class PhraseControllerTest {

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    //@MockBean
    //private IPhraseService phraseService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PhraseController phraseController;

    private Phrase phrase1;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = standaloneSetup(this.phraseController).build();// Standalone context
        phrase1 = new Phrase("phrase1", new Author(1L, "Zig Ziglar", new Date()), new Type(1L, "Inspiradora", new Date()), new Image("Imagen99", new Date()), 0L, new Date());
    }

    @Test
    public void show_PhraseWithProperId() {
        ResponseEntity<Phrase> response = restTemplate.getForEntity("/phrases/1", Phrase.class);
        Phrase phrase = response.getBody();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("1", phrase.getId().toString());
        Assert.assertEquals("Si puedes soñarlo, puedes lograrlo", phrase.getBody());
    }

    // Creación de frase utilizando una base de datos en memoria(h2)
    @Test
    public void create_PhraseWithInMemoryDB() {
        ResponseEntity<String> response = restTemplate.postForEntity("/phrases", phrase1, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertEquals(true, response.getBody().contains("phrase1"));
    }
/*
    @Test
    public void update_PhraseWithInMemoryDB() {
        ResponseEntity<Phrase> response = restTemplate.getForEntity("/phrases/1", Phrase.class);
        Phrase phrase = response.getBody();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("1", phrase.getId().toString());
        Assert.assertEquals("Si puedes soñarlo, puedes lograrlo", phrase.getBody());
    }
*/
    /*
    // Creación de frase simulando la base de datos con un mock, solo se simula la interacción con la BD, el resto del controlador se mantiene
    @Test
    public void create_PhraseWithDBMock() {
        when(phraseService.save(phrase1)).thenReturn(phrase1);

        ResponseEntity<Phrase> response = restTemplate.postForEntity("/phrases/", phrase1, Phrase.class);
        Phrase phrase = response.getBody();
        // ResponseEntity<Map<String, Object>> phraseResponse = response.getBody();
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        //Assert.assertEquals("phrase1", phrase.getBody());
    }
*/

/*
    @Test
    public void getPhraseMvc() throws Exception {
        MvcResult result = mockMvc.perform(get("/phrases/{id}", 1).contentType(MediaType.APPLICATION_JSON))
              //  .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.body", is("Si puedes soñarlo, puedes lograrlo")))
                .andReturn();
        String content = result.getResponse().getContentAsString();
    }
    */

}
