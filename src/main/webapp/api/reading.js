async function addReading(customerid, comment, dateOfReading, kindOfMeter, meterCount, meterId, substitute) {

    return await fetch("http://localhost:8069/rest/readingpage/addreading", {
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
                window.location.assign("../../index.html", "_blank");
            }

            if (response.ok) {
                return response.json();
            }
        })
        .then(data => {
            if (!data) return;

            return data.reading;
        })
        .catch(error => {
            return false;
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

async function removeReading(readingid) {

    fetch("http://localhost:8069/rest/readingpage/removereading", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            readingid: readingid
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

async function getReading(id) {
    let reading;

    await fetch("http://localhost:8069/rest/readingpage/getreading", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            readingid: id
        })
    })
        .then(res => {
            if (res.status === 401) {
                window.location.assign("../../index.html");
                return null;
            }
            return res.json();
        })
        .then(data => {
            if (!data) return; // Exit if the request was unauthorized

            reading = data.reading;
        })
        .catch(error => {
            console.error("Error:", error);
        });

    return reading;
}

async function editReading(readingid, customerid, comment, dateOfReading, kindOfMeter, meterCount, meterId, substitute) {

    return await fetch("http://localhost:8069/rest/readingpage/putreading", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            reading: {
                id: readingid,
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
                window.location.assign("../../index.html", "_blank");
            }

            if (response.ok) {
                return response.json();
            }
        })
        .then(data => {
            if (!data) return;

            return data.reading;
        })
        .catch(error => {
            return false;
        });
}