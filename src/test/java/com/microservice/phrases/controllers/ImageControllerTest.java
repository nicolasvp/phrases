package com.microservice.phrases.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.phrases.models.entity.Image;
import com.microservice.phrases.models.entity.Type;
import com.microservice.phrases.models.services.IImageService;
import com.microservice.phrases.models.services.IUtilService;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImageControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IImageService imageService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private ImageController imageController;

    private List<Image> dummyImages;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyImageMessages = new ArrayList<>();
    private Image invalidImage = new Image();

    private Image image1;
    private Image image2;
    private Image image3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(imageController)
                .build();

        createDummyImages();
        setInvalidImage();
        setInvalidImageParamsMessages();
        setEmptyImageMessages();
    }

    private void createDummyImages(){
        image1 = new Image("IMAGE1", new Date());
        image2 = new Image("IMAGE2", new Date());
        image3 = new Image("IMAGE3", new Date());

        dummyImages = Arrays.asList(image1, image2, image3);
    }

    /**
     * Image attributes with random and invalid number of characters
     * name = 21 characters
     */
    private void setInvalidImage() {
        invalidImage.setName("fwmflkftupdcqnkdsytuc");
    }

    private void setInvalidImageParamsMessages() {
        invalidParamsMessages.add("El campo name debe tener entre 1 y 20 caracteres");
    }

    private void setEmptyImageMessages() {
        emptyImageMessages.add("El campo name no puede estar vacío");
    }

    @Test
    public void index() throws Exception {
        when(imageService.findAll()).thenReturn(dummyImages);

        mockMvc.perform(get("/api/images")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("IMAGE1")));

        verify(imageService, times(1)).findAll();
        verifyNoMoreInteractions(imageService);
    }

    /* BEGIN SHOW imageController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(imageService.findById(1L)).thenReturn(image1);

        mockMvc.perform(get("/api/images/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("IMAGE1")));

        verify(imageService, times(1)).findById(1L);
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/images/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(imageService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/api/images/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(imageService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(imageService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/images/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(imageService, times(1)).findById(1L);
        verifyNoMoreInteractions(imageService);
    }

    /* END SHOW imageController method tests */

    /* BEGIN CREATE imageController method tests */

    @Test
    public void create_withProperImage() throws Exception {
        when(imageService.save(any(Image.class))).thenReturn(image1);

        mockMvc.perform(post("/api/images")
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.image").exists())
                .andExpect(jsonPath("$.image.name", is("IMAGE1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro creado con éxito")));

        verify(imageService, times(1)).save(any(Image.class));
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void create_whenImageIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyImageMessages);

        mockMvc.perform(post("/api/images")
                .content(objectMapper.writeValueAsString(new Image()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name no puede estar vacío")));
    }

    @Test
    public void create_whenImageHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/api/images")
                .content(objectMapper.writeValueAsString(invalidImage))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name debe tener entre 1 y 20 caracteres")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(imageService.save(any(Image.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/images")
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(imageService, times(1)).save(any(Image.class));
        verifyNoMoreInteractions(imageService);
    }

    /* END CREATE imageController method tests */

    /* BEGIN UPDATE imageController method tests */

    @Test
    public void update_withProperImageAndId() throws Exception {
        when(imageService.findById(anyLong())).thenReturn(image1);
        when(imageService.save(any(Image.class))).thenReturn(image1);

        mockMvc.perform(put("/api/images/{id}", 1)
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.image").exists())
                .andExpect(jsonPath("$.image.name", is("IMAGE1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro actualizado con éxito")));

        verify(imageService, times(1)).findById(anyLong());
        verify(imageService, times(1)).save(any(Image.class));
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void update_whenImageIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/images/{id}", "randomString")
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenImageIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyImageMessages);

        mockMvc.perform(put("/api/images/{id}", 1)
                .content(objectMapper.writeValueAsString(new Type()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name no puede estar vacío")));
    }

    @Test
    public void update_whenImageIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/api/images/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidImage))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name debe tener entre 1 y 20 caracteres")));
    }

    @Test
    public void update_whenImageIsNotFound() throws Exception {
        when(imageService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/images/{id}", anyLong())
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(imageService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(imageService.save(any(Image.class))).thenThrow(new DataAccessException("..."){});
        when(imageService.findById(anyLong())).thenReturn(image1);

        mockMvc.perform(put("/api/images/{id}", 1)
                .content(objectMapper.writeValueAsString(image1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(imageService, times(1)).save(any(Image.class));
        verify(imageService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(imageService);
    }

    /* END UPDATE imageController method tests */

    /* BEGIN DELETE imageController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(imageService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro eliminado con éxito")));

        verify(imageService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenImageIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(imageService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(imageService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(imageService);
    }

    /* END DELETE imageController method tests */
}
