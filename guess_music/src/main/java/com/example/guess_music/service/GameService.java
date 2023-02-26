package com.example.guess_music.service;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.ChatRoom;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.game.User;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Transactional
@Service
public class GameService {
    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AnswerRepository answerRepository;


    private Map<String, ChatRoom> chatRooms;
    private ArrayList<User> users;
    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        log.info("GameService init");
        chatRooms = new LinkedHashMap<>();
        users = new ArrayList<>();
    }
    public void testinit(){
        log.info("GameService TEST init");
        chatRooms = new LinkedHashMap<>();
        users = new ArrayList<>();
    }
    public String getHint(String type,String roomId){
        ChatRoom room = this.findById(roomId);
        Long gameIndex= room.getGameIndex();
        Long seq=room.getSeq();
        if(type.equals("singer")){
            Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq);
            if(opt.isPresent())
                return opt.get().getSinger();
            else{
                log.error("Cant find singer");
                return "Nothing";
            }
        }
        if(type.equals("initial")){
            Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq);
            if(opt.isPresent())
                return opt.get().getInitial();
            else{
                log.error("Cant find initial");
                return "Nothing";
            }

        }
        log.error("Invalid hint type");
        return "False type";
    }
    public List<Game> getGameList(){
        Optional<List<Game>> opt = Optional.of(gameRepository.findAll());
        if(opt.isPresent())
            return opt.get();
        else {
            log.error("Cant find game list");
            return new ArrayList<Game>();
        }
    }

    public List<ChatRoom> findAllRoom() {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoom> result = new ArrayList<>(chatRooms.values());
        Collections.reverse(result);

        return result;
    }

    //채팅방 하나 불러오기
    public ChatRoom findById(String roomId) {
        if(checkRoom())
            return chatRooms.get(roomId);
        else{
            log.error("No room in server memory");
            return new ChatRoom();
        }
    }
    public void deleteById(String roomId) {
        if(checkRoom())
            chatRooms.remove(roomId);
        else{
            log.error("No room in server memory");
        }
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
    public User findUserByName(String name) {
        Optional<User> result = users.stream().filter(target -> target.getName().equals(name)).findAny();
        if(result.isPresent())
            return result.get();
        else {
            log.error("No Playing user in server memory");
            return null;
        }
    }
    public void deleteUserByName(String name) {
        Iterator it=users.iterator();
        while(it.hasNext()){
            User next = (User) it.next();
            if(next.getName().equals(name)){
                it.remove();
                break;
            }
        }
    }
    //채팅방 생성
    public ChatRoom createRoom(Long gameIndex,String name,String ownerName) {
        Optional<Game> opt = gameRepository.findById(gameIndex);
        Game game;
        if(opt.isPresent())
            game=opt.get();
        else {
            log.error("Cannot find game in GameDB");
            return new ChatRoom();
        }
        ChatRoom chatRoom = ChatRoom.create(gameIndex,name,game.getTitle(),game.getSongNum(),ownerName);
        chatRoom.setRoomUserNum(0);
        chatRooms.put(chatRoom.getRoomId(), chatRoom);


        return chatRoom;
    }

    public List<AnswerListMapping> findAnswerByRoomId(String roomId){
        if(!checkRoom())
            return null;
        ChatRoom room = this.findById(roomId);
        Long gameIndex= room.getGameIndex();
        Long seq=room.getSeq();
        Optional<List<AnswerListMapping>> opt = answerRepository.findAnswerListByGameIndexAndSeq(gameIndex,seq);
        if(opt.isPresent()){
            return opt.get();
        }
        else {
            log.error("Cant find answer in Answer DB");
            return null;
        }
    }
    public User createUser(String roomId, String username) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if(member.isPresent()){
            User user = User.create(member.get().getName(), roomId, 0L);
            users.add(user);
            return user;
        }else{
            log.error("Cannot find user in Member DB");
            return null;
        }
    }

    public Member findMemberByUsername(String username){
        Optional<Member> member = memberRepository.findByUsername(username);
        if(member.isPresent())
            return member.get();
        else return null;
    }

    public Music findMusicByRoomId(String roomId){
        ChatRoom room = this.findById(roomId);
        Long gameIndex= room.getGameIndex();
        Long seq=room.getSeq();
        log.info("gameIndex: "+gameIndex+" seq: "+seq);
        Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq);
        if(opt.isPresent()){
            Optional<Music> byId = musicRepository.findById(opt.get().getMusic().getId());
            if(byId.isPresent())
                return byId.get();
        }
        return null;
    }
}
