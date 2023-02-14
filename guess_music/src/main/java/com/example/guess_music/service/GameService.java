package com.example.guess_music.service;

import com.example.guess_music.domain.game.ChatRoom;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.game.User;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
public class GameService {
    public GameService(AnswerRepository answerRepository, GameRepository gameRepository) {
        this.answerRepository = answerRepository;
        this.gameRepository = gameRepository;
    }

    private final AnswerRepository answerRepository;
    private final GameRepository gameRepository;

    private Map<String, ChatRoom> chatRooms;
    private ArrayList<User> users;
    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
        users = new ArrayList<>();
    }

    public String getHint(String type,String roomId){
        ChatRoom room = this.findById(roomId);
        Long gameIndex= room.getGameIndex();
        Long seq=room.getSeq();
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
    public List<User> findAllUserByRoomId(String roomId) {
        List<User> result = users.stream().filter(target -> target.getRoomId().equals(roomId)).collect(Collectors.toList());
        return result;
    }
    public User findUserByUsername(String username) {
        Optional<User> result = users.stream().filter(target -> target.getName().equals(username)).findAny();
        if(result.isPresent())
            return result.get();
        else return null;
    }
    public void deleteUserByUsername(String username) {
        Iterator it=users.iterator();
        while(it.hasNext()){
            User next = (User) it.next();
            if(next.getName().equals(username)){
                it.remove();
                break;
            }
        }
    }
    //채팅방 생성
    public ChatRoom createRoom(Long gameIndex,String name,String ownerName) {
        Optional<Game> opt = gameRepository.findGameByGameIndex(gameIndex);
        Game game;
        if(opt.isPresent())
            game=opt.get();
        else return new ChatRoom();
        ChatRoom chatRoom = ChatRoom.create(gameIndex,name,game.getTitle(),game.getSongNum(),ownerName);
        chatRoom.setRoomUserNum(0);
        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        //create User(방장)
        //this.createUser(ownerName,chatRoom.getRoomId());

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

    public User createUser(String roomId, String username) {
        User user = User.create(username, roomId, 0L);
        users.add(user);
        return user;
    }
}
