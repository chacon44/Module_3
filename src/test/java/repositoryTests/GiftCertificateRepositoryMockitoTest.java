package repositoryTests;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.mapper.TagRowMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.repository.GiftCertificateTagRepositoryImpl;
import config.TestRepositoryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class})
public class GiftCertificateRepositoryMockitoTest {

    private GiftCertificateTagRepository giftCertificateTagRepository;

    @Mock
    TagRowMapper tagRowMapper;

    @Mock
    GiftCertificateRowMapper giftCertificateRowMapper;

    @Mock
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        giftCertificateTagRepositoryImpl = new GiftCertificateTagRepositoryImpl(jdbcTemplate, tagRowMapper, giftCertificateRowMapper);
    }

    private final GiftCertificate giftCertificate =
            new GiftCertificate("name4", "description4", 3.40, 4L);

    private final GiftCertificateRequestDTO giftCertificateRequestDTO =
            new GiftCertificateRequestDTO("name41", "description41", 31.40, 41L, List.of());


    private GiftCertificateTagRepositoryImpl giftCertificateTagRepositoryImpl;

    @Test
    public void saveGiftCertificate_saveAnyCertificate_verifyIfValidIdIsReturned() {

//        when(jdbcTemplate.queryForObject(
//                anyString(),
//                Mockito.eq(Long.class),
//                anyString(),
//                anyString(),
//                Mockito.anyDouble(),
//                Mockito.anyLong())).thenReturn(1L);
//        // When
//        Optional<Long> actual = giftCertificateTagRepositoryImpl.saveGiftCertificate(giftCertificateRequestDTO);
//
//        assertEquals(1L, actual.get());
//
//        //Check if method has been called once and with correct parameters type and orders
//        Mockito.verify(jdbcTemplate, Mockito.times(1))
//                .queryForObject(anyString(),
//                        Mockito.eq(Long.class),
//                        Mockito.eq(giftCertificate.getName()),
//                        Mockito.eq(giftCertificate.getDescription()),
//                        Mockito.eq(giftCertificate.getPrice()),
//                        Mockito.eq(giftCertificate.getDuration())
//                );

    }

    @Test
    public void deleteJoinTableByCertificateId() {
        long giftCertificateId = 1L;
        int expectedUpdateCount = 1;

        when(jdbcTemplate.update(anyString(), Mockito.eq(giftCertificateId))).thenReturn(expectedUpdateCount);

        boolean result = giftCertificateTagRepositoryImpl.deleteCertificateFromJoinTable(giftCertificateId);

        assertTrue(result);
        Mockito.verify(jdbcTemplate, Mockito.times(1)).update(anyString(), Mockito.eq(giftCertificateId));
    }

    @Test
    public void deleteJoinTableByTagId() {

        long tagId = 1L;
        int expectedUpdateCount = 1;

        when(jdbcTemplate.update(anyString(), Mockito.eq(tagId))).thenReturn(expectedUpdateCount);
        when(giftCertificateTagRepositoryImpl.deleteCertificateFromJoinTable(tagId));

        Mockito.verify(jdbcTemplate, Mockito.times(1)).update(anyString(), Mockito.eq(tagId));

    }

    @Test
    public void deleteTagById() {

        long tagId = 1L;
        int expectedUpdateCount = 1;

        when(jdbcTemplate.update(anyString(), Mockito.eq(tagId))).thenReturn(expectedUpdateCount);

        boolean result = giftCertificateTagRepositoryImpl.deleteTag(tagId);

        assertTrue(result);
        Mockito.verify(jdbcTemplate, Mockito.times(1)).update(anyString(), Mockito.eq(tagId));

    }

}
