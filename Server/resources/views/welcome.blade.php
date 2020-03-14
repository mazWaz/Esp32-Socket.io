<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>ESP32 Dashboard</title>

    <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link
        href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
        rel="stylesheet">
    <link href="css/sb-admin-2.min.css" rel="stylesheet">
</head>
<body id="page-top">
<div id="wrapper">
    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">
            <div class="container-fluid">
                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">Dashboard</h1>
                </div>
                <div class="row justify-content-center">
                    <div class="col-xl-2 col-md-6 mb-4">
                        <div class="card border-left-primary shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">LED
                                        </div>
                                        <div id="txtLed" class="h5 mb-0 font-weight-bold text-gray-800">OFF</div>
                                    </div>
                                    <div class="col-auto">
                                        <div class="custom-control custom-switch" >
                                            <input type="checkbox" class="custom-control-input" id="customSwitches">
                                            <label class="custom-control-label" for="customSwitches" ></label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-xl-2 col-md-6 mb-4">
                        <div class="card border-left-success shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">Touch Sensor
                                        </div>
                                        <div id="touch_read" class="h5 mb-0 font-weight-bold text-gray-800">Not Touch</div>
                                    </div>
                                    <div class="col-auto">

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row justify-content-center">
                    <div class="col-xl-4 col-lg-5">
                        <div class="card shadow mb-4">
                            <!-- Card Header - Dropdown -->
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">Potentio Meter</h6>
                            </div>
                            <canvas id="myChart" width="400" height="400"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap core JavaScript-->
<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="js/Chart.min.js"></script>

<!-- Core plugin JavaScript-->
<script src="vendor/jquery-easing/jquery.easing.min.js"></script>

<!-- Custom scripts for all pages-->
<script src="js/sb-admin-2.min.js"></script>
<script src="js/Chart.min.js"></script>
Chart.min.js

<!-- Page level plugins -->


<!-- Page level custom scripts -->
<script src="js/demo/chart-area-demo.js"></script>
<script src="js/demo/chart-pie-demo.js"></script>
<script src='http://localhost:3000/socket.io/socket.io.js'></script>
<script>
    var load = 0;
    var bg = 255;

    //pake axios
        var switchStatus = false;
        $("#customSwitches").on('change', function() {
            if ($(this).is(':checked')) {
                var req = new XMLHttpRequest();
                req.open("GET", "http://localhost:3000/ledOn", true);
                if (req.overrideMimeType)
                    req.overrideMimeType("text/plain");
                req.send(null);
            }
            else {
                var req = new XMLHttpRequest();
                req.open("GET", "http://localhost:3000/ledOff", true);
                if (req.overrideMimeType)
                    req.overrideMimeType("text/plain");
                req.send(null);
            }
        });

    var ctx = document.getElementById("myChart");

    // And for a doughnut chart
    var myDoughnutChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            datasets: [
                {
                    data: [load,bg],
                    backgroundColor: [
                        "#FF6384",
                        "#A5A5A5"
                    ],
                    hoverBackgroundColor: [
                        "#FF6384",
                        "#A5A5A5"
                    ]
                }]
        },
        options: {
            rotation: 1 * Math.PI,
            circumference: 1 * Math.PI,
        }
    });
    var socket = io.connect('http://localhost:3000');
    socket.on("status_potentio", (data) => {
        console.log("sensor" + data);
        load = data;
        var bgH = bg-data;
        myDoughnutChart.data.datasets[0].data[0] = load;
        myDoughnutChart.data.datasets[0].data[1] = bgH;
        myDoughnutChart.update();
    });
    socket.on("status_led", (data) => {
        console.log("sensor" + data);
        document.getElementById("txtLed").innerHTML = data;
        if(data === "ON"){
            document.getElementById("customSwitches").checked = true;
        }if(data === "OFF"){
            document.getElementById("customSwitches").checked = false;
        }
    });
    socket.on("status_touch", (data) => {
        console.log("sensor" + data);
        document.getElementById("touch_read").innerHTML = data;
    });

</script>

</body>

</html>
