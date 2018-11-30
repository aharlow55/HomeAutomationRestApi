package com.teamtreehouse.room;

import com.teamtreehouse.user.User;
import com.teamtreehouse.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RepositoryEventHandler(Room.class)
public class RoomEventHandler {

    private final UserRepository users;

    @Autowired
    public RoomEventHandler(UserRepository users) {
        this.users = users;
    }

    @HandleBeforeCreate
    public void allowOnlyAdminsToCreateRooms(Room room) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = users.findByName(name);
        if (Arrays.asList(user.getRoles()).contains("ROLE_ADMIN")) {
            room.addAdministrators(user);
        } else {
            throw new AccessDeniedException("Only administrators can create rooms");
        }
    }
}