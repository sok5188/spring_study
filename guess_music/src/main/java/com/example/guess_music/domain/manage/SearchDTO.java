package com.example.guess_music.domain.manage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO {
    private int pageNum;             // 현재 페이지 번호
    private int totalPageSize;         // 화면 하단에 출력할 페이지 사이즈
    private int pagingSize;
//    private String keyword;       // 검색 키워드
//    private String searchType;    // 검색 유형
//
    public SearchDTO() {
        this.pageNum = 0;
        this.pagingSize = 10;
    }

}
