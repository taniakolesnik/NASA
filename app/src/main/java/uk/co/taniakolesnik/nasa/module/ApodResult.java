package uk.co.taniakolesnik.nasa.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApodResult {

    @SerializedName("copyright")
    @Expose
    private String copyright;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("hdurl")
    @Expose
    private String hdurl;

    @SerializedName("media_type")
    @Expose
    private String media_type;

    @SerializedName("service_version")
    @Expose
    private String service_version;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("url")
    @Expose
    private String url;

    public ApodResult(String copyright, String date, String hdurl, String media_type, String service_version, String title, String url) {
        this.copyright = copyright;
        this.date = date;
        this.hdurl = hdurl;
        this.media_type = media_type;
        this.service_version = service_version;
        this.title = title;
        this.url = url;
    }

    public ApodResult() {
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHdurl() {
        return hdurl;
    }

    public void setHdurl(String hdurl) {
        this.hdurl = hdurl;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getService_version() {
        return service_version;
    }

    public void setService_version(String service_version) {
        this.service_version = service_version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}
