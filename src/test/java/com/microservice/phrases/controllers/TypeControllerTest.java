package com.microservice.phrases.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.phrases.Type;
import com.microservice.phrases.models.services.ITypeService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TypeControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ITypeService typeService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private TypeController typeController;

    private List<Type> dummyTypes;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyTypeMessages = new ArrayList<>();
    private Type invalidType = new Type();

    private Type type1;
    private Type type2;
    private Type type3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(typeController)
                .build();

        createDummyTypes();
        setInvalidType();
        setInvalidTypeParamsMessages();
        setEmptyTypeMessages();
    }

    private void createDummyTypes(){
        type1 = new Type("TYPE1", new Date());
        type2 = new Type("TYPE2", new Date());
        type3 = new Type("TYPE3", new Date());

        dummyTypes = Arrays.asList(type1, type2, type3);
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

    private void setEmptyTypeMessages() {
        emptyTypeMessages.add("The name field can't be empty");
    }

    @Test
    public void index() throws Exception {
        when(typeService.findAll()).thenReturn(dummyTypes);

        mockMvc.perform(get("/api/types")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("TYPE1")));

        verify(typeService, times(1)).findAll();
        verifyNoMoreInteractions(typeService);
    }

    /* BEGIN SHOW typeController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(typeService.findById(1L)).thenReturn(type1);

        mockMvc.perform(get("/api/types/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("TYPE1")));

        verify(typeService, times(1)).findById(1L);
        verifyNoMoreInteractions(typeService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/types/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(typeService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/api/types/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(typeService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(typeService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(typeService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/types/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(typeService, times(1)).findById(1L);
        verifyNoMoreInteractions(typeService);
    }

    /* END SHOW typeController method tests */

    /* BEGIN CREATE typeController method tests */

    @Test
    public void create_withProperType() throws Exception {
        when(typeService.save(any(Type.class))).thenReturn(type1);
        
        mockMvc.perform(post("/api/types")
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.type.name", is("TYPE1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));

        verify(typeService, times(1)).save(any(Type.class));
        verifyNoMoreInteractions(typeService);
    }

    @Test
    public void create_whenTypeIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyTypeMessages);

        mockMvc.perform(post("/api/types")
                .content(objectMapper.writeValueAsString(new Type()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")));
    }

    @Test
    public void create_whenTypeHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/api/types")
                .content(objectMapper.writeValueAsString(invalidType))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 20 characters")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(typeService.save(any(Type.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/types")
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(typeService, times(1)).save(any(Type.class));
        verifyNoMoreInteractions(typeService);
    }

    /* END CREATE typeController method tests */

    /* BEGIN UPDATE typeController method tests */

    @Test
    public void update_withProperTypeAndId() throws Exception {
        when(typeService.findById(anyLong())).thenReturn(type1);
        when(typeService.save(any(Type.class))).thenReturn(type1);
        
        mockMvc.perform(put("/api/types/{id}", 1)
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.type.name", is("TYPE1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));

        verify(typeService, times(1)).findById(anyLong());
        verify(typeService, times(1)).save(any(Type.class));
        verifyNoMoreInteractions(typeService);
    }

    @Test
    public void update_whenTypeIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/types/{id}", "randomString")
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenTypeIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyTypeMessages);

        mockMvc.perform(put("/api/types/{id}", 1)
                .content(objectMapper.writeValueAsString(new Type()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")));
    }

    @Test
    public void update_whenTypeIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/api/types/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidType))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 20 characters")));
    }

    @Test
    public void update_whenTypeIsNotFound() throws Exception {
        when(typeService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/types/{id}", anyLong())
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(typeService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(typeService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(typeService.save(any(Type.class))).thenThrow(new DataAccessException("..."){});
        when(typeService.findById(anyLong())).thenReturn(type1);

        mockMvc.perform(put("/api/types/{id}", 1)
                .content(objectMapper.writeValueAsString(type1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(typeService, times(1)).save(any(Type.class));
        verify(typeService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(typeService);
    }

    /* END UPDATE typeController method tests */

    /* BEGIN DELETE typeController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(typeService).delete(anyLong());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/types/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));

        verify(typeService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(typeService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/types/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenTypeIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(typeService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/types/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(typeService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(typeService);
    }

    /* END DELETE typeController method tests */
}
