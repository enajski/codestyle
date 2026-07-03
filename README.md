# codestyle

Clojure code style tooling: linting, basic formatting, and advanced formatting.

## Tools

| Tool | Alias | Underlying library |
|------|-------|--------------------|
| Lint | `:lint` | clj-kondo |
| Basic format | `:basic-format` | cljfmt |
| Advanced format | `:advanced-format` | zprint |

## Usage

Run from your project directory. All tools default to `src`, `test`, `dev`, `api`
(and a few other common paths) when no paths are given.

### Lint

```bash
# Lint default paths
clojure -M:lint

# Lint specific paths
clojure -M:lint src/myapp test/myapp
```

### Basic Format (cljfmt)

```bash
# Check (default paths)
clojure -M:basic-format check

# Fix (default paths)
clojure -M:basic-format fix

# Check specific paths
clojure -M:basic-format check src/myapp

# Use custom config
clojure -M:basic-format check --config path/to/cljfmt.edn
```

Looks for `cljfmt.edn` in the project root automatically.

### Advanced Format (zprint)

```bash
# Check (default paths)
clojure -M:advanced-format check

# Fix (default paths)
clojure -M:advanced-format fix

# Fix specific path
clojure -M:advanced-format fix src/myapp/core.clj

# Use custom config
clojure -M:advanced-format fix --config path/to/.zprint.edn
```

Looks for `.zprint.edn` in the project root. Falls back to the bundled default config.

## Default zprint config

Located at `resources/codestyle/zprint.edn`. Key rules:

- **Width**: 100 characters
- **Maps**: one key-value per line, no commas
- **`let` / `loop` / `for` / `doseq` / `binding` / `with-open` / `with-redefs`**:
  binding name on its own line, value on next line, blank line between pairs.
  Single-pair exception: both on one line, no blank separator.
- **`cond->` / `cond->>`**: each test on its own line, value on next, blank line between clauses
- **`case`**: each dispatch value on its own line, result on next, blank line between clauses
- **`fn`**: signature vector on its own line before body
- **Comments**: preserved as-is, no reflowing

Example of `let` output:

```clojure
(let [resolved-rules
      (read-rules ctx)

      role
      (get-in ctx [:meta :user :role])]
  (do-something resolved-rules role))
```

## Adding to a project

Pick one coordinate style. The `:git/sha` and `:mvn/version` values below are
placeholders — replace `<SHA>` with a real commit hash and `<VERSION>` with a
published version.

### From git (no publish required)

```clojure
{:aliases
 {:lint            {:deps {io.github.enajski/codestyle
                           {:git/url "https://github.com/enajski/codestyle.git"
                            :git/sha "<SHA>"}}
                    :main-opts ["-m" "codestyle.lint"]}
  :basic-format    {:deps {io.github.enajski/codestyle
                           {:git/url "https://github.com/enajski/codestyle.git"
                            :git/sha "<SHA>"}}
                    :main-opts ["-m" "codestyle.basic-format"]}
  :advanced-format {:deps {io.github.enajski/codestyle
                           {:git/url "https://github.com/enajski/codestyle.git"
                            :git/sha "<SHA>"}}
                    :main-opts ["-m" "codestyle.advanced-format"]}}}
```

Latest SHA on `main`:

```bash
clojure -Sresolve-tags     # if you tag releases
# or
git ls-remote https://github.com/enajski/codestyle.git main
```

### Local checkout

```clojure
{:aliases
 {:lint            {:deps {io.github.enajski/codestyle
                           {:local/root "../codestyle"}}
                    :main-opts ["-m" "codestyle.lint"]}}}
```

### Published jar (after `clojure -T:build install` or a real release)

```clojure
{:aliases
 {:lint            {:deps {io.github.enajski/codestyle {:mvn/version "<VERSION>"}}
                    :main-opts ["-m" "codestyle.lint"]}
  :basic-format    {:deps {io.github.enajski/codestyle {:mvn/version "<VERSION>"}}
                    :main-opts ["-m" "codestyle.basic-format"]}
  :advanced-format {:deps {io.github.enajski/codestyle {:mvn/version "<VERSION>"}}
                    :main-opts ["-m" "codestyle.advanced-format"]}}}
```

Override the defaults with project-specific paths and settings by extending `:main-opts`:

```clojure
:main-opts ["-m" "codestyle.basic-format" "fix" "src" "test" "api"]
```

## CI integration

```bash
clojure -M:lint
clojure -M:basic-format check
clojure -M:advanced-format check
```

All tools exit non-zero on failure.

## Building locally

```bash
clojure -T:build jar
clojure -T:build install
```

Override version:

```bash
clojure -T:build install :app-version '"1.2.3"'
```
