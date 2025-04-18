package com.example.tteoksang.domain.repository;

import com.example.tteoksang.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Integer> {
    MemberProfile findByNickname(String nickname);
}
