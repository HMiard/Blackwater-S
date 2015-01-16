$(document).ready( function(){

    var location = "localhost:8035";
    var user = new WebSocket("ws://"+location);
    var pickedRoom = "";

    user.onopen = function(e){
        console.log("Connected successfully to "+location);
        connected()
    };

    user.onmessage = function(e){
        pushToChat(e.data);
    };

    user.onerror = function(e){
        console.log("Server offline.");
        serverOffline()
    };

    user.onclose = function(e){
        console.log("Server offline.");
        serverOffline()
    };


    $("#rooms").find(".roomButton").click(function(){askName($(this).html())});
    $("#get_name").find(".roomButton").click(function(){enterChat()});
    $("#leaveButton").click(function(){leave()});

    document.onkeydown = function(e){
        // Pressed ENTER
        if (e.keyCode == 13)
            sendMsg()
    };

    /**
     * Triggered by clicking on a room.
     *
     * @param room
     */
    function askName(room){

        pickedRoom = room;
        $("#content").empty();
        $("#rooms").css({display:"none"});
        $("#get_name").css({display:"block"});
    }

    /**
     * Triggered by clicking on the "OK" button.
     */
    function enterChat(){

        var msg = {};
        msg["action"] = "join";
        msg["name"] = $("#name").val();
        msg["room"] = pickedRoom;

        user.send(JSON.stringify(msg));
        console.log(msg);
        console.log(JSON.stringify(msg));
        $("#get_name").css({display:"none"});
    }


    function pushToChat(msg){

        $("#content").append(msg+"<br/>");
    }

    /**
     * Sends the message when the ENTER key is pressed.
     */
    function sendMsg(){

        var writer = $("#writer");
        var msg = {};
        msg["action"] = "say";
        msg["content"] = writer.val();

        writer.val("");
        user.send(JSON.stringify(msg));
    }

    /**
     * Clicked on the "leave" button
     */
    function leave(){

        var msg = {"action":"leave"};
        user.send(JSON.stringify(msg));

        $("#content").empty();
        connected();
    }


    function connected(){

        $("#connection_msg").remove();
        $("#rooms").css({display:"block"});
    }


    function serverOffline(){

        $("#chat").empty();
        pushToChat("Server Offline.");
    }

});