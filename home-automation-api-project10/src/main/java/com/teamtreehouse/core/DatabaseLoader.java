package com.teamtreehouse.core;

import com.teamtreehouse.control.Control;
import com.teamtreehouse.device.Device;
import com.teamtreehouse.room.Room;
import com.teamtreehouse.room.RoomRepository;
import com.teamtreehouse.user.User;
import com.teamtreehouse.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseLoader implements ApplicationRunner {
    private final RoomRepository rooms;
    private final UserRepository users;

    @Autowired
    public DatabaseLoader(RoomRepository rooms, UserRepository users) {
        this.rooms = rooms;
        this.users = users;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<User> administrators = Arrays.asList(
                new User("admin", "password",
                        new String[]{"ROLE_ADMIN", "ROLE_USER"}),
                new User("user1", "pass1", new String[]{"ROLE_USER"}),
                new User("user2", "pass2", new String[]{"ROLE_USER"})
        );
        users.saveAll(administrators);

        List<String> administratorNames = new ArrayList<>();
        for (User administrator : administrators) {
            administratorNames.add(administrator.getName());
        }

        List<String> inputList = new ArrayList<>();
        // Prefix: r = room, d = device, c = control
        // inputList string: rName, rArea, dName, cName, cValue, cLastModifiedBy, rAdministrator(s)
        inputList.add("Living_room  300     lighting    timer   10  admin       admin");
        inputList.add("Bedroom      200     sound       level   2   user1       admin user1");
        inputList.add("Kitchen      100     climate     level   1   user2       admin user2");

        for (String inputValues : inputList) {

            String[] inputTokens = inputValues.split(" +");
            String roomName = inputTokens[0].replace("_", " ");
            int roomArea = Integer.parseInt(inputTokens[1]);
            String deviceName = inputTokens[2];
            String controlName = inputTokens[3];
            int controlValue = Integer.parseInt(inputTokens[4]);

            String modifyingUserName = inputTokens[5];
            int modifyingUserIndex = administratorNames.indexOf(modifyingUserName);
            User modifyingUser = administrators.get(modifyingUserIndex);

            User controlLastModifiedBy = modifyingUser;

            Room room = new Room(roomName, roomArea);
            Device device = new Device(deviceName);
            Control control = new Control(controlName, controlValue, controlLastModifiedBy);

            room.addDevice(device);
            device.addControl(control);

            for (int i = 6; i < inputTokens.length; i++) {

                String administratorName = inputTokens[i];
                int administratorIndex = administratorNames.indexOf(administratorName);
                User administrator = administrators.get(administratorIndex);

                room.addAdministrators(administrator);
            }
            rooms.save(room);
        }
    }
}
