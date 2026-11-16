-- The operational plan as a tree: plans hold objectives, objectives hold
-- activities, and activities hold the tasks a delivery is registered for.
--
-- Each row has a UUID primary key and a business code built from its
-- parent's: 2025-2030 / 2025-2030-01 / 2025-2030-01-03 / 2025-2030-01-03-0032.
-- The code is what other systems name a row by, so it is unique; the UUID is
-- what rows reference each other by. A plan, an objective, and an activity are
-- shown by their name beside the code, and the description says what they are
-- in longer form.
CREATE TABLE plans (
    id UUID PRIMARY KEY,
    code VARCHAR(9) NOT NULL UNIQUE,
    name TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE objectives (
    id UUID PRIMARY KEY,
    plan_id UUID NOT NULL REFERENCES plans (id),
    code VARCHAR(12) NOT NULL UNIQUE,
    name TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE activities (
    id UUID PRIMARY KEY,
    objective_id UUID NOT NULL REFERENCES objectives (id),
    code VARCHAR(15) NOT NULL UNIQUE,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    measurement_unit TEXT NOT NULL,
    target BIGINT NOT NULL CHECK (target >= 1)
);

-- kind says which kind of task a row holds. A follow-up task is planned for
-- one beneficiary, so its beneficiary_id is set when it is defined. A
-- delivery's submission_id and execution_date are set when it is registered
-- and cleared when it is rejected or relinked, so a pending task has neither.
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    activity_id UUID NOT NULL REFERENCES activities (id),
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    kind VARCHAR(40) NOT NULL CHECK (kind IN ('BENEFICIARY_FOLLOW_UP')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'DELIVERED', 'COMPLETED')),
    submission_id UUID,
    execution_date DATE,
    beneficiary_id TEXT,
    CHECK ((submission_id IS NULL) = (execution_date IS NULL)),
    CHECK ((status = 'PENDING') = (submission_id IS NULL)),
    CHECK (kind <> 'BENEFICIARY_FOLLOW_UP' OR beneficiary_id IS NOT NULL)
);

CREATE INDEX tasks_activity_status_idx ON tasks (activity_id, status);
