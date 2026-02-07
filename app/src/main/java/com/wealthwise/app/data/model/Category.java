package com.wealthwise.app.data.model;

public class Category {
    private long id;
    private String name;
    private TransactionType type;
    private String iconName;
    private String colorHex;
    private boolean isDefault;

    public Category() {}

    public Category(long id, String name, TransactionType type, String iconName,
                    String colorHex, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.iconName = iconName;
        this.colorHex = colorHex;
        this.isDefault = isDefault;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
