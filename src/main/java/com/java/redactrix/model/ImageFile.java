package com.java.redactrix.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ImageFile {
	@Id
	@GeneratedValue
    private long id;
    private String path;
    
    
    
    
    public ImageFile() {
		super();
	}

	public ImageFile(Long id, String path) {
    		this.id = id;
    		this.path = path;
    }

    public ImageFile(String path) {
       this.setPath(path);
    }
    
    public long getId() {
    	return id;
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Image [id: " + id+ " path " + path;
	}
}