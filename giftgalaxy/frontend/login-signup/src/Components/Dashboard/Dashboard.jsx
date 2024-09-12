import React, { useEffect } from 'react';
import './Dashboard.css'; 
import logo from '../Assets/logo3.png'
import { useNavigate } from 'react-router-dom';

const Dashboard = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();

  // UseEffect to check authentication on component mount
  useEffect(() => {
    const token = localStorage.getItem('jwtToken'); // Get token from localStorage
    if (!token) {
      // If no token is found, redirect to the login page
      navigate('/');
    }
  }, [navigate]); // Adding navigate as a dependency ensures it runs once on mount

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



  const handleGoBack = () => {
    if (window.history.length > 1) {
      navigate(-1); // if there are previous pages, go back
    } else {
      navigate('/'); 
    }
  };

  return (
    <div className="dashboard">
      <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
      </div>
      <div className="main-container">
        <div className="intro-text">
          <h2>Welcome to Your Dashboard</h2>
          <p>Here you can manage your profile, view and add important events, and manage your wishlist. Choose an option below to get started!</p>
        </div>
        <div className="main-button-container">
          <button className="main-button">Gift suggestion</button>
          <button className="updateButton" onClick={goToProfileUpdate}>Update profile and events</button>
          <button className="eventButton">Your wishlist</button>
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
