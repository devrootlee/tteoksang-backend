package com.example.tteoksang.controller;

import com.example.tteoksang.dto.requestdto.LocalLoginReq;
import com.example.tteoksang.dto.requestdto.ValidateLocalIdReq;
import com.example.tteoksang.dto.requestdto.LocalSignUpReq;
import com.example.tteoksang.dto.requestdto.ValidateNicknameReq;
import com.example.tteoksang.dto.responsedto.*;
import com.example.tteoksang.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        LocalLoginRes res = memberService.localLogin(request); // jwt 추출

        // 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("token", res.getJwt())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofHours(1))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(CommonRes.success(res));
    }

    @GetMapping("/loginStatus")
    @Operation(summary = "로그인 상태")
    public ResponseEntity<CommonRes<LoginStatusRes>> loginStatus(Authentication authentication) {
        return ResponseEntity.ok(CommonRes.success(memberService.loginStatus(authentication)));
    }
}
