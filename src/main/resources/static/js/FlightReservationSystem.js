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