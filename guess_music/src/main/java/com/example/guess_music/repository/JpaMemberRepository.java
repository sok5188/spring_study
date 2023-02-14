package com.example.guess_music.repository;

import com.example.guess_music.domain.auth.Member;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository{

    private final EntityManager em;
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findByNo(Long memberId) {
        Member member=em.find(Member.class,memberId);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findbyUsername(String username) {
        List<Member> result = em.createQuery("select m from Member m where m.username= :username", Member.class).setParameter("username", username).getResultList();
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name=:name", Member.class).setParameter("name", name).getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }
}
