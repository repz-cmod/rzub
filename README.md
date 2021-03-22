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

## Setup and Run

### Requirements:

- Java 8+ installed on your system
- Apache maven **(only if you want to build from scratch)**

To build from scratch, clone the repository and run this command in project root:

```
maven clean package -DskipTests
```
You now have `rzub.jar` in the `target` folder.

Instead of builting the app by yourself, you can find released JARs (in `rzub.zip` files) in [releases](https://github.com/repz-cmod/rzub/releases) section.


### Configuration:
In order to run the project, you need your `rzub.jar` file and `config.json` file to be in same folder. You can get a sample of the config file from [env directory](https://github.com/repz-cmod/rzub/tree/main/env). Rename it to `config.json`

- In modules section you can enable or disable application modules by changing values of `true` or `false`. See #modules-section below to understand more.
- `iw4madminUrl` should point to your IW4MAdmin url ending with a `/`
- You need to create a database for this bot.
  - The fastest way to initialize database for the bot is to set `"hbm2ddl": "create"` in config.json for the first run. This initializes the database for you.
  - Then, for normal runs change it to `"hbm2ddl": "validate"`. (restart is required)
  - If you are updating the bot and you need the database to be updated, probably its best choice to set `"hbm2ddl": "update"`
  - Supporting `driver` values: `org.mariadb.jdbc.Driver` (MariaDB, default) - `com.mysql.jdbc.Driver` (MySQL)
  - Supported `dialect` values: `org.hibernate.dialect.MariaDB103Dialect` (MariaDB, default) - `org.hibernate.dialect.MySQL5InnoDBDialect` (MySQL InnoDB)
- `discord -> token`: bot token on discord
- `discord -> channels -> access-grant`: ID of the channel used for "access grant" feature (see modules, this can be disabled, empty value also works)
- `discord -> rokes`:
  - `management`: Highest rank in server for very restricted commands
  - `jmanagement`: Lower rank with more normal administration (usually read only) commands
  - `base_member_role`: ID of role for normal users (useful for "acess grant" module)
  - `donator`: ID of role for donators
- `discord -> ipb`: list of user id's that can perform `!ipb` and `!ipb2` commands

### Run

Assuming you have `rzub.jar` and `config.json` in same location:

Linux:
```
./repz.jar --server.port=9000
```

Windows:
```
java -jar repz.jar --server.port=9000
```

You can replace `9000` with any port of your choice. If your config.json file is not in same location you can pass the location like: `--repz.conf.file=/path/to/config.json`

