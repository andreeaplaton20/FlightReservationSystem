const swiper = new Swiper('.slider-wrapper', {
    loop: true,
    grabCursor: true,
    spaceBetween: 30,

    pagination: {
        el: '.swiper-pagination',
        clickable: true,
        dynamicBullets: true
    },

    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },

    breakpoints: {
        0: {
            slidesPerView: 1
        },
        768: {
            slidesPerView: 2
        },
        1024: {
            slidesPerView: 3
        }
    }
});

function openEditModal(id, flightNumber, departureCity, arrivalCity, departureTime, arrivalTime, availableSeats, price,
                       airlineName, airlineCode, airlineCountry,
                       airplaneModel, airplaneCapacity,
                       departureAirportName, departureAirportCode, departureCountry,
                       arrivalAirportName, arrivalAirportCode, arrivalCountry) {

    document.getElementById('editId').value = id;
    document.getElementById('editFlightNumber').value = flightNumber;
    document.getElementById('editDepartureTime').value = departureTime;
    document.getElementById('editArrivalTime').value = arrivalTime;
    document.getElementById('editAvailableSeats').value = availableSeats;
    document.getElementById('editPrice').value = price;

    document.getElementById('editAirlineName').value = airlineName;
    document.getElementById('editAirlineCode').value = airlineCode;
    document.getElementById('editAirlineCountry').value = airlineCountry;

    document.getElementById('editAirplaneModel').value = airplaneModel;
    document.getElementById('editAirplaneCapacity').value = airplaneCapacity;

    document.getElementById('editDepartureAirportName').value = departureAirportName;
    document.getElementById('editDepartureAirportCode').value = departureAirportCode;
    document.getElementById('editDepartureCity').value = departureCity;
    document.getElementById('editDepartureCountry').value = departureCountry;

    document.getElementById('editArrivalAirportName').value = arrivalAirportName;
    document.getElementById('editArrivalAirportCode').value = arrivalAirportCode;
    document.getElementById('editArrivalCity').value = arrivalCity;
    document.getElementById('editArrivalCountry').value = arrivalCountry;

    document.getElementById('editModal').style.display = 'block';
    document.getElementById('overlay').style.display = 'block';
}


function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
    document.getElementById('overlay').style.display = 'none';
}

function openAddModal() {
    document.getElementById('FlightNumber').value = '';
    document.getElementById('DepartureTime').value = '';
    document.getElementById('ArrivalTime').value = '';
    document.getElementById('AvailableSeats').value = '';
    document.getElementById('Price').value = '';

    document.getElementById('AirlineName').value = '';
    document.getElementById('AirlineCode').value = '';
    document.getElementById('AirlineCountry').value = '';

    document.getElementById('AirplaneModel').value = '';
    document.getElementById('AirplaneCapacity').value = '';

    document.getElementById('DepartureAirportName').value = '';
    document.getElementById('DepartureAirportCode').value = '';
    document.getElementById('DepartureCity').value = '';
    document.getElementById('DepartureCountry').value = '';

    document.getElementById('ArrivalAirportName').value = '';
    document.getElementById('ArrivalAirportCode').value = '';
    document.getElementById('ArrivalCity').value = '';
    document.getElementById('ArrivalCountry').value = '';

    document.getElementById('addModal').style.display = 'block';
    document.getElementById('overlay').style.display = 'block';
}


function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
    document.getElementById('overlay').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('destinationSearch');
    const resultsDiv = document.getElementById('searchResults');

    let timeout = null;

    searchInput.addEventListener('input', function() {
        clearTimeout(timeout);
        const query = this.value.trim();
        if (query.length < 2) {
            resultsDiv.style.display = 'none';
            resultsDiv.innerHTML = '';
            return;
        }
        timeout = setTimeout(() => {
            fetch(`/api/flights/search-destinations?query=${encodeURIComponent(query)}`)
                .then(res => res.json())
                .then(data => {
                    resultsDiv.innerHTML = '';
                    if (!Array.isArray(data) || data.length === 0) {
                        resultsDiv.style.display = 'none';
                        return;
                    }

                    const seen = new Set();

                    data.forEach(flight => {
                        const key = `${flight.destinationCity}|${flight.destinationAirport}`;
                        if (!seen.has(key)) {
                            seen.add(key);
                            const item = document.createElement('div');
                            item.className = 'search-result-item';
                            item.textContent = `${flight.destinationCity} (${flight.destinationAirport})`;
                            item.addEventListener('click', () => {
                                window.location.href = '/custom-login?redirect=flight&flightId=' + flight.id;
                            });
                            resultsDiv.appendChild(item);
                        }
                    });

                    if (seen.size === 0) {
                        resultsDiv.style.display = 'none';
                    } else {
                        resultsDiv.style.display = 'block';
                    }
                })
                .catch(err => {
                    resultsDiv.innerHTML = '';
                    resultsDiv.style.display = 'none';
                    console.error('Error fetching flights:', err);
                });
        }, 250);
    });

    document.addEventListener('click', function(e) {
        if (!resultsDiv.contains(e.target) && e.target !== searchInput) {
            resultsDiv.style.display = 'none';
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const contactForm = document.querySelector('.contacts-form');
    if(contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(contactForm);
            const params = new URLSearchParams();
            for (const pair of formData.entries()) {
                params.append(pair[0], pair[1]);
            }

            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            fetch('/api/flights/contact/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    [csrfHeader]: csrfToken
                },
                body: params
            })
                .then(res => res.text())
                .then(resp => {
                    alert(resp);
                    contactForm.reset();
                })
                .catch(err => {
                    alert("There was an error sending your message. Please try again later.");
                });
        });
    }
});