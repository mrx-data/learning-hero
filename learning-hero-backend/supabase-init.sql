-- 学习英雄数据库初始化脚本
-- 请在 Supabase SQL Editor 中执行此脚本

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    openid VARCHAR(64) UNIQUE NOT NULL,
    nick_name VARCHAR(64),
    avatar_url VARCHAR(512),
    total_questions INT DEFAULT 0,
    correct_count INT DEFAULT 0,
    study_days INT DEFAULT 0,
    last_study_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建题目表
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    topic VARCHAR(128) NOT NULL,
    question TEXT NOT NULL,
    option_a VARCHAR(512) NOT NULL,
    option_b VARCHAR(512) NOT NULL,
    option_c VARCHAR(512) NOT NULL,
    option_d VARCHAR(512) NOT NULL,
    answer INT NOT NULL,
    explanation TEXT,
    difficulty VARCHAR(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建学习记录表
CREATE TABLE IF NOT EXISTS study_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic VARCHAR(128) NOT NULL,
    difficulty VARCHAR(16) NOT NULL,
    total_questions INT NOT NULL,
    correct_count INT NOT NULL,
    score INT NOT NULL,
    duration INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建答题明细表
CREATE TABLE IF NOT EXISTS answer_details (
    id BIGSERIAL PRIMARY KEY,
    record_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_answer INT,
    is_correct INT NOT NULL,
    answer_time INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建错题表
CREATE TABLE IF NOT EXISTS wrong_answers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    wrong_count INT DEFAULT 1,
    last_wrong_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建成就表
CREATE TABLE IF NOT EXISTS achievements (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    icon_url VARCHAR(512),
    condition_type VARCHAR(32) NOT NULL,
    condition_value INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户成就表
CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_users_openid ON users(openid);
CREATE INDEX IF NOT EXISTS idx_questions_topic ON questions(topic);
CREATE INDEX IF NOT EXISTS idx_questions_difficulty ON questions(difficulty);
CREATE INDEX IF NOT EXISTS idx_study_records_user_id ON study_records(user_id);
CREATE INDEX IF NOT EXISTS idx_study_records_created_at ON study_records(created_at);
CREATE INDEX IF NOT EXISTS idx_answer_details_record_id ON answer_details(record_id);
CREATE INDEX IF NOT EXISTS idx_wrong_answers_user_id ON wrong_answers(user_id);
CREATE INDEX IF NOT EXISTS idx_user_achievements_user_id ON user_achievements(user_id);

-- 插入初始成就数据
INSERT INTO achievements (code, name, description, condition_type, condition_value) VALUES
('FIRST_STUDY', '初出茅庐', '完成首次学习', 'STUDY_COUNT', 1),
('STUDY_10', '学海无涯', '累计学习 10 次', 'STUDY_COUNT', 10),
('QUESTION_100', '百题斩将', '累计答题 100 道', 'QUESTION_COUNT', 100),
('QUESTION_500', '千题大师', '累计答题 500 道', 'QUESTION_COUNT', 500),
('STREAK_7', '连续学霸', '连续学习 7 天', 'STREAK_DAYS', 7),
('PERFECT', '满分达人', '单次答题全部正确', 'PERFECT_SCORE', 1)
ON CONFLICT (code) DO NOTHING;
