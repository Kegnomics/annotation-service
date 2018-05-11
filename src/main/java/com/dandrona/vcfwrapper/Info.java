package com.dandrona.vcfwrapper;

import lombok.Data;

enum Type {
    Integer,
    String,
    Flag,
    Float,
    Character,
    Unknown
}

@Data
public class Info extends Base{
    private Type type;
    private String number;

    private void setType(String type) {
        switch (type) {
            case "Integer":
                this.type = Type.Integer;
                break;
            case "String":
                this.type = Type.String;
                break;
            case "Flag":
                this.type = Type.Flag;
                break;
            case "Float":
                this.type = Type.Float;
                break;
            case "Character":
                this.type = Type.Character;
                break;
            default:
                this.type = Type.Unknown;
        }
    }

    @Override
    public void setField(String fieldName, String fieldValue) {
        switch (fieldName) {
            case "Type":
                setType(fieldValue);
                break;
            case "Number":
                setNumber(fieldValue);
                break;
            default:
                super.setField(fieldName, fieldValue);
                break;
        }
    }
}
