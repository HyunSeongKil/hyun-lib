package dev.hyunlab.hyunlib.misc;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Field;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;

import dev.hyunlab.hyunlib.dto.CpuDto;
import dev.hyunlab.hyunlib.dto.EmailDto;
import dev.hyunlab.hyunlib.dto.EmailV2Dto;
import dev.hyunlab.hyunlib.dto.FileStoreDto;
import dev.hyunlab.hyunlib.dto.MemoryDto;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.validation.constraints.NotNull;

public class HyunHelper {
    private static final Logger logger = LoggerFactory.getLogger(HyunHelper.class);

    @SuppressWarnings("unchecked")
    public static <T> T defaultValue(Object obj, T defaultVal) {
        if (isNullOrEmpty(obj)) {
            return defaultVal;
        }
        return (T) obj;
    }

    // #region reflection
    public static void copyFields(Object source, Object target) throws IllegalAccessException {
        copyFields(source, target, Map.of());
    }

    /**
     * 
     * @param source
     * @param target
     * @param specialValueMap key: field name, value: field value.
     *                        source와 다른 값을 target에 설정할 때 사용
     * @throws IllegalAccessException
     */
    public static void copyFields(Object source, Object target, Map<String, Object> specialValueMap)
            throws IllegalAccessException {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        try {
            for (Field sourceField : sourceClass.getDeclaredFields()) {
                Field targetField = targetClass.getDeclaredField(sourceField.getName());

                if (specialValueMap.containsKey(targetField.getName())) {
                    Object specialValue = specialValueMap.get(targetField.getName());
                    if (targetField.getType().equals(specialValue.getClass())) {
                        targetField.setAccessible(true);
                        targetField.set(target, specialValue);
                    }
                } else {
                    if (targetField.getType().equals(sourceField.getType())) {
                        sourceField.setAccessible(true);
                        targetField.setAccessible(true);
                        targetField.set(target, sourceField.get(source));
                    }
                }

            }
        } catch (NoSuchFieldException ex) {
            logger.error("Field not found during copyFields: {}", ex.getMessage());
        }
    }

    // #endregion

    // #region os/cpu/memory/disk info
    public static MemoryDto getOsMemoryInfo() {
        OperatingSystemMXBean baseOsBean = ManagementFactory.getOperatingSystemMXBean();
        long totalMemory = -1L;
        long freeMemory = -1L;
        long usedMemory = -1L;
        if (baseOsBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) baseOsBean;
            totalMemory = osBean.getTotalMemorySize();
            freeMemory = osBean.getFreeMemorySize();
            usedMemory = totalMemory - freeMemory;
        }
        return MemoryDto
                .builder()
                .totalMemory(totalMemory)
                .freeMemory(freeMemory)
                .usedMemory(usedMemory)
                .build();
    }

    public static Map<String, Object> getOsInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return Map.of(
                "name", osBean.getName(),
                "arch", osBean.getArch(),
                "version", osBean.getVersion(),
                "availableProcessors", osBean.getAvailableProcessors());
    }

    public static CpuDto getCpuInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return CpuDto
                .builder()
                .name(osBean.getName())
                .arch(osBean.getArch())
                .version(osBean.getVersion())
                .availableProcessors(osBean.getAvailableProcessors())
                .systemLoadAverage(osBean.getSystemLoadAverage())
                .build();

    }

    public static Map<String, Object> getJvmInfo() {
        Runtime runtime = Runtime.getRuntime();
        return Map.of(
                "name", System.getProperty("java.runtime.name"),
                "version", System.getProperty("java.runtime.version"),
                "vendor", System.getProperty("java.vendor"),
                "availableProcessors", runtime.availableProcessors());
    }

    public static MemoryDto getJvmMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return MemoryDto
                .builder()
                .totalMemory(totalMemory)
                .freeMemory(freeMemory)
                .usedMemory(usedMemory)
                .build();
    }

    public static List<FileStoreDto> getDiskInfo() {
        List<FileStoreDto> fileStoreDtos = new java.util.ArrayList<>();

        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            try {

                FileStoreDto dto = FileStoreDto.builder()
                        .name(store.name())
                        .type(store.type())
                        .totalSpace(store.getTotalSpace())
                        .usedSpace(store.getTotalSpace() - store.getUsableSpace())
                        .usableSpace(store.getUsableSpace())
                        .build();

                fileStoreDtos.add(dto);
            } catch (IOException e) {
                // 오류 처리 (예: 로그 기록)
                e.printStackTrace();
            }
        }

        return fileStoreDtos;
    }

    /**
     * 시스템 정보 로그 출력
     * 
     * @since 20260424
     */
    public static void logSystemInfo() {
        logger.info("OS Info: {}", getOsInfo());
        logger.info("CPU Info: {}", getCpuInfo());
        logger.info("JVM Info: {}", getJvmInfo());
        logger.info("OS Memory Info: {}", getOsMemoryInfo());
        logger.info("JVM Memory Info: {}", getJvmMemoryInfo());
        logger.info("Disk Info: {}", getDiskInfo());
    }

    // #endregion

    // #region stack trace
    public static String getStackTraceString(Exception ex) {
        java.io.StringWriter sw = new java.io.StringWriter();
        ex.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }

    public static String getClassName(Exception ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            return stackTrace[0].getClassName();
        }

        return "UnknownClass";
    }

    public static String getMethodName(Exception ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            return stackTrace[0].getMethodName();
        }

        return "UnknownMethod";
    }
    // #endregion

    public static String padLeft(String input, int length, char padChar) {
        if (input == null) {
            input = "";
        }
        if (input.length() >= length) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - input.length(); i++) {
            sb.append(padChar);
        }
        sb.append(input);
        return sb.toString();
    }

    public static String padRight(String input, int length, char padChar) {
        if (input == null) {
            input = "";
        }
        if (input.length() >= length) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < length - input.length(); i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    // #region transform
    public static String markdownToHtmlString(String markdown) {
        Parser parser = new Parser.Builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlContent = renderer.render(document);
        if (isNullOrEmpty(htmlContent)) {
            return "";
        }

        return htmlContent
                .replace("\n", "<br>")
                .replace("  ", "&nbsp;&nbsp;");

    }
    // #endregion

    // #region email

    /**
     * smpt를 이용한 메일발송
     * 
     * @author gravity@vaiv.kr
     * @since 20260420
     * @throws IOException
     */
    @SuppressWarnings("UseSpecificCatch")
    public static boolean sendEmail(@NotNull EmailV2Dto dto) throws MessagingException, IOException {
        // #region
        Supplier<Boolean> validate = () -> {
            if (dto.getSmtpDto() == null) {
                logger.warn("SMTP 정보가 없습니다.");
                return false;
            }
            if (isNullOrEmpty(dto.getSmtpDto().getHost()) || isNullOrEmpty(dto.getSmtpDto().getPort())) {
                logger.warn("SMTP 호스트 또는 포트 정보가 없습니다.");
                return false;
            }
            if (isNullOrEmpty(dto.getAuthentication().getEmailAddress())
                    || isNullOrEmpty(dto.getAuthentication().getPassword())) {
                logger.warn("인증 정보가 없습니다.");
                return false;
            }
            if (isNullOrEmpty(dto.getTos())) {
                logger.warn("수신자 정보가 없습니다.");
                return false;
            }
            if (isNullOrEmpty(dto.getSubject()) || isNullOrEmpty(dto.getContent())) {
                logger.warn("제목 또는 본문 내용이 없습니다.");
                return false;
            }

            return true;
        };

        Supplier<Properties> smtpPropertiesSupplier = () -> {
            Properties props = new Properties();
            props.put("mail.smtp.host", dto.getSmtpDto().getHost());
            props.put("mail.smtp.port", dto.getSmtpDto().getPort()); // 보통 25, 465(SSL), 587(TLS)
            props.put("mail.smtp.auth", dto.getSmtpDto().isAuth() ? "true" : "false");
            if (dto.getSmtpDto().isStarttlsEnable()) {
                props.put("mail.smtp.starttls.enable", "true"); // TLS 사용 시
                props.put("mail.smtp.starttls.required", "true"); // TLS 필수 설정
            }
            if (dto.getSmtpDto().isSslEnable()) {
                props.put("mail.smtp.ssl.enable", "true"); // SSL 사용 시
            }
            return props;
        };

        Function<Properties, Session> sessionFunction = (props) -> Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(dto.getAuthentication().getEmailAddress(),
                        dto.getAuthentication().getPassword());
            }
        });

        Function<Session, MimeMessage> messageFunction = (session) -> {
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(dto.getFrom().getEmailAddress()));

                var toEmailAddresses = dto.getTos()
                        .stream()
                        .map(EmailV2Dto.EmailUser::getEmailAddress)
                        .toList();
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(String.join(",", toEmailAddresses)));
                return message;
            } catch (MessagingException e) {
                throw new RuntimeException("Error while creating email message", e);
            }
        };

        Supplier<Multipart> multipartSupplier = () -> {
            try {
                // 본문 파트
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setContent(dto.getContent(), "text/html; charset=UTF-8");

                // Multipart에 본문과 첨부파일 추가
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);

                // 첨부파일 파트
                if (dto.getAttachments() != null) {
                    for (EmailV2Dto.Attachment attachment : dto.getAttachments()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        attachmentPart.attachFile(attachment.getPath().toFile());
                        attachmentPart.setFileName(attachment.getFileName());
                        multipart.addBodyPart(attachmentPart);
                    }
                }

                return multipart;
            } catch (IOException | MessagingException e) {
                throw new RuntimeException("Error while creating email multipart content", e);
            }
        };

        // #endregion

        if (!validate.get()) {
            logger.error("Invalid EmailV2Dto: {}", dto);
            throw new IllegalArgumentException("Invalid EmailDto");
        }

        // SMTP 서버 설정 (예: 회사 메일 서버)
        Properties props = smtpPropertiesSupplier.get();

        // 인증 세션 생성
        Session session = sessionFunction.apply(props);
        session.setDebug(true);

        // 메일 메시지 작성
        MimeMessage message = messageFunction.apply(session);
        message.setSubject(dto.getSubject());
        // 메시지에 Multipart 설정
        message.setContent(multipartSupplier.get());

        try {
            // 메일 전송
            Transport.send(message);
        } catch (Exception e) {
            logger.error("{}", dto);
            throw e;
        }

        return true;
    }

    /**
     * @deprecated HyunHelper.sendEmail(EmailDto) 대신 sendEmail(EmailV2Dto) 사용 권장
     *             smpt를 이용한 메일발송
     * 
     * @author gravity@vaiv.kr
     * @throws IOException
     * @since 20260203
     */
    public static boolean sendEmail(@NotNull EmailDto dto) throws MessagingException, IOException {
        // #region
        Supplier<Boolean> validate = () -> {
            if (dto.getSmtpDto() == null) {
                return false;
            }
            if (isNullOrEmpty(dto.getSmtpDto().getHost()) || isNullOrEmpty(dto.getSmtpDto().getPort())) {
                return false;
            }
            if (isNullOrEmpty(dto.getFromEmailAddress()) || isNullOrEmpty(dto.getFromEmailPassword())) {
                return false;
            }
            if (isNullOrEmpty(dto.getToEmailAddresses())) {
                return false;
            }
            if (isNullOrEmpty(dto.getTitle()) || isNullOrEmpty(dto.getBody())) {
                return false;
            }

            return true;
        };
        // #endregion

        if (!validate.get()) {
            throw new IllegalArgumentException("Invalid EmailDto");
        }

        // SMTP 서버 설정 (예: 회사 메일 서버)
        Properties props = new Properties();
        props.put("mail.smtp.host", dto.getSmtpDto().getHost());
        props.put("mail.smtp.port", dto.getSmtpDto().getPort()); // 보통 25, 465(SSL), 587(TLS)
        props.put("mail.smtp.auth", dto.getSmtpDto().isAuth() ? "true" : "false");
        props.put("mail.smtp.starttls.enable", dto.getSmtpDto().isStarttlsEnable() ? "true" : "false"); // TLS 사용 시

        // 인증 세션 생성
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(dto.getFromEmailAddress(), dto.getFromEmailPassword());
            }
        });

        // 메일 메시지 작성
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(dto.getFromEmailAddress()));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(String.join(",", dto.getToEmailAddresses())));
        message.setSubject(dto.getTitle());

        // 본문 파트
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(dto.getBody(), "text/html; charset=UTF-8");

        // Multipart에 본문과 첨부파일 추가
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        // 첨부파일 파트
        if (dto.getPaths() != null && !dto.getPaths().isEmpty()) {
            for (int i = 0; i < dto.getPaths().size(); i++) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(dto.getPaths().get(i).toFile());
                attachmentPart.setFileName(dto.getFilenames().get(i));
                multipart.addBodyPart(attachmentPart);
            }
        }

        // 메시지에 Multipart 설정
        message.setContent(multipart);

        // 메일 전송
        Transport.send(message);

        return true;
    }

    /**
     * gmail SMTP로 이메일 발송
     * 
     * @param fromEmail
     * @param appPassword
     * @param toEmails
     * @param subject
     * @param body
     * @param files
     * @param filenames
     * @return
     * @throws MessagingException
     */
    public static boolean sendEmail(@NotNull String fromEmail,
            @NotNull String appPassword,
            @NotNull java.util.List<String> toEmails,
            @NotNull String subject,
            @NotNull String body,
            @NotNull List<File> files,
            @NotNull List<String> filenames) throws MessagingException {
        org.springframework.mail.javamail.JavaMailSenderImpl mailSender = new org.springframework.mail.javamail.JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(fromEmail);
        mailSender.setPassword(appPassword);

        java.util.Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(toEmails.toArray(new String[0])); // 여러명 가능
        helper.setSubject(subject);
        helper.setText(body, true);

        for (int i = 0; i < files.size(); i++) {
            File attachment = files.get(i);
            String filename = filenames.get(i);
            helper.addAttachment(filename, attachment);
        }

        //
        mailSender.send(message);

        return true;
    }
    // #endregion

    // #region file/dir

    public static Path createYmdPath() {
        String ymd = getCurrentYmd();
        return createYmdPath(ymd);
    }

    public static Path createYmdPath(String ymd) {
        return Paths.get(ymd.substring(0, 4), ymd.substring(4, 6), ymd.substring(6, 8));
    }

    public static Path createYmPath() {
        String ym = getCurrentYm();
        return createYmPath(ym);
    }

    public static Path createYmPath(String ym) {
        return Paths.get(ym.substring(0, 4), ym.substring(4, 6));
    }

    /**
     * 파일 또는 디렉토리 삭제
     * path이 null이거나 존재하지 않으면 false 반환
     * 
     * @param path
     * @return 삭제 성공 여부
     */
    public static boolean deletePath(Path path) {
        if (path == null) {
            return false;
        }
        if (!path.toFile().exists()) {
            return false;
        }

        return path.toFile().delete();
    }

    public static String getJavaTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 파일 또는 디렉토리를 zip으로 압축
     * 
     * @param destPath    압축할 파일 또는 디렉토리 경로
     * @param zipFilePath 압축된 zip 파일이 생성될 경로 (파일명 포함)
     * @return
     * @throws IOException
     */
    public static boolean compressPathToZip(Path destPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new java.io.FileOutputStream(zipFilePath.toFile()))) {
            Files.walk(destPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        Path relativePath = destPath.relativize(path);
                        ZipEntry zipEntry = new ZipEntry(relativePath.toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Error while compressing to zip", e);
                        }
                    });

            return true;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 여러 파일 또는 디렉토리를 zip으로 압축
     * 
     * @param destPaths
     * @param zipFilePath
     * @return
     * @throws IOException
     */
    public static boolean compressPathToZip(List<Path> destPaths, Path zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new java.io.FileOutputStream(zipFilePath.toFile()))) {
            for (Path destPath : destPaths) {
                if (Files.isDirectory(destPath)) {
                    throw new IllegalArgumentException("Directory compression is not supported. Path: " + destPath);
                }

                ZipEntry zipEntry = new ZipEntry(destPath.getFileName().toString());
                try {
                    zos.putNextEntry(zipEntry);
                    Files.copy(destPath, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException("Error while compressing to zip", e);
                }
            }
            return true;
        } catch (IOException e) {
            throw e;
        }
    }
    // #endregion

    // #region date/time

    /**
     * 현재 날짜를 yyyyMMdd 형식으로 반환
     * 
     * @return yyyyMMdd 문자열
     */
    public static String getCurrentYmd() {
        return getYmd(LocalDate.now());
    }

    /**
     * 현재 날짜를 yyyyMM 형식으로 반환
     * 
     * @return
     */
    public static String getCurrentYm() {
        return getCurrentYmd().substring(0, 6);
    }

    /**
     * 현재 날짜를 yyyyMMdd 형식으로 반환
     * 
     * @return yyyyMMdd 문자열
     */
    public static String getYmd(LocalDate date) {
        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String getCurrentHms() {
        return getHms(LocalTime.now());
    }

    public static String getHms(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HHmmss"));
    }

    /**
     * 현재 날짜시간을 yyyyMMdd 형식으로 반환
     * 
     * @return yyyyMMdd 문자열
     */
    public static String getYyyyMMdd(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     * yyyyMMdd 문자열을 LocalDate로 변환
     * 
     * @param yyyyMMdd yyyyMMdd 형식의 문자열
     * @return LocalDate 객체
     */
    public static LocalDate parseYyyyMMdd(String yyyyMMdd) {
        return LocalDate.parse(yyyyMMdd, DateTimeFormatter.BASIC_ISO_DATE);
    }

    // #endregion

    /**
     * 객체가 null이거나 비어있는지 확인
     *
     * String, 배열, Collection, Map 타입을 지원하며,
     * 그 외 타입은 null만 체크합니다.
     *
     * @param obj 검사할 객체
     * @return null 또는 비어있으면 true, 아니면 false
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }

        if (obj.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(obj) == 0;
        }

        if (obj instanceof java.util.Collection) {
            return ((java.util.Collection<?>) obj).isEmpty();
        }

        if (obj instanceof java.util.Map) {
            return ((java.util.Map<?, ?>) obj).isEmpty();
        }

        return false;
    }

    /**
     * 객체가 null이 아니고 비어있지 않은지 확인
     *
     * String, 배열, Collection, Map 타입을 지원하며,
     * 그 외 타입은 null만 체크합니다.
     *
     * @param obj 검사할 객체
     * @return null 또는 비어있으면 false, 아니면 true
     */
    public static boolean isNotNullOrEmpty(Object obj) {
        return !isNullOrEmpty(obj);
    }

    /**
     * 접두어 + 년월일(yyMMdd) + UUID8 생성
     * 
     * @param pre
     * @return
     */
    public static String createYmdUuid8(String pre) {
        String ymd = java.time.LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE).substring(2);
        return pre + ymd + createUuid8().substring(0, 8);
    }

    /**
     * 랜덤한 6자리 숫자 문자열 생성
     * 
     * @return 6자리 숫자 문자열
     */
    public static String random6DigitNumber() {
        int num = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(num);
    }

    /**
     * 8자리 UUID 문자열 생성
     * 
     * @return 8자리 UUID 문자열
     */
    public static String createUuid8() {
        String uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        // 첫 글자가 알파벳이 아닐 경우, 알파벳이 나올 때까지 반복
        int idx = 0;
        while (idx < uuid.length() && !Character.isLetter(uuid.charAt(idx))) {
            idx++;
        }
        // 알파벳이 발견되면 그 위치부터 8글자, 아니면 0부터 8글자
        if (idx + 8 <= uuid.length()) {
            return uuid.substring(idx, idx + 8);
        } else {
            // 만약 uuid 내에 알파벳이 없거나, 남은 길이가 8보다 짧으면 새로 생성
            return createUuid8();
        }
    }

    // #endregion

    // #region crypto
    /**
     * 입력 문자열의 SHA-512 해시값을 반환
     *
     * @param input 입력 문자열
     * @return SHA-512 해시값(16진수 문자열)
     */
    public static String sha512(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not found", e);
        }
    }

    /**
     * 입력 문자열의 SHA-256 해시값을 반환
     * ! 64비트 cpu 환경에서는 sha512가 더 빠를 수 있음. sha256은 32비트 cpu에서 더 빠름
     * 
     * @param plainText
     * @return 256비트 해시값(16진수 문자열)
     */
    public static String sha256(String plainText) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * AES 암호화
     * 
     * @param plainText 평문
     * @param key       16/24/32비트 키
     * @return 암호문(Base64)
     */
    public static String aesEncode(String plainText, String key) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("AES key is null or empty");
        }

        try {
            javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(java.nio.charset.StandardCharsets.UTF_8), "AES");
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encoding error", e);
        }
    }

    /**
     * AES 복호화
     * 
     * @param cipherText 암호문(Base64)
     * @param key        16/24/32바이트 키
     * @return 평문
     */
    public static String aesDecode(String cipherText, String key) {
        if (cipherText == null || cipherText.isEmpty()) {
            return "";
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("AES key is null or empty");
        }

        try {
            javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(java.nio.charset.StandardCharsets.UTF_8), "AES");
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = java.util.Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decoding error", e);
        }
    }

    // #endregion

    /**
     * 문자열에서 파일 확장자 추출
     * 
     * @param filename 파일명 또는 경로
     * @return 확장자 (점 포함), 없으면 빈 문자열
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * 문자열이 숫자로만 구성되어 있는지 확인
     * null 또는 빈 문자열은 false 반환
     * 
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }
}
