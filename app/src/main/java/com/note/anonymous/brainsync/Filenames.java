package com.note.anonymous.brainsync;


import java.io.File;
/**
 * Created by Matthew Bulat on 14/04/2015.
 */
public class Filenames {
    private String filename;
    private boolean selected = false;
    private File file;

    protected Filenames(String filename, boolean selected){
        super();
        this.filename=filename;
        this.selected=selected;
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


}
