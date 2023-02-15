package com.example.guess_music.repository;

import com.example.guess_music.domain.auth.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findByNo(Long memberId);

    Optional<Member> findbyUsername(String id);

    Optional<Member> findByName(String name);

    List<Member> findAll();

    void updateName(String name, String member);
}
