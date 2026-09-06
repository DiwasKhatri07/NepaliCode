# NepaliCode architecture

NepaliCode is organized as a layered Android application. The user interface is built with Jetpack Compose, while the Nepali language engine remains a focused Kotlin subsystem that can be tested independently from individual screens.

## Presentation layer

The `nepalicode/ui` package contains the main screens and the `NepaliCodeViewModel`. Screens represent editor, project files, terminal, package, reference, theme, and automation experiences. Reusable dialogs and navigation-scale components live under `nepalicode/ui/components`.

## Editor layer

The `nepalicode/editor` package owns tokenization, syntax highlighting, completion behavior, editor-specific themes, and text-field integration. It translates language-engine concepts into responsive editing behavior without making the UI responsible for parsing rules.

## Language layer

The `nepalilang/core` package contains the language model, lexer, parser, AST, interpreter, linter, runtime environment, standard library, values, and token definitions. A feature that changes syntax should normally be implemented here first, then exposed through editor services and examples.

## Data and persistence

The `nepalicode/data` package models projects and files. Room and Kotlin coroutines provide the persistence and asynchronous foundations required for a mobile workspace.

## Build and integrations

Gradle Kotlin DSL manages Android plugins, Kotlin Compose, KSP, Room code generation, Moshi generation, Firebase integrations, and release packaging. Environment-backed secrets are loaded through the secrets Gradle plugin. Public source excludes `.env`, keystores, and generated build directories.

## Design principles

NepaliCode favors explicit code, learner-friendly diagnostics, small composable features, accessibility-aware UI, and changes that can be tested from the command line. The project should remain approachable to a contributor who is learning Android or language implementation.
