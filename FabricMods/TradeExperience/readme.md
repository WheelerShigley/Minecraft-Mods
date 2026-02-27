# [Trade Experience](https://modrinth.com/mod/trade-experience/version/1.0.9_1.21.11)
![Trade](https://cdn.modrinth.com/data/3nZC9EUc/images/e155faf899935d332a8c3110b10d1192e64a2784.png)

## Paying
Give [experience](https://minecraft.wiki/w/Experience) to other players by interacting with them and typing the amount in chat!
This can allow for experience to function as a form of currency.

Alternatively, one can use the "pay" [command](https://minecraft.wiki/w/Commands).
Usage: "/pay <TARGET> <AMOUNT>"

To check the amount of experience you have, use "/balance" or "/bal".
![Balance](https://cdn.modrinth.com/data/3nZC9EUc/images/1579e50ecdf1f7e9b896c035612e6d7fe5c9bd4b.png)

---

## Other Features

Trades time out after a configurable amount of time (default of thirty seconds).

If you lack sufficient experience to complete an attempted trade/gift, you will be shown how much you have in relation to the amount attempted.
![Insufficient Experience](https://cdn.modrinth.com/data/3nZC9EUc/images/6d03d5c0479a68b7715e3a1d8f2e0b7710fe31e7.png)

The name of the "currency" (experience) is configuration (default of "experience").

If one has permissions, they may check other player's balances with "/bal <Player Name>" or "/balance <Player Name"; by default, this is set for permission-level four (default for operators).

---

## Gamerules and Conguration

### Gamerules

| Name | Identifier | default Value | Description |
| ---- | ---------- | ------------- | ----------- |
| Trade Timeout Time | trade_timeout_time | 600 (game-ticks) | Time (in ticks) for a trade to timeout. |
| Interaction Trade Initiation | interaction_trade_initiation | true | Right-Click a Player to Initiate a Trade. |

Gamerules may be changed with commands like ``/gamerule trade_experience:trade_timeout_time 1000``.

### Configuration

trade_experience.properties:
```md
# Monetary-like experience name.
experience_name: experience
```