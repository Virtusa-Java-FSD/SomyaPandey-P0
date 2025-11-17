package org.example.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.example.config.MongoConnection;
import org.example.model.Post;
import org.bson.types.ObjectId;
import com.mongodb.client.model.Sorts;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PostDAO {

    private final MongoCollection<Post> collection;

    public PostDAO() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("posts", Post.class);
    }

    public ObjectId save(Post post) {
        collection.insertOne(post);
        return post.getId();
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        collection.find().sort(Sorts.descending("createdAt")).into(posts);
        return posts;
    }

    public Post findById(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    public List<ObjectId> findIdsByUserId(int userId) {
        List<ObjectId> ids = new ArrayList<>();
        collection.find(eq("userId", userId)).map(Post::getId).into(ids);
        return ids;
    }

    public long deleteByUserId(int userId) {
        return collection.deleteMany(eq("userId", userId)).getDeletedCount();
    }
}