package com.example.guess_music.controller;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;
import com.example.guess_music.service.ManagerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
public class ManagerController {
    private final ManagerService managerService;
    private Long gameIndex;
    public ManagerController(ManagerService manageService) {
        this.managerService = manageService;
    }

    @GetMapping("/manage")
    public String enterManager(){
        return "manage/manager";
    }

    @ResponseBody
    @DeleteMapping("/manage")
    public String deleteGame(@RequestParam("gameIndex") Long gameIndex){
        if(managerService.delete(gameIndex))
            return "Success";
        else return "Fail";
    }

    @GetMapping("/manage/createGame")
    public String createGamePage(){
        return "manage/createGame";
    }

    @PostMapping("/manage/createGame")
    public String createGame(CreateGameForm form){
        System.out.println("in");
        Game game = new Game();
        game.setTitle(form.getTitle());
        managerService.join(game);
        System.out.println("fin");
        return "redirect:/manage";
    }
    @GetMapping("/manage/modifyGame")
    public String modifyGamePage(@RequestParam("gameIndex") Long gameIndex, Model model){
        model.addAttribute("idx",gameIndex);
        return "manage/updateSong";
    }
    @ResponseBody
    @PostMapping("/manage/modifyGame")
    public String modifyGameTitle(@RequestParam("gameIndex") Long gameIndex, @RequestParam("newTitle") String title){
        if(managerService.updateGameTitle(gameIndex,title))
            return "Success";
        else return "Fail";
    }
    @DeleteMapping("/manage/modifyGame")
    @ResponseBody
    public String deleteSong(@RequestParam("gameIndex") Long gameIndex,@RequestParam("seq") Long seq){
        if(managerService.delete(gameIndex,seq))
            return "Success";
        else return "Fail";

    }
    @ResponseBody@GetMapping("/manage/songList")
    public List<Answers> modifySong(@RequestParam("gameIndex") Long gameIndex){
        return managerService.getAnswerList(gameIndex);
    }

    @GetMapping("/manage/upload")
    public String uploadSong(@RequestParam("gameIndex")Long gameIndex,Model model) throws IOException {
        //추후 멀티 부분에서 세션에 저장하든 해야 할 지도..?
        this.gameIndex=gameIndex;
        model.addAttribute("gameIndex",gameIndex);
        return "manage/uploadSong";
    }

    @PostMapping("/manage/upload")
    public String saveSong(SaveSongForm form) throws IOException {
        String[] split = form.getMp3().getOriginalFilename().split("\\.");
        String extension= split[split.length-1];

        if(extension.equals("mp3")&&split.length>1){
            Long result = managerService.storeFile(form.getAnswer(), form.getSinger(), form.getInitial(), gameIndex);
            System.out.println("result is : "+result);
            if(result!=-1){
                System.out.println("save success");
                //ec2서버용
                String folder="/home/ubuntu/audio/";
                //Local 테스트용
                //String folder="/Users/sin-wongyun/Desktop/guessAudio/";
                String filename=gameIndex+"-"+result+".mp3";
                form.getMp3().transferTo(new File(folder+filename));
                String red="redirect:/manage/upload?gameIndex="+gameIndex;
                return red;
            }
        }
        //bad시 처리 구현해야 함
        return "Bad";
    }
    @ResponseBody
    @PostMapping("/manage/updateAnswer")
    public String updateAnswer(@RequestParam("id") Long id,@RequestParam("answer") String answer){
        System.out.println("get update Answer req : "+id);
        if(managerService.updateAnswer(id,answer))
            return "Success";
        else
            return "Fail";
    }

    @ResponseBody
    @PostMapping("/manage/addAnswer")
    public String addAnswer(@RequestParam("seq") Long seq,@RequestParam("answer") String answer,@RequestParam("gameIndex") Long gameIndex){
        System.out.println("get add Answer req : "+seq);

        if(managerService.addAnswer(gameIndex,seq,answer))
            return "Success";
        else
            return "Fail";

    }
    @ResponseBody
    @DeleteMapping("/manage/updateAnswer")
    public String deleteAnswer(@RequestParam("ansId") Long id){
        System.out.println("get delete Answer req : "+id);
        if(managerService.deleteAnswer(id))
            return "Success";
        else
            return "Fail";
    }

}
