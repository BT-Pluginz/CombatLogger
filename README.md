# BT's CombatLogger
![Static Badge](https://img.shields.io/badge/MC-1.13-green)
![Static Badge](https://img.shields.io/badge/MC-1.14-green)
![Static Badge](https://img.shields.io/badge/MC-1.15-green)
![Static Badge](https://img.shields.io/badge/MC-1.16-green)
![Static Badge](https://img.shields.io/badge/MC-1.17-green)
![Static Badge](https://img.shields.io/badge/MC-1.18-green)
![Static Badge](https://img.shields.io/badge/MC-1.19-green)
![Static Badge](https://img.shields.io/badge/MC-1.20-green)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/qiyG0tnT?logo=Modrinth&style=flat-square)


![forthebadge](https://forthebadge.com/images/badges/works-on-my-machine.svg)

<p align="center">
    <a href="https://discord.pluginz.dev">
        <img src="https://i.imgur.com/JgDt1Fl.png" width="300">
    </a>
    <br>
    <i>Please join the Discord if you have questions!</i>
</p>
<br>

This Minecraft Plugin punishes people when they leave the Server while they are in Combat.
## Features
- Start the combat Timer for a specific Player with `/cl start <player>` (requires Permission: `combatlogger.start`, `default: op`)
- Stop the combat Timer for a specific Player with `/cl stop <player>` (requires Permission: `combatlogger.stop`, `default: op`)
- List all Players that are currently in Combat with `/cl list` (requires Permission: `combatlogger.list`, `default: op`)
- Get and set the Combat time with `/cl settime` to get the current time use `/cl settime <seconds>` to set the new time (requires Permission: `combatlogger.settimer`, `default: op`)
- Reload the Config and Allys with `/cl reload`  (requires Permission: `combatlogger.reload`, `default: op`)
- Ally with another Player, so when you hit each-other the Timer won't start `/cl ally` (requires Permission: `combatlogger.ally`, `default: player`)
  - `/cl ally add <player>` to add someone as an ally
  - `/cl ally remove <player>` to remove an ally
- Help command `/cl help` displays all the commands a player has permission to use
## Permission 
- `combatlogger.start`: Allows players to start combat for a specific player (`default: op`)
- `combatlogger.stop`: Allows players to stop combat for a specific player (`default: op`)
- `combatlogger.list`: Allows players to list all players currently in combat (`default: op`)
- `combatlogger.settimer`: Allows players to set the combat timer (`default: op`)
- `combatlogger.reload`: Allows Players to reload the config and allys (`default: op`)
- `combatlogger.ally`: Allows players to ally with other players (`default: player`)
## Installation 
To install the plugin, simply download the latest release file and place it in your server's plugins folder. Then, restart your server.
## Support 
For Support open an issue or join the [Discord](https://discord.pluginz.dev)
## License

This project is licensed under the [MIT License](LICENSE).

[![forthebadge](https://forthebadge.com/images/badges/powered-by-black-magic.svg)](https://forthebadge.com)
