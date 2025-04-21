package com.example.tteoksang.domain.repository;

import com.example.tteoksang.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Integer> {
    Optional<MemberProfile> findByNickname(String nickname);

    MemberProfile findByMemberId(int memberId);
}
