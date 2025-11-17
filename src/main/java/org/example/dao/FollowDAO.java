package org.example.dao;

import org.example.config.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    private final Connection conn = MySQLConnection.getConnection();

    public FollowDAO() {
        ensureTable();
    }

    private void ensureTable() {
        try (Statement st = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS follows (" +
                    "follow_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "follower_id INT NOT NULL," +
                    "following_id INT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY uq_follow (follower_id, following_id)," +
                    "INDEX idx_follower (follower_id)," +
                    "INDEX idx_following (following_id)," +
                    "CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                    "CONSTRAINT fk_following FOREIGN KEY (following_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                    ")";
            st.executeUpdate(sql);
        } catch (SQLException ignored) {
        }
    }

    public boolean follow(int followerId, int followingId) throws Exception {
        if (followerId == followingId) return false;
        String sql = "INSERT INTO follows (follower_id, following_id, created_at) VALUES (?, ?, NOW())";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, followerId);
        ps.setInt(2, followingId);
        try {
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }
    }

    public boolean unfollow(int followerId, int followingId) throws Exception {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, followerId);
        ps.setInt(2, followingId);
        return ps.executeUpdate() > 0;
    }

    public boolean isFollowing(int followerId, int followingId) throws Exception {
        String sql = "SELECT 1 FROM follows WHERE follower_id = ? AND following_id = ? LIMIT 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, followerId);
        ps.setInt(2, followingId);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public int countFollowers(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM follows WHERE following_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int countFollowing(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public List<Integer> getFollowers(int userId) throws Exception {
        String sql = "SELECT follower_id FROM follows WHERE following_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        List<Integer> list = new ArrayList<>();
        while (rs.next()) list.add(rs.getInt(1));
        return list;
    }

    public List<Integer> getFollowing(int userId) throws Exception {
        String sql = "SELECT following_id FROM follows WHERE follower_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        List<Integer> list = new ArrayList<>();
        while (rs.next()) list.add(rs.getInt(1));
        return list;
    }
}
