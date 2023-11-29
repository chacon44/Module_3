package service;

//import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.service.GiftCertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;


@ExtendWith(MockitoExtension.class)
public class CertificateServiceTest {

    @Mock
    GiftCertificateTagRepository giftCertificateTagRepository;


    private GiftCertificate
            response = new GiftCertificate(),
            request = new GiftCertificate();

    List<Long> tagIdsList = null;

    private GiftCertificateService giftCertificateService;

    public void createData() {

        Tag
                tag1 = new Tag(1L, "tag 3"),
                tag2 = new Tag(2L, "tag 1"),
                tag3 = new Tag(3L, "tag 2"),
                tag4 = new Tag(4L, "blue"),
                tag5 = new Tag(5L, "colour"),
                tag6 = new Tag(6L, "animal 1");

        List<Long> tagIdsList = List.of(1L, 3L, 4L, 5L);

        request = new GiftCertificate(
                "certificate for test", "description for test", 10.50, 10L);

        response.setId(1L);
        response.setName("certificate");
        response.setDescription("description 1");
        response.setPrice(310.00);
        response.setDuration(20L);
        response.setCreateDate("2023-11-21T16:48:04:309Z");
        response.setLastUpdateDate("2023-12-25T16:48:04:309Z");
        response.setTags(asList(tag1, tag5));
    }

    @BeforeEach
    void setUp(){
        giftCertificateService = new GiftCertificateService(giftCertificateTagRepository);
        createData();

    }

    @Test
    public void saveAndGetSaved(){

        List<Long> tagIdsList = List.of(1L, 3L, 4L, 5L);
        //ACT
        giftCertificateService.saveGiftCertificate(request, tagIdsList);
        giftCertificateService.getGiftCertificateById(1L);

        Mockito.verify(giftCertificateTagRepository, Mockito.times(1))
                .saveGiftCertificate(
                        Mockito.eq(request),
                        Mockito.eq(tagIdsList));

        Mockito.verify(giftCertificateTagRepository, Mockito.times(1))
                .getGiftCertificateByName(
                        Mockito.eq(request.getName()));
    }

    @Test
    public void getNonSavedCertificate() {
        giftCertificateService.getGiftCertificateById(1000L);
        Mockito.verify(giftCertificateTagRepository).getGiftCertificateById(1000L);
    }
}
