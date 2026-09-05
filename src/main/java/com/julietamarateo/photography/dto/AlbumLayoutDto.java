package com.julietamarateo.photography.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AlbumLayoutDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("xPos")
    @JsonAlias({"x", "xPos", "xpos", "x_pos"})
    private Double xPos;

    @JsonProperty("yPos")
    @JsonAlias({"y", "yPos", "ypos", "y_pos"})
    private Double yPos;

    @JsonProperty("width")
    private Double width;

    @JsonProperty("height")
    private Double height;

    @JsonProperty("rotation")
    @JsonAlias({"rotation", "rotate", "rot"})
    private Double rotation = 0.0;

    @JsonProperty("zIndex")
    @JsonAlias({"zIndex", "zindex", "z_index"})
    private Integer zIndex = 1;

    public AlbumLayoutDto() {
    }

    public AlbumLayoutDto(String id, Double xPos, Double yPos, Double width, Integer zIndex) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.zIndex = zIndex;
    }

    public AlbumLayoutDto(String id, Double xPos, Double yPos, Double width, Double height, Double rotation, Integer zIndex) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.zIndex = zIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getXPos() {
        return xPos;
    }

    public void setXPos(Double xPos) {
        this.xPos = xPos;
    }

    public Double getYPos() {
        return yPos;
    }

    public void setYPos(Double yPos) {
        this.yPos = yPos;
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

    public Double getRotation() {
        return rotation != null ? rotation : 0.0;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation != null ? rotation : 0.0;
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    @JsonIgnore
    public Double getX() {
        return xPos;
    }

    public void setX(Double x) {
        this.xPos = x;
    }

    @JsonIgnore
    public Double getY() {
        return yPos;
    }

    public void setY(Double y) {
        this.yPos = y;
    }
}
