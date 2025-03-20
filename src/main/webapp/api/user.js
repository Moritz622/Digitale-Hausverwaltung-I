function changePassword() {
    var password1 = document.getElementById("password1").value.toString();
    var password2 = document.getElementById("password2").value.toString();

    if (password1 === password2) {
        fetch("http://localhost:8069/rest/customerpage/changepassword", {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                token: localStorage.getItem("token"),
                password: password1
            })
        })
            .then(response => {
                if (response.ok) {
                    showInfobox();
                }
            })
            .catch(error => {
                console.log("error: " + error);
            });
    } else {
        document.getElementById("password1").classList.add("missingData");
        document.getElementById("password2").classList.add("missingData");
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