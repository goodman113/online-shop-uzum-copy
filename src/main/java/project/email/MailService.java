package project.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    final JavaMailSender mailSender;
    @Async
    @SneakyThrows
    public void sendMail(String to, String code) {
        String content = buildUzumEmail(code);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Code verification");
        helper.setText(content, true);

        mailSender.send(mimeMessage);
    }
    public String buildUzumEmail(String code) {
        return """
        <div style="background:#F4F5F7; padding:30px; font-family:Arial, sans-serif;">
          <div style="background:#ffffff; max-width:420px; margin:auto; padding:25px 30px;
                      border-radius:12px; box-shadow:0 2px 6px rgba(0,0,0,0.05);">

              <h2 style="text-align:center; color:#222; margin-bottom:20px;">
                  Payment confirmation
              </h2>

              <p style="font-size:15px; color:#444; text-align:center;">
                  Your verification code:
              </p>

              <div style="font-size:36px; font-weight:bold; letter-spacing:6px; 
                          text-align:center; margin:15px 0 25px 0;">
                  %s
              </div>

              <p style="font-size:13px; color:#666; text-align:center;">
                  Code is active for 1 minute.
              </p>

              <p style="font-size:12px; color:#999; text-align:center; margin-top:30px;">
                  if it was not you — just ignore this message.
              </p>

          </div>
        </div>
        """.formatted(code);
    }
}
