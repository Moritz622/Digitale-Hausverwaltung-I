function logout() {
    localStorage.setItem("token", "");
    window.location.assign("index.html");
}