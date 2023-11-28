package com.epam.esm.validators;

public class TagValidator {
    public static String validateForSave (String tagName){

        return tagName == null || tagName.isEmpty() ? "Tag name is required" : "Valid";
    }
}
