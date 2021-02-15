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

## Compiling
All you have to do is clone the repository in any directory and run `./gradlew` on Linux or `.\gradlew.bat` on Windows in your terminal of choice, the compiled jar file will be located in `./build/libs/fancyechests-{version}-all.jar`
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
