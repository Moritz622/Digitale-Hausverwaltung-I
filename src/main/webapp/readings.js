function showReadingImportPopup() {
	document.getElementById("readingImportPopup").classList.add("visible");
	document.getElementById("readingImportPopup").classList.add("middlePopup");
}

async function closeReadingImportPopup() {
	document.getElementById("readingImportPopup").classList.add("hide");

	await sleep(800);

	document.getElementById("readingImportPopup").classList.remove("visible");
	document.getElementById("readingImportPopup").classList.remove("hide");
	document.getElementById("readingImportPopup").classList.remove("middlePopup");
}

function showReadingExportPopup() {
	document.getElementById("readingExportPopup").classList.add("visible");
	document.getElementById("readingExportPopup").classList.add("middlePopup");
}

async function closeReadingExportPopup() {
	document.getElementById("readingExportPopup").classList.add("hide");

	await sleep(800);

	document.getElementById("readingExportPopup").classList.remove("visible");
	document.getElementById("readingExportPopup").classList.remove("hide");
	document.getElementById("readingExportPopup").classList.remove("middlePopup");
}

function sleep(ms) {
	return new Promise(resolve => setTimeout(resolve, ms));
}