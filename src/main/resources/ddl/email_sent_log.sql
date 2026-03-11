-- EmailSentLog 테이블 DDL (PostgreSQL)
-- 이메일 발송 로그를 저장하는 테이블

CREATE TABLE email_sent_log (
    email_sent_log_id VARCHAR(50) NOT NULL,
    email_type VARCHAR(50),
    from_email_address VARCHAR(255),
    to_email_address VARCHAR(255),
    email_title VARCHAR(500),
    success_yn VARCHAR(1),
    error_content VARCHAR(500),
    atchmnfl_group_id VARCHAR(50),
    register_id VARCHAR(50),
    regist_dt TIMESTAMP,
    
    CONSTRAINT pk_email_sent_log PRIMARY KEY (email_sent_log_id)
);

-- 테이블 코멘트
COMMENT ON TABLE email_sent_log IS '이메일 발송 로그';

-- 컬럼 코멘트
COMMENT ON COLUMN email_sent_log.email_sent_log_id IS '이메일 발송 로그 ID (Primary Key)';
COMMENT ON COLUMN email_sent_log.email_type IS '메일 유형 (EmailType)';
COMMENT ON COLUMN email_sent_log.from_email_address IS '발신자 이메일 (AES 암호화)';
COMMENT ON COLUMN email_sent_log.to_email_address IS '수신자 이메일 (AES 암호화)';
COMMENT ON COLUMN email_sent_log.email_title IS '메일 제목';
COMMENT ON COLUMN email_sent_log.success_yn IS '발신 성공 여부 (Y: 성공, N: 실패)';
COMMENT ON COLUMN email_sent_log.error_content IS '발송 실패 내용';
COMMENT ON COLUMN email_sent_log.atchmnfl_group_id IS '첨부파일 그룹 ID';
COMMENT ON COLUMN email_sent_log.register_id IS '등록자 ID';
COMMENT ON COLUMN email_sent_log.regist_dt IS '등록 일시';

-- 인덱스 (필요시 추가)
-- CREATE INDEX idx_email_sent_log_email_type ON email_sent_log(email_type);
-- CREATE INDEX idx_email_sent_log_regist_dt ON email_sent_log(regist_dt);
-- CREATE INDEX idx_email_sent_log_register_id ON email_sent_log(register_id);
