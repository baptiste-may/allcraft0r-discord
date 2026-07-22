# 🤖 AGENTS.md — Repository Guidelines & Instructions for AI Assistants

Welcome to the **Allcraft0r Discord Bot** repository. This document serves as the authoritative guide for AI coding assistants (and human contributors) working on this codebase.

---

## 🎯 Project Identity & Principles

* **Community Hub**: Discord bot for the **Allcraft0r** Minecraft Redstone YouTube community.
* **Core Stack**: **Java 25**, **Spring Boot 4.1.0**, **JDA 6.4.2**, **PostgreSQL**, **Flyway**, **MapStruct**, **Lombok**, **Spotless**.
* **Quality Standard**: Zero compiler warnings (`-Xlint:all`), zero broken tests, strictly enforced Google Java Style formatting, clean architecture with explicit error handling.

---

## 🎨 Source Code Style & Idioms

All source code under `src/main/java` must strictly adhere to the developer's idiomatic style:

### 1. Modern Java & Variable Declarations
* **Local Variables**: Use `final var` for local variable declarations:
  ```java
  final var currentMoney = moneyService.executeDaily(event.getUser().getId());
  final var user = getOrCreateUser(userId);
  ```
* **String Formatting**: Prefer the modern `.formatted(...)` String instance method over `String.format(...)`:
  ```java
  "Tu as actuellement %s".formatted(discordConfig.formatRedstoneNumber(money))
  ```
* **Stream API & Functional Syntax**: Use Java 16+ `.toList()` on streams instead of `.collect(Collectors.toList())`. Favor method references where concise (`OptionMapping::getAsLong`, `userMapper::toUserMoneyDTO`).
* **Random Generation**: Always use thread-safe `ThreadLocalRandom.current()` instead of `Math.random()` or `new Random()` in async Discord interaction context.

### 2. Spring, Lombok & Import Patterns
* **Dependency Injection**: Use `private final` fields initialized via Lombok's `@RequiredArgsConstructor`. Avoid `@Autowired` or explicit constructor definitions:
  ```java
  @Component
  @Getter
  @RequiredArgsConstructor
  public class MoneyCommand extends SlashCommand {
    private final MoneyService moneyService;
    private final DiscordConfig discordConfig;
    ...
  }
  ```
* **SlashCommand Metadata**: Annotate command classes with `@Getter` and declare private fields for metadata so Lombok generates getters (`getName()`, `getDescription()`, `getType()`):
  ```java
  private final String name = "money";
  private final String description = "Affiche son nombre de redstones";
  private final CommandType type = CommandType.ECONOMY;
  ```
* **No Wildcard Imports**: Avoid wildcard imports (`import java.awt.*;`). Always use explicit single-class imports (e.g. `import java.awt.Color;`) per Google Java Style formatting.

### 3. Discord Messaging & Visuals
* **Localization**: User-facing command responses and descriptions **must be in French**.
* **Redstone Currency Display**: Always format currency using `discordConfig.formatRedstoneNumber(amount)` to maintain consistent custom redstone emoji formatting across all embeds.
* **Interaction Acknowledgment Safety**: Always check `event.isAcknowledged()` before replying to interactions in exception/catch blocks:
  ```java
  if (event.isAcknowledged()) {
    event.getHook().sendMessage(errorMessage).setEphemeral(true).queue();
  } else {
    event.reply(errorMessage).setEphemeral(true).queue();
  }
  ```
* **JDA Embeds & Markdown**: Chain embed builder methods fluently. When generating dynamic markdown headers, cap header levels at `Math.min(level, 3)` as Discord Markdown only supports up to `###` (Heading 3).

### 4. Exception Handling & Persistence
* **Domain Exceptions**: Define explicit domain exceptions under `fr.may_baptiste.allcraft0r_discord.system.exception` carrying state context (e.g., `CannotExecuteDailyException(LocalDateTime nextAvailable)`) rather than throwing raw or generic exceptions.
* **Entities & Provisioning**: JPA entities use `@Entity`, `@Data`, and `@Table(name = "snake_case_name")`. Service entity lookup helpers use clean functional patterns (e.g. `orElseGet(...)` for `getOrCreateUser`).

---

## 🏗️ Architecture & Component Design

### 1. Slash Command Pattern
All Discord slash commands **must**:
* Extend `fr.may_baptiste.allcraft0r_discord.core.SlashCommand`.
* Be annotated with `@Component` so Spring automatically registers them with JDA via `DiscordConfig`.
* Be placed in the appropriate subpackage under `fr.may_baptiste.allcraft0r_discord.commands`:
  * `admin` — Channel administration (`/lock`, `/unlock`).
  * `economy` — Redstone currency (`/daily`, `/money`, `/dashboard`).
  * `game` — Interactive games & gambling (`/spin`, `/blackjack`, `/roulette`).
  * `fun` — Entertainment commands (`/8ball`, `/eyes`, `/tank`).
  * `utils` — Utility & Creator hub (`/help`, `/links`, `/ping`, `/send`).
* Throw `CommandExecutionException` (or a sub-class in `system.exception`) for expected execution failures. The base `SlashCommand` handles logging and ephemeral error replies.

### 2. Database & Flyway Migrations
* Schema updates **must** use Flyway migration scripts in `src/main/resources/db/migration/` (e.g. `V1__init.sql`, `V2__add_new_feature.sql`).
* **Never edit an existing migration file** that has already been committed or applied. Always create a new versioned migration.
* JPA Entities belong in `fr.may_baptiste.allcraft0r_discord.system.entity`.

### 3. Services & Mappers
* Domain business logic belongs in `fr.may_baptiste.allcraft0r_discord.system.service`.
* Data transformation should use **MapStruct** mappers.
* Annotation Processor Order: Lombok annotation processor **must** run before MapStruct so generated getters/setters are available during compilation.

---

## 🧪 Verification & Development Workflow

Agents **must** run validation commands before considering any coding task complete:

### Mandatory Commands

```bash
# 1. Code Formatting Check & Fix
./gradlew spotlessCheck
./gradlew spotlessApply

# 2. Run Unit Tests
./gradlew test

# 3. Run Integration Tests
./gradlew integrationTest

# 4. Full Quality Check (Runs tests + integration tests + checks)
./gradlew check
```

### Test Structure & Conventions
* **Unit Tests**: Located under `src/test/java/`. Fast, isolated tests for domain models, renderers, mappers, and services.
* **Integration Tests**: Located under `src/integrationTest/java/`. Extends `AbstractIntegration`, boots Spring context with H2 database and mocked JDA.
* **Nested Display Names**: Do not place `@DisplayName` annotations on `@Nested` inner test classes; rely on descriptive class naming instead.

### Build & Compiler Settings
* All Java compilation tasks enable standard warnings (`-Xlint:all`). Do not introduce code that triggers unchecked or raw type warnings.

### Git Pre-Commit Hook
* The repository uses a pre-commit hook located at `scripts/hooks/pre-commit`.
* The hook is installed automatically when `./gradlew compileJava` or `./gradlew installGitHooks` is executed.
* It verifies code formatting with `spotlessCheck` and runs `./gradlew test` before every commit.

---

## 🚨 Guidelines for AI Agents

1. **Inspect before modifying**: Never guess class names, method signatures, or file paths. Always use inspection tools (`view_file`, `grep_search`).
2. **Do not swallow errors**: Never handle exceptions by returning silent nulls, dummy fallbacks, or empty arrays without explicit architectural reasons.
3. **Preserve existing functionality**: When adding or updating commands, ensure command parameters and interactions match Discord's expectations.
4. **Environment Variables**: If introducing a new configuration parameter, document it in `.env.example` and `src/main/resources/application.properties`.
5. **Format before finishing**: Always execute `./gradlew spotlessApply` if Java files were edited.

---

<p align="center">
  <i>Keep the code clean, the redstone powered, and the community engaged! ⚡</i>
</p>
