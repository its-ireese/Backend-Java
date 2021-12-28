package com.projectx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectx.model.File;
import com.projectx.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
public class FileControllerTest {
    private File expected;
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private FileService fileService;
    @InjectMocks
    private FileController fileController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mvc = webAppContextSetup(context).build();
        expected = new File("aaa", "test", "test", ("test").getBytes(), null);
    }

    //converts Object into a Json String
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //need to test throwing an exception to get bad request and false!!!!!
    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile viable = new MockMultipartFile("file", expected.getName(), expected.getType(),
                expected.getData());
        mvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                .file(viable))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testGetListFiles() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testGetFile() throws Exception {
        when(fileService.getFile(expected.getId())).thenReturn(expected);
        mvc.perform(MockMvcRequestBuilders.get("/api/files/{id}", expected.getId())
                .param("id", expected.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(expected.getData())));
    }
}
