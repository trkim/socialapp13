package com.soapp.project.sisas_android_chat.studyMakeShow;

/**
 * Created by eelhea on 2016-11-11.
 */
public class StudyListMyItemChild {

    private String study_name;
    private String study_date;
    private String study_comment;

    public StudyListMyItemChild(String study_name, String study_date, String study_comment){
        this.study_name = study_name;
        this.study_date = study_date;
        this.study_comment = study_comment;
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
}
