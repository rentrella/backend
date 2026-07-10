---
name: commit
description: Analyzes Git changes to separate commits by function and purpose, and applies Conventional Commit rules to maintain a clean Git history.
---

# Commit Manager

## Goal

Create clean, maintainable Git history by grouping changes into logical commits.

## Instructions

### Analyze changes first

Always inspect changes before creating commits.

```bash
git status
git diff
git diff --staged
```

### Split commits by purpose

If multiple unrelated changes exist, create one commit per purpose. Stage only the files that belong to that purpose (`git add <file> ...`) before each commit — do not `git add -A` when unrelated changes are mixed in the working tree.

Examples:

Good — split by purpose (2 purposes → 2 commits):

* Commit 1: Add login API
* Commit 2: Update README

Bad — unrelated purposes squashed into one commit:

* Add login API + Update README + Change Docker configuration → all in a single commit

### Conventional Commits

커밋 메시지는 **한국어**로 작성한다.

형식:

```text
feat: 로그인 엔드포인트 추가
refactor: 인증 서비스 분리
chore: 의존성 업데이트
```

### Never

* Create giant commits
* Mix frontend and backend changes
* Mix feature work and refactoring
* Commit dependency directories (`node_modules`, `.gradle`, `vendor`, etc.)
* Commit `.env` or other secret/credential files
* Commit build artifacts (`build/`, `dist/`, `target/`, etc.)
