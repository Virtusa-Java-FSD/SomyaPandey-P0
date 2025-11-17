package org.example.service;

import org.bson.types.ObjectId;
import org.example.dao.CommentDAO;
import org.example.model.Comment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private MockedConstruction<CommentDAO> commentDaoConstruction;

    @AfterEach
    void tearDown() {
        if (commentDaoConstruction != null) commentDaoConstruction.close();
    }

    @Test
    void addComment_delegatesToDao() {
        Comment c = new Comment();
        commentDaoConstruction = mockConstruction(CommentDAO.class, (mock, ctx) -> {
            doNothing().when(mock).save(c);
        });

        CommentService svc = new CommentService();
        svc.addComment(c);
        CommentDAO mockDao = commentDaoConstruction.constructed().get(0);
        verify(mockDao, times(1)).save(c);
    }

    @Test
    void getComments_returnsFromDao() {
        ObjectId postId = new ObjectId();
        List<Comment> list = List.of(new Comment());
        commentDaoConstruction = mockConstruction(CommentDAO.class, (mock, ctx) -> {
            when(mock.findByPostId(postId)).thenReturn(list);
        });

        CommentService svc = new CommentService();
        List<Comment> out = svc.getComments(postId);
        assertEquals(list, out);
    }
}
