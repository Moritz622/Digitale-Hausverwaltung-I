async function loadHomepage() {
    document.getElementById("customerCount").innerHTML = (await getAllCustomers()).length;

    console.log(await getAllCustomers());
}