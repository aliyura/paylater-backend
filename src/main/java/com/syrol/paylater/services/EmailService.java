package com.syrol.paylater.services;
        import com.syrol.paylater.pojos.APIResponse;
        import com.syrol.paylater.pojos.EmailMessage;
        import com.syrol.paylater.util.App;
        import com.syrol.paylater.util.Response;
        import lombok.RequiredArgsConstructor;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.context.i18n.LocaleContextHolder;
        import org.springframework.stereotype.Service;
        import org.thymeleaf.context.Context;
        import org.thymeleaf.TemplateEngine;

        import javax.mail.Message;
        import javax.mail.PasswordAuthentication;
        import javax.mail.Session;
        import javax.mail.Transport;
        import javax.mail.internet.InternetAddress;
        import javax.mail.internet.MimeMessage;
        import java.util.Calendar;
        import java.util.Properties;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final Response response;
    private  String emailSender="hello@paylaterhub.com";
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final TemplateEngine templateEngine;
    private final App app;

    public APIResponse<String> sendEmail(EmailMessage emailMessage) {

        final String username = "hello@paylaterhub.com";
        final String password = "0Xhoc9fD2wpA";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.paylaterhub.com");
        props.put("mail.smtp.port", "587");

        try {
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });


            logger.info("Sending Email....");
            EmailMessage email = processEmailTemplate(emailMessage);
            if (emailMessage.getBody() != null) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailSender));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getRecipient()));
                message.setSubject(email.getSubject());
                message.setContent(email.getBody(), "text/html; charset=utf-8");
                Transport.send(message);
                logger.info("Email sent out");
                app.print(email);
                return response.success("Email Sent Successfully");
            } else {
                logger.info("Email Body Required");
                return response.failure("Email Body Required");
            }
        } catch (Exception e) {
            logger.info("Unable to send Email");
            e.printStackTrace();
            return response.failure(e.getMessage());
        }
    }

    private EmailMessage  processEmailTemplate(EmailMessage emailMessage) {
        try {
            Context ctx = new Context(LocaleContextHolder.getLocale());
            if (emailMessage.getRecipient()!=null) {
                ctx.setVariable("name", "Dear " +emailMessage.getRecipientName()+ ",");
                ctx.setVariable("footname", emailMessage.getRecipient());
                ctx.setVariable("year", Calendar.getInstance().get(Calendar.YEAR));
            } else {
                ctx.setVariable("name",emailMessage.getSubject());
            }
            ctx.setVariable("body", emailMessage.getBody());

            final String htmlContent = templateEngine.process("mail/html/system-email.html", ctx);
            logger.info("Arranged email template ");
            emailMessage.setBody(htmlContent);
            return emailMessage;
        } catch (Exception ex) {
            System.out.println("Unable to generate email template");
            ex.printStackTrace();
            return null;
        }
    }

}
