async function register() {
    var username = document.getElementById("username").value;
    var email = document.getElementById("email").value;
    var password = document.getElementById("password1").value;
    var passwordConfirm = document.getElementById("password2").value;

    if (password != passwordConfirm) {
        loginFailed();
    } else {
        fetch("http://localhost:8069/rest/users/register", {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password
            })
        }).then(res => res.json())
            .then(data => {
                loginSuccessfull();
            })
            .catch(loginFailed());
    }
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