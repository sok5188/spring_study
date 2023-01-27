package com.example.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.Encoder;
import java.io.*;
import java.sql.Blob;
import java.util.Base64;
import java.util.LinkedHashMap;

@Controller
public class TestController {
    @GetMapping("/testPage")
    public String testpage(){

        System.out.println("enter someone");
        return "test";
    }
    @GetMapping("/getsong")
    @ResponseBody
    public Blob send() throws Exception {
        File file=new File("/Users/sin-wongyun/Desktop/888.mp3");
        return convertFileToBlob(file);
    }
    private Blob convertFileToBlob(File file) throws Exception {

        Blob blob = null;
        FileInputStream inputStream = null;

        try {
            byte[] byteArray = new byte[(int) file.length()];
            inputStream = new FileInputStream(file);
            inputStream.read(byteArray);

            blob = new javax.sql.rowset.serial.SerialBlob(byteArray);

        } catch (Exception e) {
            throw e;

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }



            } catch (Exception e) {

                inputStream = null;



            } finally {
                inputStream = null;
            }
        }

        return blob;
    }
    public String fileToString(File file) throws IOException {

        //필요한 객체들을 세팅한다.
        //스트링으로 최종변환한 값을 담는 객체
        String fileString = new String();
        //읽은 파일을 인풋 스트림으로 활용하기 위한 객체
        FileInputStream inputStream =  null;
        //읽은 스트림을 바이트배열로 만들기 위한 객체
        ByteArrayOutputStream byteOutStream = null;

        //파일을 인풋 스트림 객체에 넣는다.
        inputStream = new FileInputStream(file);
        byteOutStream = new ByteArrayOutputStream();
        int len = 0;
        //바이트 배열임시생성 (왜 1024인지는 모른다 안다면 댓글부탁 드립니다.)
        byte[] buf = new byte[1024];

        //읽어들인 스트림이 False(-1)이 아닐때까지 루프를 돌린다.
        while ((len = inputStream.read(buf)) != -1) {
            //byte배열로 데이터를 입출력하는기 위해 읽어들인다.
            byteOutStream.write(buf, 0, len);

        }

        //바이트배열에 읽은 스트림을 넣는다.
        byte[] fileArray = byteOutStream.toByteArray();


        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encoderResult = null;
        //읽어들인 바이트배열을 통신을위한base64로 인코딩해서 바이트배열에 넣는다.
        encoderResult = encoder.encode(fileArray);

        //해당 바이트 배열을 스트링으로 변환한다.
        fileString = new String(encoderResult);

        return fileString;

    }
    @GetMapping("/download")
    public void download(HttpServletResponse response) {

        // 직접 파일 정보를 변수에 저장해 놨지만, 이 부분이 db에서 읽어왔다고 가정한다.
        String fileName = "888.mp3";
        String saveFileName = "/Users/Desktop/888.mp3";
        // 맥일 경우 "/tmp/connect.png" 로 수정
        String contentType = "audio/mpeg";
        int fileLength = 116303;

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", "" + fileLength);
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");

        try(
                FileInputStream fis = new FileInputStream(saveFileName);
                OutputStream out = response.getOutputStream();
        ){
            int readCount = 0;
            byte[] buffer = new byte[1024];
            while((readCount = fis.read(buffer)) != -1){
                out.write(buffer,0,readCount);
            }
        }catch(Exception ex){
            throw new RuntimeException("file Save Error");
        }
    }
}
