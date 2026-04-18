// package com.mvw.core.models.dto;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.util.ArrayList;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// public class GalleryTest {

//     private Gallery gallery;

//     @BeforeEach
//     void setUp() {
//         gallery = new Gallery();
//     }

//     @Test
//     void testGetImagesReturnsNullByDefault() {
//         assertNull(gallery.getImages());
//     }

//     @Test
//     void testGetSortedImagesReturnsEmptyListWhenImagesIsNull() {
//         List<Images> sorted = gallery.getSortedImages();
//         assertNotNull(sorted);
//         assertTrue(sorted.isEmpty());
//     }

//     @Test
//     void testGetSortedImagesReturnsEmptyListWhenImagesIsEmpty() {
//         setField(gallery, "images", new ArrayList<>());
//         List<Images> sorted = gallery.getSortedImages();
//         assertNotNull(sorted);
//         assertTrue(sorted.isEmpty());
//     }

//     @Test
//     void testGetSortedImagesFiltersNullPriority() {
//         List<Images> imagesList = new ArrayList<>();
//         Images imgWithPriority = new Images();
//         setField(imgWithPriority, "priority", "1");
//         Images imgWithoutPriority = new Images();
//         // imgWithoutPriority has null priority
        
//         imagesList.add(imgWithPriority);
//         imagesList.add(imgWithoutPriority);
//         setField(gallery, "images", imagesList);
        
//         List<Images> sorted = gallery.getSortedImages();
//         assertEquals(1, sorted.size());
//         assertEquals("1", sorted.get(0).getPriority());
//     }

//     @Test
//     void testGetSortedImagesSortsByPriority() {
//         List<Images> imagesList = new ArrayList<>();
        
//         Images img1 = new Images();
//         setField(img1, "priority", "3");
//         Images img2 = new Images();
//         setField(img2, "priority", "1");
//         Images img3 = new Images();
//         setField(img3, "priority", "2");
        
//         imagesList.add(img1);
//         imagesList.add(img2);
//         imagesList.add(img3);
//         setField(gallery, "images", imagesList);
        
//         List<Images> sorted = gallery.getSortedImages();
//         assertEquals(3, sorted.size());
//         assertEquals("1", sorted.get(0).getPriority());
//         assertEquals("2", sorted.get(1).getPriority());
//         assertEquals("3", sorted.get(2).getPriority());
//     }

//     @Test
//     void testGetSortedImagesWithSingleImage() {
//         List<Images> imagesList = new ArrayList<>();
//         Images img = new Images();
//         setField(img, "priority", "5");
//         imagesList.add(img);
//         setField(gallery, "images", imagesList);
        
//         List<Images> sorted = gallery.getSortedImages();
//         assertEquals(1, sorted.size());
//         assertEquals("5", sorted.get(0).getPriority());
//     }

//     @Test
//     void testGetSortedImagesAllNullPriorities() {
//         List<Images> imagesList = new ArrayList<>();
//         imagesList.add(new Images());
//         imagesList.add(new Images());
//         setField(gallery, "images", imagesList);
        
//         List<Images> sorted = gallery.getSortedImages();
//         assertTrue(sorted.isEmpty());
//     }

//     private void setField(Object target, String fieldName, Object value) {
//         try {
//             java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
//             field.setAccessible(true);
//             field.set(target, value);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }
// }
