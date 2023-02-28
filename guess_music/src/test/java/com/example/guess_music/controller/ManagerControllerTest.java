package com.example.guess_music.controller;

import com.example.guess_music.domain.game.Game;
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
        void 게임수정_제목변경() throws Exception{
            Long gameIndex = makeGame();
            mockMvc.perform(post("/manage/modifyGame").param("gameIndex",gameIndex.toString()).param("title","test"))
        }
    }
    @Nested
    @DisplayName("except API Test")
    class notAPI{

        @Test
        void 게임생성페이지() throws Exception {
            mockMvc.perform(get("/manage/createGame")).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("manage/createGame"));
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
//        @Test
//        void 노래추가() throws Exception{
//            Long gameIndex = makeGame();
//            String fileName = "testMP3";
//            String contentType = "audio/mpeg";
//            String filePath = "testMP3.mp3";
//            MockMultipartFile mockMultipartFile = new MockMultipartFile("testMP3","testMP3.mp3",contentType,filePath.getBytes());
//            mockMvc.perform(multipart()post("/manage/upload").param("gameIndex",gameIndex.toString()).multi)
//        }

    }

}