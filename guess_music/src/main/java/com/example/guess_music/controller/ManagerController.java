package com.example.guess_music.controller;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;
import com.example.guess_music.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
public class ManagerController {
    private final ManagerService managerService;

    public ManagerController(ManagerService manageService) {
        this.managerService = manageService;
    }

    @GetMapping("/manage")
    public String enterManager(){
        return "manage/manager";
    }

    @GetMapping("/manage/createGame")
    public String createGamePage(){
        return "manage/createGame";
    }

    @PostMapping("/manage/createGame")
    public String createGame(CreateGameForm form){
        Game game = new Game();
        game.setTitle(form.getTitle());
        managerService.join(game);

        return "redirect:/manage";
    }

    @GetMapping("/manage/modifyGame")
    public String modifyGamePage(@RequestParam("gameIndex") Long gameIndex, Model model){
        model.addAttribute("idx",gameIndex);
        return "manage/updateSong";
    }

    @ResponseBody@GetMapping("/manage/songList")
    public List<Answers> modifySong(@RequestParam("gameIndex") Long gameIndex){
        List<Answers> answerList = managerService.getAnswerList(gameIndex);
        return answerList;
    }
    @ResponseBody
    @DeleteMapping("/manage")
    public String deleteGame(@RequestParam("gameIndex") Long gameIndex){
        if(managerService.delete(gameIndex))
            return "Success";
        else return "Fail";
    }

    @GetMapping("/manage/upload")
    public String uploadSong(){
        return "manage/uploadSong";
    }

    @PostMapping("/manage/upload")
    public String saveSong(SaveSongForm form) throws IOException {
        System.out.println(form);
        System.out.println(form.getAnswer() + " / "+ form.getMp3());
        //For test
        try {
            File newFile = new File("./src/main/resources/static/audio/5-5.mp3");
            if (newFile.createNewFile())
                System.out.println("success");
            else
                System.out.println("already exist");
        } catch (IOException e){
            System.out.println("error !!");
            e.printStackTrace();
        }

        File newFile2 = new File("./src/main/resources/static/audio/4-4.mp3");
        try{
            form.getMp3()
        }
        //save file(need to check mp3)

        //save song to db

        return "redirect:/manage/upload";
    }
}
