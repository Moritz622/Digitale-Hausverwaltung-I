function addReading() {
    var customerid = "13b53ae0-15c8-4273-80ac-2587699485b6";
    var comment = "Test_" + Math.floor(Math.random() * 10000000);
    var dateOfReading = "2025-03-0" + Math.floor(Math.random() * 10);
    var kindOfMeter = "HEIZUNG";
    var meterCount = Math.floor(Math.random() * 10000000);
    var meterId = Math.floor(Math.random() * 10000000).toString();
    var substitute = false;

    fetch("http://localhost:8069/rest/customerpage/addreading", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            reading: {
                customer: {
                    id: customerid
                },
                dateOfReading: dateOfReading,
                kindOfMeter: kindOfMeter,
                meterCount: meterCount,
                meterId: meterId,
                comment: comment,
                substitute: substitute
            }
        })
    })
        .then(response => {
            if (response.status === 401) {
                window.location.assign("../../index.html");
            }

            if (response.ok) {
                console.log("Ablesung erfolgreich hinzugef�gt");
            }
        })
        .catch(error => {
            console.log("error: " + error);
        });
}
async function getAllReadings() {
    let readings;

    await fetch("http://localhost:8069/rest/readingpage/getallreadings", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token")
        })
    })
        .then(res => {
            if (res.status === 401) {
                window.location.assign("../../index.html"); // Removed "_blank"
                return null; // Stop execution by returning null
            }
            return res.json(); // Parse JSON if status is OK
        })
        .then(data => {
            if (!data) return; // Exit if the request was unauthorized

            readings = data.readings;
        })
        .catch(error => {
            console.error("Error:", error);
        });

    console.log(readings);

    return readings;
}