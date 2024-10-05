import { jwtDecode } from "jwt-decode";

export const clearTokenIfExpired = (setIsLoggedIn) => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        const currentTime = Date.now() / 1000; // Current time in seconds
  
        if (decodedToken.exp < currentTime) {
          // Token is expired, remove it
          localStorage.removeItem('jwtToken');
          setIsLoggedIn(false); // Set the logged-in state to false
          console.log('JWT token is expired and has been removed');
        }
      } catch (error) {
        console.error('Failed to decode token:', error);
        localStorage.removeItem('jwtToken');
        setIsLoggedIn(false); // In case of any token error, log out the user
      }
    }
  };