package com.example.tteoksang.domain.repository;

import com.example.tteoksang.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}
