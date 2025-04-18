package com.example.tteoksang.domain.repository.custom;

import com.example.tteoksang.domain.MemberLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberLocalRepository extends JpaRepository<MemberLocal, Integer> {
    MemberLocal findByLocalId(String localId);
}
