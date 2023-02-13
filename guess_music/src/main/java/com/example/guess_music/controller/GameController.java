package com.example.guess_music.controller;

import com.example.guess_music.domain.ChatRoom;
import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.Result;
import com.example.guess_music.domain.User;
import com.example.guess_music.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Game")
public class GameController {
    private final GameService gameService;
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    @Autowired
    private HttpSession session;
    @Autowired
    SimpUserRegistry simpUserRegistry;


    @ResponseBody
    @GetMapping("/gameList")
    public List<Game> gameList(){
        List<Game> gameList = gameService.getGameList();

        for (Game g :
                gameList) {
            System.out.println(g.getGameIndex()+" / "+g.getTitle()+" / "+g.getSongNum());
        }
        if(gameList.isEmpty()){
            //??
        }
        return gameList;
    }

    @PostMapping("/skip/{roomId}")
    @ResponseBody
    public String skipGame(@PathVariable String roomId){
        ChatRoom room = gameService.findById(roomId);
        room.setSeq(room.getSeq()+1);
        System.out.println("Now Room:"+room.getGameTitle()+" / "+room.getSeq());
        return "success";
    }

    @GetMapping("/getAnswer/{roomId}")
    @ResponseBody
    public List<String> getAnswer(@PathVariable String roomId){
         List<String> answerByRoomId = gameService.findAnswerByRoomId(roomId);
        String singer = gameService.getHint("singer", roomId);
        if(answerByRoomId!=null&&singer!=null){
            List<String> ans=(List<String>)answerByRoomId;
            List<String> result=new ArrayList<>();
            result.add(ans.get(0));
            result.add(singer);
            return result;
        }else{
            return Collections.singletonList("False");
        }
    }


    @GetMapping("/hint")
    @ResponseBody
    public String Hint(@RequestParam Map<String,Object>map){
        String roomId = (String) map.get("roomId");
        String type= (String) map.get("type");
        System.out.println("at controller in Hint : "+roomId+ " / "+type);
        return gameService.getHint(type,roomId);
    }

    @GetMapping("/roomList")
    public String Room(){

        System.out.println("return room list html");
        return "game/roomList";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return gameService.findAllRoom();
    }

    @GetMapping("/createRoom")
    public String createRoom(){
        return "game/createRoom";
    }

    @PostMapping("/createRoom")
    @ResponseBody
    public String createRooms(@RequestParam Map<String,Object>map){
        String roomName= (String) map.get("name");
        Long gameIndex = Long.parseLong((String) map.get("gameIndex"));
        ChatRoom room = gameService.createRoom(gameIndex, roomName, (String) session.getAttribute("name"));
        room.setRoomStatus("WAITING");
        return room.getRoomId();
    }

    @GetMapping("/waitingRoom/{roomId}")
    public String waitingRoom(Model model, @PathVariable String roomId){
        model.addAttribute("roomId", roomId);
        return "game/waitingRoom";
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
        String username = (String) session.getAttribute("name");
        return username;
    }

    @GetMapping("/getUsers/{roomId}")
    @ResponseBody
    public List<User> getUserList(@PathVariable String roomId){
        return gameService.findAllUserByRoomId(roomId);
    }

    @GetMapping("/session")
    @ResponseBody
    public String getSession(){
        System.out.println("session : "+session.getId() + " / "+ session.getServletContext().getContextPath());
        String name = (String) session.getAttribute("name");
        System.out.println("name ! "+name);
        return session.toString();
    }


}
