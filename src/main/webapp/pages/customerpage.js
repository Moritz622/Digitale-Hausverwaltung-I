async function saveCustomer() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    var firstname = document.getElementById("firstname").value;
    var lastname = document.getElementById("lastname").value;
    var birthdate = document.getElementById("birthdate").value;
    var gender = document.getElementById("gender").value;

    var validRequest = true;

    if (firstname == "") {
        document.getElementById("firstname").classList.add("missingData");
        validRequest = false;
    }

    if (lastname == "") {
        document.getElementById("lastname").classList.add("missingData");
        validRequest = false;
    }

    if (birthdate == "") {
        document.getElementById("birthdate").classList.add("missingData");
        validRequest = false;
    }

    if (!validRequest)
        return;

    if (id != null) {
        var customer = await getCustomer(id);

        if (customer != null) {
            if (await editCustomer(firstname, lastname, birthdate, gender, id))
                showInfobox();

            return;
        }
    }

    var newCustomer = await addCustomer(firstname, lastname, birthdate, gender)

    if (newCustomer != null) {
        showInfobox();

        const url = new URL(window.location.href);
        url.searchParams.set("id", newCustomer.id);

        window.history.pushState({}, '', url);
    }
}

async function loadCustomerpage() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id != null) {
        var customer = await getCustomer(id);

        document.getElementById("firstname").value = customer.firstName;
        document.getElementById("lastname").value = customer.lastName;
        document.getElementById("birthdate").value = customer.birthDate;
        document.getElementById("gender").value = customer.gender;

        const today = new Date();
        const formattedDateEnd = today.toISOString().split('T')[0];

        today.setMonth(today.getMonth() - 1);
        const formattedDateStart = today.toISOString().split('T')[0];

        var readings = await getCustomerReadings(id, formattedDateStart, formattedDateEnd);

        console.log(readings);

        google.charts.load('current', { 'packages': ['corechart'] });
        google.charts.setOnLoadCallback(drawChart);

        const result = [['Datum', 'Heizung', 'Strom', 'Wasser', 'Unbekannt']];

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
            if (typ === "Heizung") {
                currentData.heizung = wert;
            } else if (typ === "Strom") {
                currentData.strom = wert;
            } else if (typ === "Wasser") {
                currentData.wasser = wert;
            } else {
                currentData.unbekannt = wert;
            }
        });

        // Konvertiere die Map in das gewünschte Format
        dataMap.forEach((value, datum) => {
            result.push([datum, value.heizung, value.strom, value.wasser, value.unbekannt]);
        });

        console.log(result);

        function drawChart() {
            var data = google.visualization.arrayToDataTable([
                result
            ]);

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

async function deleteCustomer() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id != null) {
        removeCustomer(id);

        window.location = "customers.html";
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