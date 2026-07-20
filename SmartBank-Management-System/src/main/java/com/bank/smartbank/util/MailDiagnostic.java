package com.bank.smartbank.util;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class MailDiagnostic {

    private static final Logger log = LoggerFactory.getLogger(MailDiagnostic.class);

    private final ConfigurableEnvironment environment;
    private final MailProperties mailProperties;

    public MailDiagnostic(ConfigurableEnvironment environment, MailProperties mailProperties) {
        this.environment = environment;
        this.mailProperties = mailProperties;
    }

    @PostConstruct
    public void diagnose() {
        log.info("");
        log.info("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        log.info("  MAIL DIAGNOSTIC — RUNTIME PROPERTY RESOLUTION");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        log.info("");
        log.info("--- 1. Active Profiles ---");
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) {
            log.info("  (none)");
        } else {
            for (String p : profiles) {
                log.info("  active: {}", p);
            }
        }

        log.info("");
        log.info("--- 2. PropertySources (highest to lowest priority) ---");
        int i = 1;
        for (PropertySource<?> ps : environment.getPropertySources()) {
            log.info("  {}. name=\"{}\", class={}", i++, ps.getName(), ps.getClass().getSimpleName());
        }

        log.info("");
        log.info("--- 3. Resolved spring.mail.* from Environment ---");
        log.info("  spring.mail.host    = \"{}\"", environment.getProperty("spring.mail.host"));
        log.info("  spring.mail.port    = \"{}\"", environment.getProperty("spring.mail.port"));
        log.info("  spring.mail.username = \"{}\"", environment.getProperty("spring.mail.username"));
        String pw = environment.getProperty("spring.mail.password");
        log.info("  spring.mail.password present = {}", pw != null && !pw.isEmpty());

        log.info("");
        log.info("--- 4. MailProperties Bean (auto-configured by Spring Boot) ---");
        log.info("  MailProperties.host     = \"{}\"", mailProperties.getHost());
        log.info("  MailProperties.port     = {}", mailProperties.getPort());
        log.info("  MailProperties.username  = \"{}\"", mailProperties.getUsername());
        log.info("  MailProperties.password present = {}",
                mailProperties.getPassword() != null && !mailProperties.getPassword().isEmpty());

        log.info("");
        log.info("--- 5. System.getenv() (raw OS environment variables) ---");
        log.info("  SMTP_HOST     present = {}", System.getenv("SMTP_HOST") != null);
        log.info("  SMTP_PORT     present = {}", System.getenv("SMTP_PORT") != null);
        log.info("  SMTP_USERNAME present = {}", System.getenv("SMTP_USERNAME") != null);
        log.info("  SMTP_PASSWORD present = {}", System.getenv("SMTP_PASSWORD") != null);

        log.info("");
        log.info("--- 6. Tracing WHERE spring.mail.username resolves FROM ---");
        boolean found = false;
        for (PropertySource<?> ps : environment.getPropertySources()) {
            Object val = ps.getProperty("spring.mail.username");
            if (val != null) {
                log.info("  >>> FOUND directly in: \"{}\"  value=\"{}\"", ps.getName(), val);
                found = true;
            }
        }
        if (!found) {
            log.info("  (not found directly — may resolve via placeholder)");
            for (PropertySource<?> ps : environment.getPropertySources()) {
                Object envVal = ps.getProperty("SMTP_USERNAME");
                if (envVal != null) {
                    log.info("  >>> SMTP_USERNAME resolves in: \"{}\"  value=\"{}\"", ps.getName(), envVal);
                }
            }
        }

        log.info("");
        log.info("--- 7. Tracing WHERE spring.mail.password resolves FROM ---");
        found = false;
        for (PropertySource<?> ps : environment.getPropertySources()) {
            Object val = ps.getProperty("spring.mail.password");
            if (val != null) {
                String s = val.toString();
                log.info("  >>> FOUND directly in: \"{}\"  value present={}, length={}",
                        ps.getName(), !s.isEmpty(), s.length());
                found = true;
            }
        }
        if (!found) {
            log.info("  (not found directly — looking for SMTP_PASSWORD...)");
            for (PropertySource<?> ps : environment.getPropertySources()) {
                Object envVal = ps.getProperty("SMTP_PASSWORD");
                if (envVal != null) {
                    String s = envVal.toString();
                    log.info("  >>> SMTP_PASSWORD resolves in: \"{}\"  value present={}, length={}",
                            ps.getName(), !s.isEmpty(), s.length());
                }
            }
        }

        log.info("");
        log.info("--- 8. SMTP Properties ---");
        log.info("  mail.smtp.auth       = {}",
                environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        log.info("  mail.smtp.starttls.enable = {}",
                environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        log.info("  mail.smtp.ssl.enable (if set) = {}",
                environment.getProperty("spring.mail.properties.mail.smtp.ssl.enable"));

        log.info("");
        log.info("--- 9. Check for @ConfigurationProperties binding (spring.mail) ---");
        log.info("  MailProperties class = {}", mailProperties.getClass().getName());
        log.info("  MailProperties toString = {}", mailProperties);

        log.info("");
        log.info("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        log.info("  END MAIL DIAGNOSTIC");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("");
    }
}
