package com.arhiser.nasa_sample.api.model;

import com.arhiser.nasa_sample.api.NasaService;

public class PhotoDTO {
    private String identifier;
    private String caption;
    private String image;
    private String date;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        //https://api.nasa.gov/EPIC/archive/enhanced/2016/12/04/png/epic_RBG_20161204003633.png?api_key=DEMO_KEY
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.nasa.gov/EPIC/archive/natural/");
        String[] dateComponents = date.split(" ")[0].split("-");
        sb
                .append(dateComponents[0]).append('/')
                .append(dateComponents[1]).append('/')
                .append(dateComponents[2]).append("/png/")
                .append(image).append(".png?api_key=").append(NasaService.KEY);
        return sb.toString();
    }
}
