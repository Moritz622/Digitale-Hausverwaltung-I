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
                showInfobox("Kunde erfolgreich gespeichert.", false);

            return;
        }
    }

    var newCustomer = await addCustomer(firstname, lastname, birthdate, gender)

    console.log(newCustomer);

    if (newCustomer.id) {
        showInfobox("Kunde erfolgreich gespeichert.", false);

        const url = new URL(window.location.href);
        url.searchParams.set("id", newCustomer.id);

        window.history.pushState({}, '', url);

        loadCustomerpage();
    } else if(newCustomer == 409) {
        showInfobox("Es existiert bereits ein Kunde mit diesem Namen.", true);
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

        document.getElementById("chartDateFrom").value = formattedDateStart;

        document.getElementById("chartDateTo").value = formattedDateEnd;

        loadChart();
    }
}

async function loadChart() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    var fromDate = new Date(document.getElementById("chartDateFrom").value);
    const formattedDateStart = fromDate.toISOString().split('T')[0];

    var toDate = new Date(document.getElementById("chartDateTo").value);
    const formattedDateEnd = toDate.toISOString().split('T')[0];

    var chartType = document.getElementById("chartType").value;

    var readings = await getCustomerReadings(id, formattedDateStart, formattedDateEnd, chartType);

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

    [...dataMap.entries()]
        .sort((a, b) => new Date(a[0]) - new Date(b[0]))
        .forEach(([datum, value]) => {
            result.push([datum, value.heizung, value.strom, value.wasser, value.unbekannt]);
        });

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

async function showInfobox(text, error) {
    document.getElementById("infobox").innerHTML = text;

    if (error) {
        document.getElementById("infobox").classList.add("error");
        document.getElementById("infobox").classList.remove("ok");
    } else {
        document.getElementById("infobox").classList.remove("error");
        document.getElementById("infobox").classList.add("ok");
    }

    document.getElementById("infobox").classList.remove("show");
    await sleep(1);
    document.getElementById("infobox").classList.add("show");
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}