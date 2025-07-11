document.addEventListener('DOMContentLoaded', function () {
    // pre-fill the memberBarcode
    const memberBarcodeParam = getQueryParam('memberBarcode');
    if (memberBarcodeParam) {
        document.getElementById('memberBarcode').value = memberBarcodeParam;
    }

    //prefill book barcode
    const bookBarcodeParam = getQueryParam('bookBarcode');
    if (bookBarcodeParam) {
        document.getElementById('bookBarcode').value = bookBarcodeParam;
    }
    const form = document.querySelector('form');

    form.addEventListener('submit', function (ev) {


        ev.preventDefault();


        const memberBarcode = document.getElementById('memberBarcode').value;
        const bookBarcode = document.getElementById('bookBarcode').value;

        console.log('memberBarcode: ', memberBarcode);
        console.log('bookBarcode', bookBarcode);

        Promise.all(
            [fetchMemberIdByBarcode(memberBarcode),
            fetchBookIdByBarcode(bookBarcode)]
        )
            .then(([memberId, bookId]) => {
                console.log('memberId: ', memberId);
                console.log('bookId: ', bookId);
                if (memberId && bookId) {
                    createCheckoutRegister(memberId, bookId);
                }
                else {
                    alert('Invalid member or book barcode');
                }
            })
            .catch(error => {
                console.error('Unexpected error: ', error);
                alert('Unexpected error.');
            })
    })
})
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}
function fetchMemberIdByBarcode(barcode) {

    return fetch(`http://localhost:8080/api/members/search?barcodeNumber=${barcode}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error fetching member by barcode');
            }
            return response.json();
        })
        .then(data => {
            console.log('Member data: ', data);
            if (data.length > 0) {
                const exactMatch = data.find(member => member.barcodeNumber === barcode);
                return exactMatch ? exactMatch.id : null;
            }
            else return null;
        })
}

function fetchBookIdByBarcode(barcode) {
    return fetch(`http://localhost:8080/api/books/search?barcodeNumber=${barcode}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error fetching book by barcode');
            }
            return response.json();
        })
        .then(data => {
            console.log('Book data: ', data);
            if (data.length > 0) {
                const exactMatch = data.find(book => book.barcodeNumber === barcode);
                return exactMatch ? exactMatch.id : null;
            }
            else return null;
        })
}

function createCheckoutRegister(memberId, bookId) {

    // first, check if the book is available
    fetch(`http://localhost:8080/api/books/${bookId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error fetching book for given barcode');
            }
            return response.json()
        })
        .then(book => {
            if (!book || book.noOfAvailableCopies <= 0) {
                alert('Checkout failed! no copies available or book not found');
                throw new Error('Checkout failed: no copies or book not found')
            }

            const payLoad = {
                "memberId": memberId,
                "bookId": bookId,
                "checkoutDate": new Date().toISOString().split('T')[0]
            }
            fetch(`http://localhost:8080/api/registers/createRegister`, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payLoad)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed while creating register')
                    }
                    return response.json();
                })
                .then(register => {
                    //decrease number of available copies
                    return decreaseCopies(bookId);
                })
                .then(() => {
                    alert('Book checked-out susscessfully and copies are updated')
                    location.reload();
                })
                .catch(error => {
                    console.error('Unexpected error during checkout: ', error);
                    alert('Unexpected error');
                })
        })

}

function decreaseCopies(id) {

    return fetch(`http://localhost:8080/api/books/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed while fetching book to decrease');
            }
            return response.json();
        })
        .then(data => {
            const updatedCopies = data.noOfAvailableCopies - 1;
            return fetch(`http://localhost:8080/api/books/updateBook/${id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ noOfAvailableCopies: updatedCopies })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to update new copies');
                    }
                    return response.json();
                })
                .catch(error => {
                    if (error.message != 'Checkout failed! no copies available or book not found') {
                        console.error('Unexpected error while updating book copies: ', error);
                        alert('Unexpected error while updating book copies');
                    }
                })
        })

}
