document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    form.addEventListener('submit', function (ev) {
        ev.preventDefault();
        const today = new Date().toISOString().split('T')[0];
        console.log('Today', today);
        const formData = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            dateOfBirth: document.getElementById('dob').value,
            address: {
                streetName: document.getElementById('streetName').value,
                streetNumber: document.getElementById('streetNo').value,
                zipCode: document.getElementById('zipCode').value,
                placeName: document.getElementById('placeName').value,
                country: document.getElementById('country').value,
                additionalInfor: document.getElementById('addInfo').value
            },
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            barcodeNumber: document.getElementById('barCode').value,
            membershipStarted: today,
            isActive: true
        }
        console.log(formData);
        addNewMember(formData);
    });
});

function addNewMember(data) {

    fetch('http://localhost:8080/api/members/addMember', {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                console.error("The response is not ok: ", response);
                throw new Error('Failed to add new member');
            }
            return response.json();
        })
        .then(data => {
            console.log('Member added successfully!');
            alert('New member added successfully!');
        })
        .catch(error => {
            console.error("Error adding new member", error);
            alert("Failed to add new member. Please try again");
        });

}