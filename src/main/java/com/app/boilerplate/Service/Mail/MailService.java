package com.app.boilerplate.Service.Mail;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Shared.Account.Event.SendEmailActivationEvent;
import com.app.boilerplate.Shared.Account.Event.ResetPasswordEvent;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MailService {
    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";
    private final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final MessageSource messageSource;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${client.base-url}")
    private String clientBaseUrl;

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart,
                          boolean isHtml) {
        this.sendEmailSync(to, subject, content, isMultipart, isHtml);
    }

    private void sendEmailSync(String to, String subject, String content, boolean isMultipart,
                               boolean isHtml) {
        log.debug(
                "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart,
                isHtml,
                to,
                subject,
                content);

        final var mimeMessage = javaMailSender.createMimeMessage();
        try {
            final var message =
                    new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(fromEmail);
            message.setSubject(subject);
            message.setText(content, isHtml);

            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailWithTemplate(User user, String templateName, String titleKey,
                                      Locale locale) {
        sendEmailWithTemplateSync(user, templateName, titleKey, locale, Collections.emptyMap());
    }

    private void sendEmailWithTemplateSync(User user, String templateName, String titleKey,
                                           Locale locale, Map<String, Object> properties, String... images) {
        if (user.getEmail() == null) {
            log.debug("Email cannot be empty for the user '{}' ", user.getId());
            return;
        }
        final var context = new Context(locale);
        context.setVariable(USER, user);
        properties.forEach(context::setVariable);
        final var content = springTemplateEngine.process(templateName, context);
        final var subject = messageSource.getMessage(titleKey, null, locale);
        this.sendEmailSync(user.getEmail(), subject, content, false, true);
    }

    @EventListener(ResetPasswordEvent.class)
    @Async
    public void sendResetPasswordEmail(ResetPasswordEvent event) {
        final var locale = LocaleContextHolder.getLocale();
        sendEmailWithTemplateSync(event.getUser(), "mail/reset-password", "mail.reset.title",
                locale, Collections.emptyMap());
    }

    @EventListener(SendEmailActivationEvent.class)
    @Async
    public void sendEmailActivation(SendEmailActivationEvent event) {
        final var locale = LocaleContextHolder.getLocale();
        final var fullUrl = clientBaseUrl + "/account/email-activation" + "?key=" + event.getToken();
        final Map<String, Object> properties = Map.of(
                "token", event.getToken(),
                "returnUrl", fullUrl,
                "helpLink", "",
                "unsubscribeUrl", ""
        );

        sendEmailWithTemplateSync(event.getUser(), "mail/email-activation", "mail.activation.title",
                locale, properties );
    }

}
