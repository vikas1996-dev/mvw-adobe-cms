package com.mvw.core.utils;

import com.mvw.core.models.dto.Images;
import com.mvw.core.models.dto.Photo;

import java.util.List;

public class ImagePathUtils {
    private ImagePathUtils() {
        /* This utility class should not be instantiated */
    }


    /**
     * Prepends the basePath to all Photo.path entries inside a list of Images.
     *
     * @param images   list of Images containing Photos
     * @param basePath base URL to prepend
     */
    public static void prependBasePath(List<Images> images, String basePath) {
        if (images == null || images.isEmpty() || basePath == null) return;

        for (Images image : images) {
            if (image.getPhoto() == null) continue;

            for (Photo photo : image.getPhoto()) {
                String path = photo.getPath();
                if (path != null && !path.startsWith("http")) {
                    photo.setPath(basePath + path);
                }
            }
        }
    }
}