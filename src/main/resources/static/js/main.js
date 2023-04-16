'use strict';

const singleUploadForm = document.querySelector('#singleUploadForm');
const singleFileUploadInput = document.querySelector('#singleFileUploadInput');
const singleFileUploadError = document.querySelector('#singleFileUploadError');
const singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

const fileListContainer = document.querySelector('.file-list-container');
const fileList = fileListContainer.querySelector('.file-list');

function uploadSingleFile(file) {
    const formData = new FormData();
    formData.append("file", file);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/uploadFile");

    const progressContainer = document.querySelector('.progress-bar-container');
    const progressBar = document.querySelector('.progress-bar');
    const progressText = document.querySelector('.progress-text');

    singleFileUploadSuccess.style.display = "none";
    singleFileUploadError.style.display = "none";

    xhr.upload.addEventListener("progress", function(event) {
        if (event.lengthComputable) {
            const percentComplete = (event.loaded / event.total) * 100;
            console.log(event.total + " " + event.loaded + " " + percentComplete)
            progressBar.style.width = percentComplete + '%';
            progressText.innerText = percentComplete.toFixed(0) + '%'; // update progress text
            progressContainer.style.display = "block";
            progressContainer.style.backgroundColor = "gray";
            progressBar.style.backgroundColor = '#11c737';
        }
    });

    xhr.onload = function () {
        console.log(xhr.responseText);
        if (xhr.status === 200) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p>";
            singleFileUploadSuccess.style.display = "block";
            progressBar.style.width = '100%';

            singleUploadForm.reset();
            updateFileList();
        } else {
            singleFileUploadSuccess.style.display = "none";
            singleFileUploadError.innerHTML = "Some Error Occurred";
        }
    }

    xhr.send(formData);
}

singleUploadForm.addEventListener('submit', function (event) {
    const files = singleFileUploadInput.files;
    if (files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
}, true);

// Fetch the list of uploaded files from the server

function updateFileList() {
    fetch('/api/uploadedFiles')
        .then(response => response.json())
        .then(data => {
            const fileListContainer = document.querySelector('.file-list');
            fileListContainer.innerHTML = `
        <table>
          <thead>
            <tr>
              <th>File Name</th>
              <th>File Size</th>
            </tr>
          </thead>
          <tbody>
          </tbody>
        </table>
      `;

            const fileList = fileListContainer.querySelector('tbody');

            data.forEach(file => {
                const fileRow = document.createElement('tr');
                fileRow.innerHTML = `
          <td><a href="${file.fileDownloadUri}" target="_blank">${file.fileName}</a></td>
          <td>${formatBytes(file.size)}</td>
        `;
                fileList.appendChild(fileRow);
            });
        })
        .catch(error => {
            console.error('Error fetching file list:', error);
        });
}

function formatBytes(bytes, decimals = 2) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

window.addEventListener('load', updateFileList);