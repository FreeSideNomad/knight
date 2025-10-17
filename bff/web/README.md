# BFF Web Module

Backend-for-Frontend (BFF) module providing screen-shaped REST endpoints for web UI.

## Purpose

Composes data from multiple bounded contexts to serve UI screens. Delegates commands to bounded context API interfaces.

## Architecture Constraints

- **ONLY depends on BC api modules** (NEVER domain or app layers)
- **NO business logic** (composition and delegation only)
- **Screen-shaped endpoints** (not entity-based CRUD)

## Dependencies

API interfaces from bounded contexts:
- `service-profile-management-api`
- `indirect-client-management-api`
- `policy-api`
- `users-api`
- `approval-engine-api`

## Endpoints

### Profile Composition
- `GET /api/profiles/servicing/{clientUrn}/summary` - Servicing profile overview
- `GET /api/profiles/online/{clientUrn}/{sequence}/dashboard` - Online profile dashboard
- `GET /api/profiles/indirect/{indirectClientUrn}/details` - Indirect client details

### User Management
- `GET /api/user-management/profiles/{profileUrn}/users` - Users for profile
- `POST /api/user-management/users` - Create user
- `GET /api/user-management/users/{userId}/permissions` - User permissions

### Approval Dashboard
- `GET /api/approvals/pending` - Pending approvals for current user
- `POST /api/approvals/{workflowId}/approve` - Approve workflow
- `POST /api/approvals/{workflowId}/reject` - Reject workflow

## Security

- OIDC client hooks (commented out, ready to enable)
- CSRF enabled for session-based auth
- All endpoints require authentication

## Configuration

- Port: 8080
- Component scan: `com.knight.bff.web`

## Testing

- ArchUnit tests enforce API-only dependencies
- Controller tests with MockMvc
- Security context mocking with `@WithMockUser`

## Running

```bash
cd bff/web
mvn spring-boot:run
```

Access at: http://localhost:8080
