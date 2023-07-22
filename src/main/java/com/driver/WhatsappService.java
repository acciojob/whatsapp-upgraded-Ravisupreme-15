package com.driver;


import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepository whatsappRepository = new WhatsappRepository();
    public String createUser(String name, String mobile)throws Exception {


        return  whatsappRepository.createUser(name,mobile);
    }

    public Group createGroup(List<User> users) {

        return whatsappRepository.createGroup(users);
    }

    public int createMessage(String content) {


        return whatsappRepository.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {

        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.

         return  whatsappRepository.sendMessage(message,sender,group);
    }

    public String changeAdmin(User approver, User user, Group group)throws Exception{


        return whatsappRepository.changeAdmin(approver,user,group);
    }

    public int removeUser(User user) throws Exception{

        return whatsappRepository.removeUser(user);
    }

    public String findMessage(Date start, Date end, int k)throws Exception{
        return whatsappRepository.findMessage(start,end,k);
    }
}
