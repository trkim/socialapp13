package com.soapp.project.sisas_android_chat.memberInfo;

/**
 * Created by eelhea on 2016-12-14.
 */

public class ScrapInRoomListItem {

    private String single_keyword;
    private String single_keyword_date;
    private String scrap_article_title;
    private String scrap_url;
    private String scrap_content;
    private String scrap_opinion;

    public ScrapInRoomListItem(String single_keyword, String single_keyword_date, String scrap_article_title, String scrap_url, String scrap_content, String scrap_opinion){
        this.single_keyword = single_keyword;
        this.single_keyword_date = single_keyword_date;
        this.scrap_article_title = scrap_article_title;
        this.scrap_url = scrap_url;
        this.scrap_content = scrap_content;
        this.scrap_opinion = scrap_opinion;
    }

    public String getSingle_keyword() {
        return single_keyword;
    }

    public void setSingle_keyword(String single_keyword) {
        this.single_keyword = single_keyword;
    }

    public String getSingle_keyword_date() {
        return single_keyword_date;
    }

    public void setSingle_keyword_date(String single_keyword_date) {
        this.single_keyword_date = single_keyword_date;
    }

    public String getScrap_article_title() {
        return scrap_article_title;
    }

    public void setScrap_article_title(String scrap_article_title) {
        this.scrap_article_title = scrap_article_title;
    }

    public String getScrap_url() {
        return scrap_url;
    }

    public void setScrap_url(String scrap_url) {
        this.scrap_url = scrap_url;
    }

    public String getScrap_content() {
        return scrap_content;
    }

    public void setScrap_content(String scrap_content) {
        this.scrap_content = scrap_content;
    }

    public String getScrap_opinion() {
        return scrap_opinion;
    }

    public void setScrap_opinion(String scrap_opinion) {
        this.scrap_opinion = scrap_opinion;
    }
}
