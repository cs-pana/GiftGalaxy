
import React, { useState, useEffect } from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import './App.css';
import LoginSignUp from './Components/LoginSignup/LoginSignUp';
import Dashboard from './Components/Dashboard/Dashboard';
import PrivateRoute from './Components/PrivateRoute';
import ProfileUpdate from './Components/ProfileUpdate/ProfileUpdate';
import ProfileInfoUpd from './Components/ProfileUpdate/ProfileInfoUpd';
import GiftSuggestion from './Components/GiftSuggestion/GiftSuggestion';
import Notifications from './Components/Notifications/Notifications';
import GiftSuggestionsResult from './Components/GiftSuggestion/GiftSuggestionResult';
import UserGiftSuggestion from './Components/GiftSuggestion/UserGiftSuggestion';
import Wishlist from './Components/Wishlist/Wishlist';
import { clearTokenIfExpired } from './tokenUtils';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('jwtToken'));
  const location = useLocation(); // Hook to get the current route

   //useful to avoid app considering old saved tokens and to use the app more smoothly (still not perfect)
    // Clear tokens only on first app load in the first tab, not in subsequent tabs or refresh
    useEffect(() => {
      const isFirstLoadInTab = !sessionStorage.getItem('tabLoaded'); // Check if the app has loaded in this tab
      const isAppAlreadyRunning = !!localStorage.getItem('appRunning'); // Check if the app is already running in another tab
      const isAppReloaded = sessionStorage.getItem('appReloaded'); // Check if this is a refresh/reload
  
      if (isFirstLoadInTab && !isAppAlreadyRunning && !isAppReloaded) {
        console.log('First tab load: Clearing tokens and marking app as running');
        // Clear tokens only in the first tab load (no refresh)
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('firebaseToken');
        sessionStorage.setItem('tabLoaded', 'true'); // Mark this tab as loaded
        localStorage.setItem('appRunning', 'true'); // Mark the app as running in a tab
        setIsLoggedIn(false);
      } else if (isAppReloaded) {
        console.log('App was reloaded, not clearing tokens');
        sessionStorage.setItem('tabLoaded', 'true'); // Mark this tab as loaded after reload
      } else {
        console.log('App is already running in another tab, no token clearing');
        sessionStorage.setItem('tabLoaded', 'true'); // Mark the tab as loaded
      }
  
      // Set a flag to indicate that the app has been reloaded (for refresh)
      sessionStorage.setItem('appReloaded', 'true');
  
      // Clean up the localStorage flag when the tab is closed
      window.addEventListener('beforeunload', () => {
        sessionStorage.removeItem('tabLoaded'); // Clear session storage for the tab
        const otherTabsStillOpen = sessionStorage.getItem('tabLoaded');
        if (!otherTabsStillOpen) {
          localStorage.removeItem('appRunning'); // Remove the flag if all tabs are closed
        }
      });
    }, []); // This effect runs only once, when the app initializes in each tab

  // Token expiration check
  useEffect(() => {
    clearTokenIfExpired(setIsLoggedIn); // Call the utility function
  }, [location]); // This effect runs whenever the route (location) changes


  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  return (
    <Routes>
      <Route 
        path="/" 
        element={
          isLoggedIn ? <Navigate to="/dashboard" /> : <LoginSignUp onLogin={handleLogin} />
        } 
      />

    <Route
        path="/wishlist/:userId"
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <Wishlist setIsLoggedIn/>
          </PrivateRoute>
        }
      
      />

      <Route
        path="/notifications/:userId"
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <Notifications setIsLoggedIn/>
          </PrivateRoute>
          
         }
      />

    {/*<Route path="*" element={<NotFound />} />*/}

      {/* Dashboard route protected by PrivateRoute */}
      <Route 
        path="/dashboard" 
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <Dashboard setIsLoggedIn={setIsLoggedIn} />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/profile-update"
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <ProfileUpdate />
          </PrivateRoute>
        }
        />
        <Route path="/profile-info-upd" element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
          <ProfileInfoUpd />
        </PrivateRoute>
        } 
        />
        <Route 
        path="/gift-suggestion" 
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <GiftSuggestion />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/gift-suggestion-result" 
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <GiftSuggestionsResult />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/user-gift-suggestion" 
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <UserGiftSuggestion />
          </PrivateRoute>
        } 
      />
    </Routes>
  );
};

export default App;
