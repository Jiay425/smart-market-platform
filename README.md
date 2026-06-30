# Smart Market Platform

Smart Market Platform is a high-concurrency intelligent marketing system based on raffle campaigns, user quota accounts, reward delivery, behavior rebate, points trading, asynchronous stock settlement, and reliable MQ compensation.

## Core Capabilities

- Raffle strategy engine with rule chains and decision trees
- Activity quota accounts with total, monthly, and daily limits
- Redis-based stock pre-deduction with asynchronous database settlement
- Behavior rebate and points account trading
- Local message table, RabbitMQ publisher confirm, consumer retry, DLQ, and DLQ compensation
- Planned extension: user-segment-driven smart strategy routing

## Local Configuration

Runtime profile files are ignored by Git to avoid committing local database, Redis, RabbitMQ, and XXL-JOB addresses.

Before starting the application, copy one of the example files and adjust it for your local environment:

```bash
cp smart-market-app/src/main/resources/application-dev.example.yml smart-market-app/src/main/resources/application-dev.yml
```

The same convention applies to `application-prod.yml` and `application-perf.yml`.

## Project Direction

The next major feature is a marketing decision layer:

```text
user -> user segment / risk score / strategy router -> dynamic strategyId -> raffle engine
```

This turns the original fixed-strategy raffle flow into a user-segment-aware intelligent marketing platform.
