-- V2: Add JPA enhancements (profile_urn column already exists, add missing if needed)
-- This migration adds any missing columns for JPA entities

-- Add profile_urn foreign key to service_enrollments if not exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_service_enrollments_profile'
    ) THEN
        ALTER TABLE service_enrollments
        ADD CONSTRAINT fk_service_enrollments_profile
        FOREIGN KEY (profile_urn) REFERENCES servicing_profiles(profile_urn)
        ON DELETE CASCADE;
    END IF;
END $$;

-- Add profile_urn to account_enrollments if not exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'account_enrollments'
        AND column_name = 'profile_urn'
    ) THEN
        ALTER TABLE account_enrollments
        ADD COLUMN profile_urn VARCHAR(255) NOT NULL
        REFERENCES servicing_profiles(profile_urn) ON DELETE CASCADE;

        CREATE INDEX idx_account_enrollments_profile ON account_enrollments(profile_urn);
    END IF;
END $$;

-- Ensure version columns exist (should be from V1, but adding safety check)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'servicing_profiles'
        AND column_name = 'version'
    ) THEN
        ALTER TABLE servicing_profiles ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
    END IF;
END $$;
