import React, { useEffect, useState } from 'react';
import './Dashboard.css'; 
import logo from '../Assets/logo3.png'
import { useNavigate } from 'react-router-dom';
import axiosInstance, {switchToProfileService} from '../axiosInstance';

const Dashboard = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();
  const[userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // UseEffect to check authentication on component mount
  useEffect(() => {
  const fetchUserData = async () => {
    try {

      const jwtToken = localStorage.getItem('jwtToken');
      if (!jwtToken) {
        setError('Invalid token.');
        setLoading(false);
        return;
      }

      // Switch to profile service to fetch user data
      console.log("Switching to profile service...");
      switchToProfileService();

      const userResponse = await axiosInstance.get(`/profiles/me`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      setUserData(userResponse.data);
      setLoading(false);
      console.log("User data fetched:", userResponse.data);
    } catch (err) {
      console.error('Error fetching user data:', err);
      setError('Error fetching user data.');
      setLoading(false);
    }
  };
  fetchUserData();
}, []);

  const handleLogout = () => {
    // Clear the JWT token from localStorage
    localStorage.removeItem('jwtToken');
    
    // Update the login state to false
    setIsLoggedIn(false);

    // Redirect to the login page
    navigate('/');
  };

  const goToProfileUpdate = () => {
    navigate('/profile-update'); // Naviga alla pagina di aggiornamento profilo
  };

  const goToGiftSuggestion = () => {
    navigate('/gift-suggestion'); // Naviga alla pagina di GiftSuggestion
  };

  const handleGoBack = () => {
    if (window.history.length > 1) {
      navigate(-1); // if there are previous pages, go back
    } else {
      navigate('/'); 
    }
  };

  const goToWishlist = () => {
    //navigate('/wishlist');
    navigate(`/wishlist/${userData.userId}`);
  }

  if (loading) {
    return (
      <div className="loading-container">
        <p>Loading data...</p>
      </div>
    );
  }
  
  if (error) {
    return (
      <div className="error-container">
        <p>{error}</p>
      </div>
    );
  }


  return (
    <div className="dashboard">
      <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
      </div>
      <div className="main-container">
        <div className="intro-text">
          <h2>Welcome to Your Dashboard,  {userData.username} {userData.surname}</h2>
          <p>Here you can manage your profile, view and add important events, and manage your wishlist. Choose an option below to get started!</p>
        </div>
        <div className="main-button-container">
          <button className="main-button" onClick={goToGiftSuggestion}>Gift suggestion</button>
          <button className="updateButton" onClick={goToProfileUpdate}>Update profile and events</button>
          <button className="wishButton" onClick={goToWishlist}>Your wishlist</button>        
          {/*<button className="back-button" onClick={handleGoBack}>Back</button> {/* Back button */}
        <div className="logout-button-container">
        <button className="logout-button" onClick={handleLogout}>Logout</button> {/* Logout button */}
        </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
