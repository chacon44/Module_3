package repositoryTests;

import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.mapper.TagRowMapper;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.repository.GiftCertificateTagRepositoryImpl;
import config.TestRepositoryConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "h2")
@ContextConfiguration(classes = {TestRepositoryConfig.class, GiftCertificateTagRepositoryImpl.class, TagRowMapper.class, GiftCertificateRowMapper.class})
public class GiftCertificateRepositoryTest {

    @Autowired
    private GiftCertificateTagRepository giftCertificateTagRepository;

}
