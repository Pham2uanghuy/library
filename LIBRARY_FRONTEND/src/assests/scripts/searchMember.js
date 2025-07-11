document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');

    form.addEventListener('submit', function (ev) {
        ev.preventDefault();

        // retrieve input values from the form
        const barcodeNumber = document.getElementById('barCode').value;
        const cardNumber = document.getElementById('cardNumber').value;
        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;

        // contruct API url 
        const url = `http://localhost:8080/api/members/search?barcodeNumber=${encodeURIComponent(barcodeNumber)}&cardNumber=${encodeURIComponent(cardNumber)}&firstName=${encodeURIComponent(firstName)}&lastName=${encodeURIComponent(lastName)}`;

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

    let html = '<table><tr><th>Card #</th><th>First Name</th><th>Last Name</th><th>Date Of Birth</th></tr>';

    data.forEach(member => {
        html += ` <tr>
                    <td>${member.id}</td>
                    <td>${member.firstName}</td>
                    <td>${member.lastName}</td>
                    <td>${member.dateOfBirth}</td>
                    <td><a href="member-info.html?id=${member.id}">View Details</a></td>
                    </tr>
                    `

    });

    html += '</table>';

    const resultsContainer = document.getElementById('resultsContainer');

    resultsContainer.innerHTML = html;

}