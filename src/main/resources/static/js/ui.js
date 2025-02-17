document.addEventListener("DOMContentLoaded", function () {
    const uploadForm = document.getElementById("uploadForm");
    const fileActions = document.getElementById("fileActions");
    const selectedCount = document.getElementById("selectedCount");
    const renameForm = document.getElementById("renameForm");
    const renameFileId = document.getElementById("renameFileId");
    const deleteFileIds = document.getElementById("deleteFileIds");
    const downloadFileIds = document.getElementById("downloadFileIds");
    const fileCheckboxes = document.querySelectorAll(".file-checkbox");

    let selectedFiles = [];

    function updateSelection() {
        selectedFiles = Array.from(fileCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value);

        selectedCount.textContent = selectedFiles.length;

        if (selectedFiles.length > 0) {
            uploadForm.classList.add("hidden");
            fileActions.classList.remove("hidden");

            if (selectedFiles.length === 1) {
                renameForm.style.display = "block";
                renameFileId.value = selectedFiles[0];
            } else {
                renameForm.style.display = "none";
            }

            deleteFileIds.value = selectedFiles.join(",");
            downloadFileIds.value = selectedFiles.join(",");
        } else {
            uploadForm.classList.remove("hidden");
            fileActions.classList.add("hidden");
        }
    }

    fileCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", updateSelection);
    });
});