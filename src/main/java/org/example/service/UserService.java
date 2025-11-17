package org.example.service;

import org.example.dao.UserDAO;
import org.example.dao.PostDAO;
import org.example.dao.LikeDAO;
import org.example.dao.CommentDAO;
import org.example.dao.ProfileDAO;
import org.example.model.User;
import org.example.model.Profile;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final PostDAO postDAO = new PostDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final ProfileDAO profileDAO = new ProfileDAO();

    public boolean register(User user, String plainPassword) throws Exception {

        if (userDAO.findByEmail(user.getEmail()) != null) return false;
        if (userDAO.findByUsername(user.getUsername()) != null) return false;

        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        user.setPasswordHash(hashed);

        boolean saved = userDAO.save(user);
        if (saved) {
            try {
                User created = userDAO.findByEmail(user.getEmail());
                if (created != null) {
                    Profile profile = new Profile();
                    profile.setUserId(created.getUserId());
                    profile.setBio("");
                    profile.setLocation("");
                    profile.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()).toString());
                    profileDAO.save(profile);
                }
            } catch (Exception ignored) {}
        }
        return saved;
    }

    public User login(String email, String password) throws Exception {
        User user = userDAO.findByEmail(email);
        if (user == null) return null;
        String stored = user.getPasswordHash();

        boolean matches = false;
        if (stored != null && stored.startsWith("$2")) {
            matches = BCrypt.checkpw(password, stored);
        } else if (stored != null) {
            matches = password.equals(stored);
        }

        if (matches) {
            return user;
        }
        return null;
    }

    public User getById(int id) throws Exception {
        return userDAO.findById(id);
    }

    public User getByUsername(String username) throws Exception {
        return userDAO.findByUsername(username);
    }

    public boolean deleteUser(int userId) throws Exception {
        java.util.List<org.bson.types.ObjectId> postIds = postDAO.findIdsByUserId(userId);

        likeDAO.deleteByUserId(userId);
        commentDAO.deleteByUserId(userId);

        if (postIds != null && !postIds.isEmpty()) {
            likeDAO.deleteByPostIds(postIds);
            commentDAO.deleteByPostIds(postIds);
        }

        postDAO.deleteByUserId(userId);

        try { profileDAO.deleteByUserId(userId); } catch (Exception ignored) {}

        return userDAO.deleteById(userId);
    }
}