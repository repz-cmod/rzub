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

## Plugins

In order to have full support of all BOT features and create discord-iw4madmin mappings, you need to add javascript plugins under [plugins](https://github.com/repz-cmod/rzub/tree/main/plugins) to your IW4MAdmin Plugins directory. Make sure to first replace bot URL in those plugin files with valid ip and port of the running bot.

## Post Run Configuration

If you want to be able to make **execute commands through discord** or **enable the donator slot reserve** you first have to create a new account (guid) for a game (lets say iw4x) and then give this account moderator/administrator access in IW4MAdmin.

Then go to the game and type `!rt`. It will give you an ID and a token. As server owner (`management` role ID) come to discord and enter `!iwlogin <clientId> <password>` (without <>). Your bot is now logged into IW4MAdmin and is ready to execute automatic or manual commands from discord.

## Commands

Here is a list of available discord commands:

| Command 	 | Description                                                                          	| Example            	            |
|----------	 |--------------------------------------------------------------------------------------	|-------------------------------	|
| !clean  	 | Cleans 20 messages from last message in a channel till it reaches certain message id 	| `!clean <MessageId>` 	          |
| !iwexec  	 | Executes commands into iw4madmin for a certain server                                	| `!iwexec <ServerId> <Command>` 	|
| !join   	 | For donators to use to make a spot on server and join a full server                    | `!join <ServerId>`              |
| !forum  	 | Sends back the forum link from the config file                                      	  | `!forum`                   	    |
| !ipb       | Blocks a range of IP Addresses.                                                        | `!ipb help` to see full         |
| !ipb2      | Blockes based on IP of same city and ISP of provided example                           | `!ipb2 help` to see full        |
| !iwl       | Looks up in IW4MAdmin for a player                                                     |                    	            |
| !iwstats   | Returns player stats in IW4MAdmin                                                      |  `!iwstats <ClientId>`          |
| !servers 	 | Returns lists of server to be used to fill <ServerId> in other commands                |                    	            |
| !register  | Sends user instrcution to map their discord to the game.                            	  |                    	            |
| !whitelist | Adds an IW4MAdmin client id to whitelist for ipb and ipb2                              |  `!whitelist help` to see full  |
