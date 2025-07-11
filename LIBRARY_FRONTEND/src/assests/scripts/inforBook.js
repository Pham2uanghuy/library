let bookId = null;

document.addEventListener('DOMContentLoaded', function () {
    bookId = getQueryParam('id');
    if (!bookId) {
        console.error('No book ID specified in the URL');
        return;
    }
    console.log(bookId);
    fetch(`http://localhost:8080/api/books/${bookId}`)
        .then(response => {
            if (!response.ok) {
                alert('Network response was not Ok');
                throw new Error('Network response was not Ok');
            }
            return response.json();
        })
        .then(data => {
            displayBookDetails(data);
            updateLinkVisibility(data.noOfAvailableCopies);

            const checkoutLink = document.getElementById('checkoutLink');
            checkoutLink.href = `book-checkout.html?barcodeNumber=${data.barcodeNumber}`;
        })
        .catch(error => {
            alert('Unexpected error: ', error.message);
            console.error('Unexpected error: ', error);
        })

    // set up current holder ink 
    const currentHoldersLink = document.getElementById('currentHoldersLink');
    currentHoldersLink.addEventListener('click', function (ev) {
        ev.preventDefault();
        fetchCurrentHolders(bookId);
    })

    const removeCopyLink = document.getElementById('removeCopyLink');
    removeCopyLink.addEventListener('click', function () {
        confirmAndRemoveBookCopy();
    })
    const addCopyLink = document.getElementById('addCopyLink');
    addCopyLink.addEventListener('click', function () {
        confirmAndAddBookCopy();
    })

    const editBookLink = document.getElementById('editBookLink');
    editBookLink.href = `edit-book.html?id=${bookId}`;

    const historyLink = document.querySelector('a[href="book-history.html"]');
    historyLink.href = `book-history.html?id=${bookId}`;
})
function getQueryParam(param) {

    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);

}
function displayBookDetails(book) {
    document.getElementById('bookTitleAndAuthor').textContent = `${book.title} ${book.author}`;
    document.getElementById('isbn').textContent = `${book.isbn}`;
    document.getElementById('publisher').textContent = `${book.publisher}`;
    document.getElementById('yearPublished').textContent = `${book.yearOfPublication}`;
    document.getElementById('placePublished').textContent = `${book.placeOfPublication}`;
    document.getElementById('noOfAvailableCopies').textContent = `${book.noOfAvailableCopies}`;

    //control the visibility of remove link
    const removeCopyLink = document.getElementById('removeCopyLink');
    if (book.noOfAvailableCopies > 0) {
        removeCopyLink.style.display = 'inline'
    }
    else {
        removeCopyLink.style.display = 'none';
    }
}

function updateLinkVisibility(availableCopies) {
    const checkoutLink = document.getElementById('checkoutLink');
    const removeCopyLink = document.getElementById('removeCopyLink');

    if (availableCopies > 0) {
        checkoutLink.style.display = 'inline';
        removeCopyLink.style.display = 'inline';
    } else {
        checkoutLink.style.display = 'none';
        removeCopyLink.style.display = 'none';
    }
}

function fetchCurrentHolders(bookId) {

    fetch(`http://localhost:8080/api/registers/book/${bookId}`)
        .then(response => {
            if (!response.ok) {
                alert("Failed to fetch current holder");
                throw new Error("Failed to fetch current holders.")
            }
            return response.json();
        })
        .then(registers => {
            const currentHolders = registers.filter(register => register.returnDate == null);

            const memberDetailsPromises = currentHolders.map(register =>
                fetch(`http://localhost:8080/api/members/${register.memberId}`)
                    .then(response => {
                        if (!response.ok) {
                            alert('Failed to fetch member details');
                            throw new Error('Failed to fetch member details');
                        }
                        return response.json();
                    })
                    .then(member => ({
                        ...member,
                        checkoutDate: register.checkoutDate,
                        dueDate: register.dueDate
                    }))
            );
            return Promise.all(memberDetailsPromises);
        })
        .then(membersWithDetails => {
            displayCurrentHolders(membersWithDetails);
        })
        .catch(error => {
            alert('Unexpected error while fetching current holder');
            console.error('Unexpected error while fetching current holders: ', error);
        })
}

function displayCurrentHolders(members) {
    console.log('displaying members: ', members);

    let html = '<p>No current holders</p>';

    if (members.length > 0) {
        html = `
            <h2> Current holders </h2>
            <table> <thead>
                <tr><th>First Name</th><th>Last Name</th><th>Card #</th><th>Checkout Date</th><th>Due Date</th>
                </thead>
                <tbody>

        `
    }
    members.forEach(member => {

        const checkoutDate = new Date(member.checkoutDate).toLocaleDateString();
        const dueDate = new Date(member.dueDate).toLocaleDateString();

        html += `
                <tr>
                    <td>${member.firstName}</td>
                    <td>${member.lastName}</td>
                    <td>${member.id}</td>
                    <td>${member.checkoutDate}</td>
                    <td>${member.dueDate}</td>
                </tr>
            `;
    })

    if (members.length > 0) { }
    html += '</tbody></table>'

    const currentHoldersContainer = document.getElementById('currentHoldersContainer');
    currentHoldersContainer.innerHTML = html;

}

function removeBookCopy() {
    updateBookCopies(-1);
}

function addBookCopy() {
    updateBookCopies(1);
}

function confirmAndAddBookCopy() {
    if (confirm('Are you sure u want to add a copy')) {
        addBookCopy();
    }
}
function confirmAndRemoveBookCopy() {
    if (confirm('Are u sure u want to remove a copy')) {
        removeBookCopy();
    }
}
function updateBookCopies(incr) {

    fetch(`http://localhost:8080/api/books/${bookId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch a book:');
            }
            return response.json();
        })
        .then(data => {
            const updatedCopies = data.noOfAvailableCopies + incr;

            fetch(`http://localhost:8080/api/books/updateBook/${bookId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ noOfAvailableCopies: updatedCopies })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to update book copies');
                    }
                    return response.json();
                })
                .then(updatedBook => {
                    if (incr === 1) alert('A copy has been successfully added');
                    else alert('A copy successfully removed');
                    location.reload();
                })
                .catch(error => {
                    console.error('Unexpected while updating copies: ', error);
                    alert('Unexpected while updating copies');
                })
        })

}
