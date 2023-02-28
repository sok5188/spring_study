package com.example.guess_music.controller;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.game.ChatRoom;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.game.User;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.repository.AnswerListMapping;
import com.example.guess_music.service.GameService;
import com.example.guess_music.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@Slf4j
@Controller
@RequestMapping("/Game")
public class GameController {
    private final GameService gameService;
    private final MemberService memberService;
    @Autowired
    public GameController(GameService gameService, MemberService memberService) {
        this.gameService = gameService;
        this.memberService = memberService;
    }
    @Autowired
    private HttpSession session;
    @Autowired
    SimpUserRegistry simpUserRegistry;


    @ResponseBody
    @GetMapping("/gameList")
    public List<Game> gameList(){
        List<Game> gameList = gameService.getGameList();

        if(gameList.isEmpty()){
            log.warn("Game List is empty..");
        }
        return gameList;
    }

    @GetMapping("/hint")
    @ResponseBody
    public String Hint(@RequestParam Map<String,Object>map){
        String roomId = (String) map.get("roomId");
        String type= (String) map.get("type");
        log.info("hint type:"+type+"roomId:"+roomId);
        return gameService.getHint(type,roomId);
    }

    @GetMapping("/skip")
    @ResponseBody
    public Long skipGame(@RequestParam Map<String,Object>map){
        String roomId = (String) map.get("roomId");
        String type= (String) map.get("type");
        ChatRoom room = gameService.findById(roomId);
        log.info("skip type:"+type+"roomId:"+roomId);
        if(type.equals("owner")){
            log.info("you  are  Owner so room seq will be increase");
            room.setSeq(room.getSeq()+1);
            log.info("Skipped Song in Room:"+roomId+"room's game:"+room.getGameTitle()+ "Now Sequence:"+room.getSeq());
        }

        return room.getSeq();
    }

    @GetMapping("/getAnswer/{roomId}")
    @ResponseBody
    public List<String> getAnswer(@PathVariable String roomId){
         List<AnswerListMapping> answerByRoomId = gameService.findAnswerByRoomId(roomId);
        String singer = gameService.getHint("singer", roomId);
        if(answerByRoomId!=null&&singer!=null){
            List<String> result=new ArrayList<>();
            result.add(answerByRoomId.get(0).getAnswer());
            result.add(singer);
            return result;
        }else{
            log.error("can't find answer by room id:"+roomId);
            return Collections.singletonList("False");
        }
    }


    @GetMapping("/roomList")
    public String Room(){
        //세션에 저장했던 username으로 멤버를 찾아 nmae을 설정
        Optional<Member> opt = memberService.findOne(String.valueOf(session.getAttribute("username")));
        if(opt.isPresent()){
            Member member = opt.get();
            session.setAttribute("name", member.getName());
        }
        else {
            log.error("can't find member from session");
            return "invalid Member";
        }
        return "game/roomList";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return gameService.findAllRoom();
    }

//    @GetMapping("/createRoom")
//    public String createRoom(){
//        return "game/createRoom";
//    }

    @GetMapping("/test")
    public String testGame(){
        System.out.println(session.getAttribute("username"));
        return "test";
    }

    @PostMapping("/createRoom")
    @ResponseBody
    public String createRooms(@RequestParam Map<String,Object>map){
        String roomName= (String) map.get("name");
        Long gameIndex = Long.parseLong((String) map.get("gameIndex"));
        ChatRoom room = gameService.createRoom(gameIndex, roomName, String.valueOf( session.getAttribute("name")));
        room.setRoomStatus("WAITING");
        return room.getRoomId();
    }

    @GetMapping("/gameRoom/{roomId}")
    public String gameRoom(Model model, @PathVariable String roomId){
        model.addAttribute("roomId", roomId);
        return "game/gameRoom";
    }
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return gameService.findById(roomId);
    }

    @PostMapping("/deleteRoom/{roomId}")
    @ResponseBody
    public String deleteRoom(@PathVariable String roomId){
        gameService.deleteById(roomId);
        return "success";
    }
    @GetMapping("/getUser")
    @ResponseBody
    public String getUser(){
        String username = String.valueOf( session.getAttribute("name"));
        if(username==null){
            log.error("can't find user's name from session");
        }
        return username;
    }

    @GetMapping("/getUsers/{roomId}")
    @ResponseBody
    public List<User> getUserList(@PathVariable String roomId){
        return gameService.findAllUserByRoomId(roomId);
    }

    @GetMapping("/getMusic/{roomId}")
    public ResponseEntity<byte[]> getMusic(@PathVariable String roomId){
        Music music = gameService.findMusicByRoomId(roomId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(music.getData());
    }



}
