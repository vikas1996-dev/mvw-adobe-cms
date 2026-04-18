package com.mvw.core.models.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Gallery {

    private List<Images> images;
    private List<Images> sortedImages;
    private List<Images> modalImages;

    public List<Images> getImages() {
        return images != null ? images : List.of();
    }

    public void setImages(List<Images> images) {
        this.images = images;
        this.sortedImages = null; // reset cache
        this.modalImages = null;  // reset cache
    }

    public List<Images> getSortedImages() {
        if (sortedImages != null) {
            return sortedImages;
        }

        if (images == null || images.isEmpty()) {
            return List.of();
        }

        // Step 1: Sort by priority
        List<Images> sorted = images.stream()
                .sorted(Comparator.comparingInt(img -> {
                    try {
                        return img.getPriority() != null ? Integer.parseInt(img.getPriority()) : Integer.MAX_VALUE;
                    } catch (NumberFormatException e) {
                        return Integer.MAX_VALUE;
                    }
                }))
                .collect(Collectors.toList());

        // Step 2: Find first video
        Images videoImage = sorted.stream()
                .filter(Images::isVideo)
                .findFirst()
                .orElse(null);

        // Step 3: Move video to second position if exists
        if (videoImage != null) {
            sorted.remove(videoImage);
            if (sorted.size() >= 1) {
                sorted.add(1, videoImage);
            } else {
                sorted.add(videoImage);
            }
        }

        sortedImages = sorted;
        return sortedImages;
    }

    public void setSortedImages(List<Images> sortedImages) {
        this.sortedImages = sortedImages;
    }

    public List<Images> getModalImages() {
        if (modalImages != null) {
            return modalImages;
        }

        if (images == null || images.isEmpty()) {
            return List.of();
        }

        // Step 1: Sort by priority
        List<Images> modal = images.stream()
                .sorted(Comparator.comparingInt(img -> {
                    try {
                        return img.getPriority() != null ? Integer.parseInt(img.getPriority()) : Integer.MAX_VALUE;
                    } catch (NumberFormatException e) {
                        return Integer.MAX_VALUE;
                    }
                }))
                .collect(Collectors.toList());

        // Step 2: Find first video
        Images videoImage = modal.stream()
                .filter(Images::isVideo)
                .findFirst()
                .orElse(null);

        // Step 3: Move video to 4th position (index 3)
        if (videoImage != null) {
            modal.remove(videoImage);
            int insertIndex = 3;
            if (modal.size() >= insertIndex) {
                modal.add(insertIndex, videoImage);
            } else {
                modal.add(videoImage);
            }
        }

        modalImages = modal;
        return modalImages;
    }

    public void setModalImages(List<Images> modalImages) {
        this.modalImages = modalImages;
    }

    @Override
    public String toString() {
        return "Gallery [images=" + images + ", sortedImages=" + sortedImages + ", modalImages=" + modalImages + "]";
    }
}