package com.driver;

import java.util.*;

public class WhatsappRepository {

    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;


    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;

    }


    public String createUser(String name, String mobile)throws Exception{

        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"
        // otherwise , create new user and return success;


        if (userMobile.contains(mobile)) {
            throw new Exception("User already exists");
        }

        userMobile.add(mobile);

        User user = new User(name, mobile);

        return "SUCCESS";


    }

    public Group createGroup(List<User> users) {

        // The list contains at least 2 users where the first user is the admin. A group has exactly one admin.
        // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        // If there are 2+ users, the name of group should be "Group count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        // Note that a personal chat is not considered a group and the count is not updated for personal chats.
        // If group is successfully created, return group.

        //For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}.
        //If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.

        if (users.size() == 2) {

            Group group = new Group(users.get(1).getName(), 2);

            adminMap.put(group, users.get(0));
            groupUserMap.put(group, users);
            groupMessageMap.put(group, new ArrayList<>());
            return group;
        }

        this.customGroupCount += 1;

        Group group = new Group("Group" + this.customGroupCount, users.size());

        adminMap.put(group, users.get(0));
        groupUserMap.put(group, users);
        groupMessageMap.put(group, new ArrayList<>());
        return group;
    }


    public int createMessage(String content) {

        // The 'i^th' created message has message id 'i'.
        // Return the message id.

        this.messageId += 1;

        Message message = new Message(this.messageId, content);

        return message.getId();


    }

    public int sendMessage(Message message, User sender, Group group)throws Exception{

        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.


        if (adminMap.containsKey(group)) {

            List<User> usersList = groupUserMap.get(group);
            boolean flag = false;

            for (User user : usersList) {

                if (user.equals(sender)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {

                senderMap.put(message, sender);
                List<Message> messages = groupMessageMap.get(group);
                messages.add(message);
                groupMessageMap.put(group, messages);
                return messages.size();
            }
            throw new Exception("You are not allowed to send message");
        }
        throw new Exception("Group does not exist");

    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{

        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        if (adminMap.containsKey(group)) {


            if (adminMap.get(group).equals(approver)) {

                boolean isPresent = false;

                List<User> userList = groupUserMap.get(group);
                for (User user1 : userList) {

                    if (user1.equals(user)) {

                        isPresent = true;
                        break;
                    }
                }

                if (isPresent) {

                    adminMap.put(group, user);
                    return "SUCCESS";
                }
                throw new Exception("User is not a participant");
            }
            throw new Exception("Approver does not have rights");
        }

        throw new Exception("Group does not exist");
    }

    public int removeUser(User user) throws Exception{

        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

        //This is a bonus problem and does not contains any marks
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)


        boolean UserFound = false;
        Group usergroup = null;
        for (Group group : groupUserMap.keySet()) {

            List<User> participants = groupUserMap.get(group);
            for (User user1 : participants) {

                if (user1.equals(user)) {
                    if (adminMap.get(group).equals(user)) {
                        throw new Exception("Cannot remove admin");
                    }
                    usergroup = group;
                    UserFound = true;
                    break;
                }
            }

            if (UserFound) {
                break;
            }

        }

        if (UserFound) {


            userMobile.remove(user);

            List<User> usersList = groupUserMap.get(usergroup);

            List<User> updateduser = new ArrayList<>();

            for (User user1 : usersList) {

                if (user1.equals(user)) {
                    continue;
                }
                updateduser.add(user1);
            }

            groupUserMap.put(usergroup, updateduser);


            List<Message> messagesList = groupMessageMap.get(usergroup);
            List<Message> updatedMessages = new ArrayList<>();

            for (Message message : messagesList) {

                if (senderMap.get(message).equals(user)) {

                    continue;
                }
                updatedMessages.add(message);
            }

            groupMessageMap.put(usergroup, updatedMessages);


            HashMap<Message, User> updatesenderMap = new HashMap<>();

            for (Message message : senderMap.keySet()) {

                if (senderMap.get(message).equals(user)) {
                    continue;
                }

                updatesenderMap.put(message, user);
            }
            senderMap = updatesenderMap;
            return updateduser.size() + updatesenderMap.size() + updatesenderMap.size();
        }

        throw new Exception("User not found");


    }

    public String findMessage(Date start, Date end, int k) throws Exception{

        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception


        List<Message> messages = new ArrayList<>();

        for (Group group : groupMessageMap.keySet()) {

            messages.addAll(groupMessageMap.get(group));
        }

        List<Message> filteredMessages = new ArrayList<>();

        for (Message message : messages) {

            if (message.getTimestamp().after(start) && message.getTimestamp().before(end)) {

                filteredMessages.add(message);
            }
        }

        if (filteredMessages.size() > k) {

            throw new Exception("K is greater than the number of messages");
        }

        Collections.sort(filteredMessages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {

                return o2.getTimestamp().compareTo(o1.getTimestamp());

            }
        });

        return filteredMessages.get(k - 1).getContent();

    }
}
