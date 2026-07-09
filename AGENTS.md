# codestyle — Agent Instructions

Standalone Clojure code style tooling. Wraps clj-kondo, cljfmt, and zprint
behind three `-main` entry points under the `codestyle.*` namespace.

## Scope

This repo has no runtime dependencies on any application framework. Do not
introduce any. It is a formatter / linter shipped as a library.

Coordinate: `io.github.enajski/codestyle`.

## Layout

- `src/codestyle/common.clj` — path resolution, arg parsing, shared helpers
- `src/codestyle/lint.clj` — clj-kondo entry point
- `src/codestyle/basic_format.clj` — cljfmt entry point
- `src/codestyle/advanced_format.clj` — zprint entry point
- `resources/codestyle/zprint.edn` — bundled default zprint config
- `build.clj` — jar / install via `clojure.tools.build`

## Documentation

Don't over-document. README covers usage. This file covers conventions.

## General

- No private functions.
- `declare` is a code smell.
- In tests prefer `nubank/matcher-combinators` `match?` over lots of `is` assertions.
- Update function callers instead of adding a new arity to a function.
- Prefer transducing variants of functions.

# Agent Instructions: Bridge Verification Workflow

You are expected to use the Bridge CLI to maintain verification-aware alignment between code, specs, tests, and evidence.

## Core Command
Always use `bb bridge` instead of `clojure -M:bridge` for faster startup.

## Prerequisites
- `bb` (babashka) must be installed.

## Verification Workflow
When the user asks to "verify this change", "check obligations", "run bridge", "analyze change", or after significant code changes and always before committing code, follow these phases:

### Phase 1: Analyze Change
Identify what changed and what obligations arise.
```bash
bb bridge next
```

### Phase 2: Run evidences

List and run evidence.
```bash
bb bridge list-evidence
bb bridge run-evidence --id <id>
```

### Phase 3/1: Iterate until converged

Run `bb bridge next` until no remaining open obligations.

## Important Constraints
- **DO NOT modify .bridge files unless instructed.** Bridge is for tracking and analysis, not for code modification.
- **Convergence is key.** If `bb bridge next` reports `regressed`, you must loop back to Phase 1.
