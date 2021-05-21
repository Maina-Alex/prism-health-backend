package com.prismhealth.files;

import org.springframework.stereotype.Service;

@Service
public class StorageProperties {

    private String location = "./uploads/files";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
