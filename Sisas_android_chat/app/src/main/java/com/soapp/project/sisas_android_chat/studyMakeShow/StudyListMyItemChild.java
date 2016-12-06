package com.soapp.project.sisas_android_chat.studyMakeShow;

/**
 * Created by eelhea on 2016-11-11.
 */
public class StudyListMyItemChild {

    private String study_name;
    private String study_date;
    private String study_comment;
    private String study_keyword;

    public StudyListMyItemChild(String study_name, String study_date, String study_comment, String study_keyword){
        this.study_name = study_name;
        this.study_date = study_date;
        this.study_comment = study_comment;
        this.study_keyword = study_keyword;
    }

    public String getStudy_name() {
        return study_name;
    }

    public void setStudy_name(String study_name) {
        this.study_name = study_name;
    }

    public String getStudy_date() {
        return study_date;
    }

    public void setStudy_date(String study_date) {
        this.study_date = study_date;
    }

    public String getStudy_comment() {
        return study_comment;
    }

    public void setStudy_comment(String study_comment) {
        this.study_comment = study_comment;
    }

    public String getStudy_keyword() {
        return study_keyword;
    }

    public void setStudy_keyword(String study_keyword) {
        this.study_keyword = study_keyword;
    }
}
