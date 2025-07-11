document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');

    form.addEventListener('submit', function (ev) {
        ev.preventDefault();

        // retrieve input values from the form
        const barcodeNumber = document.getElementById('barcodeNumber').value;
        const isbn = document.getElementById('isbn').value;
        const author = document.getElementById('author').value;
        const title = document.getElementById('title').value;

        // contruct API url 
        const url = `http://localhost:8080/api/books/search?barcodeNumber=${encodeURIComponent(barcodeNumber)}&isbn=${encodeURIComponent(isbn)}&author=${encodeURIComponent(author)}&title=${encodeURIComponent(title)}`;

        console.log(url);
        fetch(url)
            .then(response => {

                if (!response.ok) {
                    alert('Network response was not ok');
                    throw new Error('Network response was not OK');
                }
                return response.json();
            })
            .then(data => {

                console.log('Search result: ', data);
                displayResult(data);

            })
            .catch(error => {
                console.error('Errror fetching data: ', error);
                alert('An error occured.')
            })

    });

});

function displayResult(data) {

    let html = '<table><tr><th>Title</th><th>Author</th><th>Available copies</th></tr>';

    data.forEach(book => {
        html += ` <tr>
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>${book.noOfAvailableCopies}</td>
                    <td><a href="book-info.html?id=${book.id}">View Details</a></td>
                    </tr>
                    `

    });

    html += '</table>';

    const resultsContainer = document.getElementById('resultsContainer');

    resultsContainer.innerHTML = html;

}