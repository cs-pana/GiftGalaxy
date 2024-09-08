import axios from 'axios';


const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',  // Backend URL
});

// Request interceptor to include the token in each request
axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem('jwtToken');  // Store the token in localStorage
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default axiosInstance;
