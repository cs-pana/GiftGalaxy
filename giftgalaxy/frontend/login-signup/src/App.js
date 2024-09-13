
import React, { useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import LoginSignUp from './Components/LoginSignup/LoginSignUp';
import Dashboard from './Components/Dashboard/Dashboard';
import PrivateRoute from './Components/PrivateRoute';
import ProfileUpdate from './Components/ProfileUpdate/ProfileUpdate';
import ProfileInfoUpd from './Components/ProfileUpdate/ProfileInfoUpd';
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
    </Routes>
  );
};

export default App;
