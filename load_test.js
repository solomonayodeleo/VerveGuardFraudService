import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 20, // Spin up 20 virtual users
    duration: '10s', // Hammer the API continuously for 10 seconds
};

export default function () {

    const url = 'http://localhost:8081/api/transactions/verify';

    const payload = JSON.stringify({
        cardNumber: "1234567890123456",
        amount: 150.00,
        merchantId: "LOAD_TEST_MERCHANT",
        ipAddress: "192.168.1.1",
        clientId: "load_test_client"
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer your_api_key_here' // Replace with your actual API key
        },
    };

    const res = http.post(url, payload, params);

    if (res.status !== 200 && res.status !== 429) {
        console.log(`UNEXPECTED ERROR: ${res.status} | Body: ${res.body}`);
    }

    // Keep a beautiful tally of our responses at the end
    check(res, {
        'Success (200 OK)': (r) => r.status === 200,
        'Rate Limited (429 Too Many Requests)': (r) => r.status === 429,
    });
}