package serviceTests;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.service.GiftCertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GiftCertificateServiceTest {

    @Mock
    GiftCertificateTagRepository giftCertificateTagRepository;

    GiftCertificateRequestDTO giftCertificateRequestDTO = new GiftCertificateRequestDTO("name1","description1", 10.50, 100L, List.of());

    private GiftCertificateService giftCertificateService;

    @BeforeEach
    public void setUp(){

        giftCertificateService = new GiftCertificateService(giftCertificateTagRepository);
    }

    @Test
    public void save_And_Get_Saved() {

        //ACT

        giftCertificateService.saveGiftCertificate(giftCertificateRequestDTO);

        GiftCertificate giftCertificate = new GiftCertificate(
                giftCertificateRequestDTO.name(),
                giftCertificateRequestDTO.description(),
                giftCertificateRequestDTO.price(),
                giftCertificateRequestDTO.duration());

        //ASSERT
        Mockito.verify(giftCertificateTagRepository)
                .saveGiftCertificate(Mockito.eq(giftCertificateRequestDTO));
    }

}
