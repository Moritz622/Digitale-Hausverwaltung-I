async function loadCustomerspage() {
    for (let c of (await getAllCustomers())) {
        let row = document.createElement("tr");

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