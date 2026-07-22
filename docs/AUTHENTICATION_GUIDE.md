# FreelanceHub Authentication System

Complete guide to the authentication and account management flows.

## System Architecture

### Components
- **OTP Service**: Generates, hashes, and verifies one-time passwords
- **Email Service**: Sends professional HTML-formatted emails
- **Email Template Service**: Creates reusable email templates
- **Auth Service**: Orchestrates authentication flows
- **OTP Request Entity**: Stores OTP verification attempts with expiry and attempt tracking
- **Temp Signup Entity**: Temporarily stores signup data during email verification

### Security Features
- ✅ OTPs are hashed with SHA-256 before storage (never stored in plain text)
- ✅ Constant-time comparison prevents timing attacks
- ✅ OTPs expire after 10 minutes (configurable)
- ✅ Maximum 5 verification attempts per OTP
- ✅ Automatic OTP invalidation when a new OTP is generated
- ✅ Passwords hashed with BCrypt
- ✅ JWT access tokens (15 min expiry) + refresh tokens (7 days, rotated)
- ✅ Email verified flag prevents login before email confirmation

---

## API Endpoints

### 1. User Signup (Email Verification Flow)

#### Step 1: Initiate Signup
**POST** `/api/auth/register`

```json
{
  "email": "john@example.com",
  "fullName": "John Doe",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "organizationName": "My Agency"  // Optional
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "verified": false,
    "message": "OTP sent to john@example.com"
  },
  "error": null,
  "timestamp": "2025-07-22T10:30:00Z"
}
```

**Backend Actions**:
- Validates password match
- Checks email not already registered
- Generates 6-digit OTP
- Hashes OTP with SHA-256
- Stores OTP in database with 10-minute expiry
- Stores signup data in temp_signups table
- Sends email with OTP

**Email Sent**:
- Contains OTP in large, monospace font
- Shows expiry time (10 minutes)
- Includes security warning
- Professional HTML template

---

#### Step 2: Verify Email OTP
**POST** `/api/auth/verify-email-otp`

```json
{
  "email": "john@example.com",
  "otp": "123456"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "opaque-token-string...",
    "expiresInSeconds": 900,
    "user": {
      "id": 1,
      "email": "john@example.com",
      "fullName": "John Doe",
      "role": "OWNER",  // or "MEMBER" if no organization
      "organizationId": 42
    }
  },
  "error": null,
  "timestamp": "2025-07-22T10:31:00Z"
}
```

**Error Cases**:
- **400 Bad Request**: Invalid or expired OTP
- **400 Bad Request**: Max verification attempts exceeded
- **404 Not Found**: Email not found in temp signups

**Backend Actions**:
1. Verifies OTP (uses constant-time comparison)
2. Checks OTP hasn't expired
3. Checks attempts < max_attempts
4. Retrieves signup data from temp_signups
5. Creates user with `email_verified = true`
6. If organizationName provided:
   - Creates new Organization
   - Sets user role to `OWNER`
   - Associates user with organization
7. If no organizationName:
   - Sets user role to `MEMBER`
   - User is independent (organizationId is NULL)
8. Deletes temp_signups record
9. Issues access + refresh tokens

---

### 2. User Login

**POST** `/api/auth/login`

```json
{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "opaque-token-string...",
    "expiresInSeconds": 900,
    "user": {
      "id": 1,
      "email": "john@example.com",
      "fullName": "John Doe",
      "role": "OWNER",
      "organizationId": 42
    }
  },
  "error": null,
  "timestamp": "2025-07-22T10:32:00Z"
}
```

**Error Cases**:
- **400 Bad Request**: Invalid email or password
- **403 Forbidden**: Email not verified

**Backend Actions**:
1. Looks up user by email
2. Checks email_verified = true
3. Verifies password with BCrypt
4. Issues access + refresh tokens

---

### 3. Forgot Password Flow

#### Step 1: Request Password Reset OTP
**POST** `/api/auth/forgot-password`

```json
{
  "email": "john@example.com"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "verified": false,
    "message": "Password reset OTP sent to john@example.com"
  },
  "error": null,
  "timestamp": "2025-07-22T10:33:00Z"
}
```

**Error Cases**:
- **404 Not Found**: User not found

**Backend Actions**:
1. Looks up user by email
2. Generates OTP for PASSWORD_RESET purpose
3. Invalidates any previous PASSWORD_RESET OTPs for this email
4. Sends password reset email with OTP

---

#### Step 2: Verify Reset OTP
**POST** `/api/auth/verify-reset-otp`

```json
{
  "email": "john@example.com",
  "otp": "654321"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "verified": true,
    "message": "OTP verified. Proceed to reset password."
  },
  "error": null,
  "timestamp": "2025-07-22T10:34:00Z"
}
```

**Error Cases**:
- **400 Bad Request**: Invalid or expired OTP
- **400 Bad Request**: Max verification attempts exceeded

**Backend Actions**:
1. Verifies OTP for PASSWORD_RESET purpose
2. Marks OTP as used
3. Returns success response

---

#### Step 3: Reset Password
**POST** `/api/auth/reset-password`

```json
{
  "email": "john@example.com",
  "password": "NewSecurePassword456!",
  "confirmPassword": "NewSecurePassword456!"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "verified": true,
    "message": "Password reset successfully. You can now log in."
  },
  "error": null,
  "timestamp": "2025-07-22T10:35:00Z"
}
```

**Error Cases**:
- **400 Bad Request**: Passwords don't match
- **404 Not Found**: User not found

**Backend Actions**:
1. Looks up user by email
2. Updates password with new hash
3. Password is now changed

---

### 4. Token Refresh

**POST** `/api/auth/refresh`

```json
{
  "refreshToken": "opaque-token-string..."
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "accessToken": "new-jwt-token...",
    "refreshToken": "new-opaque-token...",
    "expiresInSeconds": 900,
    "user": { ... }
  },
  "error": null,
  "timestamp": "2025-07-22T10:36:00Z"
}
```

**Backend Actions**:
1. Hashes refresh token
2. Looks up in database
3. Checks not revoked and not expired
4. Revokes old refresh token
5. Issues new tokens

---

### 5. Logout

**POST** `/api/auth/logout`

```json
{
  "refreshToken": "opaque-token-string..."
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "error": null,
  "timestamp": "2025-07-22T10:37:00Z"
}
```

**Backend Actions**:
1. Hashes refresh token
2. Marks as revoked in database
3. Session ends

---

## Database Schema

### users Table Changes
```sql
ALTER TABLE users ALTER COLUMN organization_id DROP NOT NULL;
ALTER TABLE users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT false;
```

### otp_requests Table
Stores OTP verification attempts with security tracking:
- `id`: Primary key
- `email`: User email (indexed)
- `otp_hash`: SHA-256 hashed OTP (never stores plain OTP)
- `purpose`: EMAIL_VERIFICATION or PASSWORD_RESET
- `expires_at`: OTP expiry time (indexed for cleanup)
- `used_at`: When OTP was successfully used
- `attempts`: Verification attempts count
- `max_attempts`: Maximum allowed attempts (default: 5)
- `is_active`: Soft delete flag
- `created_at`, `updated_at`: Timestamps

### temp_signups Table
Stores signup data during email verification:
- `id`: Primary key
- `email`: User email (unique, indexed)
- `full_name`: User's name
- `organization_name`: Organization name (optional)
- `password_hash`: BCrypt hashed password
- `expires_at`: When signup data expires (indexed)
- `created_at`: Creation timestamp

---

## Email Templates

### Email Verification Email
- **Subject**: Verify Your FreelanceHub Email
- **Contains**:
  - FreelanceHub logo/brand
  - Personalized greeting
  - Explanation of email verification
  - OTP in large, monospace font
  - Expiry time (e.g., "10 minutes")
  - Security warning
  - Professional footer

### Password Reset Email
- **Subject**: Reset Your FreelanceHub Password
- **Contains**:
  - FreelanceHub logo/brand
  - Personalized greeting
  - Explanation of password reset request
  - OTP in large, monospace font
  - Expiry time
  - Security warning about unsolicited resets
  - Professional footer

---

## Configuration

### application.properties

```properties
# Email Configuration (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

app.mail.enabled=true
app.mail.from=noreply@freelancehub.local

# OTP Configuration
app.otp.expiry-minutes=10
app.otp.length=6

# JWT Configuration (existing)
jwt.access-token-expiration-ms=900000
jwt.refresh-token-expiration-ms=604800000
```

### Email Service Configuration
- **SMTP Host**: Gmail (gmail.com) or your email provider
- **Port**: 587 (TLS)
- **Authentication**: Required
- **app.mail.enabled**: Set to `false` for development (logs OTP instead)

---

## User Types & Roles

### Organization Owner
Created when user signs up WITH an organizationName:
- Role: `OWNER`
- Has organizationId
- Can manage organization members
- Full control over organization

### Independent User / Member
Created when user signs up WITHOUT organizationName:
- Role: `MEMBER`
- organizationId is NULL
- Can work independently
- Can later join an organization (future feature)

### Organization Admin (Future)
Owner can promote members to ADMIN
- Manages organization
- Cannot delete organization (owner only)

---

## Error Handling

All authentication errors return structured responses:

```json
{
  "success": false,
  "data": null,
  "error": "Descriptive error message",
  "timestamp": "2025-07-22T10:38:00Z"
}
```

### Common HTTP Status Codes
- **201 Created**: Signup initiated
- **200 OK**: Successful login, OTP verified, tokens refreshed
- **400 Bad Request**: Invalid input, password mismatch, OTP errors
- **403 Forbidden**: Email not verified
- **404 Not Found**: User not found, signup data not found
- **500 Internal Server Error**: Email sending failed, DB errors

---

## Security Best Practices

### ✅ Implemented
1. **OTP Hashing**: SHA-256 hash stored, plain OTP never persisted
2. **Constant-Time Comparison**: Prevents timing attacks
3. **OTP Expiry**: 10 minutes default, configurable
4. **OTP Attempt Limiting**: Max 5 attempts before invalidation
5. **OTP Invalidation**: New OTP invalidates old ones
6. **No Plaintext Storage**: All passwords hashed with BCrypt
7. **Secure Tokens**: Opaque refresh tokens (not JWTs)
8. **Token Rotation**: Refresh tokens rotate on use
9. **Email Verification**: Prevents account takeover
10. **User Enumeration Prevention**: Same error message for all login failures

### 🔒 To Implement Later
- Rate limiting (e.g., max 3 OTP requests per hour)
- Account lockout after failed login attempts
- CAPTCHA for repeated OTP requests
- 2FA/MFA support
- Audit logging for auth events

---

## Testing the Flows

### With Email Disabled (Development)
Set `app.mail.enabled=false` in application.properties:
- OTPs are logged to console
- Check logs for OTP values
- Example: `OTP generated for user@example.com with purpose EMAIL_VERIFICATION`

### With Email Enabled (Production)
Configure SMTP credentials:
- Gmail: Use app-specific password
- Other providers: Use appropriate SMTP settings
- OTPs sent via email

---

## Frontend Integration

### Signup Flow (Frontend)

```typescript
// Step 1: Request OTP
const registerResponse = await fetch('/api/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    fullName: 'John Doe',
    password: 'SecurePassword123!',
    confirmPassword: 'SecurePassword123!',
    organizationName: 'My Agency'  // optional
  })
});
// Show OTP input screen

// Step 2: Verify OTP and register
const verifyResponse = await fetch('/api/auth/verify-email-otp', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    otp: '123456'
  })
});

const { data } = await verifyResponse.json();
// Store access token, refresh token in secure storage
// Redirect to dashboard
```

### Login Flow (Frontend)

```typescript
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'SecurePassword123!'
  })
});

const { data } = await loginResponse.json();
// Store tokens and redirect
```

### Forgot Password Flow (Frontend)

```typescript
// Step 1: Request reset
await fetch('/api/auth/forgot-password', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'user@example.com' })
});
// Show OTP input

// Step 2: Verify OTP
await fetch('/api/auth/verify-reset-otp', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    otp: '654321'
  })
});
// Show new password form

// Step 3: Reset password
await fetch('/api/auth/reset-password', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'NewPassword456!',
    confirmPassword: 'NewPassword456!'
  })
});
// Show login screen
```

---

## Files Modified/Created

### New Files Created
- `auth/entity/OtpRequest.java` - OTP storage entity
- `auth/entity/TempSignup.java` - Temporary signup data storage
- `auth/repository/OtpRequestRepository.java` - OTP repository
- `auth/repository/TempSignupRepository.java` - Temp signup repository
- `auth/service/OtpService.java` - OTP generation and verification
- `auth/dto/EmailOtpRequest.java` - Email OTP request DTO
- `auth/dto/VerifyEmailOtpRequest.java` - Email verification request DTO
- `auth/dto/VerifyResetOtpRequest.java` - Reset OTP verification DTO
- `auth/dto/ResetPasswordRequest.java` - Password reset request DTO
- `auth/dto/OtpVerificationResponse.java` - OTP verification response DTO
- `email/service/EmailService.java` - Email sending service
- `email/service/EmailTemplateService.java` - Email template generation
- `common/exception/InvalidOtpException.java`
- `common/exception/EmailNotVerifiedException.java`
- `common/exception/UserNotFoundException.java`
- `common/exception/PasswordMismatchException.java`
- `db/migration/V10__add_email_verification_and_otp.sql` - Database migrations

### Modified Files
- `auth/entity/User.java` - Added email_verified, made organizationId nullable
- `auth/dto/RegisterRequest.java` - Made organizationName optional, added confirmPassword
- `auth/service/AuthService.java` - Complete rewrite with OTP flows
- `auth/controller/AuthController.java` - New endpoints for OTP/password reset
- `pom.xml` - Added spring-boot-starter-mail dependency
- `application.properties` - Added email and OTP configuration

---

## Remaining Tasks

### Phase 1: Email Configuration (Required for Production)
- [ ] Set up SMTP credentials
- [ ] Test email delivery
- [ ] Customize email domain
- [ ] Set email branding/logo

### Phase 2: Frontend Updates (After Backend Testing)
- [ ] Create signup flow UI with OTP verification
- [ ] Create login UI
- [ ] Create forgot password UI
- [ ] Update existing registration form

### Phase 3: Rate Limiting & Security Hardening
- [ ] Add rate limiting (OTP requests, login attempts)
- [ ] Add account lockout after failed attempts
- [ ] Add CAPTCHA for repeated failures
- [ ] Add audit logging

### Phase 4: Advanced Features
- [ ] Email verification resend with cooldown
- [ ] 2FA/MFA support
- [ ] Social login
- [ ] Session management dashboard
