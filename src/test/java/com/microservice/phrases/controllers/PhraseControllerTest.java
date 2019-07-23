package com.microservice.phrases.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.phrases.models.entity.Author;
import com.microservice.phrases.models.entity.Image;
import com.microservice.phrases.models.entity.Phrase;
import com.microservice.phrases.models.entity.Type;
import com.microservice.phrases.models.services.IPhraseService;
import com.microservice.phrases.models.services.IUtilService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PhraseControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IPhraseService phraseService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private PhraseController phraseController;

    private List<Phrase> dummyPhrases;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyPhraseMessages = new ArrayList<>();
    private Phrase invalidPhrase = new Phrase();

    private Phrase phrase1;
    private Phrase phrase2;
    private Phrase phrase3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(phraseController)
                .build();

        createDummyPhrases();
        setInvalidPhrase();
        setInvalidPhraseParamsMessages();
        setEmptyPhraseMessages();
    }

    private void createDummyPhrases(){
        phrase1 = new Phrase("phrase1", new Author(), new Type(), new Image(), 0L, new Date());
        phrase2 = new Phrase("phrase2", new Author(), new Type(), new Image(), 0L, new Date());
        phrase3 = new Phrase("phrase3", new Author(), new Type(), new Image(), 0L, new Date());

        dummyPhrases = Arrays.asList(phrase1, phrase2, phrase3);
    }

    /**
     * Phrase attributes with random and invalid number of characters
     * body = 201 characters
     */
    private void setInvalidPhrase() {
        invalidPhrase.setBody("iaUiupA7Q2bhhfRvBp3jA9zOe2l7fyAzyuwEZqr3NpYng9Z9Ggx4cbqMV3keCcV2qFnzw6bjCHQzzqFx72bPeFS36ZxInNzR2nztROnQEu4FCgGWXUx1QgWjPZn3bt0EcgXSpEtMqrChtC3gfpRPgqpRtoG6xQLlQK1K9Cmjyp2PqWJrlfQdCcwjxvgKgV4cXCYuRFfLv");
    }

    private void setInvalidPhraseParamsMessages() {
        invalidParamsMessages.add("El campo body debe tener entre 1 y 200 caracteres");
    }

    private void setEmptyPhraseMessages() {
        emptyPhraseMessages.add("El campo body no puede estar vacío");
        emptyPhraseMessages.add("El campo author no puede estar vacío");
        emptyPhraseMessages.add("El campo type no puede estar vacío");
        emptyPhraseMessages.add("El campo image no puede estar vacío");
    }

    @Test
    public void index() throws Exception {
        when(phraseService.findAll()).thenReturn(dummyPhrases);

        mockMvc.perform(get("/api/phrases")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].body", is("phrase1")));

        verify(phraseService, times(1)).findAll();
        verifyNoMoreInteractions(phraseService);
    }

    /* BEGIN SHOW phraseController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(phraseService.findById(1L)).thenReturn(phrase1);

        mockMvc.perform(get("/api/phrases/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.body", is("phrase1")));

        verify(phraseService, times(1)).findById(1L);
        verifyNoMoreInteractions(phraseService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/phrases/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(phraseService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/api/phrases/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(phraseService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(phraseService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(phraseService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/phrases/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(phraseService, times(1)).findById(1L);
        verifyNoMoreInteractions(phraseService);
    }

    /* END SHOW phraseController method tests */

    /* BEGIN CREATE phraseController method tests */

    @Test
    public void create_withProperPhrase() throws Exception {
        when(phraseService.save(any(Phrase.class))).thenReturn(phrase1);

        mockMvc.perform(post("/api/phrases")
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                //  .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phrase").exists())
                .andExpect(jsonPath("$.phrase.body", is("phrase1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro creado con éxito")));

        verify(phraseService, times(1)).save(any(Phrase.class));
        verifyNoMoreInteractions(phraseService);
    }

    @Test
    public void create_whenPhraseIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyPhraseMessages);

        mockMvc.perform(post("/api/phrases")
                .content(objectMapper.writeValueAsString(new Phrase()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors", hasItem("El campo body no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo author no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo type no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo image no puede estar vacío")));
    }

    @Test
    public void create_whenPhraseHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/api/phrases")
                .content(objectMapper.writeValueAsString(invalidPhrase))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("El campo body debe tener entre 1 y 200 caracteres")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(phraseService.save(any(Phrase.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/phrases")
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(phraseService, times(1)).save(any(Phrase.class));
        verifyNoMoreInteractions(phraseService);
    }

    /* END CREATE phraseController method tests */

    /* BEGIN UPDATE phraseController method tests */

    @Test
    public void update_withProperPhraseAndId() throws Exception {
        when(phraseService.findById(anyLong())).thenReturn(phrase1);
        when(phraseService.save(any(Phrase.class))).thenReturn(phrase1);

        mockMvc.perform(put("/api/phrases/{id}", 1)
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phrase").exists())
                .andExpect(jsonPath("$.phrase.body", is("phrase1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro actualizado con éxito")));

        verify(phraseService, times(1)).findById(anyLong());
        verify(phraseService, times(1)).save(any(Phrase.class));
        verifyNoMoreInteractions(phraseService);
    }

    @Test
    public void update_whenPhraseIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/phrases/{id}", "randomString")
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenPhraseIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyPhraseMessages);

        mockMvc.perform(put("/api/phrases/{id}", 1)
                .content(objectMapper.writeValueAsString(new Type()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors", hasItem("El campo body no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo author no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo type no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo image no puede estar vacío")));
    }

    @Test
    public void update_whenPhraseIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/api/phrases/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidPhrase))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("El campo body debe tener entre 1 y 200 caracteres")));
    }

    @Test
    public void update_whenPhraseIsNotFound() throws Exception {
        when(phraseService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/phrases/{id}", anyLong())
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(phraseService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(phraseService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(phraseService.save(any(Phrase.class))).thenThrow(new DataAccessException("..."){});
        when(phraseService.findById(anyLong())).thenReturn(phrase1);

        mockMvc.perform(put("/api/phrases/{id}", 1)
                .content(objectMapper.writeValueAsString(phrase1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(phraseService, times(1)).save(any(Phrase.class));
        verify(phraseService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(phraseService);
    }

    /* END UPDATE phraseController method tests */

    /* BEGIN DELETE phraseController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(phraseService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/phrases/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro eliminado con éxito")));

        verify(phraseService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(phraseService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/phrases/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenPhraseIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(phraseService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/phrases/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(phraseService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(phraseService);
    }

    /* END DELETE phraseController method tests */
}
