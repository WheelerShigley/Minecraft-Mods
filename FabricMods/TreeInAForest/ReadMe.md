# [Tree In A Forest](https://modrinth.com/mod/tree-in-a-forest/version/0.4.2_1.21.5)
If a tree falls in a forest and noone is around to hear it, does it make a sound? **No.**

![AFK](https://cdn.modrinth.com/data/QhqvfRRd/images/b78a2f1a048e83d30070040ce237c9ccef132a7f.png)


This mod makes it so that time stops when noone is online.

After 1.21.1, the server-property "pause-when-empty-seconds" can provide this functionality.

---

## Blacklisting

With "blacklisting", one can ensure that the day doesn't progress from specific players being online; this is particularly useful if you want the day-counter to be accurate: not accounting for afk/bot accounts.

Using the following commands, the blacklist can be managed.

+ ``/treeinaforest blacklist list``: Lists all blacklisted players
+ ``/treeinaforest blacklist add <player-name>``: Adds a player to the blacklist
+ ``/treeinaforest blacklist remove <player-name>``: Removes a player from the blacklist

+ Blacklisting is done by UUID when possible (immune to name changes) and will use a name when there is not an account associated with the name (or the server is offline).