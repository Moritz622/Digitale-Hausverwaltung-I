async function loadReadingspage() {
    for (let c of (await getAllCustomers())) {
        let option = document.createElement("option")
        option.value = c.id;
        option.innerHTML = c.firstName + " " + c.lastName;
        document.getElementById("customeridFilter").appendChild(option);
    }

    var count = 0;

    for (let r of (await getAllReadings())) {
        count++;

        let row = document.createElement("tr");

        var td = document.createElement("td");
        td.innerHTML = r.meterCount;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.kindOfMeter;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.dateOfReading;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.comment;
        row.appendChild(td);

        td = document.createElement("td");
        
        if (r.substitute) {
            var icon = document.createElement("i");
            icon.classList.add("fa-square-check");
            icon.classList.add("fa-solid");
            td.appendChild(icon);
            td.classList.add("true");
        } else {
            var icon = document.createElement("i");
            icon.classList.add("fa-square-xmark");
            icon.classList.add("fa-solid");
            td.appendChild(icon);
            td.classList.add("false");
        }

        td.classList.add("sub");

        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.meterId;
        row.appendChild(td);

        if (r.customer) {
            td = document.createElement("td");
            td.innerHTML = r.customer.firstName + ", " + r.customer.lastName;
            td.value = r.customer.id;
            row.appendChild(td);
        }else{
            td = document.createElement("td");
            td.innerHTML = "Kein Kunde";
            row.appendChild(td);
        }

        td = document.createElement("td");
        let div = document.createElement("div");
        div.classList.add("deleteButton");
        div.classList.add("button");


        let i = document.createElement("i");
        div.addEventListener("click", function (event) {
            event.stopPropagation(); 
            row.remove(); 
            deleteReading(r.id);
        });
        i.classList.add("fa-trash-can");
        i.classList.add("fa-solid");
        div.appendChild(i);
        td.appendChild(div);
        row.appendChild(td);

        document.getElementById("readingTable").appendChild(row);

        row.addEventListener("click", function () {
            openReading(r.id);
        });
    }

    document.getElementById("entryCount").innerHTML = count;
}

function openReading(id) {
    window.location = "reading.html?id=" + id;
}

function deleteReading(id) {
    removeReading(id);
}
function searchReadings() {
    var table = document.getElementById("readingTable");
    var rows = table.getElementsByTagName("tr");

    var typFilter = ".";

    if (document.getElementById("filterHeizung").checked)
        typFilter += "heizung,";
    if (document.getElementById("filterWasser").checked)
        typFilter += "wasser,";
    if (document.getElementById("filterStrom").checked)
        typFilter += "strom,";
    if (document.getElementById("filterUnbekannt").checked)
        typFilter += "unbekannt";

    var dateStartFilter;
    var dateEndFilter;

    if (document.getElementById("dateFromFilter").value != "") {
        dateStartFilter = new Date(document.getElementById("dateFromFilter").value);
    }

    if (document.getElementById("dateToFilter").value != "") {
        dateEndFilter = new Date(document.getElementById("dateToFilter").value);
    }

    var meteridFilter = document.getElementById("meteridFilter").value;

    var customeridFilter = document.getElementById("customeridFilter").value;

    var count = 0;

    for (var i = 1; i < rows.length; i++) {
        var cells = rows[i].getElementsByTagName("td");

        if (cells.length < 2) continue;

        var typ = cells[1].textContent.toLowerCase();
        var date = new Date(cells[2].textContent);
        var meterid = cells[5].textContent;
        var customerid = cells[6].value;

        if (!typFilter.includes(typ)) {
            rows[i].style.display = "none";
            console.log("typ nicht korrekt");
            continue;
        }

        if (dateStartFilter != null && dateStartFilter > date) {
            rows[i].style.display = "none";
            console.log("start datum");
            continue;
        }

        if (dateEndFilter != null && dateEndFilter < date) {
            rows[i].style.display = "none";
            console.log("end datum");
            continue;
        }

        if (meteridFilter != "" && meteridFilter != meterid) {
            rows[i].style.display = "none";
            console.log("meterid");
            continue;
        }

        if (customeridFilter != customerid & customeridFilter != "all") {
            rows[i].style.display = "none";
            console.log("customerid: " + customeridFilter + " " + customerid);
            continue;
        }

        rows[i].style.display = "";
        count++;
    }

    document.getElementById("entryCount").innerHTML = count;
}

async function exportCSV() {
    return await fetch("http://localhost:8069/rest/readingpage/export/csv", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Fehler beim Herunterladen');
            }
            return response.blob(); // CSV als Blob
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = 'export.csv'; // Der Dateiname
            document.body.appendChild(a);
            a.click();
            a.remove();

            window.URL.revokeObjectURL(url); // Speicher aufräumen
        })
        .catch(error => {
            console.error('Fehler:', error);
        });
}

async function exportJSON() {
    return await fetch("http://localhost:8069/rest/readingpage/export/json", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Fehler beim Herunterladen');
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = 'export.json';
            document.body.appendChild(a);
            a.click();
            a.remove();

            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error('Fehler:', error);
        });
}