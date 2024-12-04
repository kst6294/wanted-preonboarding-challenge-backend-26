package wanted.market.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.market.member.domain.dto.request.ResetPasswordRequestDto;
import wanted.market.member.domain.dto.request.ResetPasswordValidationRequestDto;
import wanted.market.member.domain.dto.response.service.ResetPasswordServiceDto;
import wanted.market.member.domain.dto.response.service.ResetPasswordValidationServiceDto;
import wanted.market.member.domain.entity.Member;
import wanted.market.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static int number;

    public static void createNumber() {
        number = (int) (Math.random() * 90000) + 100000;
    }

    @Transactional
    public MimeMessage createMail(String email) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
            log.info("메일 발송에 실패함.");
        }

        return message;
    }


    @Transactional
    public ResetPasswordServiceDto sendMail(ResetPasswordRequestDto dto) {
        try {
            Member resetPwMember = memberRepository.findByLoginIdAndEmail(dto.getUserLoginId(), dto.getUserEmail()).orElseThrow(() -> new RuntimeException("비밀번호를 재설정 하려는 회원 정보를 찾을 수 없습니다."));
            MimeMessage message = createMail(resetPwMember.getEmail());
            javaMailSender.send(message);
            log.info("비밀번호 재설정을 위한 인증번호 발송 : EMAIL : {}" , resetPwMember.getEmail());
            return new ResetPasswordServiceDto(resetPwMember.getId(), true, number);
        } catch (RuntimeException e) {
            return new ResetPasswordServiceDto(null,false, null);
        }
    }

    public ResetPasswordValidationServiceDto mailValidationCompareToNumber(ResetPasswordValidationRequestDto dto, Integer validationNumber) {
        Member resetPasswordMember = memberRepository.findByLoginId(dto.getUserLoginId()).orElseThrow(() -> new RuntimeException("일치하는 회원 정보가 없습니다."));
        if (validationNumber == number) {
            return new ResetPasswordValidationServiceDto(resetPasswordMember.getId(), true, "메일 인증에 성공하였습니다.");
        }
        return new ResetPasswordValidationServiceDto(resetPasswordMember.getId(), false, "메일 인증번호와 입력한 인증번호가 다릅니다.");
    }
}
