package com.example.tteoksang.controller;

import com.example.tteoksang.dto.requestdto.ValidateLocalIdReq;
import com.example.tteoksang.dto.requestdto.LocalSignUpReq;
import com.example.tteoksang.dto.requestdto.ValidateNicknameReq;
import com.example.tteoksang.dto.responsedto.CommonRes;
import com.example.tteoksang.dto.responsedto.ValidateLocalIdRes;
import com.example.tteoksang.dto.responsedto.LocalSignupRes;
import com.example.tteoksang.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(CommonRes.success(memberService.insertLocalSignup(request)));
    }
}
