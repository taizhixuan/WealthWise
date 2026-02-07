package com.wealthwise.app.engine.recommendation;

public class Recommendation {

    private String id;
    private RecommendationType type;
    private RecommendationPriority priority;
    private String title;
    private String description;
    private String actionText;
    private long categoryId;
    private double amount;

    public Recommendation() {}

    public Recommendation(String id, RecommendationType type, RecommendationPriority priority,
                          String title, String description) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.title = title;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public RecommendationType getType() { return type; }
    public void setType(RecommendationType type) { this.type = type; }
    public RecommendationPriority getPriority() { return priority; }
    public void setPriority(RecommendationPriority priority) { this.priority = priority; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getActionText() { return actionText; }
    public void setActionText(String actionText) { this.actionText = actionText; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
