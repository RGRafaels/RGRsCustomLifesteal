# RGRs Custom Lifesteal Weapon

This plugin is designed for Purpur 1.21.11 and includes:

- Admin heart management commands (`/heartgive`, `/heartremove`)
- Player withdraw command (`/heartwithdraw`)
- Custom heart item recipe
- Custom revive block recipe
- `/heart reload` config support
- `/heartrevive <player>` command for reviving death-banned players
- Maximum heart cap of 20
- Revival starts players at 5 hearts

## Commands

- `/heartgive <player> <amount>` - Give hearts to another player
- `/heartremove <player> <amount>` - Remove hearts from a player
- `/heartwithdraw` - Withdraw one of your own hearts into an item
- `/recipe` - Show craft recipes
- `/heartrevive <player>` - Revive a death-banned player
- `/heart reload` - Reload the config

## Recipes

### Heart recipe

- Center: Netherite block
- Corners: Gold blocks
- Remaining side slots: Diamonds

### Revive block recipe

- Center: Netherite star
- Corners: Diamond blocks
- Remaining side slots: Netherite ingots

## Build

```bash
gradlew build
```

Place the generated jar in your Purpur plugins folder and restart the server.

## Notes

This is a source-ready plugin project and is intended for a Purpur 1.21.11 server.
A full resource pack for custom textures can be added later if you want an even more advanced visual style.

## Credits

Created by RGRafaels.
