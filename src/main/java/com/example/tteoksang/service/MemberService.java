package com.example.tteoksang.service;

import com.example.tteoksang.domain.Member;
import com.example.tteoksang.domain.MemberLocal;
import com.example.tteoksang.domain.MemberProfile;
import com.example.tteoksang.domain.MemberType;
import com.example.tteoksang.domain.repository.MemberProfileRepository;
import com.example.tteoksang.domain.repository.MemberRepository;
import com.example.tteoksang.domain.repository.custom.MemberLocalRepository;
import com.example.tteoksang.dto.requestdto.LocalLoginReq;
import com.example.tteoksang.dto.requestdto.ValidateLocalIdReq;
import com.example.tteoksang.dto.requestdto.LocalSignUpReq;
import com.example.tteoksang.dto.requestdto.ValidateNicknameReq;
import com.example.tteoksang.dto.responsedto.LocalLoginRes;
import com.example.tteoksang.dto.responsedto.ValidateLocalIdRes;
import com.example.tteoksang.dto.responsedto.LocalSignupRes;
import com.example.tteoksang.dto.responsedto.ValidateNicknameRes;
import com.example.tteoksang.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final LocalDateTime now = LocalDateTime.now();

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final MemberRepository memberRepository;
    private final MemberLocalRepository memberLocalRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Transactional(readOnly = true)
    public ValidateLocalIdRes validateLocalId(ValidateLocalIdReq request) {
        boolean possible = memberLocalRepository.findByLocalId(request.getLocalId()).isEmpty();

        return ValidateLocalIdRes.builder()
                .isPossible(possible)
                .build();
    }

    @Transactional(readOnly = true)
    public ValidateNicknameRes validateNickname(ValidateNicknameReq request) {
        boolean possible = memberProfileRepository.findByNickname(request.getNickname()).isEmpty();

        return ValidateNicknameRes.builder()
                .isPossible(possible)
                .build();
    }

    @Transactional
    public LocalSignupRes localSignup(LocalSignUpReq request) {
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

    @Transactional(readOnly = true)
    public LocalLoginRes localLogin(LocalLoginReq request) {
        // 1. ID 확인
        MemberLocal memberLocal = memberLocalRepository.findByLocalId(request.getLocalId())
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // 2. Password 확인
        if (!passwordEncoder.matches(request.getLocalPassword(), memberLocal.getLocalPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. nickname 가져오기
        MemberProfile memberProfile = memberProfileRepository.findByMemberId(memberLocal.getMemberId());

        // jwt 생성
        String jwt = jwtUtil.generateJwt(memberLocal.getMemberId());


        return LocalLoginRes.builder()
                .localId(memberLocal.getLocalId())
                .nickname(memberProfile.getNickname())
                .jwt(jwt)
                .build();
    }
}
