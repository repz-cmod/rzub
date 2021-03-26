//config
const registrationUrl = "http://localhost:8083/plugin/v1/discord/register"; //replace 8083 with port you are running application on



//plugin code here, do not make any changes unless you know what you are doing
var plugin = {
    author: 'sepehr-gh',
    version: 1.2,
    name: 'Discord Registration Plugin',
    logger: null,
    manager: null,

    //sends discord message about the ban
    register: function(server, origin, token){
        var game;
        if(server.GameName  === 2){
            game = "IW4";
        }else {
            game = "T6";
        }

        var data = {
            playerName: origin.Name,
            clientId: origin.ClientId,
            game: game,
            xuid: origin.NetworkId,
            token: token
        };

        try {
            var client = new System.Net.Http.HttpClient();
            client.DefaultRequestHeaders.Add("User-Agent", "iw4madmin plugin");
            var content = new System.Net.Http.StringContent(JSON.stringify(data), System.Text.Encoding.UTF8, "application/json");
            var result = client.PostAsync(registrationUrl, content).Result;
            var co = result.Content;
            var parsedJSON = JSON.parse(co.ReadAsStringAsync().Result);
            co.Dispose();
            result.Dispose();
            client.Dispose();
            
            this.logger.WriteWarning(parsedJSON);

            if(parsedJSON.status === "ok"){
                origin.Tell("Registration is successful.");
            }else {
                origin.Tell("Registration failed.");
            }
        } catch (error) {
            this.logger.WriteWarning('There was a problem sending message to discord ' + error.message);
            origin.Tell("Registration failed.");
        }
    },

    //handle a message
    onMessage: function(gameEvent, server){
        if(gameEvent.Origin === undefined || gameEvent.Origin == null)
            return;
        const message = gameEvent.Message;
        if(message !== undefined && (message.startsWith("!discord ") || message.startsWith("/!discord "))) {
            let token = message.replace("!discord ", "").replace("/", "");
            this.logger.WriteWarning("Token: " + token + " | message: " + message);
            this.register(server, gameEvent.Origin, token);
        }
    },

    onEventAsync: function (gameEvent, server) {
        if(gameEvent.Type === 110 || gameEvent.Type === 100){
            try{
                this.logger.WriteWarning("Type" +  gameEvent.Type + ", message:" + gameEvent.Message);
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