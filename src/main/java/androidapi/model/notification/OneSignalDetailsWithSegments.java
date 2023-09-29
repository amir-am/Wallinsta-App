package androidapi.model.notification;

import java.util.ArrayList;
import java.util.HashMap;

public class OneSignalDetailsWithSegments {
    private ArrayList<String> included_segments;
    private String app_id;
    private String url;
    private String big_picture;
    private String small_icon;
    private String large_icon;
    private HashMap contents;
    private HashMap headings;

    public ArrayList<String> getIncluded_segments() {
        return included_segments;
    }

    public void setIncluded_segments(ArrayList<String> included_segments) {
        this.included_segments = included_segments;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBig_picture() {
        return big_picture;
    }

    public void setBig_picture(String big_picture) {
        this.big_picture = big_picture;
    }

    public String getSmall_icon() {
        return small_icon;
    }

    public void setSmall_icon(String small_icon) {
        this.small_icon = small_icon;
    }

    public String getLarge_icon() {
        return large_icon;
    }

    public void setLarge_icon(String large_icon) {
        this.large_icon = large_icon;
    }

    public HashMap getContents() {
        return contents;
    }

    public void setContents(HashMap contents) {
        this.contents = contents;
    }

    public HashMap getHeadings() {
        return headings;
    }

    public void setHeadings(HashMap headings) {
        this.headings = headings;
    }
}
