package com.example.guess_music.controller;

import com.example.guess_music.domain.ChatRoom;
import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.Result;
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
@RequestMapping("/Game")
public class GameController {
    private final GameService gameService;
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    @Autowired
    private HttpSession session;


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
//    @GetMapping("/gameStart")
//    public String startGame(@RequestParam(value = "gameIndex", required = false) Optional<Long> gameIdx){
//        //대기방에서 게임 시작 버튼을 누른 경우
//        if(gameIdx.isPresent()){
////            //세션에 저장한 seq는 게임 인덱스 값이 바뀌면 초기화 한다
////            if(gameIndex.equals(gameIdx.get())){
////                //직전에 진행하던 게임과 같은 게임을 선택한 경우 게임 진행을 이어간다
////            }else{
////                //게임 인덱스 값을 변경하고 seq값도 변경한다
////                session.setAttribute("seq",1);
////                gameIndex=gameIdx.get();
////            }
//            //해당 게임에 참가중인 참가자들의 세션 정보를 모두 불러와서 상태를 게임중으로 변경
//            //이거 좀 어려우려나?
//        }
//
//        return "game/intro";
//    }
    @GetMapping("/")
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
    public String getAnswer(@PathVariable String roomId){
         List<String> answerByRoomId = gameService.findAnswerByRoomId(roomId);
        if(answerByRoomId==null){
            return "False";
        }else{
            List<String> ans=(List<String>)answerByRoomId;
            return ans.get(0);
        }
    }
    @GetMapping("/checkAnswer")
    @ResponseBody
    public Result getGameAnswer(@RequestParam("target") String target){
        System.out.println("Got target : "+target);
        Long gameIndex= (Long) session.getAttribute("gameIndex");
        Long seq=(Long) session.getAttribute("seq");
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
        ChatRoom room = gameService.createRoom(gameIndex, roomName, (String) session.getAttribute("login"));
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
        String username = (String) session.getAttribute("login");
        return username;
    }
}
