async function loadCustomerspage() {
    for (let c of (await getAllCustomers())) {
        var row = document.createElement("tr");

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

        document.getElementById("customerTable").appendChild(row);

        row.addEventListener("click", function () {
            openCustomer(c.id);
        });
    }
}

function openCustomer(id) {
    window.location = "customer.html?id=" + id;
}