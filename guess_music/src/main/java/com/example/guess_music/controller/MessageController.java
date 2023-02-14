package com.example.guess_music.controller;

import com.example.guess_music.domain.ChatMessage;
import com.example.guess_music.domain.ChatRoom;
import com.example.guess_music.domain.User;
import com.example.guess_music.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.SubscriptionRegistry;
import org.springframework.messaging.simp.user.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
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
        ChatRoom room = gameService.findById(message.getRoomId());
        if(room == null) {return;}
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            //입장 시 해당 방 유저 수 증가
            room.setRoomUserNum(room.getRoomUserNum() + 1);
            message.setMessage(message.getSender()+"님이 입장하였습니다.");
        }
        if(ChatMessage.MessageType.VOTE.equals(message.getType())){
            message.setMessage("누군가가 스킵에 투표하였습니다");
        }
        if(ChatMessage.MessageType.START.equals(message.getType())){
            room.setRoomStatus("START");
            //게임 시작 시 해당 방의 구독자들의 User객체를 모두 생성하고 (임시로)인메모리 List에 저장
            Set<SimpSubscription> subs = this.findSub(message.getRoomId());
            for (SimpSubscription sub: subs) {
                String name = sub.getSession().getUser().getName();
                gameService.createUser(message.getRoomId(), name);
            }
        }
        if(ChatMessage.MessageType.LEAVE.equals(message.getType())){
            if(room.getRoomStatus().equals("START")){
                //유저 퇴장 시 해당 User객체 삭제
                gameService.deleteUserByUsername(message.getSender());
            }
            //게임 방의 유저 수 --
            room.setRoomUserNum(room.getRoomUserNum() - 1);
            if(room.getRoomUserNum() == 0){
                //아무도 없으면 방 삭제
                gameService.deleteById(message.getRoomId());
                return;
            }
            if(room.getOwnerName().equals(message.getSender())){
                //방장이 나간 경우 방장이 아닌 다른 사람을 새 방장으로 임명한다.
                Set<SimpSubscription> subs = this.findSub(message.getRoomId());
                for (SimpSubscription sub: subs) {
                    String name = sub.getSession().getUser().getName();
                    if(!name.equals(message.getSender())){
                        room.setOwnerName(name);
                    }
                }
            }
            message.setMessage(message.getSender()+"님이 퇴장하셨습니다.");
        }
        sendingOperations.convertAndSend("/topic/room/"+message.getRoomId(),message);

        List<String> answers = gameService.findAnswerByRoomId(message.getRoomId());
        if(answers== null){
            return;
        }

        if(answers.stream().filter(ans->ans.equals(message.getMessage())).findAny().isPresent()) {
            //정답자 User객체 찾아서 score++
            User userByUsername = gameService.findUserByUsername(message.getSender());
            userByUsername.setScore(userByUsername.getScore() + 1);
            //정답 메세지 설정 및 전송
            message.setMessage(message.getSender()+"님이 정답을 맞추셨습니다!");
            message.setType(ChatMessage.MessageType.ANSWER);
            sendingOperations.convertAndSend("/topic/room/"+message.getRoomId(),message);
        }

        //principal.getName();

    }

    @GetMapping("/Game/getUserCount/{roomId}")
    public int subNum(@PathVariable String roomId){
        return this.findSub(roomId).size();
    }

    @GetMapping("/Game/findSub/{roomId}")
    public void findUsers(@PathVariable String roomId){

    }
    private Set<SimpSubscription> findSub(String roomId){
        SimpSubscriptionMatcher matcher= new SimpSubscriptionMatcher() {
            @Override
            public boolean match(SimpSubscription subscription) {
                String target="/topic/room/"+roomId;
                if(subscription.getDestination().equals(target)){
                    return true;
                }
                return false;
            }
        };
        Set<SimpSubscription> subscriptions = simpUserRegistry.findSubscriptions(matcher);

        return subscriptions;
    }
}
