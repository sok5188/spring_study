package com.example.guess_music.service;

import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


//@SpringBootTest
//@Transactional
public class ManagerServiceTest {
//    @Autowired
//    ManagerService managerService;
//
//    @Autowired
//    GameRepository gameRepository;
//
//    @Autowired
//    AnswerRepository answerRepository;
//
//    @Test
//    void getValidGameIndex() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//
//        Long gameIndex = managerService.join(game);
//
//        assertThat(managerService.getValidGameIndex()).isEqualTo(gameIndex+1L);
//    }
//    @Test
//    void 게임추가() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//
//        Long gameIndex = managerService.join(game);
//
//        Optional<Game> gameByGameIndex = gameRepository.findGameByGameIndex(gameIndex);
//        assertThat(gameByGameIndex.get().getTitle()).isEqualTo(game.getTitle());
//    }
//
//
//    @Test
//    void 게임삭제() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//        Long gameIndex = managerService.join(game);
//
//        managerService.delete(gameIndex);
//        Optional<Game> gameByGameIndex = gameRepository.findGameByGameIndex(gameIndex);
//
//        assertThat(gameByGameIndex.isPresent()).isEqualTo(false);
//    }
//
//    @Test
//    void 노래저장() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//        Long gameIndex = managerService.join(game);
//        Optional<Game> gameByGameIndex = gameRepository.findGameByGameIndex(gameIndex);
//
//        Answers answers=new Answers();
//        answers.setAnswer("ForTest");
//        answers.setSinger("ForTest");
//        answers.setInitial("ForTest");
//        answers.setGameIndex(gameByGameIndex.get());
//        answers.setSeq(1L);
//        Answers save = answerRepository.save(answers);
//
//        List<String> ans=new ArrayList<>();
//        ans.add("ForTest2");
//        Long i = managerService.storeFile(ans, "ForTest2", "ForTest2", gameByGameIndex.get().getGameIndex());
//
//        assertThat(i).isEqualTo(2);
//    }
//
//    @Test
//    void 노래삭제() throws IOException {
        //ec2에 우분투랑 충돌 이슈로 잠시 테스트 중단..
//        Game game=new Game();
//        game.setTitle("forTestGame");
//        Long gameIndex = managerService.join(game);
//
//        List<String> ans=new ArrayList<>();
//        ans.add("ForTest2");
//        int i = managerService.storeFile(ans, "ForTest2", "ForTest2", gameIndex);
//
//        //String folder="/Users/sin-wongyun/Desktop/guess_music/src/main/resources/static/audio/";
//        String folder = "/home/ubuntu/audio";
//        //tring filePath= getApplicationContext().getFilesDir().getPath().toString();
//
//
////        File directory=new File(folder);
////        try {
////            if(directory.createNewFile()){
////                System.out.println("new directory has been created");
////            }else{
////                System.out.println("directory is already made");
////            }
////        }catch (IOException e){
////            e.printStackTrace();
////        }
//        String filename=gameIndex+"-"+i+".mp3";
//        File file=new File(folder+filename);
//        file.createNewFile(); //maybe throw ioexception
//
//        boolean delete = managerService.delete(gameIndex, i);
//        assertThat(file.exists()).isEqualTo(false);
//        assertThat(delete).isEqualTo(true);
//
//    }
//
//    @Test
//    void getAnswerList() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//        Long gameIndex = managerService.join(game);
//        Optional<Game> gameByGameIndex = gameRepository.findGameByGameIndex(gameIndex);
//
//        List<Answers> ansList=new ArrayList<>();
//
//        Answers answers=new Answers();
//        answers.setAnswer("ForTest");
//        answers.setSinger("ForTest");
//        answers.setInitial("ForTest");
//        answers.setGameIndex(gameByGameIndex.get());
//        answers.setSeq(1L);
//        Answers save = answerRepository.save(answers);
//
//        Answers answers2=new Answers();
//        answers2.setAnswer("ForTest");
//        answers2.setSinger("ForTest");
//        answers2.setInitial("ForTest");
//        answers2.setGameIndex(gameByGameIndex.get());
//        answers2.setSeq(1L);
//        Answers save2 = answerRepository.save(answers2);
//
//        ansList.add(answers);
//        ansList.add(answers2);
//
//        List<Answers> answerList = managerService.getAnswerList(gameIndex);
//
//        assertThat(answerList).isEqualTo(ansList);
//    }
//
//    @Test
//    void 노래수정() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//        Long gameIndex = managerService.join(game);
//        Optional<Game> gameByGameIndex = gameRepository.findGameByGameIndex(gameIndex);
//
//        Answers answers=new Answers();
//        answers.setAnswer("ForTest");
//        answers.setSinger("ForTest");
//        answers.setInitial("ForTest");
//        answers.setGameIndex(gameByGameIndex.get());
//        answers.setSeq(1L);
//        Answers save = answerRepository.save(answers);
//
//        managerService.updateAnswer(save.getId(),"ForTest2");
//
//        assertThat(answerRepository.findAnswerBySeq(gameIndex,1L).get().get(0)).isEqualTo("ForTest2");
//    }
}
