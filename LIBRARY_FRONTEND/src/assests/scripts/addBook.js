document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');

    form.addEventListener('submit', function (ev) {
        ev.preventDefault();

        const formData = {
            title: document.getElementById('title').value,
            author: document.getElementById('author').value,
            isbn: document.getElementById('isbn').value,
            publisher: document.getElementById('publisher').value,
            yearOfPublication: document.getElementById('yearPublished').value,
            placeOfPublication: document.getElementById('placePublished').value,
            noOfAvailableCopies: document.getElementById('noOfAvailableCopies').value,
            barcodeNumber: document.getElementById('barcodeNumber').value
        };

        addNewBook(formData); // Gọi hàm sau khi tạo formData
    });
});

function addNewBook(data) {
    fetch('http://localhost:8080/api/books/addBook', {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                console.error("The response is not ok: ", response);
                throw new Error('Failed to add new book');
            }
            return response.json();
        })
        .then(data => {
            console.log('Book added successfully!');
            alert('New book added successfully!');
        })
        .catch(error => {
            console.error("Error adding new book", error);
            alert("Failed to add new book. Please try again");
        });
}
