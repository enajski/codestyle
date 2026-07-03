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
