# Codex Project Instructions

## Continuity

- At the start of each task, check whether `docs/codex-worklog.md` exists. If it exists, read it before making changes.
- At the end of each completed task, update `docs/codex-worklog.md` with the relevant history, pending work, decisions, and verification performed.
- Keep the worklog concise and useful for handoff between sessions.
- Do not include secrets, credentials, API keys, keystore values, tokens, private endpoints, or full contents from sensitive local files.

## Sensitive Files

- Avoid reading or editing `secrets.properties`, `keystore.properties`, `google-services.json`, and similar local credential files unless the user explicitly asks for it and the task truly requires it.
- Prefer documenting secret-related requirements by filename and purpose only.

## Git Hygiene

- `docs/codex-worklog.md` is intended to be local/private and is ignored by Git.
- `AGENTS.md` can be committed because it should contain only general project instructions.
