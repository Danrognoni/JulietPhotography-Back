package com.julietamarateo.photography.dto;

import java.util.List;

public class ReorderPhotosDto {

    private List<PhotoOrderItem> items;

    public static class PhotoOrderItem {
        private String id;
        private Integer order;

        public PhotoOrderItem() {
        }

        public PhotoOrderItem(String id, Integer order) {
            this.id = id;
            this.order = order;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }
    }

    public ReorderPhotosDto() {
    }

    public ReorderPhotosDto(List<PhotoOrderItem> items) {
        this.items = items;
    }

    public List<PhotoOrderItem> getItems() {
        return items;
    }

    public void setItems(List<PhotoOrderItem> items) {
        this.items = items;
    }
}
