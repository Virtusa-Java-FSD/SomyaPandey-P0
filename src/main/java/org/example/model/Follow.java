package org.example.model;

public class Follow {
    private int followId;
    private int followerId;
    private int followingId;
    private String createdAt;

    public Follow() {}

    public Follow(int followId, int followerId, int followingId, String createdAt) {
        this.followId = followId;
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = createdAt;
    }

    public int getFollowId() { return followId; }
    public void setFollowId(int followId) { this.followId = followId; }

    public int getFollowerId() { return followerId; }
    public void setFollowerId(int followerId) { this.followerId = followerId; }

    public int getFollowingId() { return followingId; }
    public void setFollowingId(int followingId) { this.followingId = followingId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
