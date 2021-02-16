# FancyE-Chests
### Provide your players with isolated, fancy spinning ender chests.

This plugin allows you to place rotating ender chests (separate from their "real" ender chest) throughout the world, providing limitations for when using them such as
* Disappear on use for a certain amount of time
* On close particles (+ speed + amount)
* Rotating speed
* Number of rows per player/group with your permissions system! ([LuckPerms meta](https://luckperms.net/wiki/Prefixes,-Suffixes-&-Meta#meta), [GroupManager variables](https://elgarl.github.io/GroupManager/COMMANDS#group-variables) or PEX options)
* More??

___

This project was designed for 1.12.2 Paper (a.k.a. PaperSpigot) servers, but it works on 1.13+ as well.


## Commands & Permissions

All listed sub-commands fall under the `/fancyenderchests`/`/fec` name/label/alias and require the `fancyechests.use` permission.
* `/fec help` - Prints some plugin info and the list of available commands.
* `/fec nearest` - Teleports the player to the nearest (loaded) rotating ender chest in that world.
* `/fec reload` - Reloads the config file and player data.
* `/fec remove` - Puts the player in a "chest removal" state where you can't open rotating chests and, you can remove one of them by hitting them. Run this command again to exit said state without removing any chests.
* `/fec remove nearest` - Removes the nearest (loaded) rotating ender chest in that world and notifies the player of where it was located. This does **not** put the player in the removal state.
* `/fec set` - Places an ender chest that hides on close, preventing its usage until it appears again (see `secondsHidden` in [config.yml](https://github.com/Fefo6644/FancyE-Chests/blob/master/src/main/resources/config.yml#L5).
* `/fec setpersistent` - Places an ender chest that doesn't hide on use.


### Want to limit the number of rows people have access to?

You can accomplish that by setting the `fancyechests.rows` variable key to the desired number of rows in your permissions plugin (for example, to allow for up to five rows: `/luckperms group <group> meta set fancyechests.rows 5`/`/mangaddv <group> fancyechests.rows 5`)

**This functionality requires [Vault](https://dev.bukkit.org/projects/vault) to be installed**


## Compiling

All you have to do is clone this repository in any directory and run `./gradlew` on Linux or `.\gradlew.bat` on Windows in your terminal of choice, the compiled jar file will be located in `./build/libs/fancyechests-{version}-all.jar`
```
git clone https://github.com/Fefo6644/FancyE-Chests.git fancyechests
cd fancyechests
./gradlew
```


## Contributing

Contributions are way more than welcome! Everything will be taken into consideration and hopefully discussed.

This project follows [Google Java code style](https://google.github.io/styleguide/javaguide.html). Whilst it isn't strict (e.g. line width isn't always 100 chars/columns wide), try to follow the general layout of the file you're editing :)


## Attributions

* [adventure](https://github.com/KyoriPowered/adventure) by [KyoriPowered](https://github.com/KyoriPowered) was chosen for messages and chat components
* [brigadier](https://github.com/Mojang/brigadier) by [Mojang](https://github.com/Mojang) was chosen as command framework
