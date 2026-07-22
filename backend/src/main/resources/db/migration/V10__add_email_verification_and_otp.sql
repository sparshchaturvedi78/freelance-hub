-- Add email verification tracking to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT false;

-- Make organization_id nullable to support independent users
ALTER TABLE users ALTER COLUMN organization_id DROP NOT NULL;

-- Create OTP requests table for password resets and email verification
CREATE TABLE IF NOT EXISTS otp_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(50) NOT NULL CHECK (purpose IN ('EMAIL_VERIFICATION', 'PASSWORD_RESET')),
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    attempts INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL DEFAULT 5,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Create indexes for efficient OTP lookups
CREATE INDEX idx_otp_requests_email ON otp_requests(email);
CREATE INDEX idx_otp_requests_expires_at ON otp_requests(expires_at);
CREATE INDEX idx_otp_requests_is_active ON otp_requests(is_active);

-- Create temporary signups table for storing signup data during email verification
CREATE TABLE IF NOT EXISTS temp_signups (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    organization_name VARCHAR(255),
    password_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Create index for cleanup and lookups
CREATE INDEX idx_temp_signups_email ON temp_signups(email);
CREATE INDEX idx_temp_signups_expires_at ON temp_signups(expires_at);