async function loadHomepage() {
    document.getElementById("customerCount").innerHTML = (await getAllCustomers()).length;
    document.getElementById("readingCount").innerHTML = (await getAllReadings()).length;

}