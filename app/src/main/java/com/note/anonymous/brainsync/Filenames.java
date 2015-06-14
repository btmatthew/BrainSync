package com.note.anonymous.brainsync;


import java.io.File;

/**
 * Created by Matthew Bulat on 14/04/2015.
 */
public class Filenames {
    private String filename;
    private boolean selected = false;
    private File file;
    private Long creationDate;
    private Long editedDate;
    private String fileType;
    private int reminderIndicator;
    private String setCreation;
    private String setScheduled;

    protected Filenames(){
        super();
    }
    protected String getFilename(){
        return filename;
    }
    protected void setFilename(String filename){
        this.filename=filename;
    }
    protected boolean isSelected(){
        return selected;
    }
    protected void setSelected(boolean selected){
        this.selected=selected;
    }
    protected void setFile(File file){
        this.file=file;
    }
    protected File getFile(){
        return file;
    }
    protected Long getCreationDate(){
        return creationDate;
    }
    protected void setCreationDate(Long creationDate){
        this.creationDate=creationDate;
    }
    protected Long getEditedDate(){
        return editedDate;
    }
    protected void setEditedDate(Long editedDate){
        this.editedDate=editedDate;
    }
    protected String getFileType(){
        return fileType;
    }
    protected void setFileTypeText(){
        this.fileType="TEXT";
    }
    protected void setFileTypeImage(){
        this.fileType="IMAGE";
    }
    protected void setFileTypeVoice(){
        this.fileType="VOICE";
    }
    protected void setReminderIndicatorValue(int alarm){ this.reminderIndicator=alarm; }
    protected int getReminderIndicatorValue(){ return reminderIndicator; }
    protected void setReminderCreationTime(String setCreation) { this.setCreation = setCreation; };
    protected String getReminderCreationTime(){ return setCreation; }
    protected void setReminderScheduledTime(String setScheduled) { this.setScheduled = setScheduled; };
    protected String getReminderScheduledTime(){ return setScheduled; }


}
