package com.example.guess_music.service;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.ChatRoom;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.game.User;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.repository.*;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock
    GameRepository gameRepository;
    @Mock
    private MusicRepository musicRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AnswerRepository answerRepository;
    @InjectMocks
    GameService gameService;

//    private Map<String, ChatRoom> chatRooms= new HashMap<>();
//    private ArrayList<User> users=new ArrayList<>();
//    @Before
//    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        MockitoAnnotations.initMocks(this);
//        Method postConstruct=GameService.class.getDeclaredMethod("init",null);
//        postConstruct.setAccessible(true);
//        postConstruct.invoke(gameService);
//    }
//    @AfterEach
//    public void afterEach() {
//        chatRooms.clear();
//        users.clear();
//    }
    @BeforeEach
    public void beforeEach() {
        gameService.testinit();
    }
    private Game getGame(){
        Game game = new Game();
        game.setGameIndex(999L);
        game.setSongNum(1L);
        game.setTitle("testTitle");
        return game;
    }
    private ChatRoom makeRoom(Game game){
        ChatRoom chatRoom = ChatRoom.create(game.getGameIndex(), "testTitle", game.getTitle(), game.getSongNum(), "testOwner");
        chatRoom.setRoomUserNum(0);
        //chatRooms.put(chatRoom.getRoomId(),chatRoom);
        return chatRoom;
    }
    private Music makeMusic(Game game){
        Music music=new Music();
        music.setName("testMusic");
        music.setGame(game);
        return music;
    }
    private Answers makeAnswer(Game game, Music music){
        Answers answers = new Answers();
        answers.setAnswer("testAnswer");
        answers.setInitial("testInitial");
        answers.setSinger("testSinger");
        answers.setSeq(1L);
        answers.setGameIndex(game);
        answers.setMusic(music);
        return answers;
    }
    private Member getMember() {
        Member member=new Member();
        member.setUsername("testUserName");
        member.setName("testName");
        member.setPassword("testPWD");
        member.setEmail("lyhxr@example.com");
        member.setRole(Role.ROLE_USER);

        return member;
    }
    @Test
    void createRoom() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        //when
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");
        //then
        assertThat(room.getGameIndex()).isEqualTo(game.getGameIndex());
    }

    @Test
    void getHint() {
        //given
        Game game = getGame();
        Music music = makeMusic(game);
        Answers answers = makeAnswer(game, music);

        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");

        given(answerRepository.findByIdxSeq(game.getGameIndex(),room.getSeq())).willReturn(Optional.of(answers));
        //when
        String singer = gameService.getHint("singer", room.getRoomId());
        String initial = gameService.getHint("initial", room.getRoomId());
        //then
        assertThat(singer).isEqualTo(answers.getSinger());
        assertThat(initial).isEqualTo(answers.getInitial());
    }

    @Test
    void findAllRoom() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");
        ChatRoom room2 = gameService.createRoom(game.getGameIndex(), "testTitle2", "testOwner2");
        //when
        List<ChatRoom> allRoom = gameService.findAllRoom();
        //then
        assertThat(allRoom.size()).isEqualTo(2);
        assertThat(allRoom.get(0).getRoomId()).isEqualTo(room2.getRoomId());
    }

    @Test
    void findAnswerByRoomId() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");
        Answers ans = makeAnswer(game, makeMusic(game));
        List<AnswerListMapping> ansList=new ArrayList<AnswerListMapping>();
        AnswerListMapping a= new AnswerListMapping() {
            private String answer;
            @Override
            public String getAnswer() {
                return answer;
            }

            @Override
            public void setAnswer(String answer) {
                this.answer=answer;
            }
        };
        a.setAnswer(ans.getAnswer());
        ansList.add(a);
        given(answerRepository.findAnswerListByGameIndexAndSeq(room.getGameIndex(), room.getSeq())).willReturn(Optional.of(ansList));
        //when
        List<AnswerListMapping> answerByRoomId = gameService.findAnswerByRoomId(room.getRoomId());
        //then
        assertThat(answerByRoomId.get(0).getAnswer()).isEqualTo(ans.getAnswer());
    }

    @Test
    void createUser() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");
        Member member = getMember();
        Member member1= getMember();
        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
        member1.setUsername("testUserName1");
        member1.setName("testName1");
        given(memberRepository.findByUsername(member1.getUsername())).willReturn(Optional.of(member1));
        //when
        User testUser = gameService.createUser(room.getRoomId(), member.getUsername());
        User testUser1 = gameService.createUser(room.getRoomId(), member1.getUsername());
        List<User> allUserByRoomId = gameService.findAllUserByRoomId(room.getRoomId());
        //then
        assertThat(testUser.getName()).isEqualTo(member.getName());
        assertThat(testUser1.getName()).isEqualTo(member1.getName());

        assertThat(allUserByRoomId.size()).isEqualTo(2);

    }

    @Test
    void findUserByUsername() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");
        Member member = getMember();
        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
        User testUser = gameService.createUser(room.getRoomId(), member.getUsername());
        //when
        User userByName = gameService.findUserByName(testUser.getName());
        //then
        assertThat(userByName.getName()).isEqualTo(testUser.getName());
    }

    @Test
    void deleteUserByUsername() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");
        Member member = getMember();
        Member member1= getMember();
        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
        member1.setUsername("testUserName1");
        member1.setName("testName1");
        given(memberRepository.findByUsername(member1.getUsername())).willReturn(Optional.of(member1));
        User testUser = gameService.createUser(room.getRoomId(), member.getUsername());
        User testUser1 = gameService.createUser(room.getRoomId(), member1.getUsername());
        //when
        gameService.deleteUserByName(testUser.getName());
        List<User> allUserByRoomId = gameService.findAllUserByRoomId(room.getRoomId());
        //then
        assertThat(allUserByRoomId.size()).isEqualTo(1);
        assertThat(allUserByRoomId.get(0).getName()).isEqualTo(testUser1.getName());
    }

    @Test
    void findMusicByRoomId() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        ChatRoom room = gameService.createRoom(game.getGameIndex(), "testTitle", "testOwner");
        Music music = makeMusic(game);
        Answers answers = makeAnswer(game, music);
        given(answerRepository.findByIdxSeq(room.getGameIndex(),room.getSeq())).willReturn(Optional.of(answers));
        given(musicRepository.findById(answers.getMusic().getId())).willReturn(Optional.of(music));
        //when
        Music musicByRoomId = gameService.findMusicByRoomId(room.getRoomId());
        //then
        assertThat(musicByRoomId.getId()).isEqualTo(answers.getMusic().getId());
        assertThat(musicByRoomId.getName()).isEqualTo(answers.getMusic().getName());
    }
}