package com.example.guess_music.controller;

import com.example.guess_music.domain.ChatMessage;
import com.example.guess_music.domain.ChatRoom;
import com.example.guess_music.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.SubscriptionRegistry;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Set;

@RestController
public class MessageController {
    private final SimpMessageSendingOperations sendingOperations;
    SubscriptionRegistry subscriptionRegistry;

    private final GameService gameService;
    @Autowired
    SimpUserRegistry simpUserRegistry;
    public MessageController(SimpMessageSendingOperations sendingOperations, GameService gameService) {
        this.sendingOperations = sendingOperations;
        this.gameService = gameService;
    }

    @MessageMapping("/Game/message")
    public void enter(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender()+"님이 입장하였습니다.");
        }
        if(ChatMessage.MessageType.SKIP.equals(message.getType())){
            message.setMessage("투표로 인해 노래가 스킵됩니다");
        }
        if(ChatMessage.MessageType.START.equals(message.getType())){
            ChatRoom room = gameService.findById(message.getRoomId());
            room.setRoomStatus("START");
        }
        sendingOperations.convertAndSend("/topic/room/"+message.getRoomId(),message);
//        System.out.println("called message controller");
//        this.find(message);
//        this.getUsers();
        String answerByRoomId = gameService.findAnswerByRoomId(message.getRoomId());
        if(message.getMessage().equals(answerByRoomId)) {
            message.setMessage(message.getSender()+"님이 정답을 맞추셨습니다!");
            message.setType(ChatMessage.MessageType.ANSWER);
            sendingOperations.convertAndSend("/topic/room/"+message.getRoomId(),message);
        }
    }
    public Set<SimpUser> getUsers(){
        System.out.println(simpUserRegistry.getUsers());
        System.out.println(simpUserRegistry.getUserCount());
        return simpUserRegistry.getUsers();
    }
    public void find(ChatMessage message){
        System.out.println("Find called");
        SimpSubscriptionMatcher matcher= new SimpSubscriptionMatcher() {
            @Override
            public boolean match(SimpSubscription subscription) {
                System.out.println("subscription:"+subscription);
                if(subscription.equals("/topic/room/"+message.getRoomId())){
                    return true;
                }
                return false;
            }
        };
        Set<SimpSubscription> subscriptions = simpUserRegistry.findSubscriptions(matcher);
        Iterator<SimpSubscription> iterator = subscriptions.iterator();
        while(iterator.hasNext()){
            System.out.println("in iterator");
            System.out.println(iterator.next());
        }
        System.out.println("find end");
    }
//    @MessageMapping("/Game/startGame")
//    public void start(ChatMessage message) {
//        sendingOperations.convertAndSend("/topic/room/"+message.getRoomId(),message);
//    }
}
