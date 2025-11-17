package org.example.service;

import org.example.dao.FollowDAO;

import java.util.List;

public class FollowService {

    private final FollowDAO followDAO = new FollowDAO();

    public boolean follow(int followerId, int followingId) throws Exception {
        return followDAO.follow(followerId, followingId);
    }

    public boolean unfollow(int followerId, int followingId) throws Exception {
        return followDAO.unfollow(followerId, followingId);
    }

    public boolean isFollowing(int followerId, int followingId) throws Exception {
        return followDAO.isFollowing(followerId, followingId);
    }

    public int countFollowers(int userId) throws Exception {
        return followDAO.countFollowers(userId);
    }

    public int countFollowing(int userId) throws Exception {
        return followDAO.countFollowing(userId);
    }

    public List<Integer> getFollowers(int userId) throws Exception {
        return followDAO.getFollowers(userId);
    }

    public List<Integer> getFollowing(int userId) throws Exception {
        return followDAO.getFollowing(userId);
    }
}
