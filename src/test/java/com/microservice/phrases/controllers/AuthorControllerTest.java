package com.microservice.phrases.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.phrases.Author;
import com.microservice.phrases.models.services.IAuthorService;
import com.microservices.commons.models.services.IUtilService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthorControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IAuthorService authorService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private AuthorController authorController;

    private List<Author> dummyAuthors;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyAuthorMessages = new ArrayList<>();
    private Author invalidAuthor = new Author();

    private Author author1;
    private Author author2;
    private Author author3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authorController)
                .build();

        createDummyAuthors();
        setInvalidAuthor();
        setInvalidAuthorParamsMessages();
        setEmptyAuthorMessages();
    }

    private void createDummyAuthors(){
        author1 = new Author("AUTHOR1", new Date());
        author2 = new Author("AUTHOR2", new Date());
        author3 = new Author("AUTHOR3", new Date());

        dummyAuthors = Arrays.asList(author1, author2, author3);
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

    private void setEmptyAuthorMessages() {
        emptyAuthorMessages.add("The name field can't be empty");
    }

    @Test
    public void index() throws Exception {
        when(authorService.findAll()).thenReturn(dummyAuthors);

        mockMvc.perform(get("/api/authors")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("AUTHOR1")));

        verify(authorService, times(1)).findAll();
        verifyNoMoreInteractions(authorService);
    }

    /* BEGIN SHOW authorController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(authorService.findById(1L)).thenReturn(author1);

        mockMvc.perform(get("/api/authors/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("AUTHOR1")));

        verify(authorService, times(1)).findById(1L);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/authors/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(authorService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/api/authors/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(authorService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(authorService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(authorService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/authors/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(authorService, times(1)).findById(1L);
        verifyNoMoreInteractions(authorService);
    }

    /* END SHOW authorController method tests */

    /* BEGIN CREATE authorController method tests */

    @Test
    public void create_withProperAuthor() throws Exception {
        when(authorService.save(any(Author.class))).thenReturn(author1);
        
        mockMvc.perform(post("/api/authors")
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.author").exists())
                .andExpect(jsonPath("$.author.name", is("AUTHOR1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));

        verify(authorService, times(1)).save(any(Author.class));
        verifyNoMoreInteractions(authorService);
    }

    @Test
    public void create_whenAuthorIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyAuthorMessages);

        mockMvc.perform(post("/api/authors")
                .content(objectMapper.writeValueAsString(new Author()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")));
    }

    @Test
    public void create_whenAuthorHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/api/authors")
                .content(objectMapper.writeValueAsString(invalidAuthor))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 100 characters")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(authorService.save(any(Author.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/authors")
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(authorService, times(1)).save(any(Author.class));
        verifyNoMoreInteractions(authorService);
    }

    /* END CREATE authorController method tests */

    /* BEGIN UPDATE authorController method tests */

    @Test
    public void update_withProperAuthorAndId() throws Exception {
        when(authorService.findById(anyLong())).thenReturn(author1);
        when(authorService.save(any(Author.class))).thenReturn(author1);
        
        mockMvc.perform(put("/api/authors/{id}", 1)
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.author").exists())
                .andExpect(jsonPath("$.author.name", is("AUTHOR1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));

        verify(authorService, times(1)).findById(anyLong());
        verify(authorService, times(1)).save(any(Author.class));
        verifyNoMoreInteractions(authorService);
    }

    @Test
    public void update_whenAuthorIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/authors/{id}", "randomString")
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenAuthorIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyAuthorMessages);

        mockMvc.perform(put("/api/authors/{id}", 1)
                .content(objectMapper.writeValueAsString(new Author()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")));
    }

    @Test
    public void update_whenAuthorIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/api/authors/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidAuthor))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 100 characters")));
    }

    @Test
    public void update_whenAuthorIsNotFound() throws Exception {
        when(authorService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/authors/{id}", anyLong())
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(authorService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(authorService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(authorService.save(any(Author.class))).thenThrow(new DataAccessException("..."){});
        when(authorService.findById(anyLong())).thenReturn(author1);

        mockMvc.perform(put("/api/authors/{id}", 1)
                .content(objectMapper.writeValueAsString(author1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(authorService, times(1)).save(any(Author.class));
        verify(authorService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(authorService);
    }

    /* END UPDATE authorController method tests */

    /* BEGIN DELETE authorController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(authorService).delete(anyLong());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));

        verify(authorService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(authorService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenAuthorIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(authorService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(authorService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(authorService);
    }

    /* END DELETE authorController method tests */
}
