document.addEventListener("DOMContentLoaded", function () {
    const fileInput = document.getElementById("fileInput");
    const uploadButton = document.getElementById("uploadButton");
    const fileNameDisplay = document.getElementById("fileName");
    const uploadStatus = document.getElementById("uploadStatus");

    document.querySelector(".upload-label").addEventListener("click", function () {
        fileInput.click();
    });

    fileInput.addEventListener("change", function () {
        uploadStatus.textContent = "";
        if (fileInput.files.length > 0) {
            fileNameDisplay.textContent = `Выбран файл: ${fileInput.files[0].name}`;
            uploadButton.disabled = false;
        } else {
            fileNameDisplay.textContent = "";
            uploadButton.disabled = true;
        }
    });

    uploadButton.addEventListener("click", function () {
        const file = fileInput.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        uploadStatus.textContent = "Загрузка...";
        uploadButton.disabled = true;

        fetch("/files/upload", {
            method: "POST",
            body: formData
        })
            .then(response => response.text())
            .then(data => {
                uploadStatus.textContent = `Файл загружен: ${data}`;
                fileNameDisplay.textContent = "";
                fileInput.value = "";
                uploadButton.disabled = true;
            })
            .catch(error => {
                uploadStatus.textContent = "Ошибка загрузки файла!";
                uploadButton.disabled = false;
                console.error("Ошибка:", error);
            });
    });
});