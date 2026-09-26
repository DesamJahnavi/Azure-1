const API_URL = "/files";

// Upload file
document.getElementById("uploadForm").addEventListener("submit", async function (event) {

    event.preventDefault();

    const file = document.getElementById("file").files[0];
    const department = document.getElementById("department").value;
    const subject = document.getElementById("subject").value;
    const year = document.getElementById("year").value;
    const type = document.getElementById("type").value;

    const formData = new FormData();

    formData.append("file", file);
    formData.append("department", department);
    formData.append("subject", subject);
    formData.append("year", year);
    formData.append("type", type);

    try {

        const response = await fetch(`${API_URL}/upload`, {
            method: "POST",
            body: formData
        });

        const result = await response.text();

        document.getElementById("uploadMessage").textContent = result;

        if (response.ok) {
            document.getElementById("uploadForm").reset();
            loadFiles();
        }

    } catch (error) {

        document.getElementById("uploadMessage").textContent =
            "Upload failed: " + error.message;
    }
});


// Search files by tag
async function searchFiles() {

    const tagName = document.getElementById("searchTag").value;
    const tagValue = document.getElementById("searchValue").value;

    if (!tagValue.trim()) {
        alert("Please enter a tag value.");
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/search?tagName=${encodeURIComponent(tagName)}&tagValue=${encodeURIComponent(tagValue)}`
        );

        const files = await response.json();

        displayFiles(files, "searchResults");

    } catch (error) {

        document.getElementById("searchResults").textContent =
            "Search failed: " + error.message;
    }
}


// Load all files
async function loadFiles() {

    try {

        const response = await fetch(API_URL);

        const files = await response.json();

        displayFiles(files, "fileList");

    } catch (error) {

        document.getElementById("fileList").textContent =
            "Could not load files: " + error.message;
    }
}


// Display files
function displayFiles(files, elementId) {

    const container = document.getElementById(elementId);

    container.innerHTML = "";

    if (files.length === 0) {
        container.textContent = "No files found.";
        return;
    }

    files.forEach(function (fileName) {

        const fileItem = document.createElement("div");
        fileItem.className = "file-item";

        const fileNameElement = document.createElement("span");
        fileNameElement.className = "file-name";
        fileNameElement.textContent = fileName;

        const actions = document.createElement("div");
        actions.className = "file-actions";

        const downloadButton = document.createElement("button");
        downloadButton.textContent = "Download";

        downloadButton.onclick = function () {
            window.location.href =
                `${API_URL}/download/${encodeURIComponent(fileName)}`;
        };

        const deleteButton = document.createElement("button");
        deleteButton.textContent = "Delete";

        deleteButton.onclick = function () {
            deleteFile(fileName);
        };

        actions.appendChild(downloadButton);
        actions.appendChild(deleteButton);

        fileItem.appendChild(fileNameElement);
        fileItem.appendChild(actions);

        container.appendChild(fileItem);
    });
}


// Delete file
async function deleteFile(fileName) {

    const confirmDelete = confirm(
        "Are you sure you want to delete " + fileName + "?"
    );

    if (!confirmDelete) {
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/${encodeURIComponent(fileName)}`,
            {
                method: "DELETE"
            }
        );

        const result = await response.text();

        alert(result);

        loadFiles();

    } catch (error) {

        alert("Delete failed: " + error.message);
    }
}


// Load files when page opens
window.addEventListener("load", function () {
    loadFiles();
});