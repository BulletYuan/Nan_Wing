package com.artdream.Nan_Wing.WebPage;

/**
 * Created by BulletYuan on 2016-10-10.
 */
public class ListParams {
    private int weight;
    private String title;
    private String link;
    public ListParams(){}
    public ListParams(String link, int weight, String title) {
        this.weight = weight;
        this.title = title;
        this.link = link;
    }
    public int getweight() {
        return weight;
    }
    public void setweight(int weight) {
        this.weight = weight;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTimelength() {
        return link;
    }
    public void setTimelength(String link) {
        this.link = link;
    }

}
