var stompClient = null;
var socket = null;

function connect() {
    socket = new SockJS('/frootr');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/bowlupdates', function (message) {
            updateFruitBowl(JSON.parse(message.body));
        });
    });

    setInterval(refresh, 1000)
}

function updateFruitBowl(message) {
    var fruitMap = message.fruitBowl;
    // Sort the map by key
    var keys = [];
    for (var key in fruitMap) {
        if (fruitMap.hasOwnProperty(key)) {
            keys.push(key);
        }
    }
    keys.sort();
    var len = keys.length;
    if (len > 0) {
        $("#fruitBowl > tbody").html("");
        for (var i = 0; i < len; i++) {
            var fruit = keys[i];
            var count = fruitMap[fruit];
            if (count > 0) {
                var styleName = 'info';
                if (count < 2) {
                    styleName = 'danger';
                } else if (count < 5) {
                    styleName = 'warning';
                }
                $('#fruitBowl > tbody:last').append('<tr class="' + styleName + '"><td>' + fruit + '</td><td><span class="badge">' + count + '</span></td></tr>');
            }
        }
    }
    $('#lastUpdated').html(getDateTime());
}

function getDateTime() {
    var now     = new Date();
    var year    = now.getFullYear();
    var month   = now.getMonth()+1;
    var day     = now.getDate();
    var hour    = now.getHours();
    var minute  = now.getMinutes();
    var second  = now.getSeconds();
    if(month.toString().length == 1) {
        var month = '0'+month;
    }
    if(day.toString().length == 1) {
        var day = '0'+day;
    }
    if(hour.toString().length == 1) {
        var hour = '0'+hour;
    }
    if(minute.toString().length == 1) {
        var minute = '0'+minute;
    }
    if(second.toString().length == 1) {
        var second = '0'+second;
    }
    var dateTime = year+'/'+month+'/'+day+' '+hour+':'+minute+':'+second;
    return dateTime;
}

function refresh() {
    console.log("refreshing...");
    stompClient.send("/app/refresh", {}, {});
}
