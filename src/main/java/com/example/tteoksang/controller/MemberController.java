package com.example.tteoksang.controller;

import com.example.tteoksang.dto.requestdto.LocalLoginReq;
import com.example.tteoksang.dto.requestdto.ValidateLocalIdReq;
import com.example.tteoksang.dto.requestdto.LocalSignUpReq;
import com.example.tteoksang.dto.requestdto.ValidateNicknameReq;
import com.example.tteoksang.dto.responsedto.CommonRes;
import com.example.tteoksang.dto.responsedto.LocalLoginRes;
import com.example.tteoksang.dto.responsedto.ValidateLocalIdRes;
import com.example.tteoksang.dto.responsedto.LocalSignupRes;
import com.example.tteoksang.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "Member API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/validateLocalId")
    @Operation(summary = "로컬 회원 ID 체크")
    public ResponseEntity<CommonRes<ValidateLocalIdRes>> validateLocalId(@Valid ValidateLocalIdReq request) {
        return ResponseEntity.ok(CommonRes.success(memberService.validateLocalId(request)));
    }

    @GetMapping("/validateNickname")
    @Operation(summary = "닉네임 체크")
    public ResponseEntity<CommonRes<Object>> validateNickname(@Valid ValidateNicknameReq request) {
        return ResponseEntity.ok(CommonRes.success(memberService.validateNickname(request)));
    }

    @PostMapping("/localSignup")
    @Operation(summary = "로컬 회원 가입")
    public ResponseEntity<CommonRes<LocalSignupRes>> localSignup(@RequestBody LocalSignUpReq request) {
        return ResponseEntity.ok(CommonRes.success(memberService.localSignup(request)));
    }

    @PostMapping("/localLogin")
    @Operation(summary = "로컬 로그인")
    public ResponseEntity<CommonRes<LocalLoginRes>> localLogin(@RequestBody LocalLoginReq request) {
        LocalLoginRes res = memberService.localLogin(request); // 여기서 JWT도 같이 리턴되도록

        // 1. 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("token", res.getJwt())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofHours(1))
                .build();

        // 2. 바디에선 JWT 제외하고 필요한 값만 리턴
        LocalLoginRes resWithoutToken = LocalLoginRes.builder()
                .localId(res.getLocalId())
                .nickname(res.getNickname())
                .build();

        // 3. 커스터마이징된 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(CommonRes.success(resWithoutToken));
    }
}
