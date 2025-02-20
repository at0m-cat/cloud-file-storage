document.addEventListener("DOMContentLoaded", function () {
    const uploadForm = document.getElementById("uploadForm");
    const findForm = document.getElementById("findForm");
    const fileActions = document.getElementById("fileActions");
    const folderActions = document.getElementById("folderActions");

    const selectedFileCount = document.getElementById("selectedFileCount");
    const selectedFolderCount = document.getElementById("selectedFolderCount");

    const renameFileForm = document.getElementById("renameFileForm");
    const deleteFileForm = document.getElementById("deleteFileForm");
    const downloadFileForm = document.getElementById("downloadFileForm");

    const renameFileId = document.getElementById("renameFileId");
    const deleteFileIds = document.getElementById("deleteFileIds");
    const downloadFileIds = document.getElementById("downloadFileIds");

    const renameFolderForm = document.getElementById("renameFolderForm");
    const deleteFolderForm = document.getElementById("deleteFolderForm");
    const downloadFolderForm = document.getElementById("downloadFolderForm");

    const renameFolderId = document.getElementById("renameFolderId");
    const deleteFolderIds = document.getElementById("deleteFolderIds");
    const downloadFolderIds = document.getElementById("downloadFolderIds");

    const fileCheckboxes = document.querySelectorAll(".file-checkbox");
    const folderCheckboxes = document.querySelectorAll(".folder-checkbox");

    function clearSelection(checkboxes) {
        checkboxes.forEach(checkbox => checkbox.checked = false);
    }

    function updateSelection() {
        let selectedFiles = Array.from(fileCheckboxes).filter(checkbox => checkbox.checked).map(checkbox => checkbox.value);
        let selectedFolders = Array.from(folderCheckboxes).filter(checkbox => checkbox.checked).map(checkbox => checkbox.value);

        if (selectedFiles.length > 0) {
            clearSelection(folderCheckboxes);
            selectedFolders = [];
        }

        if (selectedFolders.length > 0) {
            clearSelection(fileCheckboxes);
            selectedFiles = [];
        }

        selectedFileCount.textContent = selectedFiles.length;
        selectedFolderCount.textContent = selectedFolders.length;

        fileActions.classList.toggle("hidden", selectedFiles.length === 0);
        folderActions.classList.toggle("hidden", selectedFolders.length === 0);
        uploadForm.classList.toggle("hidden", selectedFiles.length > 0 || selectedFolders.length > 0);
        findForm.classList.toggle("hidden", selectedFiles.length > 0 || selectedFolders.length > 0);

        renameFolderForm.style.display = selectedFolders.length === 1 ? "block" : "none";
        deleteFolderForm.style.display = selectedFolders.length > 0 ? "block" : "none";
        downloadFolderForm.style.display = selectedFolders.length === 1 ? "block" : "none";

        if (selectedFolders.length === 1) {
            renameFolderId.value = selectedFolders[0];
            deleteFolderIds.value = selectedFolders[0];
            downloadFolderIds.value = selectedFolders[0];
        } else if (selectedFolders.length > 1) {
            deleteFolderIds.value = selectedFolders.join(",");
        }

        renameFileForm.style.display = selectedFiles.length === 1 ? "block" : "none";
        deleteFileForm.style.display = selectedFiles.length > 0 ? "block" : "none";
        downloadFileForm.style.display = selectedFiles.length > 0 ? "block" : "none";

        if (selectedFiles.length === 1) {
            renameFileId.value = selectedFiles[0];
            deleteFileIds.value = selectedFiles[0];
            downloadFileIds.value = selectedFiles[0];
        } else if (selectedFiles.length > 1) {
            deleteFileIds.value = selectedFiles.join(",");
            downloadFileIds.value = selectedFiles.join(",");
        }
    }

    fileCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", updateSelection);
    });

    folderCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", updateSelection);
    });
});