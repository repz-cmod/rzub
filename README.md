```
.______       ________   __    __  .______   
|   _  \     |       /  |  |  |  | |   _  \  
|  |_)  |    `---/  /   |  |  |  | |  |_)  | 
|      /        /  /    |  |  |  | |   _  <  
|  |\  \----.  /  /----.|  `--'  | |  |_)  | 
| _| `._____| /________| \______/  |______/  
                                             
```

This is the official public release of RepZ Ultimate Bot. This bot was originally created exclusively for our RepZ community, but it's configurable enough to be used in any game projects that support IW4MAdmin. The bot mainly focuses on two things. First, commands that help us retrieve information from the server. Second, commands that help us control the server.

## Features

List of Discord-exclusive features:

- Automatic welcome message for new users that join the Discord server(can be disabled)
- Channel cleaner command
- Update bot activity with server info
- Help command that provides list of commands
- Accepting certain text inputs from users to give them a specific role (can be disabled)

Discord and IW4MAdmin features (commands for non-admins):

- Search for user in IW4MAdmin
- Show stats for players
- List servers
- Donator command to reserve an empty slot on server
- Register and match the Discord user to IW4MAdmin player

Discord and IW4MAdmin features (admin):

- Logging in through the Discord bot to IW4MAdmin to execute commands and make Donator module work
- Block a specific range of IP-addresses to prevent evasions
- Block a certain ISP and city (smarter than previous option) to prevent evasions
- Add players to whitelist to avoid IP-range block
- Execute IW4MAdmin commands through Discord

## Setup and Run

### Requirements:

- Java 8+ installed on your system
- Apache maven **(only if you want to build from scratch)**

To build from scratch, clone the repository and run this command in project root:

```
maven clean package -DskipTests
```
You now have `rzub.jar` in the `target` folder.

Instead of building the app yourself, you can find our JAR releases (in `rzub.zip` files) in [releases](https://github.com/repz-cmod/rzub/releases) section.


### Configuration:
There are two configuration files that you will need to run RZUB. These configuration files are available in [env directory](https://github.com/repz-cmod/rzub/tree/main/env).

Rename them to `settings.json` and `access.json` by editing their filenames to remove `-sample`.

You may want to keep these files in the same directory as `rzub.jar` or learn how to pass their location to the JAR file in the **Run** section below.

#### settings.json

Base configuration of the bot

- In modules section, you can enable or disable application modules by changing values to `true` or `false`. See [modules-section](#modules-switchable-features) below.
- `iw4madminUrl` should point to your IW4MAdmin URL ending with a `/`
- You need to create a database for this bot.
  - The fastest way to initialize database for the bot is to set `"hbm2ddl": "create"` in settings.json for the first run. This initializes the database for you.
  - Then, for normal runs change it to `"hbm2ddl": "validate"`. (restart is required)
  - If you are updating the bot and you need the database to be updated, it is probably best to set  `"hbm2ddl": "update"`
  - Supported `driver` values:
    - `org.mariadb.jdbc.Driver` (MariaDB, default)
    - `com.mysql.jdbc.Driver` (MySQL)
  - Supported `dialect` values:
    - `org.hibernate.dialect.MariaDB103Dialect` (MariaDB, default)
    - `org.hibernate.dialect.MySQL5InnoDBDialect` (MySQL InnoDB)
- `discord -> token`: bot token on discord
- `discord -> channels -> access-grant`: ID of the channel used for "access grant" feature (see modules; this can be disabled, empty value will also work)
- `security -> token`: setup a token here to be used in plugins in order to keep the bot safe from being abused by invalid IP-Range bans and tracking information
- `switches -> ipb-client-date-sensitive`: if enabled, IP-Range and IP-Region bans will ensure the client to be banned has more than 7 hours of playtime

#### access.json

Using this configuration file you can determine which users or roles can access certain commands using a list of their IDs. You can also go crazy and make them public, which is not recommended!

### Run

Assuming you have `rzub.jar`, `settings.json` and `access.json` in same location:

Linux:
```
chmod +x rzub.jar
./rzub.jar --server.port=9000
```

Windows:
```
java -jar rzub.jar --server.port=9000
```

You can replace `9000` with any port of your choice.

If your settings.json file is not in same location, you can pass the location like: `--rzub.conf.settings=/path/to/settings.json`

If your access.json file is not in same location, you can pass the location like: `--rzub.conf.access=/path/to/access.json`

## Plugins

In order to have full support of all bot features and create discord-iw4madmin mappings, you need to add Javascript plugins under [plugins](https://github.com/repz-cmod/rzub/tree/main/plugins) to your IW4MAdmin Plugins directory.

Make sure to first replace the bot URL in those plugin files with a valid IP and port of the running bot.

Also, if you are making the bot port public, you should make sure you change the token under `security -> token` in both *settings.json* and in plugins.
Try a random string. You can generate one [here](https://passwordsgenerator.net/)  

## Post Run Configuration

If you want to be able to make **execute commands through discord** or **enable the donator slot reserve** you first have to create a new account (guid) for a game (let's say IW4X) and then give this account moderator/administrator access in IW4MAdmin.

Then, go to the game and type `!rt`. It will give you an ID and a token. As the server owner (`management` role ID), go to your Discord server and enter `!iwlogin <clientId> <password>` (without <>). Your bot is now logged into IW4MAdmin and is ready to execute automatic or manual commands from Discord.

## Commands

Here is a list of available discord commands:

| Command 	 | Description                                                                          	| Example            	                |
|----------	 |----------------------------------------------------------------------------------------  |---------------------------------- |
| !clean  	 | Clears 20 messages from last message in a channel till it reaches certain message id 	  | `!clean <MessageId>` 	            |
| !iwexec  	 | Executes commands into iw4madmin for a certain server                                	  | `!iwexec <ServerId> <Command>` 	  |
| !join   	 | For donators to free up a spot on a full server and join                                 | `!join <ServerId>`                |
| !forum  	 | Sends the forum link from the config file                                           	    | `!forum`                   	      |
| !ipb       | Blocks a range of IP addresses                                                           | `!ipb help` to see full           |
| !ipb2      | Blockes based on IP of same city and ISP of provided example                             | `!ipb2 help` to see full          |
| !iwl       | Looks up in IW4MAdmin for a player                                                       |                    	              |
| !iwstats   | Returns player stats in IW4MAdmin                                                        |  `!iwstats <ClientId>`            |
| !servers 	 | Returns lists of server to be used to fill <ServerId> in other commands                  |                    	              |
| !register  | Sends user instrcution to match their discord to the game. (Only supports iw4x and T6)   |                    	              |
| !whitelist | Adds an IW4MAdmin client id to whitelist for ipb and ipb2                                |  `!whitelist help` to see full    |
  
## Modules (switchable features)

### analytics
For the current version, I encourage everyone to keep this module to `false` in their configuration file. In future releases, there will be a web panel for RZUB that provides analytics data of servers. If you leave the value to `true`, this data will be collected but will never be used.

### welcome
By having this module enabled, new members will get direct message from your bot. The message data is configurable through `settings.json`

### access grant
This module allows users to gain certain roles and access to the Discord server by typing a specific message or text input. If you set up the channel ID for the "access grant" feature, it will work automatically and does not require the switch to be on or off. 
