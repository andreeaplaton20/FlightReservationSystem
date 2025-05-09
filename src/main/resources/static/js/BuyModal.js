function openModal(button) {
    const form = button.closest('form');
    const flightId = form.querySelector('input[name="flightId"]').value;
    const ticketCount = form.querySelector('input[name="ticketCount"]').value;

    if (!ticketCount || isNaN(ticketCount) || ticketCount < 1) {
        alert("Please enter a valid ticket count.");
        return;
    }

    document.getElementById('seatModal').style.display = "block";

    document.getElementById('manualSelectBtn').onclick = function () {
        window.location.href = `/api/flights/select-seats?flightId=${flightId}&count=${ticketCount}`;
    };

    document.getElementById('autoSelectBtn').onclick = function () {
        window.location.href = `/api/flights/buy-ticket-auto?flightId=${flightId}&ticketCount=${ticketCount}`;
    };
}

function closeModal() {
    document.getElementById('seatModal').style.display = "none";
}

window.onclick = function(event) {
    var modal = document.getElementById('seatModal');
    if (event.target === modal) {
        closeModal();
    }
}