package com.task06;

public class Audit {
    private String id;
    private String itemKey;
    private String modificationTime;//date time in ISO 8601 formatted string
    private String updatedAttribute;
    private Object oldValue;
    private Object newValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getUpdatedAttribute() {
        return updatedAttribute;
    }

    public void setUpdatedAttribute(String updatedAttribute) {
        this.updatedAttribute = updatedAttribute;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "id='" + id + '\'' +
                ", itemKey='" + itemKey + '\'' +
                ", modificationTime='" + modificationTime + '\'' +
                ", updatedAttribute='" + updatedAttribute + '\'' +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }
}

