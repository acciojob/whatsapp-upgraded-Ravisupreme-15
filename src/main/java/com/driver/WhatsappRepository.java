package com.driver;

import java.util.*;

public class WhatsappRepository {

    // to track the no of group
    private  int userGropuCount;

    // to track the no of messages
    private  int messageId;

    //  users mobile no --->  mobile-user
    private HashMap<String , User> userHashMap;


    // admin map
   private  HashMap<Group,User> adminHasMap;



    // group map  ---> group - userList

    private HashMap<Group,List<User>> groupuserHashMap;



    // group messsage

    private HashMap<Group,List<Message>> groupmessageHashMap;


    // sender map message-user

    private HashMap<Message, User> senderMap;


    public WhatsappRepository(){
         this.userGropuCount =0;
         this.messageId = 0;
         this.userHashMap = new HashMap<>();
         this.adminHasMap = new HashMap<>();
         this.groupuserHashMap = new HashMap<>();
         this.groupmessageHashMap = new HashMap<>();
         this.senderMap  = new HashMap<>();


    }


    public String createUser(String name, String mobile)throws Exception{

        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"

        if(userHashMap.containsKey(mobile)){
              throw new Exception("User already exists");
          }
        userHashMap.put(mobile,new User(name,mobile));

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

         if(users.size()==2){

               Group group = new Group(users.get(1).getName(),2);


               adminHasMap.put(group, users.get(0));
               groupuserHashMap.put(group,users);
               groupmessageHashMap.put(group,new ArrayList<Message>());
               return group;
         }

         this.userGropuCount+=1;

         Group group = new Group("Group "+this.userGropuCount,users.size());
         adminHasMap.put(group,users.get(0));
         groupuserHashMap.put(group,users);
         groupmessageHashMap.put(group,new ArrayList<Message>());
         return group;
    }

    public int createMessage(String content) {

        // The 'i^th' created message has message id 'i'.
        // Return the message id.

        this.messageId+=1;

        Message message = new Message(messageId,content);

        return  message.getId();


    }

    public int sendMessage(Message message, User sender, Group group)throws Exception{

        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.

         // first check the group

        if(groupuserHashMap.containsKey(group)){

              boolean userFound= false;
             List<User> users = groupuserHashMap.get(group);
             for (User user:users){

                  if(sender.equals(user.getName())){

                       userFound = true;
                       break;
                  }
             }

             if (userFound){

                 senderMap.put(message,sender);
                 List<Message>  messages =  groupmessageHashMap.get(group);

                 messages.add(message);

                 groupmessageHashMap.put(group,messages);

                 return messages.size();
             }
             else{
                 throw new Exception("You are not allowed to send message");
             }
        }
        else{
            throw new Exception("Group does not exist");
        }
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{

        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

         if(groupuserHashMap.containsKey(group)){

            if(adminHasMap.get(group).equals(approver)){


                  boolean isPresent = false;

                  List<User> usersList = groupuserHashMap.get(group);

                  for(User user1: usersList){

                      if(user.equals(user1.getName())){

                            isPresent = true;
                            break;
                      }
                  }
                  if(isPresent){

                      adminHasMap.put(group,user);
                      return "SUCCESS";
                  }
                  else {
                      throw new Exception("User is not a participant");
                  }
            }
            else throw new Exception("Approver does not have rights");
         }
         else throw new Exception("Group does not exist");
    }

    public int removeUser(User user) throws Exception{

        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

        boolean userFound = false;
        Group userGroup = null;

        for (Group group : groupuserHashMap.keySet()){

            List<User> paricipants = groupuserHashMap.get(group);

              for(User participant : paricipants){

                  if(participant.equals(user)) {
                      if (adminHasMap.get(group).equals(participant)) {

                          throw new Exception("Cannot remove admin");
                      }
                      userGroup = group;
                      userFound = true;
                      break;
                  }
              }
              if(userFound){
                  break;
              }
        }


        if(userFound){
            List<User> users = groupuserHashMap.get(userGroup);
             List<User> upadatedUsers = new ArrayList<>();

             for(User participant: users){

                   if(participant.equals(user)){
                       continue;
                   }
                   upadatedUsers.add(participant);
             }

             groupuserHashMap.put(userGroup,upadatedUsers);

             List<Message> messages  = groupmessageHashMap.get(userGroup);
             List<Message> updatedmessage = new ArrayList<>();

             for(Message message: messages){

                   if(senderMap.get(message).equals(user)){

                         continue;
                   }
                   updatedmessage.add(message);
             }

             groupmessageHashMap.put(userGroup,updatedmessage);

             HashMap<Message,User> updatedSenderMap = new HashMap<>();

             for (Message message: senderMap.keySet()){

                  if(senderMap.get(message).equals(user)){
                      continue;
                  }

                  updatedSenderMap.put(message,senderMap.get(message));
             }
             senderMap = updatedSenderMap;

             return upadatedUsers.size()+updatedmessage.size()+updatedSenderMap.size();
        }
       else throw new Exception("User not found");
    }

    public String findMessage(Date start, Date end, int k) throws Exception{

        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception


        List<Message> messages = new ArrayList<>();

        for (Group group : groupmessageHashMap.keySet()) {

            messages.addAll(groupmessageHashMap.get(group));
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
