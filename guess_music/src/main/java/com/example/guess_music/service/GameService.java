package com.example.guess_music.service;

import com.example.guess_music.domain.ChatRoom;
import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.Result;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
public class GameService {
    public GameService(AnswerRepository answerRepository, GameRepository gameRepository) {
        this.answerRepository = answerRepository;
        this.gameRepository = gameRepository;
    }

    private final AnswerRepository answerRepository;
    private final GameRepository gameRepository;

    private Map<String, ChatRoom> chatRooms;
    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }
    public Result getResult(String target, Long gameIndex, Long seq){
        Result result=new Result();
        Optional<List<String>> opt = answerRepository.findAnswerBySeq(gameIndex, seq);
        Optional<String> singerBySeq = answerRepository.findSingerBySeq(gameIndex, seq);
        if(opt.isPresent()){
            // gameIndex,seq에 맞는 answers가 존재하는 경우 해당 list의 nullable을 푼다
            List<String> answers=opt.get();
            result.setAnswer(answers.get(0));
            // 사용자가 입력한 target과 db에 존재하는 정답들을 비교하여 정답 여부를 리턴한다.
            Optional<String> answer= answers.stream().filter(ans -> ans.equals(target)).findAny();
            if(answer.isPresent()){
                result.setResult("Right");
            }else{
                result.setResult("Wrong");
            }
            if(singerBySeq.isPresent())
                result.setSinger(singerBySeq.get());
            return result;
        }
       //어쩌면 예외 핸들링 해야 할 부분..

        return new Result();
    }
    public Long getGameSize(Long gameIndex){
        //db에서 해당 게임의 인덱스를 가지고 게임 내의 노래 수를 가져와서 return하는 함수
        Optional<Game> opt = gameRepository.findGameByGameIndex(gameIndex);
        if(opt.isPresent())
            return opt.get().getSongNum();
        else return 0L;
    }

    public String getHint(String type,String roomId){
        ChatRoom room = this.findById(roomId);
        Long gameIndex= room.getGameIndex();
        Long seq=room.getSeq();
        System.out.println("in service get hint seq : "+seq);
        if(type.equals("singer")){
            Optional<String> opt = answerRepository.findSingerBySeq(gameIndex, seq);
            if(opt.isPresent())
                return opt.get();
            else
                return "Nothing";
        }
        if(type.equals("initial")){
            Optional<String> opt = answerRepository.findInitialBySeq(gameIndex, seq);
            if(opt.isPresent())
                return opt.get();
            else
                return "Nothing";

        }
        return "False type";
    }
    public List<Game> getGameList(){
        Optional<List<Game>> opt = gameRepository.findGameList();
        System.out.println("got list from repo");
        if(opt.isPresent())
            return opt.get();
        else return new ArrayList<Game>();
    }

    public List<ChatRoom> findAllRoom() {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoom> result = new ArrayList<>(chatRooms.values());
        Collections.reverse(result);

        return result;
    }

    //채팅방 하나 불러오기
    public ChatRoom findById(String roomId) {
        return chatRooms.get(roomId);
    }
    public void deleteById(String roomId) {
        chatRooms.remove(roomId);
    }
    private boolean checkRoom() {
        if(chatRooms.size()==0)
            return false;
        return true;

    }
    //채팅방 생성
    public ChatRoom createRoom(Long gameIndex,String name,String ownerName) {
        Optional<Game> opt = gameRepository.findGameByGameIndex(gameIndex);
        Game game;
        if(opt.isPresent())
            game=opt.get();
        else return new ChatRoom();
        System.out.println("song num is : "+game.getSongNum());
        ChatRoom chatRoom = ChatRoom.create(gameIndex,name,game.getTitle(),game.getSongNum(),ownerName);
        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public List<String> findAnswerByRoomId(String roomId){
        if(!checkRoom())
            return null;
        ChatRoom room = this.findById(roomId);
        Long gameIndex= room.getGameIndex();
        Long seq=room.getSeq();
        Optional<List<String>> opt = answerRepository.findAnswerBySeq(gameIndex,seq);
        if(opt.isPresent()){
            return opt.get();
        }
        else return null;
    }
}
