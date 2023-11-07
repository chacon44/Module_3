package com.epam.esm.model;

import lombok.Getter;

public class Tag{

    private Long tagId;
    @Getter
    private String tagName;

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
