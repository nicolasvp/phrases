package com.microservice.phrases.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.phrases.models.entity.Phrase;
import com.microservice.phrases.models.services.IPhraseService;
import com.microservice.phrases.models.services.IUtilService;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        phrase1 = new Phrase();
        phrase2 = new Phrase();
        phrase3 = new Phrase();

        dummyPhrases = Arrays.asList(phrase1, phrase2, phrase3);
    }

    /**
     * Phrase attributes with random and invalid number of characters
     * name = 101 characters
     */
    private void setInvalidPhrase() {

    }

    private void setInvalidPhraseParamsMessages() {
        invalidParamsMessages.add("El campo name debe tener entre 1 y 100 caracteres");
    }

    private void setEmptyPhraseMessages() {
        emptyPhraseMessages.add("El campo name no puede estar vacío");
    }
}
