package com.example.guess_music.service;

import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import com.example.guess_music.repository.MusicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private MusicRepository musicRepository;

    @Mock
    private AnswerRepository answerRepository;
    @InjectMocks
    ManagerService managerService;
    private Game getGame(){
        Game game = new Game();
        game.setGameIndex(999L);
        game.setSongNum(0L);
        game.setTitle("testTitle");
        return game;
    }
    private Music makeMusic(Game game){
        Music music=new Music();
        music.setName("testMusic");
        music.setGame(game);
        return music;
    }
    private Answers makeAnswer(Game game){
        Answers answers = new Answers();
        answers.setAnswer("testAnswer");
        answers.setInitial("testInitial");
        answers.setSinger("testSinger");
        answers.setSeq(1L);
        answers.setGameIndex(game);
        return answers;
    }
    private Answers makeAnswer(Game game,Music music){
        Answers answers = new Answers();
        answers.setAnswer("testAnswer");
        answers.setInitial("testInitial");
        answers.setSinger("testSinger");
        answers.setSeq(1L);
        answers.setGameIndex(game);
        answers.setMusic(music);
        return answers;
    }

    @Test
    void join() {
        //given
        Game game=new Game();
        game.setTitle("testTitle");
        game.setSongNum(0L);
        game.setGameIndex(1L);
        given(gameRepository.save(game)).willReturn(game);
        //when
        Long join = managerService.join(game);
        //then
        assertThat(join).isEqualTo(1L);
    }


    @Test
    void getAnswerList() {
        //given
        Game game = getGame();
        Answers answers = makeAnswer(game);
        Answers answers1 = makeAnswer(game);
        List<Answers> answersList= new ArrayList<>();
        answersList.add(answers);
        answersList.add(answers1);
        given(answerRepository.findByGameIndex(game.getGameIndex())).willReturn(answersList);
        //when
        List<Answers> answerList = managerService.getAnswerList(game.getGameIndex());
        //then
        assertThat(answerList.size()).isEqualTo(2);
        assertThat(answerList.get(0).getAnswer()).isEqualTo(answers.getAnswer());
    }

    @Test
    void storeAnswers() {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        given(gameRepository.findSongNum(game.getGameIndex())).willReturn(game.getSongNum());
        //빈 게임에 새로운 정답을 추가하는 것이니 해당 answer로 된 singer는 존재하지 않음
        //edge case 테스트 필요?
        given(answerRepository.findAllSingerByAnswer("testAnswer",game.getGameIndex())).willReturn(null);
        //어차피 정답 저장 기능은 return값을 사용하지 않기에 빈 객체 리턴
        given(answerRepository.save(ArgumentMatchers.any())).willReturn(new Answers());
        //이 값도 사용하지 않으니.. 무의미한 값 리턴
        given(gameRepository.addSongToGame(game.getGameIndex())).willReturn(1);

        List<String> answer=new ArrayList<>();
        answer.add("testAnswer");
        answer.add("testAnswer1");
        //when
        Long savedSeq = managerService.storeAnswers(answer, "testSinger", "testInitial", game.getGameIndex(), makeMusic(game));
        //then
        //게임의 곡 수가 0이기에 노래를 추가 한 후 1개로 변경되어야 함
        assertThat(savedSeq).isEqualTo(1L);
    }

    @Test
    void addAnswer() {
        //given
        Game game = getGame();
        //노래를 해당 게임에 추가한 것으로 간주
        game.setSongNum(1L);
        Music music = makeMusic(game);
        Answers answers = makeAnswer(game,music);
        List<Answers> ansList=new ArrayList<>();
        ansList.add(answers);
        given(answerRepository.findByIdxSeq(game.getGameIndex(),answers.getSeq())).willReturn(ansList);
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        Answers newAnswer = makeAnswer(game, music);
        newAnswer.setAnswer("newTestAnswer");
        //mock한 newAnswer와 실제 서비스의 answers는 다른 공간에서 생성된 다른 객체라서 일치하지 않음
        //따라서, any사용해서 newAnswer리턴하게 만듬
        given(answerRepository.save(any())).willReturn(newAnswer);
        //when
        Answers saved = managerService.addAnswer(answers.getGameIndex().getGameIndex(), answers.getSeq(), "newTestAnswer");
        //then
        assertThat(saved.getAnswer()).isEqualTo(newAnswer.getAnswer());
    }

    @Test
    void storeMusic() throws IOException {
        //given
        Game game = getGame();
        given(gameRepository.findById(game.getGameIndex())).willReturn(Optional.of(game));
        String fileName = "testMP3";
        String contentType = "audio/mpeg";
        String filePath = "testMP3.mp3";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("testMP3","testMP3.mp3",contentType,filePath.getBytes());
        Music music=makeMusic(game);
        music.setName(fileName);
        music.setType(contentType);
        music.setData(mockMultipartFile.getBytes());
        given(musicRepository.save(ArgumentMatchers.any())).willReturn(music);
        //when
        Music saved = managerService.storeMusic(mockMultipartFile, game.getGameIndex());
        //then
        assertThat(saved.getName()).isEqualTo(fileName);
    }
}