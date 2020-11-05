//config
const trackerBaseUrl = "http://localhost:8083/api/v1/client/"; //replace 8083 with port you are running application on
const banMessage = "Evading Detected by IP-Range";

// discord config
const discordConfig = {
    enable: false,
    webhookUrl: '',
    title: "**Player was banned by Tracker Service**",
    footer: {"text": "Tracker Plugin | v1.0 | By RepZ Sep"}, //footer, change it to your server information if you want
    colorValue: 7506394,
    iw4adminUrlPrefix: ''
};

//plugin code here, do not make any changes unless you know what you are doing
var plugin = {
    author: 'sepehr-gh',
    version: 1.2,
    name: 'RepZ Tracker Plugin',
    logger: null,
    manager: null,
    trackerId: 0,

    setCharAt: function(str,index,chr) {
        if(index > str.length-1) return str;
        return str.substring(0,index) + chr + str.substring(index+1);
    },

    //cleans game string colors
    cleanColors: function(input){
        var index = 0;
        do{
            index = input.indexOf("^");
            input = this.setCharAt(input, index, "");
            input = this.setCharAt(input, index, "");
        }while(index !== -1);

        return input;
    },

    //gets server id
    serverId: function(server){
        return server.IP+""+server.Port;
    },

    getRandomArbitrary: function(min, max) {
        return Math.random() * (max - min) + min;
    },

    //--------------------------------------//

    sendDiscordMessage: function(origin, server){
        if(!discordConfig.enable) return;
        let cleanHostname = this.cleanColors(server.Hostname);
        var embed = {
            "title": discordConfig.title,
            "description": "Player **" + origin.CleanedName + "** (["+origin.ClientId+"]("+discordConfig.iw4adminUrlPrefix + origin.ClientId+")) has tried to evade ban.\n" +
            "**Reason**: `"+ banMessage+"`\n"+
            "**Server**: "+ cleanHostname+"\n"+
            "**Client IP**: "+origin.IPAddressString+"\n",
            "color": discordConfig.colorValue,
            "timestamp": new Date().toISOString(),
            "footer": discordConfig.footer
        };

        var embeds = []; embeds[0] = embed;
        var webhookData = {"embeds": embeds};

        try {
            var client = new System.Net.Http.HttpClient();
            client.DefaultRequestHeaders.Add("User-Agent", "iw4admin plugin");
            var content = new System.Net.Http.StringContent(JSON.stringify(webhookData), System.Text.Encoding.UTF8, "application/json");
            var result = client.PostAsync(discordConfig.webhookUrl, content).Result;
            result.Dispose();
            client.Dispose();
        } catch (error) {
            this.logger.WriteWarning('There was a problem sending message to discord ' + error.message);
        }
    },

    sendTrack: function(gameEvent, server){
        var sid = this.serverId(server);
        var tid = this.trackerId;
        var data = {
            ip: gameEvent.Origin.IPAddressString,
            serverId: sid,
            clientId: gameEvent.Origin.ClientId,
            trackerId: tid
        };

        try {

            var url = trackerBaseUrl + (gameEvent.Type === 4 ? "join" : "leave");

            var client = new System.Net.Http.HttpClient();
            client.DefaultRequestHeaders.Add("User-Agent", "iw4admin plugin");
            var content = new System.Net.Http.StringContent(JSON.stringify(data), System.Text.Encoding.UTF8, "application/json");
            var result = client.PostAsync(blockCheckUrl, content).Result;
            var co = result.Content;
            var parsedJSON = JSON.parse(co.ReadAsStringAsync().Result);
            co.Dispose();
            result.Dispose();
            client.Dispose();

            return parsedJSON;
        } catch (error) {
            this.logger.WriteWarning('There was a problem sending ip check message to server ' + error.message);
            return {
                status: "fail",
                ban: false
            };
        }
    },

    ban: function(origin){
        origin.Ban(banMessage, _IW4MAdminClient, false);
    },

    onConnect: function(gameEvent, server){
        var trackResponse = this.sendTrack(gameEvent, server);
        if(trackResponse.status === "ok" && trackResponse.ban){
            this.ban(gameEvent.Origin);
            this.sendDiscordMessage(gameEvent.Origin, server);
        }
    },

    onEventAsync: function (gameEvent, server) {
        if((gameEvent.Type === 4 || gameEvent.Type === 5 || gameEvent.Type === 6)){
            try{
                this.onConnect(gameEvent, server);
            }catch (error){
                this.logger.WriteWarning('There was a with handling IP-Range blocking: ' + error.message);
            }
        }
    },

    onLoadAsync: function (manager) {
        this.manager = manager;
        this.logger = manager.GetLogger(0);
        this.trackerId = new Date().getTime() / 1000 + this.getRandomArbitrary(0, 500);
    },

    onUnloadAsync: function () {
    },

    onTickAsync: function (server) {
    }
};
