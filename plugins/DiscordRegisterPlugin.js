//config
const registrationUrl = "http://localhost:8083/plugin/v1/discord/register"; //replace 8083 with port you are running application on



//plugin code here, do not make any changes unless you know what you are doing
var plugin = {
    author: 'sepehr-gh',
    version: 1.0,
    name: 'Discord Registration Plugin',
    logger: null,
    manager: null,

    //sends discord message about the ban
    register: function(server, origin, token){
        var game;
        if(server.Game === 2){
            game = "IW4";
        }else {
            game = "T6";
        }

        var data = {
            playerName: origin.CleanedName,
            clientId: origin.ClientId,
            game: game,
            xuid: origin.NetworkId,
            token: token
        };

        try {
            var client = new System.Net.Http.HttpClient();
            client.DefaultRequestHeaders.Add("User-Agent", "iw4admin plugin");
            var content = new System.Net.Http.StringContent(JSON.stringify(data), System.Text.Encoding.UTF8, "application/json");
            var result = client.PostAsync(registrationUrl, content).Result;
            result.Dispose();
            client.Dispose();
        } catch (error) {
            this.logger.WriteWarning('There was a problem sending message to discord ' + error.message);
        }
    },

    //handle a message
    onMessage: function(gameEvent, server){
        if(gameEvent.Origin === undefined || gameEvent.Origin == null)
            return;
        const message = gameEvent.Message;
        if(message !== undefined && message.startsWith("!discord ")) {
            let token = message.replace("!discord ", "");
            this.register(server, gameEvent.Origin, token);
        }
    },

    onEventAsync: function (gameEvent, server) {
        if(gameEvent.Type === 100){ //client sent a message
            try{
                this.onMessage(gameEvent, server);
            }catch (error){
                this.logger.WriteWarning('There was a with handling message in DRP: ' + error.message);
            }
        }
    },

    onLoadAsync: function (manager) {
        this.manager = manager;
        this.logger = manager.GetLogger(0);
    },

    onUnloadAsync: function () {
    },

    onTickAsync: function (server) {
    }
};