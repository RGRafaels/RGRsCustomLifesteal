# RGRs Custom Lifesteal Weapon

A Purpur 1.21.11 plugin project for custom hearts, commands, recipes, and revival.

## Requirements

- Purpur 1.21.11
- Java 21

Important: do not use Java 25 with Purpur 1.21.11. The server runtime is Java 21 compatible.

## Build

```bash
./gradlew clean build
```

## Commands

- `/heart` - main plugin info and reload
- `/heartgive <player> <amount>`
- `/heartremove <player> <amount>`
- `/heartwithdraw`
- `/heartrevive <player>`
- `/recipe`

## Features

- max hearts: 20
- start hearts: 10
- revive start hearts: 5
- custom heart item recipe
- custom revive block recipe
- death-banned player management

Place the generated jar in your Purpur `plugins` folder and restart the server.
