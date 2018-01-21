var socket;

$(function(){

    connect();

    function connect(){
        socket = new WebSocket("wss://safe-river-80569.herokuapp.com/socket");
        socket.onopen = function(){
            message('Socket Status: '+socket.readyState+' (open)');
        }
        socket.onmessage = function(message){
            if(message.data.toString().startsWith("Player") || message.data.toString().startsWith("draw")){
                showDialog(message.data);
            }
            else{
                console.log(message.data)
                grid = JSON.parse(message.data);
            }
            buildGrid.gridbuild(grid);
        }
        socket.onerror = function(){
            console.log(message.data)
        }
        socket.onclose = function(){
            socket.close()
        }
        function message(msg){
            $('#wsLog').append('<p>' + msg +'</p>');
        }
    }

});

function showDialog(data) {
    swal({
        title: data,
        closeOnClickOutside: false,
        closeOnConfirm: false
    }, function(isConfirm){
        if (isConfirm) {
            window.location.reload();
        }
    });
}


$(function(){
    addListeners();

    function addListeners(){
        var zero = document.getElementById("0")
        var one = document.getElementById("1")
        var two = document.getElementById("2")
        var three = document.getElementById("3")
        var four = document.getElementById("4")
        var five = document.getElementById("5")
        var six = document.getElementById("6")
        var newGame = document.getElementById("BUTTONNEWGAME")

        if(zero) {
            zero.addEventListener("click", function () {
                console.log("selected row 0");
                socket.send(0);
            })
        }
        if(one) {
            document.getElementById("1").addEventListener("click", function () {
                console.log("selected row 1");
                socket.send(1);
            })
        }
        if(two) {
            document.getElementById("2").addEventListener("click", function () {
                console.log("selected row 2");
                socket.send(2);
            })
        }
        if(three) {
            document.getElementById("3").addEventListener("click", function () {
                console.log("selected row 3");
                socket.send(3);
            })
        }
        if(four) {
            document.getElementById("4").addEventListener("click", function () {
                console.log("selected row 4");
                socket.send(4);
            })
        }
        if(five) {
            document.getElementById("5").addEventListener("click", function () {
                console.log("selected row 5");
                socket.send(5);
            })
        }
        if(six) {
            document.getElementById("6").addEventListener("click", function () {
                console.log("selected row 6");
                socket.send(6);
            })
        }
        if(newGame) {
            document.getElementById("BUTTONNEWGAME").addEventListener("click", function () {
                console.log("starting new game");
                socket.send("newGame")
            });
        }
    }
});


var buildGrid = new Vue({
    el: '#buildGrid',
    methods: {
        gridbuild: function (grid) {
            var innerhtml = "";
            var k = 0;
            for (var i=0; i <= 5; i++){
                innerhtml += '<tr>'
                for (var j=0; j <= 6; j++){
                    if(grid[j+k] == " "){
                        innerhtml += '<td class = "EMPTYGRID"> <p>&nbsp</p> </td>'
                    }
                    if(grid[j+k] == "O"){
                        innerhtml += '<td class = "REDGRID"> <p>&nbsp</p> </td>'
                    }
                    if(grid[j+k] == "X"){
                        innerhtml += '<td class = "BLACKGRID"> <p>&nbsp</p> </td>'
                    }
                }
                k += 7;
                innerhtml += '</tr>'
            }
            document.getElementById("grid").innerHTML = innerhtml;
            document.getElementById("customers").style.visibility = "visible";
        }
    }
}).$mount('#buildGrid')