async function saveReading() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    var customerid = document.getElementById("customerid").value;
    var comment = document.getElementById("comment").value;
    var metercount = document.getElementById("metercount").value;
    var kindofmeter = document.getElementById("kindofmeter").value;
    var substitute = document.getElementById("substitute").value;
    var meterid = document.getElementById("meterid").value;
    var dateofreading = document.getElementById("dateofreading").value;
    var validRequest = true;

    if (metercount == "") {
        document.getElementById("metercount").classList.add("missingData");
        validRequest = false;
    }
    if (customerid == "") {
        document.getElementById("customerid").classList.add("missingData");
        validRequest = false;
    }

    if (dateofreading == "") {
        document.getElementById("dateofreading").classList.add("missingData");
        validRequest = false;
    }

    if (!validRequest)
        return;

    if (id != null) {
        var reading = await getReading(id);

        if (reading != null) {
            if (await editReading(customerid, comment, dateofreading, kindofmeter, metercount, meterid, substitute))
                showInfobox();

            return;
        }
    }

    var newReading = await addReading(customerid, comment, dateofreading, kindofmeter, metercount, meterid, substitute)

    if (newReading != null) {
        showInfobox();

        const url = new URL(window.location.href);
        url.searchParams.set("id", newReading.id);

        window.history.pushState({}, '', url);

        loadReadingpage();
    }
}

async function loadReadingpage() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    for (let c of (await getAllCustomers())) {
        let option = document.createElement("option")
        option.value = c.id;
        option.innerHTML = c.firstName +" "+ c.lastName;
        document.getElementById("customerid").appendChild(option);
    }

    if (id != null) {
        var reading = await getReading(id);

        document.getElementById("firstname").value = reading.firstName;
        document.getElementById("lastname").value = reading.lastName;
        document.getElementById("birthdate").value = reading.birthDate;
        document.getElementById("gender").value = reading.gender;

        const today = new Date();
        const formattedDateEnd = today.toISOString().split('T')[0];

        today.setMonth(today.getMonth() - 1);
        const formattedDateStart = today.toISOString().split('T')[0];

        var readings = await getReadingReadings(id, formattedDateStart, formattedDateEnd);

        console.log(readings);

        google.charts.load('current', { 'packages': ['corechart'] });
        google.charts.setOnLoadCallback(drawChart);

        const result = [["Datum", "Heizung", "Strom", "Wasser", "Unbekannt"]];

        const dataMap = new Map();

        readings.forEach(reading => {
            const datum = reading.dateOfReading;

            const typ = reading.kindOfMeter;
            const wert = reading.meterCount;

            if (!dataMap.has(datum)) {
                dataMap.set(datum, {
                    heizung: 0,
                    strom: 0,
                    wasser: 0,
                    unbekannt: 0
                });
            }

            const currentData = dataMap.get(datum);
            if (typ === "HEIZUNG") {
                currentData.heizung = wert;
            } else if (typ === "STROM") {
                currentData.strom = wert;
            } else if (typ === "WASSER") {
                currentData.wasser = wert;
            } else {
                currentData.unbekannt = wert;
            }
        });

        if (dataMap.size == 0) {
            result.push(["Keine Daten vorhanden", 0, 0, 0, 0]);
        }

        // Konvertiere die Map in das gewünschte Format
        dataMap.forEach((value, datum) => {
            result.push([datum, value.heizung, value.strom, value.wasser, value.unbekannt]);
        });

        console.log(result);

        function drawChart() {
            var data = google.visualization.arrayToDataTable(result);

            var options = {
                title: 'Ablesedaten',
                curveType: 'function',
                legend: { position: 'side' }
            };

            var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

            chart.draw(data, options);
        }
    }
}

async function deleteReading() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id != null) {
        removeReading(id);

        window.location = "readings.html";
    }
}

function updateMissingData(element) {
    element.classList.remove("missingData");
}

async function showInfobox() {
    document.getElementById("infobox").classList.remove("show");
    await sleep(1);
    document.getElementById("infobox").classList.add("show");
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}