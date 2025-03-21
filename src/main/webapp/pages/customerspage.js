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
