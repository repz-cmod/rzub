```
.______       ________   __    __  .______   
|   _  \     |       /  |  |  |  | |   _  \  
|  |_)  |    `---/  /   |  |  |  | |  |_)  | 
|      /        /  /    |  |  |  | |   _  <  
|  |\  \----.  /  /----.|  `--'  | |  |_)  | 
| _| `._____| /________| \______/  |______/  
                                             
```

RepZ Ultimate Bot is a bot created exlusive for RepZ, but its also configurable enough to be used in any game project supporting IW4MAdmin. The bot focuses on mainly 2 things. First, commands that help us retrieve some information from the server. Second, commands that help us control the server.

## Features

List of features that only affect discord

- Automatic welcome message for new users that join Discord (can be disabled)
- Channel cleaner command
- Update bot activity with server info
- Help command to provide list of commands
- Accepting a certain text from user to give them a role to access the discord (can be disabled)

Discord and IW4MAdmin features (commands for none admins):

- search for user is iw4madmin
- provide stats for players
- list servers
- donator command to reserve an empty slot on server
- register and map the discord user to iw4madmin player

Discord and IW4MAdmin features (admin):

- logging in the discord bot to iw4madmin to be able to execute commands and make donator app work
- Block a specific range of IP-addresses to prevent evasions
- Block a certain ISP and City (smarter than previous one) to prevent evasions
- Add players to whitelist to avoid IP-Range block
- Execute iw4madmin commands through discord

## How to run

Requirements:

- Java 8+ installed on your system
- Apache maven **(only if you want to build from scratch)**

To build from scratch, clone the repository and run this command in project root:

```
maven clean package -DskipTests
```
You now have `rzub.jar` in the `target` folder.

Instead of builting the app by yourself, you can find released JARs in [releases](https://github.com/repz-cmod/rzub/releases) section.

