async function getAllCustomers() {
    let customers;

    await fetch("http://localhost:8069/rest/customerpage/getallcustomers", {
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

            customers = data.customers;
        })
        .catch(error => {
            console.error("Error:", error);
        });

    console.log(customers);

    return customers;
}

async function getCustomer(id) {
    let customer;

    await fetch("http://localhost:8069/rest/customerpage/getcustomer", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            customerid: id
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

            customer = data.customer;
        })
        .catch(error => {
            console.error("Error:", error);
        });

    return customer;
}

function addTestCustomer() {
    var firstname = "Marco_" + Math.floor(Math.random() * 10000000);
    var lastname = "Polo_" + Math.floor(Math.random() * 10000000);
    var birthdate = "2000-01-01";
    var gender = "M";

    fetch("http://localhost:8069/rest/customerpage/addcustomer", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            customer: {
                firstName: firstname,
                lastName: lastname,
                birthDate: birthdate,
                gender: gender,
                user: ""
            }
        })
    })
        .then(response => {
            if (response.status === 401) {
                window.location.assign("../../index.html", "_blank");
            }

            if (response.ok) {
                console.log("Kunde erfolgreich angelegt");

                //hier logik wenn kunde erfolgreich angelegt wurde
            }
        })
        .catch(error => {
            console.log("nigga " + error);
        });
}

async function addCustomer(firstname, lastname, birthdate, gender) {
    return await fetch("http://localhost:8069/rest/customerpage/addcustomer", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            customer: {
                firstName: firstname,
                lastName: lastname,
                birthDate: birthdate,
                gender: gender,
                user: ""
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

            return response.status;
        })
        .then(data => {
            if (!data) return;

            if (data >= 400) {
                return data;
            }

            return data.customer;
        })
        .catch(error => {
            return response.status;
        });
}

function removeCustomer(customerid) {
    fetch("http://localhost:8069/rest/customerpage/removecustomer", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            customerid: customerid
        })
    })
        .then(response => {
            if (response.status === 401) {
                window.location.assign("../../index.html", "_blank");
            }

            if (response.ok) {
                console.log("Kunde erfolgreich angepasst");
            }
        })
        .catch(error => {
            console.log(error);
        });
}

async function editCustomer(firstname, lastname, birthdate, gender, id) {
    return await fetch("http://localhost:8069/rest/customerpage/putcustomer", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            customer: {
                firstName: firstname,
                lastName: lastname,
                birthDate: birthdate,
                gender: gender,
                user: "",
                id: id
            }
        })
    })
        .then(response => {
            if (response.status === 401) {
                window.location.assign("../../index.html", "_blank");
            }

            if (response.ok) {
                return true;
            }
        })
        .catch(error => {
            return false;
        });
}

async function getCustomerReadings(customerid, startDate, endDate, type) {
    return await fetch("http://localhost:8069/rest/customerpage/getcustomerreadings", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token"),
            customerid: customerid,
            startdate: startDate,
            enddate: endDate,
            type: type
        })
    })
        .then(res => res.json())
        .then(data => {
            const readings = data.readings;

            readings.forEach(reading => {
                console.log(`${reading.firstName} ${reading.lastName} ${reading.id}`);
            });

            return readings;
        })
        .catch();
}
