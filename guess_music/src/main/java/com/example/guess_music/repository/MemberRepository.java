package com.example.guess_music.repository;

import com.example.guess_music.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findByNo(Long memberId);

    Optional<Member> findById(String id);

    Optional<Member> findByName(String name);

    List<Member> findAll();
}
