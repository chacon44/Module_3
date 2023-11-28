package controller;

import com.epam.esm.controller.CertificatesController;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CertificatesControllerTest {

    @Mock
    private GiftCertificateService giftCertificateService;

    @InjectMocks
    private CertificatesController certificatesController;

    private MockMvc mockMvc;

    private final Date date = new Date();

    private List<Long> tagIdsList = new ArrayList<>();

    private Tag
            tag1 = new Tag(),
            tag2 = new Tag(),
            tag3 = new Tag(),
            tag4 = new Tag(),
            tag5 = new Tag(),
            tag6 = new Tag();
    private GiftCertificate
            giftCertificateResponse1 = new GiftCertificate(),
            giftCertificate2 = new GiftCertificate(),
            giftCertificate3 = new GiftCertificate();
    GiftCertificate giftCertificate = null;
    List<Tag> tagList = asList(tag1,tag2,tag3,tag4,tag5,tag6);

    public void createData(){

        giftCertificate = new GiftCertificate(
                "certificate for test", "description for test", 10.50, 10L);
        tag1 = new Tag(1L, "tag 3");
        tag2 = new Tag(2L, "tag 1");
        tag3 = new Tag(3L, "tag 2");
        tag4 = new Tag(4L, "blue");
        tag5 = new Tag(5L, "colour");
        tag6 = new Tag(6L, "animal 1");

        tagIdsList = new ArrayList<>(asList(1L, 3L, 4L, 5L));
        tagList = asList(tag1, tag3, tag4, tag5);
        giftCertificateResponse1 = giftCertificate;
        giftCertificateResponse1.setId(1L);
        giftCertificateResponse1.setCreateDate("2023-11-21T16:48:04:309Z");
        giftCertificateResponse1.setLastUpdateDate("2023-12-25T16:48:04:309Z");
        giftCertificateResponse1.setTags(tagList);
    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.certificatesController).build();
        createData();
    }

    @Test
    public void testPostGiftCertificate_giftCertificateNotFound_CreatedReturned()  throws Exception {

        when(giftCertificateService.saveGiftCertificate(eq(giftCertificate), eq(tagIdsList))).thenReturn(new ResponseEntity(giftCertificateResponse1, HttpStatus.CREATED));

        mockMvc.perform(post("/certificate").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(giftCertificate)))
        //Assert
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(giftCertificateResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(giftCertificateResponse1.getName())))
                .andExpect(jsonPath("$.description", is(giftCertificateResponse1.getDescription())))
                .andExpect(jsonPath("$.price", is(giftCertificateResponse1.getPrice())))
                .andExpect(jsonPath("$.duration", is(giftCertificateResponse1.getDuration().intValue())))
                .andExpect(jsonPath("$.createDate", is(giftCertificateResponse1.getCreateDate())))
                .andExpect(jsonPath("$.lastUpdateDate", is(giftCertificateResponse1.getLastUpdateDate())))
                .andExpect(jsonPath("$.tags", is(new ObjectMapper().readValue(asJsonString(giftCertificateResponse1.getTags()), List.class))))
                .andExpect(status().isCreated());
    }

}
