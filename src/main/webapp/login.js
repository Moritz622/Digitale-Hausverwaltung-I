if (localStorage.getItem("token") != "") {
    isLoggedIn();
}

function isLoggedIn() {
    fetch("http://localhost:8069/rest/users/isloggedin", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            token: localStorage.getItem("token")
        })
    })
        .then(response => {
            if (response.status === 200) {
                loginSuccessfull();
            }
        })
}

function login() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;


    fetch("http://localhost:8069/rest/users/login", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            email: "---",
            password: password
        })
    })
        .then(res => res.json())
        .then(data => {
            localStorage.setItem("token", data.token);

            console.log(data.token);

            loginSuccessfull();
        })
        .catch(loginFailed());
}

async function loginFailed() {
    document.getElementsByClassName("formContainer")[0].classList.add("loginFailed");

    await sleep(1000);

    document.getElementsByClassName("formContainer")[0].classList.remove("loginFailed");
}

async function loginSuccessfull() {
    document.getElementsByClassName("formContainer")[0].classList.add("loginSuccessfull");

    await sleep(800);

    window.location.assign("homepage.html", "_blank");
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}