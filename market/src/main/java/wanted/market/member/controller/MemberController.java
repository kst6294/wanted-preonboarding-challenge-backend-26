package wanted.market.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.market.member.domain.dto.request.MemberRequestDto;
import wanted.market.member.domain.dto.response.MemberResponseDto;
import wanted.market.member.domain.entity.Member;
import wanted.market.member.repository.MemberRepository;
import wanted.market.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(MemberRequestDto dto) {
        Member member = memberService.memberJoin(dto);
        if (member.equals(null)) {
            return ResponseEntity.ok(new MemberResponseDto(false, null));
        }
        return ResponseEntity.ok(new MemberResponseDto(true, member.getMemberName()));
    }

    @PostMapping("/delete-id")
    public ResponseEntity<MemberResponseDto> deleteId(MemberRequestDto dto) {
        try {
            memberService.deleteMember(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new MemberResponseDto(false, null));
        }

        return ResponseEntity.ok(new MemberResponseDto(true, dto.getUsername()));
    }
}
