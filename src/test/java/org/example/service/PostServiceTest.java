package org.example.service;

import org.bson.types.ObjectId;
import org.example.dao.PostDAO;
import org.example.model.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private MockedConstruction<PostDAO> postDaoConstruction;

    @AfterEach
    void tearDown() {
        if (postDaoConstruction != null) postDaoConstruction.close();
    }

    @Test
    void createPost_returnsIdFromDao() {
        ObjectId id = new ObjectId();
        Post p = new Post();
        p.setId(id);

        postDaoConstruction = mockConstruction(PostDAO.class, (mock, ctx) -> {
            when(mock.save(p)).thenReturn(id);
        });

        PostService svc = new PostService();
        ObjectId out = svc.createPost(p);
        assertEquals(id, out);
        PostDAO mockDao = postDaoConstruction.constructed().get(0);
        verify(mockDao, times(1)).save(p);
    }

    @Test
    void getAllPosts_returnsListFromDao() {
        List<Post> list = List.of(new Post());
        postDaoConstruction = mockConstruction(PostDAO.class, (mock, ctx) -> {
            when(mock.findAll()).thenReturn(list);
        });

        PostService svc = new PostService();
        List<Post> out = svc.getAllPosts();
        assertEquals(list, out);
    }

    @Test
    void getPost_returnsFromDao() {
        ObjectId id = new ObjectId();
        Post post = new Post();
        postDaoConstruction = mockConstruction(PostDAO.class, (mock, ctx) -> {
            when(mock.findById(id)).thenReturn(post);
        });

        PostService svc = new PostService();
        Post out = svc.getPost(id);
        assertEquals(post, out);
    }
}
