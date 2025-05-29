document.getElementById('paymentForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const data = new URLSearchParams(formData);
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    try {
        const response = await fetch('/api/flights/simulate-payment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken,
            },
            body: data
        });

        console.log("Status:", response.status);

        if (!response.ok) {
            const text = await response.text();
            console.error("Server error response:", text);
            throw new Error("Server Error");
        }

        const result = await response.json();
        console.log("Răspuns JSON primit:", result);

        const modal = document.getElementById('paymentModal');
        document.getElementById('paymentStatusTitle').textContent =
            result.paymentSuccess ? 'Payment Successful ✅' : 'Payment Failed ❌';

        document.getElementById('paymentMessage').innerHTML = result.paymentSuccess
            ? `Your payment of <strong>${result.totalPrice}</strong> RON for flight <strong>${result.flightNumber}</strong> has been confirmed.`
            : `Please check your card details and try again.`;

        document.getElementById('downloadSection').style.display =
            result.paymentSuccess ? 'block' : 'none';

        modal.style.display = 'flex';
        clearInterval(countdown);

    } catch (error) {
        alert("An error in processing the payment has occurred.");
        console.error(error);
    }
});


function downloadTicket() {
    const firstName = document.querySelector('input[name="cardholderName"]').value;
    const lastName = document.querySelector('input[name="cardholderLastName"]').value;
    const flightNumber = document.querySelector('input[name="flightNumber"]').value;
    const totalPrice = document.querySelector('input[name="totalPrice"]').value;
    const departure = document.getElementById('departureCity').textContent;
    const departureTime = document.getElementById('departureTime').textContent;
    const destination = document.getElementById('destinationCity').textContent;
    const arrivalTime = document.getElementById('arrivalTime').textContent;
    const assignedSeat = document.getElementById("assignedSeat").textContent;

    const url = `/api/flights/download-ticket` +
        `?flightNumber=${encodeURIComponent(flightNumber)}` +
        `&firstName=${encodeURIComponent(firstName)}` +
        `&lastName=${encodeURIComponent(lastName)}` +
        `&departure=${encodeURIComponent(departure)}` +
        `&departureTime=${encodeURIComponent(departureTime)}` +
        `&destination=${encodeURIComponent(destination)}` +
        `&arrivalTime=${encodeURIComponent(arrivalTime)}` +
        `&assignedSeat=${encodeURIComponent(assignedSeat)}` +
        `&totalPrice=${encodeURIComponent(totalPrice)}`;

    window.open(url, '_blank');
}

let timeLeft = 15 * 60;
const timerElement = document.getElementById("timer");

const countdown = setInterval(() => {
    const minutes = Math.floor(timeLeft / 60);
    const seconds = timeLeft % 60;
    timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

    if (timeLeft <= 0) {
        clearInterval(countdown);
        alert("Reservation expired. Please select seats again.");
        window.location.href = "/api/flights/page"; // sau alt redirect
    }

    timeLeft--;
}, 1000);

function closeModalAndRedirect() {
    document.getElementById('paymentModal').style.display = 'none';
    window.location.href = '/';
}