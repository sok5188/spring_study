package com.example.guess_music.repository;

import com.example.guess_music.domain.auth.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByUsername(String id);

    Optional<Member> findByName(String name);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.name=?1 where m.username=?2")
    int updateName(String name, String member);
}
