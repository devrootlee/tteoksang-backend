package com.example.tteoksang.domain.repository.custom;

import com.example.tteoksang.domain.MemberLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberLocalRepository extends JpaRepository<MemberLocal, Integer> {
    Optional<MemberLocal> findByLocalId(String localId);

    MemberLocal findByLocalIdAndLocalPassword(String localId, String localPassword);
}
