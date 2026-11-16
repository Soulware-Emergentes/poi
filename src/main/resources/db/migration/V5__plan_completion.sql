-- Whether each activity, objective and plan is complete. An activity is
-- complete once it has tasks and every one of them is completed, an objective
-- once it has activities and all are complete, and a plan once it has
-- objectives and all are complete. A task defined later opens them again.
--
-- The plan learns of tasks through events, so it keeps the tasks each activity
-- still has open in activity_open_tasks, and has_tasks records that an
-- activity ever had one. task_id carries no foreign key: tasks are another
-- aggregate, and the plan only holds their identity.
ALTER TABLE plans ADD COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE objectives ADD COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE activities
    ADD COLUMN has_tasks BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE activity_open_tasks (
    activity_id UUID NOT NULL REFERENCES activities (id) ON DELETE CASCADE,
    task_id UUID NOT NULL,
    PRIMARY KEY (activity_id, task_id)
);

-- The tasks already defined, counted the way the events would have.
INSERT INTO activity_open_tasks (activity_id, task_id)
SELECT activity_id, id
FROM tasks
WHERE status <> 'COMPLETED';

UPDATE activities a
SET has_tasks = EXISTS (SELECT 1 FROM tasks t WHERE t.activity_id = a.id);

UPDATE activities a
SET completed = a.has_tasks
    AND NOT EXISTS (SELECT 1 FROM activity_open_tasks o WHERE o.activity_id = a.id);

UPDATE objectives o
SET completed = EXISTS (SELECT 1 FROM activities a WHERE a.objective_id = o.id)
    AND NOT EXISTS (SELECT 1 FROM activities a WHERE a.objective_id = o.id AND NOT a.completed);

UPDATE plans p
SET completed = EXISTS (SELECT 1 FROM objectives o WHERE o.plan_id = p.id)
    AND NOT EXISTS (SELECT 1 FROM objectives o WHERE o.plan_id = p.id AND NOT o.completed);
