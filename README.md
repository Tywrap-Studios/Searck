<div align="center">
  <h1>Searck</h1>

![banner](./branding/searck-banner-rounded.png)

[![fabric-api](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api)
[![fabric-language-kotlin](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/requires/fabric-language-kotlin_vector.svg)](https://modrinth.com/mod/fabric-language-kotlin)

[![discord-plural](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/social/discord-plural_vector.svg)](https://tiazzz.me/discord)

**Search right here and right now**
</div>

## About

Searck is a simple Minecraft mod that adds a Satisfactory-style,
promptly available search bar. Just press N and a sleek, true-to-Minecraft
search bar will swiftly be pulled up. 

Features include:

- Making big calculations
  - We have support ranging from simple addition, all the way to tangents and even `atan2` functions when used correctly! All following standard PEMDAS rules and blazingly fast using the Shunting Yard Algorithm!
- Looking up any block or item in the registry and displaying its icon and name
- Doing "quick actions" by pressing Enter:
  - Quickly swapping between items in your currently selected slot if you have the selected item in your inventory (indicated by a yellow outline in the search results list!)
  - Opening the info screen: viewing an item's icon, name and tooltip without needing to have it in your inventory
  - Pushing the outcome of the calculation to the search bar
- Opening the recipes or usages of the item
- Sleek, in-game config screen using MidnightLib
  - With lots of option to configure as well at that! Choose a different item indexer, or even appoint to a custom one! Want to instantly open a recipe viewer from the quick action? Change it here!
- Coded fully in Kotlin
- Available for a great range of Minecraft versions, and actively maintained to support the latest versions

The search menu aims to be as true to Minecraft as possible, making it great for
any modpack, especially Vanilla+ ones! So, get right to searchin'!

## Installing
[![modrinth](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/searck/versions)
[![github](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/available/github_vector.svg)](https://github.com/Tywrap-Studios/Searck/releases)

### Compatibility table

| Minecraft Version | JEI, REI or EMI | MidnightLib              |
|-------------------|-----------------|--------------------------|
| 1.21-1.21.1       | ✅ All           | ⚠️ Manual Install Needed |
| 1.21.2-1.21.3     | ⚠️ Just REI     | ⚠️ Manual Install Needed |
| 1.21.4-1.21.5     | ⚠️ Just REI     | ⚠️ Manual Install Needed |
| 1.21.6-1.21.8     | ⚠️ Just REI     | ✅ Included               |
| 1.21.9-1.21.10    | ⚠️ Just REI     | ✅ Included               |
| 1.21.11           | ⚠️ JEI and REI  | ✅ Included               |
| 26.1.x            | ⚠️ JEI and REI  | ✅ Included               |
| 26.2(.x)          | ⚠️ JEI and REI  | ✅ Included               |

## For developers

Read: [README-DEVS.md](./README-DEVS.md)
