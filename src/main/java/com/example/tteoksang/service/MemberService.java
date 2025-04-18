package com.example.tteoksang.service;

import com.example.tteoksang.domain.Member;
import com.example.tteoksang.domain.MemberLocal;
import com.example.tteoksang.domain.MemberType;
import com.example.tteoksang.domain.repository.MemberProfileRepository;
import com.example.tteoksang.domain.repository.MemberRepository;
import com.example.tteoksang.domain.repository.custom.MemberLocalRepository;
import com.example.tteoksang.dto.requestdto.ValidateLocalIdReq;
import com.example.tteoksang.dto.requestdto.LocalSignUpReq;
import com.example.tteoksang.dto.requestdto.ValidateNicknameReq;
import com.example.tteoksang.dto.responsedto.ValidateLocalIdRes;
import com.example.tteoksang.dto.responsedto.LocalSignupRes;
import com.example.tteoksang.dto.responsedto.ValidateNicknameRes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final LocalDateTime now = LocalDateTime.now();

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final MemberLocalRepository memberLocalRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Transactional(readOnly = true)
    public ValidateLocalIdRes validateLocalId(ValidateLocalIdReq request) {
        boolean possible = memberLocalRepository.findByLocalId(request.getLocalId()) == null;

        return ValidateLocalIdRes.builder()
                .isPossible(possible)
                .build();
    }

    @Transactional(readOnly = true)
    public ValidateNicknameRes validateNickname(ValidateNicknameReq request) {
        boolean possible = memberProfileRepository.findByNickname(request.getNickname()) == null;

        return ValidateNicknameRes.builder()
                .isPossible(possible)
                .build();
    }

    @Transactional
    public LocalSignupRes insertLocalSignup(LocalSignUpReq request) {
        // member 저장
        Member member = Member.builder()
                .memberType(MemberType.Local)
                .createdAt(now)
                .build();
        member = memberRepository.save(member);

        // member local 저장
        MemberLocal memberLocal = MemberLocal.builder()
                .memberId(member.getMemberId())
                .localId(request.getLocalId())
                .localPassword(passwordEncoder.encode(request.getLocalPassword()))
                .createdAt(now)
                .build();
        memberLocal = memberLocalRepository.save(memberLocal);

        return LocalSignupRes.builder()
                .localId(memberLocal.getLocalId())
                .build();
    }
}
