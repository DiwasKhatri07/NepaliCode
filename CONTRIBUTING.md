# Contributing to NepaliCode

Thank you for helping build Nepali-first developer tools. Contributions can improve the language engine, Android interface, accessibility, examples, tests, documentation, or release process.

## Before you begin

Please search existing issues and pull requests before opening a new one. For substantial changes, open an issue first so the design can be discussed before implementation begins.

## Local setup

1. Install Android Studio, Android SDK Platform 36, Build Tools 36, and Java 21.
2. Clone the repository and open it in Android Studio.
3. Copy `.env.example` to `.env` when AI-backed features are needed.
4. Never commit API keys, keystores, generated build folders, or personal machine configuration.

## Development loop

Use a focused branch for each change:

```bash
git checkout -b feat/short-description
./gradlew test
./gradlew assembleDebug
git diff --check
```

Keep pull requests small enough to review. Explain the motivation, summarize the implementation, describe testing, and include screenshots or a short recording for visible UI changes.

## Language changes

A new language feature should include an example program, lexer/token changes when needed, parser or AST coverage, interpreter behavior, linter behavior where appropriate, and documentation. Favor clear diagnostics and predictable behavior over clever shortcuts.

## Commit messages

Use concise imperative messages such as `Add list literal parsing` or `Improve file manager empty state`. Avoid mixing unrelated refactors with feature changes.

## Pull request checklist

- The change is scoped and documented.
- Tests or a clear manual verification process are included.
- `./gradlew test` passes locally when applicable.
- `./gradlew assembleDebug` succeeds.
- No secrets, keystores, or generated build artifacts are included.
- User-facing strings and accessibility descriptions are considered.
- Screenshots are included for meaningful UI changes.

## Questions

Open a discussion or issue with a clear title, reproduction steps, expected behavior, and relevant logs. Please follow the [Code of Conduct](CODE_OF_CONDUCT.md).
