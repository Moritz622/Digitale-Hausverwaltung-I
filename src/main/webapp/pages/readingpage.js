async function saveReading() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    var customerid = document.getElementById("customerid").value;
    var comment = document.getElementById("comment").value;
    var metercount = document.getElementById("metercount").value;
    var kindofmeter = document.getElementById("kindofmeter").value;
    var substitute = document.getElementById("substitute").checked;
    var meterid = document.getElementById("meterid").value;
    var dateofreading = document.getElementById("dateofreading").value;

    var newCustomer = (document.getElementById("customerid").value === "new");
    
    var customerfirstName = document.getElementById("firstname").value;
    var customerlastName = document.getElementById("lastname").value;
    var customerbirthdate = document.getElementById("birthdate").value;
    var customergender = document.getElementById("gender").value;

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

    if (newCustomer) {
        if (customerfirstName === "") {
            document.getElementById("firstname").classList.add("missingData");
            validRequest = false;
        }
        if (customerlastName === "") {
            document.getElementById("lastname").classList.add("missingData");
            validRequest = false;
        }
        if (customerbirthdate === "") {
            document.getElementById("birthdate").classList.add("missingData");
            validRequest = false;
        }
    }

    if (!validRequest)
        return;

    if (newCustomer) {
        var c = await addCustomer(customerfirstName, customerlastName, customerbirthdate, customergender);

        customerid = c.id;
    }

    if (id != null) {
        var reading = await getReading(id);

        if (reading != null) {
            if (await editReading(id, customerid, comment, dateofreading, kindofmeter, metercount, meterid, substitute) != null)
                showInfobox();

            return;
        }
    }

    var newReading = await addReading(customerid, comment, dateofreading, kindofmeter, metercount, meterid, substitute);

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

        if (reading.customer) {
            document.getElementById("customerid").value = reading.customer.id;
        }

        document.getElementById("metercount").value = reading.meterCount;
        document.getElementById("kindofmeter").value = reading.kindOfMeter.toString().toUpperCase();

        const date = new Date(reading.dateOfReading);
        const formattedDate = date.toISOString().split('T')[0]; // ergibt yyyy-mm-dd
        document.getElementById("dateofreading").value = formattedDate;

        document.getElementById("comment").value = reading.comment;
        document.getElementById("meterid").value = reading.meterId;
        document.getElementById("substitute").checked = reading.substitute;

        customerChanged();
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

function openCustomer() {
    var id = document.getElementById("customerid").value;

    window.open("customer.html?id=" + id);
}

function customerChanged() {
    if (customerid.value === "new") {
        document.getElementById("inputContainerCustomer").style.display = "";
    } else {
        document.getElementById("inputContainerCustomer").style.display = "none";
    }
}