package com.example.guess_music.controller;

import com.example.guess_music.domain.ChatRoom;
import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.Result;
import com.example.guess_music.domain.Room;
import com.example.guess_music.service.ChatService;
import com.example.guess_music.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class GameController {
    private final GameService gameService;
    private final ChatService chatService;
    @Autowired
    public GameController(GameService gameService, ChatService chatService) {
        this.gameService = gameService;
        this.chatService = chatService;
    }
    @Autowired
    private HttpSession session;
    @GetMapping("/select")
    public String selectGame(){
        return "game/select";
    }


    @ResponseBody
    @GetMapping("/gameList")
    public List<Game> gameList(){
        List<Game> gameList = gameService.getGameList();
        System.out.println("got list from service : "+gameList);
        for (Game g :
                gameList) {
            System.out.println(g.getGameIndex()+" / "+g.getTitle()+" / "+g.getSongNum());
        }
        if(gameList.isEmpty()){
            //??
        }
        return gameList;
    }
    @GetMapping("/gameStart")
    public String startGame(@RequestParam(value = "gameIndex", required = false) Optional<Long> gameIdx){
        //대기방에서 게임 시작 버튼을 누른 경우
        if(gameIdx.isPresent()){
//            //세션에 저장한 seq는 게임 인덱스 값이 바뀌면 초기화 한다
//            if(gameIndex.equals(gameIdx.get())){
//                //직전에 진행하던 게임과 같은 게임을 선택한 경우 게임 진행을 이어간다
//            }else{
//                //게임 인덱스 값을 변경하고 seq값도 변경한다
//                session.setAttribute("seq",1);
//                gameIndex=gameIdx.get();
//            }
            //해당 게임에 참가중인 참가자들의 세션 정보를 모두 불러와서 상태를 게임중으로 변경
            //이거 좀 어려우려나?
        }

        return "game/intro";
    }
    @GetMapping("/Game")
    public String createGame(Model model){
        int seq;
        Long gameIndex;
        if(session.getAttribute("seq")!=null){
            seq=(int)session.getAttribute("seq");
        }else {
            //세션에 seq정보가 없는 경우
            session.setAttribute("seq",1);
            seq=1;
        }
        if(session.getAttribute("gameIndex")!=null)
            gameIndex= (Long) session.getAttribute("gameIndex");
        else return "False";
        String music=gameIndex+"-"+seq;
        Long gameSize = gameService.getGameSize(gameIndex);
        System.out.println("now target music is : "+music+"and game index,seq : "+gameIndex+" / "+seq);
        model.addAttribute("music",music).addAttribute("remainSong",gameSize-seq+1).addAttribute("totalSong",gameSize);

        return "game/Game";
    }

    @GetMapping("/Game/checkAnswer")
    @ResponseBody
    public Result getGameAnswer(@RequestParam("target") String target){
        System.out.println("Got target : "+target);
        Long gameIndex= (Long) session.getAttribute("gameIndex");
        int seq=(int) session.getAttribute("seq");
        Result result=new Result();
        Long gameSize = gameService.getGameSize(gameIndex);
        Result results=gameService.getResult(target,gameIndex,seq);
        result.setAnswer(results.getAnswer());
        result.setSinger(results.getSinger());
        if(target.equals("skip")|| results.getResult() =="Right"){
            //score 처리 부분 만들어야 함
            //if(results.getResult()=="Right")

            session.setAttribute("seq",++seq);
            if(seq>gameSize) {
                result.setResult("Game End");
                session.removeAttribute("seq");
//                session.setAttribute("seq",seq);
            }else{
                result.setResult("Next Song");
            }
        }else{
            result.setResult("Wrong Answer");
        }

        return result;
    }

    @GetMapping("/Game/hint")
    @ResponseBody
    public String Hint(@RequestParam("type") String type){
        Long gameIndex=(Long) session.getAttribute("gameIndex");
        int seq= (int) session.getAttribute("seq");
        System.out.println("at controller in Hint : "+gameService.getHint(type,gameIndex,seq));
        return gameService.getHint(type,gameIndex,seq);
    }

    @GetMapping("/roomList")
    public String Room(){

        System.out.println("return room list html");
        return "game/RoomList";
    }

    @GetMapping("/Game/rooms")
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
        ChatRoom room = gameService.createRoom(gameIndex, roomName);
        System.out.println("room"+room);
        return room.getRoomId();
    }

    @GetMapping("/Game/waitingRoom/{roomId}")
    public String waitingRoom(Model model, @PathVariable String roomId){
        model.addAttribute("roomId", roomId);
        return "game/waitingRoom";
    }
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return gameService.findById(roomId);
    }
}
