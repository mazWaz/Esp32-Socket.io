//var js
var app = require('express')();
var http = require('http').createServer(app);
var io = require('socket.io')(http);
app.get('/ledOff', function (req, res) {

    //send data to sockets.
    io.sockets.emit('change_led',"OFF");

    res.send({});
});
app.get('/ledOn', function (req, res) {

    //send data to sockets.
    io.sockets.emit('change_led',"ON");

    res.send({});
});
io.on('connection',function (socket) {
    console.log('User Connected!')
    socket.on('status', function (data) {
       // console.log(data);
    });
    socket.on('status_potentio',(data)=>{
        console.log(data);
        socket.broadcast.emit('status_potentio',data);
    });
    socket.on('status_led',(data)=>{
        console.log(data);
        socket.broadcast.emit('status_led',data);
    });
    socket.on('status_touch',(data)=>{
        console.log(data);
        socket.broadcast.emit('status_touch',data);
    });
});
http.listen(3000, function () {
    console.log('listening on *:3000');
});


