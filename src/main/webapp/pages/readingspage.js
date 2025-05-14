async function loadReadingspage() {
    for (let r of (await getAllReadings())) {
        let row = document.createElement("tr");

        console.log(r.comment);

        var td = document.createElement("td");
        td.innerHTML = r.meterCount;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.kindOfMeter;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.dateOfReading;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.comment;
        row.appendChild(td);

        td = document.createElement("td");
        
        if (r.substitute) {
            var icon = document.createElement("i");
            icon.classList.add("fa-square-check");
            icon.classList.add("fa-solid");
            td.appendChild(icon);
            td.classList.add("true");
        } else {
            var icon = document.createElement("i");
            icon.classList.add("fa-square-xmark");
            icon.classList.add("fa-solid");
            td.appendChild(icon);
            td.classList.add("false");
        }

        td.classList.add("sub");

        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.meterId;
        row.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = r.customer.firstName + ", " + r.customer.lastName;
        row.appendChild(td);

        td = document.createElement("td");
        let div = document.createElement("div");
        div.classList.add("deleteButton");
        div.classList.add("button");


        let i = document.createElement("i");
        div.addEventListener("click", function (event) {
            event.stopPropagation(); 
            row.remove(); 
            deleteReading(r.id);
        });
        i.classList.add("fa-trash-can");
        i.classList.add("fa-solid");
        div.appendChild(i);
        td.appendChild(div);
        row.appendChild(td);

        document.getElementById("readingTable").appendChild(row);

        row.addEventListener("click", function () {
            openReading(r.id);
        });
    }
}

function openReading(id) {
    window.location = "reading.html?id=" + id;
}

function deleteReading(id) {
    removeReading(id);
}
