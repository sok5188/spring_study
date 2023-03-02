package com.example.guess_music.controller;

import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.domain.manage.SaveSongForm;
import com.example.guess_music.repository.AnswerListMapping;
import com.example.guess_music.service.ManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@Transactional
class ManagerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ManagerService managerService;

    Long makeGame(){
        Game game=new Game();
        game.setTitle("testGameTitle");
        Long index = managerService.join(game);
        return index;
    }
    @Nested
    @DisplayName("API Test")
    class apiTest{
        @Test
        void 게임삭제() throws Exception {
            Long gameIndex = makeGame();
            mockMvc.perform(delete("/manage").param("gameIndex", String.valueOf(gameIndex)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Success"));

        }
        @Test
        void 게임수정_제목변경() throws Exception{
            Long gameIndex = makeGame();
            mockMvc.perform(post("/manage/modifyGame").param("gameIndex",gameIndex.toString()).param("newTitle","test"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Success"));
        }
        @Test
        void 노래삭제() throws Exception{
            Long gameIndex = makeGame();

            String contentType = "audio/mpeg";
            String filePath = "testMP3.mp3";
            MockMultipartFile mockMultipartFile = new MockMultipartFile("mp3","testMP3.mp3",contentType,filePath.getBytes());
            System.out.println("before store music:"+managerService.getMusicNum(gameIndex));
            Music music = managerService.storeMusic(mockMultipartFile, gameIndex);
            System.out.println("after store music:"+managerService.getMusicNum(gameIndex));

            List<String> ansList=new ArrayList<>();
            ansList.add("test1");
            ansList.add("test2");
            Long maxSeq = managerService.storeAnswers(ansList, "testSinger", "testInitial", gameIndex, music);

            System.out.println(managerService.getSongNum(gameIndex));
            mockMvc.perform(delete("/manage/modifyGame").param("gameIndex", String.valueOf(gameIndex)).param("seq", String.valueOf(maxSeq)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Success"));

            System.out.println("after delete music:"+managerService.getMusicNum(gameIndex));
            assertThat(managerService.getSongNum(gameIndex)).isEqualTo(0L);


        }
        @Test
        void 정답추가() throws Exception{
            Long gameIndex = makeGame();

            List<String> ansList=new ArrayList<>();
            ansList.add("testAnswer");

            String contentType = "audio/mpeg";
            String filePath = "testMP3.mp3";
            MockMultipartFile mockMultipartFile = new MockMultipartFile("mp3","testMP3.mp3",contentType,filePath.getBytes());
            Music music = managerService.storeMusic(mockMultipartFile, gameIndex);

            Long seq = managerService.storeAnswers(ansList, "testSinger", "testInitial", gameIndex, music);
            mockMvc.perform(post("/manage/addAnswer").param("seq",seq.toString()).param("answer","newAnswer").param("gameIndex",gameIndex.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Success"));
            assertThat(managerService.getAnswerNum(gameIndex, seq)).isEqualTo(2L);
        }
        @Test
        void 정답삭제() throws Exception{
            Long gameIndex = makeGame();

            List<String> ansList=new ArrayList<>();
            ansList.add("testAnswer");

            String contentType = "audio/mpeg";
            String filePath = "testMP3.mp3";
            MockMultipartFile mockMultipartFile = new MockMultipartFile("mp3","testMP3.mp3",contentType,filePath.getBytes());
            Music music = managerService.storeMusic(mockMultipartFile, gameIndex);

            Long seq = managerService.storeAnswers(ansList, "testSinger", "testInitial", gameIndex, music);
            Answers ans = managerService.getAnswerForOnlyTest(gameIndex, seq);
            System.out.println(ans.getId().toString());
            mockMvc.perform(delete("/manage/updateAnswer").param("ansId", String.valueOf(ans.getId())))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Success"));
            assertThat(managerService.getSongNum(gameIndex)).isEqualTo(0L);
        }
    }
    @Nested
    @DisplayName("making View Test")
    class testView{

        @Test
        void 게임생성페이지() throws Exception {
            mockMvc.perform(get("/manage/createGame")).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("manage/createGame"));
        }

        @Test
        void 게임수정_상세조회() throws Exception{
            Long gameIndex = makeGame();
            mockMvc.perform(get("/manage/modifyGame").param("gameIndex",gameIndex.toString())).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("manage/updateSong"));
        }
        @Test
        void 노래추가_view() throws Exception{
            Long gameIndex = makeGame();
            mockMvc.perform(get("/manage/upload").param("gameIndex",gameIndex.toString())).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("manage/uploadSong"));
        }
        @Test
        void 노래추가() throws Exception{
            Long gameIndex = makeGame();

            String contentType = "audio/mpeg";
            String filePath = "testMP3.mp3";
            MockMultipartFile mockMultipartFile = new MockMultipartFile("mp3","testMP3.mp3",contentType,filePath.getBytes());


            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("answer","testAnswer1");
            map.add("answer","testAnswer2");
            map.add("singer","testSinger");
            map.add("initial","testInitial");

            mockMvc.perform(multipart("/manage/upload").file(mockMultipartFile).params(map).param("gameIndex",gameIndex.toString())).andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/manage/upload?gameIndex="+gameIndex))
                    ;


        }
        @Test
        void 게임생성() throws Exception {
            Long beforeIndex = managerService.getValidGameIndex();
            mockMvc.perform(post("/manage/createGame").param("title","testTitle")).andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/manage"))
            ;
            Long afterIndex = managerService.getValidGameIndex();
            System.out.println(beforeIndex+"/"+afterIndex);
            assertThat(afterIndex).isGreaterThan(beforeIndex);
        }


    }

}