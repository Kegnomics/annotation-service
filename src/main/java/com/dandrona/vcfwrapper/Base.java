package com.dandrona.vcfwrapper;

import lombok.Data;

@Data
public class Base {
    private String ID;
    private String description;

    public void setField(String fieldName, String fieldValue) {
        switch (fieldName) {
            case "ID":
                setID(fieldValue);
                break;
            case "Description":
                setDescription(fieldValue);
                break;
        }
    }
}
