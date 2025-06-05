function showSection(section) {
    document.getElementById('book-section').style.display = (section === 'book') ? 'block' : 'none';
    document.getElementById('tickets-section').style.display = (section === 'tickets') ? 'block' : 'none';
    let btns = document.querySelectorAll('.submenu-btn');
    btns.forEach(btn => btn.classList.remove('active'));
    btns[section === 'book' ? 0 : 1].classList.add('active');
}

function filterTickets() {
    let searchValue = document.getElementById('ticketSearch').value.toLowerCase();
    let filter = document.getElementById('ticketFilter').value;
    let now = new Date();
    let tickets = document.querySelectorAll('.ticket-card');
    tickets.forEach(ticket => {
        let departure = new Date(ticket.getAttribute('data-departure'));
        let destination = ticket.getAttribute('data-destination').toLowerCase();
        let flightNumber = ticket.getAttribute('data-flightnumber').toLowerCase();

        let matchesSearch = destination.includes(searchValue) || flightNumber.includes(searchValue);
        let isUpcoming = departure > now;
        let isPast = departure <= now;
        let matchesFilter = (filter === 'all') || (filter === 'upcoming' && isUpcoming) || (filter === 'past' && isPast);

        ticket.style.display = (matchesSearch && matchesFilter) ? '' : 'none';
    });
}