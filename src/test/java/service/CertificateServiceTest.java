package service;

import com.epam.esm.Dto.Errors.ErrorDTO;
import com.epam.esm.exceptions.Codes;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.service.GiftCertificateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epam.esm.exceptions.Codes.CERTIFICATE_FOUND;
import static com.epam.esm.exceptions.Messages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;


@ExtendWith(MockitoExtension.class)
public class CertificateServiceTest {

    @InjectMocks
    private GiftCertificateService giftCertificateService;

    @Mock
    GiftCertificateTagRepository giftCertificateTagRepository;

    @Mock
    GiftCertificate giftCertificate;

    @Mock
    Tag tag;

    private
    List<Long> tagIdsList = List.of(1L, 3L);

    @Test
    public void saveGiftCertificate_correctlySaved() {

        GiftCertificate giftCertificateToSave = new GiftCertificate("name", "description", 10.1, 20L);
        Mockito.when(giftCertificateTagRepository.saveGiftCertificate(giftCertificateToSave, tagIdsList)).thenReturn(giftCertificate);
        Mockito.when(giftCertificateTagRepository.getGiftCertificateByName(giftCertificateToSave.getName()))
                .thenReturn(null, giftCertificateToSave); // Return null at first call, then return the giftCertificateToSave


        ResponseEntity<?> actual = giftCertificateService.saveGiftCertificate(giftCertificateToSave, tagIdsList);
        Mockito.verify(giftCertificateTagRepository).saveGiftCertificate(Mockito.eq(giftCertificateToSave),Mockito.eq(tagIdsList));
        Mockito.verify(giftCertificateTagRepository, Mockito.times(2))
                .getGiftCertificateByName(Mockito.eq(giftCertificateToSave.getName()));

        assertEquals(CREATED, actual.getStatusCode());
        assertEquals(giftCertificateToSave, actual.getBody());
    }

    @Test
    public void saveGiftCertificate_cannotBeSaved() {

        GiftCertificate giftCertificateToSave = new GiftCertificate("name", "description", 10.1, 20L);
        Mockito.when(giftCertificateTagRepository.saveGiftCertificate(giftCertificateToSave, tagIdsList)).thenReturn(null);
        Mockito.when(giftCertificateTagRepository.getGiftCertificateByName(giftCertificateToSave.getName()))
                .thenReturn(null); // Return null at first call, then return the giftCertificateToSave


        ResponseEntity<?> actual = giftCertificateService.saveGiftCertificate(giftCertificateToSave, tagIdsList);
        Mockito.verify(giftCertificateTagRepository).saveGiftCertificate(Mockito.eq(giftCertificateToSave),Mockito.eq(tagIdsList));
        Mockito.verify(giftCertificateTagRepository, Mockito.times(1))
                .getGiftCertificateByName(Mockito.eq(giftCertificateToSave.getName()));

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(new ErrorDTO(CERTIFICATE_COULD_NOT_BE_SAVED, 500), actual.getBody());
    }

    @Test
    public void saveGiftCertificate_alreadyExisting() {

        GiftCertificate giftCertificateToSave = new GiftCertificate("name", "description", 10.1, 20L);
        Long idFound = giftCertificate.getId();
        ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_ALREADY_EXISTS.formatted(idFound), CERTIFICATE_FOUND);

        Mockito.when(giftCertificateTagRepository.saveGiftCertificate(giftCertificateToSave, tagIdsList)).thenReturn(giftCertificate);
        Mockito.when(giftCertificateTagRepository.getGiftCertificateByName(giftCertificateToSave.getName()))
                .thenReturn(giftCertificate); // Return null at first call, then return the giftCertificateToSave


        ResponseEntity<?> actual = giftCertificateService.saveGiftCertificate(giftCertificateToSave, tagIdsList);
        Mockito.verify(giftCertificateTagRepository).saveGiftCertificate(Mockito.eq(giftCertificateToSave),Mockito.eq(tagIdsList));
        Mockito.verify(giftCertificateTagRepository, Mockito.times(1))
                .getGiftCertificateByName(Mockito.eq(giftCertificateToSave.getName()));

        assertEquals(HttpStatus.FOUND, actual.getStatusCode());
        assertEquals(errorResponse, actual.getBody());
    }

    @Test
    public void getGiftCertificate_certificateExist() {

        Mockito.when(giftCertificateTagRepository.getGiftCertificateById(giftCertificate.getId())).thenReturn(giftCertificate);
        ResponseEntity<?> actual = giftCertificateService.getGiftCertificateById(giftCertificate.getId());
        Mockito.verify(giftCertificateTagRepository).getGiftCertificateById(Mockito.eq(giftCertificate.getId()));

        assertEquals(HttpStatus.FOUND, actual.getStatusCode());
        assertEquals(giftCertificate, actual.getBody());
    }

    @Test
    public void getGiftCertificate_certificateNotExist() {

        String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificate.getId());

        ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        Mockito.when(giftCertificateTagRepository.getGiftCertificateById(giftCertificate.getId())).thenReturn(null);
        ResponseEntity<?> actual = giftCertificateService.getGiftCertificateById(giftCertificate.getId());
        Mockito.verify(giftCertificateTagRepository).getGiftCertificateById(Mockito.eq(giftCertificate.getId()));

        assertEquals(expected.getStatusCode(), actual.getStatusCode());
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void deleteGiftCertificate_certificateExists() {
        Long giftCertificateId = giftCertificate.getId();
        Mockito.when(giftCertificateTagRepository.deleteGiftCertificate(giftCertificateId)).thenReturn(true);
        ResponseEntity<?> actual = giftCertificateService.deleteGiftCertificate(giftCertificateId);
        Mockito.verify(giftCertificateTagRepository).deleteGiftCertificate(Mockito.eq(giftCertificateId));
        assertEquals(HttpStatus.FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteGiftCertificate_certificateNotExist() {
        Long giftCertificateId = 10000L;
        String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId);
        ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(HttpStatus.NO_CONTENT).body(errorResponse);
        Mockito.when(giftCertificateTagRepository.deleteGiftCertificate(giftCertificateId)).thenReturn(false);
        ResponseEntity<?> actual = giftCertificateService.deleteGiftCertificate(giftCertificateId);
        Mockito.verify(giftCertificateTagRepository).deleteGiftCertificate(Mockito.eq(giftCertificateId));
        assertEquals(expected.getStatusCode(), actual.getStatusCode());
        assertEquals(expected.getBody(), actual.getBody());
    }
    @Test
    public void updateGiftCertificate_parametersValid_certificateUpdated() {
        // Arrange
        Long id = giftCertificate.getId();

        Mockito.when(giftCertificateTagRepository.getGiftCertificateByName(giftCertificate.getName())).thenReturn(giftCertificate);
        Mockito.when(giftCertificateTagRepository.getGiftCertificateById(id)).thenReturn(null);
        // Act
        ResponseEntity<?> actual = giftCertificateService.updateGiftCertificate(id, giftCertificate, tagIdsList);

        // Assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    public void updateGiftCertificate_parametersNotValid() {
        // Arrange
        Long id = giftCertificate.getId();
        giftCertificate.setName(null);

        // Act
        ResponseEntity<?> actual = giftCertificateService.updateGiftCertificate(id, giftCertificate, tagIdsList);

        // Assert
        assertTrue(actual.getBody() instanceof ErrorDTO);
    }

    @Test
    public void updateGiftCertificate_certificateAlreadyExists() {
        // Arrange
        Long id = giftCertificate.getId();
        List<Long> tagIds = new ArrayList<>();

        // Act
        ResponseEntity<?> actual = giftCertificateService.updateGiftCertificate(id, giftCertificate, tagIds);

        // Assert
        assertEquals(HttpStatus.FOUND, actual.getStatusCode());
    }

    @Test
    public void updateGiftCertificate_idNotAssociatedToAnyCertificate() {
        // Arrange
        Long id = 999L;
        GiftCertificate giftCertificateToUpdate = new GiftCertificate("name", "description", 10.1, 20L);

        Mockito.when(giftCertificateTagRepository.updateGiftCertificate(id,giftCertificateToUpdate,tagIdsList)).thenReturn(null);
        Mockito.when(giftCertificateTagRepository.getGiftCertificateById(id)).thenReturn(null);
        // Act
        ResponseEntity<?> actual = giftCertificateService.updateGiftCertificate(id, giftCertificateToUpdate, tagIdsList);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateGiftCertificate_someTagsDoNotExist() {
        // Arrange
        Long id = giftCertificate.getId();
        List<Long> tagIdsList = Arrays.asList(999L, 1000L);

        // Act
        ResponseEntity<?> actual = giftCertificateService.updateGiftCertificate(id, giftCertificate, tagIdsList);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

}
