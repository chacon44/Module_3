package service;

import com.epam.esm.Dto.Errors.ErrorDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.epam.esm.exceptions.Codes.TAG_BAD_REQUEST;
import static com.epam.esm.exceptions.Messages.TAG_COULD_NOT_BE_SAVED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    public static final String TAG_NAME = "name";
    public static final long TAG_ID = 1L;

    @InjectMocks
    private TagService tagService;

    @Mock
    GiftCertificateTagRepository giftCertificateTagRepository;

    @Mock
    Tag tag;

    @Test
    public void saveTag() {

        Mockito.when(giftCertificateTagRepository.saveTag(TAG_NAME)).thenReturn(tag);
        Mockito.when(giftCertificateTagRepository.getTagById(TAG_ID)).thenReturn(tag);
        Mockito.when(tag.getId()).thenReturn(TAG_ID);

        ResponseEntity<?> actual = tagService.saveTag(TAG_NAME);
        Mockito.verify(giftCertificateTagRepository).saveTag(
                Mockito.eq(TAG_NAME)
        );

        assertEquals(CREATED, actual.getStatusCode());
        assertEquals(tag, actual.getBody());
    }

    @Test
    public void saveTag_cannotSaveTag() {

        Mockito.when(giftCertificateTagRepository.saveTag(TAG_NAME)).thenReturn(null);
        //Mockito.when(giftCertificateTagRepository.getTagById(TAG_ID)).thenReturn(tag);
        //Mockito.when(tag.getId()).thenReturn(TAG_ID);

        ResponseEntity<?> actual = tagService.saveTag(TAG_NAME);
        ErrorDTO expected = new ErrorDTO(TAG_COULD_NOT_BE_SAVED, TAG_BAD_REQUEST);
        Mockito.verify(giftCertificateTagRepository).saveTag(
                Mockito.eq(TAG_NAME)
        );

        assertInstanceOf(ErrorDTO.class, actual.getBody());
        ErrorDTO actualBody = (ErrorDTO) actual.getBody();
        assertEquals(expected.errorMessage(),actualBody.errorMessage());
        assertEquals(expected.errorCode(),actualBody.errorCode());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
        //assertEquals(tag, actual.getBody());
    }
}
