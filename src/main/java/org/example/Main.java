package org.example;
import org.example.model.*;
import org.example.service.*;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Scanner;
public class Main {
    static Scanner sc = new Scanner(System.in);
    static UserService userService = new UserService();
    static PostService postService = new PostService();
    static LikeService likeService = new LikeService();
    static CommentService commentService = new CommentService();
    static FollowService followService = new FollowService();
    static User loggedInUser = null;
    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("\n=== SOCIAL MEDIA APP ===");
            if (loggedInUser == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose: ");
                String menuInput = sc.nextLine();
                int ch;
                try {
                    ch = Integer.parseInt(menuInput.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number from the menu.");
                    continue;
                }
                switch (ch) {
                    case 1 -> register();
                    case 2 -> login();
                    case 3 -> System.exit(0);
                    default -> System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("1. Create Post");
                System.out.println("2. View Feed");
                System.out.println("3. Like a Post");
                System.out.println("4. Comment on Post");
                System.out.println("5. Follow a User");
                System.out.println("6. Unfollow a User");
                System.out.println("7. View My Followers");
                System.out.println("8. View Who I Follow");
                System.out.println("9. Delete My Account");
                System.out.println("10. Logout");
                System.out.println("11. Exit");
                System.out.print("Choose: ");

                String menuInput = sc.nextLine();
                int ch;
                try {
                    ch = Integer.parseInt(menuInput.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number from the menu.");
                    continue;
                }
                switch (ch) {
                    case 1 -> createPost();
                    case 2 -> viewFeed();
                    case 3 -> likePost();
                    case 4 -> commentOnPost();
                    case 5 -> followUser();
                    case 6 -> unfollowUser();
                    case 7 -> viewMyFollowers();
                    case 8 -> viewWhoIFollow();
                    case 9 -> deleteMyAccount();
                    case 10 -> logout();
                    case 11 -> System.exit(0);
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
    }
    static void register() throws Exception {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Full Name: ");
        String fullName = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();
        User u = new User();
        u.setUsername(username);
        u.setFullName(fullName);
        u.setEmail(email);
        boolean ok = userService.register(u, pass);
        if (ok) System.out.println("Registered successfully.");
        else System.out.println("User already exists (email/username).");
    }
    static void login() throws Exception {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();
        User u = userService.login(email, pass);
        if (u == null) {
            System.out.println("Invalid credentials.");
        } else {
            loggedInUser = u;
            System.out.println("Login successful. Welcome " + u.getFullName());
        }
    }
    static void createPost() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter post content: ");
        String content = sc.nextLine();

        Post p = new Post();
        p.setId(new ObjectId());
        p.setUserId(loggedInUser.getUserId());
        p.setContent(content);
        p.setTags(List.of());
        p.setCreatedAt(System.currentTimeMillis());

        postService.createPost(p);

        System.out.println("Post created.");
    }
    static void viewFeed() {
        List<Post> posts = postService.getAllPosts();

        System.out.println("\n=== FEED ===");
        if (loggedInUser != null) {
            String me = "User#" + loggedInUser.getUserId();
            try {
                User u = userService.getById(loggedInUser.getUserId());
                if (u != null) me = u.getUsername() + " (" + u.getFullName() + ")";
            } catch (Exception ignored) {}
            System.out.println("You are logged in as: " + me + " [ID: " + loggedInUser.getUserId() + "]");
        }
        if (posts.isEmpty()) {
            System.out.println("No posts yet.");
            return;
        }
        for (Post p : posts) {
            long likeCount = likeService.countLikes(p.getId());
            List<Comment> comments = commentService.getComments(p.getId());
            System.out.println("\nPost ID: " + p.getId());
            System.out.println("User ID: " + p.getUserId());
            String authorLabel = "User#" + p.getUserId();
            try {
                User author = userService.getById(p.getUserId());
                if (author != null) {
                    authorLabel = author.getUsername() + " (" + author.getFullName() + ")";
                }
            } catch (Exception ignored) {}
            if (loggedInUser != null && p.getUserId() == loggedInUser.getUserId()) {
                authorLabel += " (You)";
            }
            System.out.println("Author: " + authorLabel);
            try {
                int authorFollowers = followService.countFollowers(p.getUserId());
                int authorFollowing = followService.countFollowing(p.getUserId());
                System.out.println("Author Followers: " + authorFollowers + " | Following: " + authorFollowing);
            } catch (Exception ignored) {
            }
            System.out.println("Content: " + p.getContent());
            System.out.println("Likes: " + likeCount);
            System.out.println("Comments:");

            for (Comment c : comments) {
                System.out.println(" - (" + c.getUserId() + ") " + c.getText());
            }
        }
    }
    static void likePost() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter Post ID to Like: ");
        String id = sc.nextLine();
        if (!ObjectId.isValid(id)) {
            System.out.println("Invalid Post ID. Please enter the 24-character hex ID shown in the feed.");
            return;
        }
        Like like = new Like();
        like.setId(new ObjectId());
        like.setPostId(new ObjectId(id));
        like.setUserId(loggedInUser.getUserId());
        like.setLikedAt(System.currentTimeMillis());

        likeService.addLike(like);

        System.out.println("Liked.");
    }
    static void commentOnPost() {        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter Post ID: ");
        String id = sc.nextLine();
        if (!ObjectId.isValid(id)) {
            System.out.println("Invalid Post ID. Please enter the 24-character hex ID shown in the feed.");
            return;
        }
        System.out.print("Comment: ");
        String text = sc.nextLine();

        Comment c = new Comment();
        c.setId(new ObjectId());
        c.setPostId(new ObjectId(id));
        c.setUserId(loggedInUser.getUserId());
        c.setText(text);
        c.setCommentedAt(System.currentTimeMillis());


        commentService.addComment(c);
        System.out.println("Comment added.");
    }
    static void followUser() throws Exception {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter username or User ID to follow: ");
        String input = sc.nextLine().trim();

        Integer targetId = null;
        try {
            targetId = Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            // Not a number, try username
            User u = userService.getByUsername(input);
            if (u != null) targetId = u.getUserId();
        }
        if (targetId == null) {
            System.out.println("User not found. Enter a valid username or numeric ID.");
            return;
        }
        if (targetId == loggedInUser.getUserId()) {
            System.out.println("You cannot follow yourself.");
            return;
        }
        boolean ok = followService.follow(loggedInUser.getUserId(), targetId);
        System.out.println(ok ? "Now following." : "Already following or user not found.");
    }
    static void unfollowUser() throws Exception {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter username or User ID to unfollow: ");
        String input = sc.nextLine().trim();
        Integer targetId = null;
        try {
            targetId = Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            User u = userService.getByUsername(input);
            if (u != null) targetId = u.getUserId();
        }
        if (targetId == null) {
            System.out.println("User not found. Enter a valid username or numeric ID.");
            return;
        }
        boolean ok = followService.unfollow(loggedInUser.getUserId(), targetId);
        System.out.println(ok ? "Unfollowed." : "You were not following this user.");
    }
    static void viewMyFollowers() throws Exception {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        List<Integer> ids = followService.getFollowers(loggedInUser.getUserId());
        System.out.println("Followers (" + ids.size() + "):");
        for (Integer id : ids) {
            User u = userService.getById(id);
            String label = (u != null ? (u.getUsername() + " (" + u.getFullName() + ")") : ("User#" + id));
            System.out.println(" - " + label);
        }
    }
    static void deleteMyAccount() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.println("This will permanently delete your account and all your posts, likes, and comments.");
        System.out.print("Type DELETE to confirm: ");
        String confirm = sc.nextLine().trim();
        if (!"DELETE".equalsIgnoreCase(confirm)) {
            System.out.println("Cancelled.");
            return;
        }
        try {
            boolean ok = userService.deleteUser(loggedInUser.getUserId());
            if (ok) {
                System.out.println("Your account has been deleted.");
                loggedInUser = null;
            } else {
                System.out.println("Delete failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }
    static void viewWhoIFollow() throws Exception {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        List<Integer> ids = followService.getFollowing(loggedInUser.getUserId());
        System.out.println("Following (" + ids.size() + "):");
        for (Integer id : ids) {
            User u = userService.getById(id);
            String label = (u != null ? (u.getUsername() + " (" + u.getFullName() + ")") : ("User#" + id));
            System.out.println(" - " + label);
        }
    }
    static void logout() {
        loggedInUser = null;
        System.out.println("Logged out.");
    }
}
