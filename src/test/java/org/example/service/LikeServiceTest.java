package org.example.service;

import org.bson.types.ObjectId;
import org.example.dao.LikeDAO;
import org.example.model.Like;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    private MockedConstruction<LikeDAO> likeDaoConstruction;

    @AfterEach
    void tearDown() {
        if (likeDaoConstruction != null) likeDaoConstruction.close();
    }

    @Test
    void addLike_delegatesToDao() {
        Like like = new Like();
        likeDaoConstruction = mockConstruction(LikeDAO.class, (mock, ctx) -> {
            doNothing().when(mock).save(like);
        });

        LikeService svc = new LikeService();
        svc.addLike(like);
        LikeDAO mockDao = likeDaoConstruction.constructed().get(0);
        verify(mockDao, times(1)).save(like);
    }

    @Test
    void countLikes_returnsFromDao() {
        ObjectId postId = new ObjectId();
        likeDaoConstruction = mockConstruction(LikeDAO.class, (mock, ctx) -> {
            when(mock.countLikes(postId)).thenReturn(5L);
        });

        LikeService svc = new LikeService();
        long out = svc.countLikes(postId);
        assertEquals(5L, out);
    }
}
