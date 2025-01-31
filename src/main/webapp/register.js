function register() {
    var username = document.getElementById("username").value;
    var email = document.getElementById("email").value;
    var password = document.getElementById("password1").value;
    var passwordConfirm = document.getElementById("password2").value;

    if (password != passwordConfirm) {
        console.log("behindert");
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
        });
    }
}
