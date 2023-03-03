package com.example.guess_music.controller;

import com.example.guess_music.domain.game.ChatRoom;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.service.GameService;
import com.example.guess_music.service.ManagerService;
import com.example.guess_music.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
@Transactional
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameService gameService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private  ManagerService managerService;
    private MockHttpSession session;
    private Long gameIndex;
    private String roomId;
    @BeforeEach
    public void beforeEach() throws IOException {
        makeGame();
        addSongToGame();
        session=new MockHttpSession();
        session.setAttribute("name","user");
        makeRoom();
    }
    @AfterEach
    public void afterEach(){
        session.invalidate();
    }

    Long makeGame(){
        Game game=new Game();
        game.setTitle("testGameTitle");
        Long index = managerService.join(game);
        gameIndex=index;
        return index;
    }
    void addSongToGame() throws IOException {
        String contentType = "audio/mpeg";
        String filePath = "testMP3.mp3";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("mp3","testMP3.mp3",contentType,filePath.getBytes());
        Music music = managerService.storeMusic(mockMultipartFile, gameIndex);

        List<String> ansList=new ArrayList<>();
        ansList.add("testAnswer");
        ansList.add("testAnswer2");
        managerService.storeAnswers(ansList,"testSinger","testInitial",gameIndex,music);
    }
    void makeRoom(){
        ChatRoom room = gameService.createRoom(gameIndex, "testRoom", "user");
        room.setRoomStatus("WAITING");
        roomId=room.getRoomId();
    }
    @Test
    void 게임목록불러오기() throws Exception{
        //뭐 따로 검증하기 힘들 것 같음.. 게임 DB가 비워져있다는 보장이 없으니..
        //일단 그냥 0번째 게임이 불러와 졌으면 ok로 ..
        MvcResult mvcResult = mockMvc.perform(get("/Game/gameList")).andDo(print())
                .andExpect(jsonPath("$[0]").exists())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        System.out.println(contentAsString);
    }
    @Test
    void 방만들기() throws Exception{
        MvcResult mvcResult = mockMvc.perform(post("/Game/createRoom").param("name", "testRoom").param("gameIndex", gameIndex.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        System.out.println(contentAsString);

    }
    @Test
    void 방목록불러오기() throws Exception{
        //일단 admin은 무조건 존재한다는 가정 하에 admin으로
        session.setAttribute("username","admin");
        mockMvc.perform(get("/Game/roomList").session(session)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("game/roomList"));
    }
    @Test
    void 힌트받기() throws Exception{
        mockMvc.perform(get("/Game/hint").param("roomId",roomId).param("type","singer"))
                .andDo(print())
                .andExpect(content().string("testSinger"))
        ;
        mockMvc.perform(get("/Game/hint").param("roomId",roomId).param("type","initial"))
                .andDo(print())
                .andExpect(content().string("testInitial"))
        ;
    }
    @Test
    void 스킵하기() throws Exception {
        ChatRoom byId = gameService.findById(roomId);
        Long before=byId.getSeq();
        mockMvc.perform(get("/Game/skip").param("roomId",roomId).param("type","owner"))
                .andDo(print())
                .andExpect(status().isOk());
        ChatRoom after = gameService.findById(roomId);
        assertThat(after.getSeq()).isEqualTo(before+1);

    }
    @Test
    void 정답받기() throws Exception{
        mockMvc.perform(get("/Game/getAnswer/" + roomId)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("testAnswer"))
                .andExpect(jsonPath("$[1]").value("testSinger"));
    }
    @Test
    void 방입장하기() throws Exception{
        mockMvc.perform(get("/Game/gameRoom/"+roomId)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("game/gameRoom"));
    }
    @Test
    void 방삭제하기() throws Exception{
        mockMvc.perform(post("/Game/deleteRoom/"+roomId)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
        assertThat(gameService.findById(roomId)).isNull();
    }
    @Test
    void 사용자이름찾기() throws Exception{
        mockMvc.perform(get("/Game/getUser").session(session)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("user"));
    }
    @Test
    void 방내의사용자찾기() throws Exception{
        gameService.createUser(roomId,"admin");
        mockMvc.perform(get("/Game/getUsers/"+roomId)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("admin"));
    }

}