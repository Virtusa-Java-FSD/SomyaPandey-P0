package org.example.service;

import org.example.dao.ProfileDAO;
import org.example.model.Profile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private MockedConstruction<ProfileDAO> profileDaoConstruction;

    @AfterEach
    void tearDown() {
        if (profileDaoConstruction != null) profileDaoConstruction.close();
    }

    @Test
    void createProfile_returnsFromDao() throws Exception {
        Profile profile = new Profile();
        profileDaoConstruction = mockConstruction(ProfileDAO.class, (mock, ctx) -> {
            when(mock.save(profile)).thenReturn(true);
        });

        ProfileService svc = new ProfileService();
        boolean out = svc.createProfile(profile);
        assertTrue(out);
        ProfileDAO mockDao = profileDaoConstruction.constructed().get(0);
        verify(mockDao, times(1)).save(profile);
    }

    @Test
    void getProfileByUserId_returnsFromDao() throws Exception {
        Profile profile = new Profile();
        profileDaoConstruction = mockConstruction(ProfileDAO.class, (mock, ctx) -> {
            when(mock.findByUserId(7)).thenReturn(profile);
        });

        ProfileService svc = new ProfileService();
        Profile out = svc.getProfileByUserId(7);
        assertEquals(profile, out);
    }
}
