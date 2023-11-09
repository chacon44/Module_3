package repositoryTests;

import com.epam.esm.model.GiftCertificate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import testConfig.TestConfig;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
class GiftCertificateRepositoryTest {


    private static final Long CERTIFICATE_ID = 1L;
    private static final String CERTIFICATE_NAME = "certificate";
    private static final String CERTIFICATE_DESCRIPTION = "description";
    private static final Double CERTIFICATE_PRICE = 49.95;
    private static final Long CERTIFICATE_DURATION = 50L;
    private static final String CERTIFICATE_CREATE_DATE = LocalDateTime.now().toString();
    private static final String CERTIFICATE_LAST_UPDATE_DATE = LocalDateTime.now().toString();


    @Mock
    JdbcTemplate jdbcTemplate;

    @Test
    public void testCreateCertificate() {
        // Arrange
        GiftCertificate certificate = new GiftCertificate();

        certificate.setCertificateId(CERTIFICATE_ID);
        certificate.setCertificateName(CERTIFICATE_NAME);
        certificate.setCertificateDescription(CERTIFICATE_DESCRIPTION);
        certificate.setCertificatePrice(CERTIFICATE_PRICE);
        certificate.setCertificateDuration(CERTIFICATE_DURATION);
        certificate.setCertificateCreateDate(CERTIFICATE_CREATE_DATE);
        certificate.setCertificateLastUpdateDate(CERTIFICATE_LAST_UPDATE_DATE);

        KeyHolder keyHolder = mock(KeyHolder.class);
        when(jdbcTemplate.update(Mockito.any(), Mockito.any(KeyHolder.class))).thenAnswer((Answer<?>) keyHolder);

        // Assert
        Mockito.verify(jdbcTemplate, Mockito.times(1)).update(Mockito.any(), Mockito.any(KeyHolder.class));
    }

}
