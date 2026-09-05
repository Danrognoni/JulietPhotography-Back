package com.julietamarateo.photography.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoLayoutDto {

    private String id;
    private Double x;
    private Double y;
    private Double width;
    private Double height;
    private Integer zIndex;

    @JsonProperty("rotation")
    @JsonAlias({"rotation", "rotate", "rot"})
    private Double rotation = 0.0;

    public PhotoLayoutDto() {
    }

    public PhotoLayoutDto(String id, Double x, Double y, Double width, Double height, Integer zIndex) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
    }

    public PhotoLayoutDto(String id, Double x, Double y, Double width, Double height, Integer zIndex, Double rotation) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.rotation = rotation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    public Double getRotation() {
        return rotation != null ? rotation : 0.0;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation != null ? rotation : 0.0;
    }
}
