let memberId = null;

document.addEventListener('DOMContentLoaded', function () {

    memberId = getQueryParam('id');
    if (!memberId) {
        console.error('No memberID specified in the URL');
        return;
    }

    const url = `http://localhost:8080/api/members/${memberId}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                console.error('Network error');
                throw new Error('Network response was not ok');

            }
            return response.json();
        })
        .then(data => {
            displayMemberDetails(data);
            // fetch checkout books here
            fetchCheckoutBooks(data.id)
        })
        .catch(error => {
            console.error('an error occured: ', error);
            alert('An error occured,');
        })

    const memberHistoryLink = document.querySelector('a[href="member-history.html"]')
    if (memberHistoryLink) memberHistoryLink.href = `member-history.html?id=${memberId}`

    const editMemberLink = document.querySelector('a[href="edit-member.html"]');
    if (editMemberLink) editMemberLink.href = `edit-member.html?id=${memberId}`;
});

function getQueryParam(param) {

    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);

}

function displayMemberDetails(member) {

    document.getElementById('memberFullName').textContent = `${member.firstName} ${member.lastName}`;
    document.getElementById('cardNumber').textContent = `${member.id}`;
    let address = member.address != null ? `${member.address.streetName} ${member.address.streetNumber} ${member.address.placeName} ${member.address.country}` : `N/A`;
    document.getElementById('address').textContent = `${address}`;
    document.getElementById('phone').textContent = `${member.phone}`;
    document.getElementById('email').textContent = `${member.email}`;
    let dob = new Date(member.dateOfBirth).toLocaleDateString("en-US");
    document.getElementById('dob').textContent = `${dob}`;
    let membershipStarted = new Date(member.membershipStarted).toLocaleTimeString("en-US");
    document.getElementById('membershipStarted').textContent = `${membershipStarted}`;
    let membershipEnded = member.membershipEnded ? new Date(member.membershipEnded).toLocaleDateString('en-US') : 'N/A';
    document.getElementById('membershipEnded').textContent = `${membershipEnded}`;
    document.getElementById('membershipStatus').textContent = member.isActive ? 'Active' : 'Terminateed';

    // dunamically set 'terminate membership' or 'Activate membership' link
    const membershipActionLink = document.getElementById('membershipActionLink');
    if (member.isActive) {
        membershipActionLink.textContent = 'Terminate Membership';
        membershipActionLink.onclick = terminateMembership;
    } else {
        membershipActionLink.textContent = 'Activate Membership';
        membershipActionLink.onclick = activateMembership;
    }

    //control the visibility of the checkout link based on member's active status
    const checkoutLink = document.getElementById('checkoutLink');
    if (member.isActive) {
        checkoutLink.style.display = 'inLine';
        checkoutLink.href = `book-checkout.html?memberBarcode=${member.barcodeNumber}`;
    }
    else {
        checkoutLink.style.display = 'none';
    }
}

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

            const bookDetailsPromise = data.filter(data => data.returnDate === null)
                .map(data => {
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

function displayCheckoutBooks(books) {

    console.log('Books to display: ', books);

    const booksHeading = document.getElementById('checkOutBooksHeading');

    const booksTable = document.getElementById('checkOutBooks');

    booksTable.innerHTML = '';

    if (books.length === 0 || !books) {
        booksHeading.style.display = 'none';
        return;
    }

    let tbody = document.createElement('tbody');
    booksTable.appendChild(tbody);

    books.forEach((book, index) => {
        let row = tbody.insertRow();
        let detailCell = row.insertCell(0);
        let actionCell = row.insertCell(1);

        detailCell.innerHTML = `${index + 1}, ${book.title}, ${book.author}, {Due date: ${book.dueDate}}`;

        // action cell
        let space = document.createTextNode('\u00A0\u00A0\u00A0');
        actionCell.appendChild(space);

        let returnLink = document.createElement('a');
        returnLink.href = 'javascript:void(0)';
        returnLink.textContent = 'Return this book';
        returnLink.onclick = function () {
            returnBook(book.registerId, book.title, book.author);
        }

        actionCell.appendChild(returnLink);
    });

}

function returnBook(registerId, bookTitle, bookAuthor) {
    console.log('Inside returnBook() method');
    const payLoad = {
        returnDate: new Date().toISOString().split("T")[0] // today's date in ISO format
    };
    console.log('returnBook payload: ', payLoad);

    fetch(`http://localhost:8080/api/registers/updateRegister/${registerId}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payLoad)
    })
        .then(response => {
            if (!response.ok) {
                console.error('Failed to return a book');
                alert('Failed to return a book');
                throw new Error('Failed to return a book');
            }
        })
        .then(async () => {
            const response = await fetch(`http://localhost:8080/api/registers/${registerId}`);
            if (!response.ok) {
                console.error('Failed to fetch updated register details');
                alert('Failed to fetch updated register details');
                throw new Error('Failed to fetch updated register details');
            }
            return response.json();
        })
        .then(data => {
            increaseBookCopies(data.bookId);
            return data;
        })
        .then(data => {
            let alertMessage = `Book "${bookTitle}, ${bookAuthor}" successfully returned`
            if (data.overdueFine != null) {
                let formattedFine = parseFloat(data.overdueFine).toFixed(2);
                alertMessage += `\n\nOverdue Fine: ${formattedFine} USD`;
                alert(alertMessage);
                fetchCheckoutBooks(memberId);
            }
        })
        .catch(erro => {
            console.error('An error occured: ', error);
            alert('An error occured');
        })
}

function increaseBookCopies(bookId) {

    fetch(`http://localhost:8080/api/books/${bookId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch book details');
            }
            return response.json();
        })
        .then(data => {
            const updatedCopies = data.noOfAvailableCopies + 1;
            console.log('Number of available copies before returning: ', data.noOfAvailableCopies);
            return fetch(`http://localhost:8080/api/books/updateBook/${bookId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ noOfAvailableCopies: updatedCopies })
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update book copies');

            }
            return response.json();
        })
        .catch(error => {
            console.error('An error occured: ', error);
            alert('An error occured');
        })
}

function terminateMembership() {
    updateMembershipStatus(false);
}
function activateMembership() {
    updateMembershipStatus(true);
}
function updateMembershipStatus(isActive) {
    // prepare payload
    const today = new Date().toISOString().split('T')[0];
    const payload = isActive ? { membershipEnded: "", isActive: true } : { membershipEnded: today, isActive: false };
    console.log('update membershipstatus payload: ', payload);

    //use fetch to perform update request
    fetch(`http://localhost:8080/api/members/updateMember/${memberId}`, {
        method: "PATCH",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update membership status');
            }
            return response.json();
        })
        .then(data => {
            console.log('membership status updated succesfully: ', data);
            location.reload();
        })
        .catch(error => {
            console.error('Error occured while trying to update membership status: ');
            alert('Error occured while trying to update membership status:')
        })
}