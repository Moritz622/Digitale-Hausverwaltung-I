async function loadCustomerspage() {
    var count = 0;

    for (let c of (await getAllCustomers())) {
        let row = document.createElement("tr");

        count++;

        var td = document.createElement("td");
        td.innerHTML = c.lastName;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = c.firstName;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = c.birthDate;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = c.gender;
        row.appendChild(td);

        td = document.createElement("td");
        let div = document.createElement("div");
        div.classList.add("deleteButton");
        div.classList.add("button");
        let i = document.createElement("i");
        div.addEventListener("click", function (event) {
            event.stopPropagation(); 
            row.remove(); 
            deleteCustomer(c.id);
        });
        i.classList.add("fa-trash-can");
        i.classList.add("fa-solid");
        div.appendChild(i);
        td.appendChild(div);
        row.appendChild(td);

        document.getElementById("customerTable").appendChild(row);

        row.addEventListener("click", function () {
            openCustomer(c.id);
        });
    }

    document.getElementById("entryCount").innerHTML = count;
}

function openCustomer(id) {
    window.location = "customer.html?id=" + id;
}

function deleteCustomer(id) {
    removeCustomer(id);
}

function searchCustomers() {
    var input = document.getElementById("searchInput").value.toLowerCase();
    var table = document.getElementById("customerTable");
    var rows = table.getElementsByTagName("tr");

    for (var i = 1; i < rows.length; i++) {
        var cells = rows[i].getElementsByTagName("td");

        if (cells.length < 2) continue;

        var nachname = cells[0].textContent.toLowerCase();
        var vorname = cells[1].textContent.toLowerCase();

        console.log(nachname + " " + vorname);

        if (nachname.includes(input) || vorname.includes(input)) {
            rows[i].style.display = "";
        } else {
            rows[i].style.display = "none";
        }
    }
}

function showReadingImportPopup() {
    document.getElementById("readingImportPopup").classList.add("visible");
    document.getElementById("readingImportPopup").classList.add("middlePopup");
}

async function closeReadingImportPopup() {
    document.getElementById("readingImportPopup").classList.add("hide");

    await sleep(800);

    document.getElementById("readingImportPopup").classList.remove("visible");
    document.getElementById("readingImportPopup").classList.remove("hide");
    document.getElementById("readingImportPopup").classList.remove("middlePopup");
}

function showReadingExportPopup() {
    document.getElementById("readingExportPopup").classList.add("visible");
    document.getElementById("readingExportPopup").classList.add("middlePopup");
}

async function closeReadingExportPopup() {
    document.getElementById("readingExportPopup").classList.add("hide");

    await sleep(800);

    document.getElementById("readingExportPopup").classList.remove("visible");
    document.getElementById("readingExportPopup").classList.remove("hide");
    document.getElementById("readingExportPopup").classList.remove("middlePopup");
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function exportCSV() {
    return await fetch("http://localhost:8069/rest/customerpage/export/csv", {
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
            a.download = 'customers.csv'; // Der Dateiname
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
    return await fetch("http://localhost:8069/rest/customerpage/export/json", {
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
            a.download = 'customers.json';
            document.body.appendChild(a);
            a.click();
            a.remove();

            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error('Fehler:', error);
        });
}

async function exportXML() {
    return await fetch("http://localhost:8069/rest/customerpage/export/xml", {
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
            a.download = 'customers.xml';
            document.body.appendChild(a);
            a.click();
            a.remove();

            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error('Fehler:', error);
        });
}

async function importReadings() {
    const fileInput = document.getElementById("importFileUpload");
    const file = fileInput.files[0];

    if (!file) {
        alert("Please select a file first.");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("token", JSON.stringify({ "token": localStorage.getItem("token") }));

    const response = await fetch("http://localhost:8069/rest/customerpage/importreadings", {
        method: "POST",
        body: formData // No need to set headers — browser handles multipart automatically
    });

    const result = await response.text();

    location.reload();
}