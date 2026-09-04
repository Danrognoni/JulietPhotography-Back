package com.julietamarateo.photography.dto;

public class CameraSpecsDto {
    private String camera;
    private String lens;
    private String aperture;
    private String shutter;
    private String iso;

    public CameraSpecsDto() {
    }

    public CameraSpecsDto(String camera, String lens, String aperture, String shutter, String iso) {
        this.camera = camera;
        this.lens = lens;
        this.aperture = aperture;
        this.shutter = shutter;
        this.iso = iso;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getLens() {
        return lens;
    }

    public void setLens(String lens) {
        this.lens = lens;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getShutter() {
        return shutter;
    }

    public void setShutter(String shutter) {
        this.shutter = shutter;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }
}
