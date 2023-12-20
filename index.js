const express = require('express');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');

const app = express();
const secretKey = 'your-secret-key'; // Ganti dengan kunci rahasia yang aman

app.use(bodyParser.json());

// Endpoint untuk login
app.post('/api/login', (req, res) => {
    // Contoh data pengguna (biasanya data ini akan disimpan di database)
    const users = [
        { id: 1, username: 'user1', password: 'pass1' },
        { id: 2, username: 'user2', password: 'pass2' },
    ];

    // Verifikasi data pengguna
    const { username, password } = req.body;
    const user = users.find(u => u.username === username && u.password === password);

    if (user) {
        // Buat token JWT
        const token = jwt.sign({ userId: user.id, username: user.username }, secretKey, { expiresIn: '1h' });

        res.json({ success: true, message: 'Login successful', token });
    } else {
        res.status(401).json({ success: false, message: 'Invalid username or password' });
    }
});

// Middleware untuk verifikasi token sebelum mengakses route berikutnya
function verifyToken(req, res, next) {
    const token = req.headers['authorization'];

    if (!token) {
        return res.status(403).json({ success: false, message: 'Token not provided' });
    }

    jwt.verify(token, secretKey, (err, decoded) => {
        if (err) {
            return res.status(401).json({ success: false, message: 'Failed to authenticate token' });
        }

        req.decoded = decoded;
        next();
    });
}

// Contoh route yang memerlukan otentikasi
app.get('/api/dashboard', verifyToken, (req, res) => {
    res.json({ success: true, message: 'Welcome to the dashboard', user: req.decoded });
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
