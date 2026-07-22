# Authentication API - Quick Reference

Copy-paste ready API examples for frontend integration.

## Base URL
```
http://localhost:8081/api
```

---

## Signup with Email Verification

### 1. Request OTP for Signup
```http
POST /auth/register
Content-Type: application/json

{
  "email": "john@example.com",
  "fullName": "John Doe",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "organizationName": "Acme Inc"
}
```

**Response (201 Created)**
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

---

### 2. Verify Email OTP & Complete Registration
```http
POST /auth/verify-email-otp
Content-Type: application/json

{
  "email": "john@example.com",
  "otp": "123456"
}
```

**Response (200 OK) - User Created as OWNER**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "x7k9m2p5q8r1s4t7u0v3w6y9z2a5b8c1",
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
  "timestamp": "2025-07-22T10:31:00Z"
}
```

**Response (200 OK) - User Created as MEMBER (no organization)**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "x7k9m2p5q8r1s4t7u0v3w6y9z2a5b8c1",
    "expiresInSeconds": 900,
    "user": {
      "id": 1,
      "email": "jane@example.com",
      "fullName": "Jane Smith",
      "role": "MEMBER",
      "organizationId": null
    }
  },
  "error": null,
  "timestamp": "2025-07-22T10:31:00Z"
}
```

---

## Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "x7k9m2p5q8r1s4t7u0v3w6y9z2a5b8c1",
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

**Response (400 Bad Request) - Invalid Credentials**
```json
{
  "success": false,
  "data": null,
  "error": "Invalid email or password",
  "timestamp": "2025-07-22T10:32:00Z"
}
```

**Response (403 Forbidden) - Email Not Verified**
```json
{
  "success": false,
  "data": null,
  "error": "Email john@example.com is not verified. Please verify your email before logging in.",
  "timestamp": "2025-07-22T10:32:00Z"
}
```

---

## Forgot Password

### 1. Request Password Reset OTP
```http
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "john@example.com"
}
```

**Response (200 OK)**
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

**Response (404 Not Found)**
```json
{
  "success": false,
  "data": null,
  "error": "User with email john@example.com not found",
  "timestamp": "2025-07-22T10:33:00Z"
}
```

---

### 2. Verify Reset OTP
```http
POST /auth/verify-reset-otp
Content-Type: application/json

{
  "email": "john@example.com",
  "otp": "654321"
}
```

**Response (200 OK)**
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

**Response (400 Bad Request) - Invalid OTP**
```json
{
  "success": false,
  "data": null,
  "error": "Invalid or expired OTP",
  "timestamp": "2025-07-22T10:34:00Z"
}
```

**Response (400 Bad Request) - Max Attempts Exceeded**
```json
{
  "success": false,
  "data": null,
  "error": "Invalid or expired OTP",
  "timestamp": "2025-07-22T10:34:00Z"
}
```

---

### 3. Reset Password
```http
POST /auth/reset-password
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "NewSecurePassword456!",
  "confirmPassword": "NewSecurePassword456!"
}
```

**Response (200 OK)**
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

**Response (400 Bad Request) - Passwords Don't Match**
```json
{
  "success": false,
  "data": null,
  "error": "Passwords do not match",
  "timestamp": "2025-07-22T10:35:00Z"
}
```

---

## Token Management

### Refresh Access Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "x7k9m2p5q8r1s4t7u0v3w6y9z2a5b8c1"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "new_opaque_token_string_after_rotation",
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
  "timestamp": "2025-07-22T10:36:00Z"
}
```

**Response (400 Bad Request) - Invalid/Expired Refresh Token**
```json
{
  "success": false,
  "data": null,
  "error": "Invalid refresh token",
  "timestamp": "2025-07-22T10:36:00Z"
}
```

---

### Logout
```http
POST /auth/logout
Content-Type: application/json

{
  "refreshToken": "x7k9m2p5q8r1s4t7u0v3w6y9z2a5b8c1"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": null,
  "error": null,
  "timestamp": "2025-07-22T10:37:00Z"
}
```

---

## Error Codes & Status Codes

| Status | Meaning | Common Causes |
|--------|---------|---------------|
| 201 | Created | Signup initiated, OTP sent |
| 200 | OK | Login success, OTP verified, token refreshed |
| 400 | Bad Request | Validation error, password mismatch, invalid OTP |
| 403 | Forbidden | Email not verified |
| 404 | Not Found | User not found, signup data expired |
| 500 | Server Error | Email sending failed, database error |

### Error Response Format
All errors follow this format:
```json
{
  "success": false,
  "data": null,
  "error": "Descriptive error message",
  "timestamp": "2025-07-22T10:37:00Z"
}
```

---

## Token Storage Recommendations

### Secure Storage (Recommended)
```javascript
// Use httpOnly cookie (most secure)
// Automatically sent by browser with each request
// Protected from XSS attacks

// Backend sets cookie:
Set-Cookie: accessToken=...; HttpOnly; Secure; SameSite=Strict
Set-Cookie: refreshToken=...; HttpOnly; Secure; SameSite=Strict
```

### LocalStorage (If necessary)
```javascript
// Store tokens
localStorage.setItem('accessToken', response.data.accessToken);
localStorage.setItem('refreshToken', response.data.refreshToken);

// Use in requests
const headers = {
  'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
};
```

### ⚠️ Never
- Store tokens in global state without encryption
- Send tokens in URL parameters
- Log tokens to console in production

---

## Making Authenticated Requests

### Using Access Token
All protected API endpoints require the access token:

```javascript
fetch('/api/clients', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${accessToken}`,
    'Content-Type': 'application/json'
  }
})
```

### Token Expiry Handling
1. Access token expires in 15 minutes
2. When API returns 401 Unauthorized:
   ```javascript
   const response = await fetch('/api/auth/refresh', {
     method: 'POST',
     headers: { 'Content-Type': 'application/json' },
     body: JSON.stringify({ refreshToken })
   });
   
   // Get new tokens from response
   // Retry original request
   ```

---

## Development Testing

### Disable Email (Development)
In `application.properties`:
```properties
app.mail.enabled=false
```

OTPs will be logged to console:
```
2025-07-22 10:30:00 - OTP generated for john@example.com with purpose EMAIL_VERIFICATION
OTP: 123456
```

### Enable Email (Staging/Production)
```properties
app.mail.enabled=true
spring.mail.host=smtp.gmail.com
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

---

## TypeScript Types

```typescript
// Login Response
interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresInSeconds: number;
  user: UserResponse;
}

// User Info
interface UserResponse {
  id: number;
  email: string;
  fullName: string;
  role: 'OWNER' | 'ADMIN' | 'MEMBER' | 'CLIENT';
  organizationId: number | null;
}

// OTP Response
interface OtpVerificationResponse {
  verified: boolean;
  message: string;
}

// Generic API Response
interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: string | null;
  timestamp: string;
}
```

---

## Curl Examples for Testing

### Signup
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Test User",
    "password": "TestPass123!",
    "confirmPassword": "TestPass123!",
    "organizationName": "Test Org"
  }'
```

### Verify OTP
```bash
curl -X POST http://localhost:8081/api/auth/verify-email-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123!"
  }'
```

### Refresh Token
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "token_here"
  }'
```

---

## Rate Limiting (Future Implementation)

Currently NOT implemented. Planned:
- Max 3 OTP requests per hour
- Max 5 failed login attempts per hour
- 15-minute lockout after failed attempts

---

## Security Notes

✅ **What's Protected**
- Passwords hashed with BCrypt (never stored in plain text)
- OTPs hashed with SHA-256 (never stored in plain text)
- Refresh tokens are opaque (random strings, not JWTs)
- Access tokens are short-lived (15 minutes)
- Email verification required before login
- Constant-time comparison prevents timing attacks

⚠️ **What's NOT Yet Protected**
- Rate limiting (coming soon)
- Account lockout (coming soon)
- 2FA/MFA (future feature)
- Audit logging (future feature)

🔒 **Client-Side Security**
- Always use HTTPS in production
- Store tokens securely (httpOnly cookies preferred)
- Implement logout when token expires
- Never expose tokens in logs or network tabs
