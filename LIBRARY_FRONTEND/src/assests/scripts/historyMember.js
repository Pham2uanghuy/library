document.addEventListener('DOMContentLoaded', function () {

    memberIdParam = getQueryParam('id');
    if (memberIdParam) {
        fetchCheckoutBooks(memberIdParam);

    }
    else {
        alert('Member not found');
        console.error('Member not found')
    }
})

function fetchCheckoutBooks(memberId) {

    // fetch data from checkout_register
    fetch(`http://localhost:8080/api/registers/member/${memberId}`)
        .then(response => {
            if (!response.ok) {
                console.error('Network error');
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.length === 0) {
                displayCheckoutBooks([]);
                return;
            }

            const bookDetailsPromise = data.map(data => {
                return fetch(`http://localhost:8080/api/books/${data.bookId}`)
                    .then(response => {
                        if (!response.ok) {
                            console.error('Network error: ', response);
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(bookDetailsPromise => ({
                        ...bookDetailsPromise,
                        dueDate: data.dueDate,
                        checkoutDate: data.checkoutDate,
                        returnDate: data.returnDate,
                        overdueFine: data.overdueFine,
                        registerId: data.id
                    }))
                    .catch(error => {
                        console.error('an error occured: ', error);
                        alert('An error occured,');
                    })
            })

            Promise.all(bookDetailsPromise)
                .then(bookDetails => {
                    displayCheckoutBooks(bookDetails)
                })
        })
        .catch(error => {
            console.error('An error occured: ', error);
            alert('An error occured');
        })

}
function getQueryParam(param) {

    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);

}

function displayCheckoutBooks(books) {

    console.log('Books to display: ', books);

    const booksHeading = document.getElementById('checkOutBooksHeading');

    const booksTable = document.getElementById('checkOutBooks');

    booksTable.innerHTML = '';

    if (books.length === 0 || !books) {
        booksHeading.style.display = 'none';
        return;
    }

    let thead = document.createElement('thead');
    thead.innerHTML = `
        <tr>
            <th>Index</th>
            <th>Book ID</th>
            <th>Tittle</th>
            <th>Author</th>
            <th>Checkout Date</th>
            <th>Due Date</th>
            <th>Return Date</th>
            <th>Overdue Fine</th>
            <th>Status</th>
        </tr>

    `
    booksTable.appendChild(thead);

    let tbody = document.createElement('tbody');
    booksTable.appendChild(tbody);

    books.forEach((book, index) => {
        let row = tbody.insertRow();

        row.insertCell(0).innerText = index + 1;
        row.insertCell(1).innerText = book.id;
        row.insertCell(2).innerText = book.title;
        row.insertCell(3).innerText = book.author;
        row.insertCell(4).innerText = book.checkoutDate;
        row.insertCell(5).innerText = book.dueDate;
        row.insertCell(6).innerText = book.returnDate;
        row.insertCell(7).innerText = book.overdueFine;
        if (book.returnDate) {
            row.insertCell(8).innerText = 'returned';
        }
    });

}