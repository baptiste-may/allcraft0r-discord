# ⚡ Allcraft0r Discord Bot

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg?style=for-the-badge&logo=openjdk)](https://jdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F.svg?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![JDA](https://img.shields.io/badge/JDA-6.4.2-5865F2.svg?style=for-the-badge&logo=discord)](https://github.com/discord-jda/JDA)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1.svg?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![Code Style](https://img.shields.io/badge/Code%20Style-Spotless-4285F4.svg?style=for-the-badge)](https://github.com/diffplug/spotless)

> Official Discord bot for **Allcraft0r**'s YouTube community—the ultimate hub for Minecraft Redstone engineers, gamers, and community members. Built with **Spring Boot 4**, **Java 25**, and **JDA 6**.

---

## 🌟 Features Overview

### 🔴 Redstone Economy
* **`/daily`** — Claim your daily Redstone reward streak.
* **`/money`** — Check your current Redstone balance and currency status.
* **`/dashboard`** — View community economy statistics and your user profile overview.

### 🎰 Minigames & Gambling
* **`/spin`** — Test your luck on the Redstone spin wheel! Wager your redstone dust to multiply your earnings.
* **`/blackjack`** — Play interactive Blackjack against the house with real-time rendered felt table images.
* **`/roulette`** — Place redstone bets on color, parity, or green zero with rendered roulette wheel graphics.

### 🎭 Entertainment & Fun
* **`/8ball`** — Ask the magic 8-ball any question about redstone circuits, life, or Minecraft.
* **`/eyes`** — Send expressive staring eyes into the channel.
* **`/tank`** — Deploy the ASCII Redstone Tank.

### 🛡️ Administration
* **`/lock`** — Lock a text channel to prevent messages during maintenance.
* **`/unlock`** — Unlock a previously locked text channel.

### 🛠️ Utilities & Creator Hub
* **`/links`** — Quick links to Allcraft0r's YouTube channel, social media, and redstone tutorials.
* **`/help`** — Interactive command guide and bot documentation.
* **`/ping`** — Check API gateway latency and heartbeat.
* **`/send`** — Admin command for channel announcements and message relays.

---

## 🛠️ Technology Stack

* **Language**: Java 25 (Latest LTS / Bleeding Edge Features)
* **Framework**: Spring Boot `4.1.0` (with Actuator & Spring Data JPA)
* **Discord Library**: JDA (Java Discord API) `6.4.2`
* **Database**: PostgreSQL with **Flyway** schema migrations
* **Data Mapping & Boilerplate**: Lombok & MapStruct `1.6.3`
* **Code Formatting**: Spotless (Google Java Format)
* **Deployment**: Docker multi-stage container build (Coolify ready)

---

## ⚙️ Configuration & Environment Variables

Copy `.env.example` to `.env` or set the following environment variables in your environment/container:

```env
# ── Discord Configuration ──────────────────────────────────────────────────────
DISCORD_BOT_TOKEN=your_discord_bot_token_here
DISCORD_GUILD_ID=123456789012345678
DISCORD_ADMIN_CHANNEL_ID=123456789012345678
DISCORD_REDSTONE_EMOJI_ID=123456789012345678

# ── Database Configuration ─────────────────────────────────────────────────────
DB_HOST=localhost
DB_PORT=5432
DB_NAME=allcraft0r
DB_USER=allcraft0r_user
DB_PASSWORD=your_secure_password

# Optional: Full JDBC URL override
# SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/allcraft0r

# ── Application Configuration ──────────────────────────────────────────────────
PORT=8080
```

---

## 🚀 Getting Started

### Prerequisites

* **JDK 25** installed
* **PostgreSQL** instance running locally or via Docker
* **Discord Bot Token** with relevant Slash Command permissions

### Running Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/baptiste-may/allcraft0r-discord.git
   cd allcraft0r-discord
   ```

2. **Setup environment variables**:
   ```bash
   cp .env.example .env
   # Edit .env with your credentials
   ```

3. **Build & Run Application**:
   ```bash
   ./gradlew bootRun
   ```

4. **Health Check**:
   Once started, verify the actuator endpoint:
   ```bash
   curl http://localhost:8080/health
   ```

---

## 🧪 Testing & Quality Assurance

This project enforces strict code style guidelines and automated git pre-commit hooks via **Spotless**.

* **Run Unit Tests**:
  ```bash
  ./gradlew test
  ```

* **Run Integration Tests**:
  ```bash
  ./gradlew integrationTest
  ```

* **Check Code Formatting**:
  ```bash
  ./gradlew spotlessCheck
  ```

* **Apply Automatic Code Formatting**:
  ```bash
  ./gradlew spotlessApply
  ```

* **Install Git Pre-commit Hooks**:
  *(Hooks are automatically installed during `./gradlew compileJava`)*
  ```bash
  ./gradlew installGitHooks
  ```

---

## 🐳 Docker & Container Deployment

### Build Docker Image

```bash
docker build -t allcraft0r-discord:latest .
```

### Run with Docker

```bash
docker run -d \
  --name allcraft0r-bot \
  -p 8080:8080 \
  --env-file .env \
  allcraft0r-discord:latest
```

> **Note for Coolify**: The project includes container optimization flags (`-XX:+UseContainerSupport`, `-XX:MaxRAMPercentage=75.0`) and runs as a non-root user (`appuser`). Simply connect your repository in Coolify, specify `.env` variables, and deploy!

---

## 📁 Project Structure

```
allcraft0r_discord/
├── .github/                  # GitHub Actions CI workflows
├── scripts/                  # Helper scripts & Git hooks
│   └── hooks/pre-commit      # Pre-commit hook (Spotless + Tests)
├── src/
│   ├── main/
│   │   ├── java/fr/may_baptiste/allcraft0r_discord/
│   │   │   ├── Allcraft0rDiscordApplication.java  # Main application entry point
│   │   │   ├── commands/                          # Discord Slash Command handlers
│   │   │   │   ├── economy/                       # Daily, Money, Dashboard
│   │   │   │   ├── fun/                           # 8Ball, Eyes, Tank
│   │   │   │   ├── game/                          # Spin game
│   │   │   │   └── utils/                         # Help, Links, Ping, Send
│   │   │   ├── config/                            # Discord & App configuration
│   │   │   ├── core/                              # Core command abstractions
│   │   │   └── system/                            # Entities, Services, Exceptions
│   │   └── resources/
│   │       ├── application.properties             # Spring Configuration
│   │       └── db/migration/                      # Flyway SQL migrations
│   └── test/                                      # Unit tests
│   └── integrationTest/                           # Integration tests
├── build.gradle              # Gradle dependencies & tasks definition
├── Dockerfile                # Multi-stage Docker build file
├── AGENTS.md                 # AI Agent Guidelines & Architecture Rules
├── LICENSE                   # MIT License
└── README.md                 # Project Documentation
```

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more details.

---

<p align="center">
  Crafted with ❤️ for the <b>Allcraft0r</b> Community.
</p>
