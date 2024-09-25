
import React, { useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import LoginSignUp from './Components/LoginSignup/LoginSignUp';
import Dashboard from './Components/Dashboard/Dashboard';
import PrivateRoute from './Components/PrivateRoute';
import ProfileUpdate from './Components/ProfileUpdate/ProfileUpdate';
import ProfileInfoUpd from './Components/ProfileUpdate/ProfileInfoUpd';
import GiftSuggestion from './Components/GiftSuggestion/GiftSuggestion';
import Notifications from './Components/Notifications/Notifications';
import GiftSuggestionsResult from './Components/GiftSuggestion/GiftSuggestionResult';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('jwtToken'));

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
    </Routes>
  );
};

export default App;
