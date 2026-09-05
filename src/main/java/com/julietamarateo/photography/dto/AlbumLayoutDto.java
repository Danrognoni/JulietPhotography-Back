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
