import axios from 'axios';


const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',  // Backend URL default to authentication service
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

// switch to profileservice
export const switchToProfileService = () => {
    axiosInstance.defaults.baseURL = 'http://localhost:8081'; 
};

// switch back to the authenticationservice
export const switchToAuthService = () => {
    axiosInstance.defaults.baseURL = 'http://localhost:8080';
};

export default axiosInstance;
