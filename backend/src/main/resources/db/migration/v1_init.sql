create table tenants (
  id uuid primary key,
  slug varchar(80) unique not null,
  name varchar(150) not null,
  created_at timestamptz not null default now()
);

create table users (
  id uuid primary key,
  tenant_id uuid not null references tenants(id),
  email varchar(200) not null,
  display_name varchar(200) not null,
  password_hash varchar(255),
  role varchar(40) not null,
  created_at timestamptz not null default now(),
  unique (tenant_id, email)
);

create table projects (
  id uuid primary key,
  tenant_id uuid not null references tenants(id),
  key varchar(20) not null,
  name varchar(200) not null,
  created_at timestamptz not null default now(),
  unique (tenant_id, key)
);

create table issues (
  id uuid primary key,
  tenant_id uuid not null references tenants(id),
  project_id uuid not null references projects(id),
  issue_key varchar(30) not null,
  title varchar(250) not null,
  description text,
  status varchar(40) not null,
  assignee_user_id uuid,
  created_at timestamptz not null default now(),
  unique (tenant_id, issue_key)
);

create index idx_users_tenant on users(tenant_id);
create index idx_projects_tenant on projects(tenant_id);
create index idx_issues_tenant on issues(tenant_id);
create index idx_issues_project on issues(project_id);
