package repositoryTests;

import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.mapper.TagRowMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.repository.GiftCertificateTagRepositoryImpl;
import config.TestRepositoryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "h2")
@ContextConfiguration(classes = {TestRepositoryConfig.class, GiftCertificateTagRepositoryImpl.class, TagRowMapper.class, GiftCertificateRowMapper.class})
class GiftCertificateTagRepositoryImplTest {

    @Autowired
    private GiftCertificateTagRepository giftCertificateTagRepository;

    public GiftCertificate giftCertificate = new GiftCertificate(
            "certificate for test", "description for test", 10.50, 10L);

    public Date date = new Date();

    public List<Long> tagIdsList = new ArrayList<>(asList(1L, 3L, 4L, 5L));
    public Long nonExistingId = 1000L;
    public String nonExistingName = "xDfi4vc#sl";
    public String existingCertificateName = "certificate";
    Tag tag1 = new Tag(1L, "tag 3");
    Tag tag2 = new Tag(2L, "tag 1");
    Tag tag3 = new Tag(3L, "tag 2");
    Tag tag4 = new Tag(4L, "blue");
    Tag tag5 = new Tag(5L, "colour");
    Tag tag6 = new Tag(6L, "animal 1");
    GiftCertificate giftCertificate1 = new GiftCertificate();
    GiftCertificate giftCertificate2 = new GiftCertificate();
    GiftCertificate giftCertificate3 = new GiftCertificate();
    List<GiftCertificate> giftCertificateList = asList(giftCertificate1,giftCertificate2,giftCertificate3);
    List<Tag> tagList = asList(tag1,tag2,tag3,tag4,tag5,tag6);
    public void createData(){

        giftCertificate1.setId(1L);
        giftCertificate1.setName("certificate");
        giftCertificate1.setDescription("description 1");
        giftCertificate1.setPrice(310.00);
        giftCertificate1.setDuration(20L);
        giftCertificate1.setCreateDate("2023-11-21T16:48:04:309Z");
        giftCertificate1.setLastUpdateDate("2023-12-25T16:48:04:309Z");
        giftCertificate1.setTags(asList(tag1,tag5));

        giftCertificate2.setId(2L);
        giftCertificate2.setName("certificate 2");
        giftCertificate2.setDescription("description 2");
        giftCertificate2.setPrice(372.12);
        giftCertificate2.setDuration(11L);
        giftCertificate2.setCreateDate("2023-11-25T16:28:04:309Z");
        giftCertificate2.setLastUpdateDate("2023-12-20T16:48:04:309Z");
        giftCertificate2.setTags(asList(tag2,tag6));

        giftCertificate3.setId(3L);
        giftCertificate3.setName("name 3");
        giftCertificate3.setDescription("description three");
        giftCertificate3.setPrice(300.50);
        giftCertificate3.setDuration(23L);
        giftCertificate3.setCreateDate("2023-11-24T16:18:04:309Z");
        giftCertificate3.setLastUpdateDate("2023-12-10T16:48:04:309Z");
        giftCertificate3.setTags(asList(tag1, tag3, tag4, tag5));
    }

    @BeforeEach
    void setUp(){
        createData();
    }
    //CERTIFICATES
    @Test
    void saveGiftCertificate_correctRequest() {

        String formattedDate = giftCertificateTagRepository.formattingDate(date);

        Long actualIdSaved = giftCertificateTagRepository.saveGiftCertificate(giftCertificate, date, tagIdsList);
        assertNotNull(actualIdSaved);
        Long expectedIdSaved = giftCertificateList.size() + 1L;
        assertEquals(expectedIdSaved, actualIdSaved);

        GiftCertificate giftCertificateSaved = giftCertificateTagRepository.getGiftCertificateById(actualIdSaved);
        assertNotNull(giftCertificateSaved);

        assertEquals(giftCertificate.getName(), giftCertificateSaved.getName());
        assertEquals(giftCertificate.getDescription(), giftCertificateSaved.getDescription());
        assertEquals(giftCertificate.getPrice(), giftCertificateSaved.getPrice());
        assertEquals(giftCertificate.getDuration(), giftCertificateSaved.getDuration());
        assertEquals(formattedDate, giftCertificateSaved.getCreateDate());
        assertEquals(formattedDate, giftCertificateSaved.getLastUpdateDate());
        assertEquals(tagIdsList, giftCertificateTagRepository.tagsByCertificateId(actualIdSaved));
    }

    @Test
    void saveGiftCertificate_AlreadyExistingName() {
        giftCertificate.setName(existingCertificateName);
        Long id = giftCertificateTagRepository.saveGiftCertificate(giftCertificate1, date, tagIdsList);
        assertNull(id);
    }

    @Test
    void getGiftCertificateById_existingId() {
        GiftCertificate expected = giftCertificate1;

        GiftCertificate actual = giftCertificateTagRepository.getGiftCertificateById(1L);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getGiftCertificateById_nonExistingId() {
        GiftCertificate actual = giftCertificateTagRepository.getGiftCertificateById(nonExistingId);

        assertNull(actual);
    }

    @Test
    void getGiftCertificateByName_existingName(){
        GiftCertificate expected = giftCertificate1;

        GiftCertificate actual = giftCertificateTagRepository.getGiftCertificateByName("certificate");

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
    @Test
    void getGiftCertificateByName_nonExistingName(){

        GiftCertificate actual = giftCertificateTagRepository.getGiftCertificateByName(nonExistingName);
        assertNull(actual);
    }

    @Test
    void getCertificatesByTagName_existingTagName(){

        List<GiftCertificate> expected = new ArrayList<>();
        expected.add(giftCertificate1);
        expected.add(giftCertificate3);

        List<GiftCertificate> actual = giftCertificateTagRepository.getCertificatesByTagName("colour");

        assertEquals(expected,actual);
    }

    @Test
    void getCertificatesByTagName_nonExistingTagName(){

        List<GiftCertificate> actual = giftCertificateTagRepository.getCertificatesByTagName(nonExistingName);

        assertTrue(actual.isEmpty());
    }

    @Test
    void getCertificatesBySearchWord_existingWord(){

        List<GiftCertificate> expected = asList(giftCertificate1, giftCertificate2);
        List<GiftCertificate> actual = giftCertificateTagRepository.getCertificatesBySearchWord("certificate");

        assertEquals(expected,actual);
    }

    @Test
    void getCertificatesBySearchWord_partiallyContainsWord(){

        List<GiftCertificate> expected = asList(giftCertificate1, giftCertificate2);
        List<GiftCertificate> actual = giftCertificateTagRepository.getCertificatesBySearchWord("cert");

        assertEquals(expected,actual);
    }

    @Test
    void getCertificatesBySearchWord_nonExistingWord(){

        List<GiftCertificate> actual = giftCertificateTagRepository.getCertificatesBySearchWord(nonExistingName);
        assertTrue(actual.isEmpty());
    }

    @Test
    void sortCertificates_sortByNameOnly_ascendantOrder(){

        giftCertificate1.setName("position 2");
        giftCertificate3.setName("position 1");
        giftCertificate2.setName("position 3");

        String nameOrder = "ASC";
        String dateOrder = "";
        List <GiftCertificate> expected = new ArrayList<>((asList(giftCertificate3 ,giftCertificate1, giftCertificate2)));

        List<GiftCertificate> sorted = giftCertificateTagRepository.sortCertificates(giftCertificateList, nameOrder, dateOrder);
        assertEquals(expected, sorted);
    }
    @Test
    void sortCertificates_sortByNameOnly_descendantOrder(){

        giftCertificate1.setName("position 2");
        giftCertificate3.setName("position 1");
        giftCertificate2.setName("position 3");

        String nameOrder = "DESC";
        String dateOrder = "";
        List <GiftCertificate> expected = new ArrayList<>((asList(giftCertificate2 ,giftCertificate1, giftCertificate3)));

        List<GiftCertificate> sorted = giftCertificateTagRepository.sortCertificates(giftCertificateList, nameOrder, dateOrder);
        assertEquals(expected, sorted);
    }
    @Test
    void sortCertificates_sortByDateOnly_descendantOrder(){

        String nameOrder = "";
        String dateOrder = "ASC";
        List <GiftCertificate> expected = new ArrayList<>((asList(giftCertificate1, giftCertificate3, giftCertificate2)));

        List<GiftCertificate> sorted = giftCertificateTagRepository.sortCertificates(giftCertificateList, nameOrder, dateOrder);
        assertEquals(expected, sorted);
    }

    @Test
    void sortCertificates_sortByNameAndDate(){

        giftCertificate1.setName("position 2");
        giftCertificate2.setName("position 2");
        giftCertificate3.setName("position 1");

        giftCertificate1.setCreateDate("2021-11-21T16:48:04:309Z");
        giftCertificate2.setCreateDate("2020-11-21T16:48:04:309Z");
        giftCertificate3.setCreateDate("2023-11-21T16:48:04:309Z");

        String nameOrder = "DESC";
        String dateOrder = "ASC";
        List <GiftCertificate> expected = new ArrayList<>((asList(giftCertificate2, giftCertificate1, giftCertificate3)));

        List<GiftCertificate> sorted = giftCertificateTagRepository.sortCertificates(giftCertificateList, nameOrder, dateOrder);
        assertEquals(expected, sorted);
    }

    @Test
    void sortCertificates_noOrdersDefined(){


        String nameOrder = null;
        String dateOrder = null;
        List <GiftCertificate> expected = new ArrayList<>(List.of(giftCertificate2, giftCertificate1, giftCertificate3));

        List<GiftCertificate> sorted = giftCertificateTagRepository.sortCertificates(expected, nameOrder, dateOrder);
        assertEquals(expected, sorted);
    }

    @Test
    void filterCertificates() {

        String nameOrder = "DESC";
        String dateOrder = "ASC";
        List <GiftCertificate> expected = new ArrayList<>(List.of(giftCertificate2));

        List<GiftCertificate> sorted = giftCertificateTagRepository.filterCertificates("tag 1", "certificate", nameOrder, dateOrder);
        assertEquals(expected, sorted);
    }

    @Test
    void deleteCertificate_existingCertificate(){
        assertTrue(giftCertificateTagRepository.deleteGiftCertificate(giftCertificate1.getId()));
    }

    @Test
    void deleteCertificate_nonExistingCertificate(){
        assertFalse(giftCertificateTagRepository.deleteGiftCertificate(nonExistingId));
    }

    @Test
    void updateCertificate_existingCertificate(){

        Long id = giftCertificate1.getId();
        assertNotNull(giftCertificateTagRepository.updateGiftCertificate(id, giftCertificate, tagIdsList));
    }

    @Test
    void updateCertificate_nonExistingCertificate(){

        assertNull(giftCertificateTagRepository.updateGiftCertificate(nonExistingId, giftCertificate, tagIdsList));
    }
    //TAGS
    @Test
    void getTagById_getExistingTag() {
        Tag optionalTag = giftCertificateTagRepository.getTagById(tag5.getId());
        assertNotNull(optionalTag);
    }

    @Test
    void getTagById_getNonExistingTag() {
        Tag optionalTag = giftCertificateTagRepository.getTagById(nonExistingId);
        assertNull(optionalTag);
    }

    @Test
    void tagsByCertificateId_existingCertificateId() {

        List<Long> expectedTagIds = giftCertificate3.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
        List<Long> actualTags = giftCertificateTagRepository.tagsByCertificateId(3L);

        assertEquals(expectedTagIds, actualTags);
    }

    @Test
    void tagsByCertificateId_nonExistingCertificateId() {

        List<Long> actualTags = giftCertificateTagRepository.tagsByCertificateId(nonExistingId);
        assertTrue(actualTags.isEmpty());
    }

    @Test
    void getTagsListByCertificateId_existingId() {
        List<Tag> expectedTags = giftCertificate1.getTags();

        List<Tag> actualTags = giftCertificateTagRepository.getTagsListByCertificateId(giftCertificate1.getId());

        assertEquals(expectedTags, actualTags);
    }

    @Test
    void getTagsListByCertificateId_nonExistingId() {

        List<Tag> tagsList = giftCertificateTagRepository.getTagsListByCertificateId(nonExistingId);

        assertTrue(tagsList.isEmpty());
    }

    @Test
    void getTagByName_getExistingTagName() {
        Long actualTagId = giftCertificateTagRepository.getTagByName(tag1.getName());
        Long expectedTagId = tag1.getId();

        assertNotNull(actualTagId);
        assertEquals(expectedTagId, actualTagId);
    }

    @Test
    void getTagByName_getNonExistingTagName() {
        Long tag = giftCertificateTagRepository.getTagByName(nonExistingName);

        assertNull(tag);
    }

    @Test
    void saveTag_validRequest() {

        tag1.setName(nonExistingName);
        Long id = giftCertificateTagRepository.saveTag(tag1);

        assertNotNull(id);
        int expected = tagList.size() + 1;
        assertEquals(expected, id);
    }

    @Test
    void saveTag_alreadyExistingTag_returnNull() {

        Long id = giftCertificateTagRepository.saveTag(tag1);

        assertNull(id);
    }

    @Test
    void saveTag_notValidRequest() {

        tag1.setName("");
        Long id = giftCertificateTagRepository.saveTag(tag1);

        assertNull(id);
    }

    @Test
    void deleteTag_existingTag() {

        assertTrue(giftCertificateTagRepository.deleteTag(tag1.getId()));
    }

    @Test
    void deleteTag_nonExistingTag() {

        assertFalse(giftCertificateTagRepository.deleteTag(nonExistingId));
    }

    @Test
    void deleteTagFromJoinTable_existingTag() {
        List<GiftCertificate> certificatesLinkedToTag = giftCertificateTagRepository.getCertificatesByTagName(tag1.getName());

        assertFalse(certificatesLinkedToTag.isEmpty());

        giftCertificateTagRepository.deleteTagFromJoinTable(tag1.getId());

        Exception exception = assertThrows(RuntimeException.class, () ->
                giftCertificateTagRepository.deleteTagFromJoinTable(tag1.getId()));

        String expectedMessage = "This id is not attached to any certificate";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void deleteTagFromJoinTable_nonExistingTag() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                giftCertificateTagRepository.deleteTagFromJoinTable(nonExistingId));

        String expectedMessage = "This id does not exists";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void joinTags_correctRequest() {

        Long id = giftCertificate1.getId();
        giftCertificateTagRepository.joinTags(id, tagIdsList);

        GiftCertificate giftCertificate = giftCertificateTagRepository.getGiftCertificateById(id);

        assertNotNull(giftCertificate);

        List<Tag> savedTags = giftCertificate.getTags();
        List<Long> tagIds = new ArrayList<>();
        for (Tag savedTag : savedTags) tagIds.add(savedTag.getId());

        assertEquals(tagIdsList, tagIds);
    }

    @Test
    void joinTags_nonExistingTags() {

        tagIdsList.add(nonExistingId);

        Exception exception = assertThrows(RuntimeException.class, () ->
                giftCertificateTagRepository
                        .joinTags(1L, tagIdsList));

        String expectedMessage = "Tag with id " + nonExistingId + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void joinTags_emptyTagList() {

        Exception exception = assertThrows(RuntimeException.class, () ->
                giftCertificateTagRepository
                        .joinTags(1L, List.of()));

        String expectedMessage = "Tag list is empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void joinTags_nonExistingCertificate() {

        Exception exception = assertThrows(RuntimeException.class, () ->
                giftCertificateTagRepository
                        .joinTags(nonExistingId, tagIdsList));

        String expectedMessage = "Gift certificate with id " + nonExistingId + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void joinTags_nullCertificateID() {

        Exception exception = assertThrows(RuntimeException.class, () ->
                giftCertificateTagRepository
                        .joinTags(null, tagIdsList));

        String expectedMessage = "Certificate id is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void filterValidTags_existingTags() {
        assertTrue(giftCertificateTagRepository.filterValidTags(tagIdsList));
    }

    @Test
    void filterValidTags_nonExistingTags() {
        tagIdsList.add(nonExistingId);

        assertFalse(giftCertificateTagRepository.filterValidTags(tagIdsList));
    }
}
