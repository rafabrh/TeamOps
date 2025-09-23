-- projetos e equipes
CREATE TABLE IF NOT EXISTS projects (
                                        id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name            varchar(120) NOT NULL,
    description     varchar(1000) NOT NULL,
    start_date      date NOT NULL,
    end_date_planned date,
    status          varchar(32) NOT NULL, -- PLANNED, IN_PROGRESS, DONE, CANCELED
    manager_id      uuid NOT NULL REFERENCES users(id),
    created_at      timestamptz NOT NULL DEFAULT now(),
    updated_at      timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT chk_project_status CHECK (status IN ('PLANNED','IN_PROGRESS','DONE','CANCELED'))
    );

CREATE TABLE IF NOT EXISTS teams (
                                     id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name        varchar(120) NOT NULL UNIQUE,
    description varchar(500) NOT NULL,
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS team_members (
                                            team_id uuid NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (team_id, user_id)
    );

CREATE TABLE IF NOT EXISTS project_teams (
                                             project_id uuid NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    team_id    uuid NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, team_id)
    );
