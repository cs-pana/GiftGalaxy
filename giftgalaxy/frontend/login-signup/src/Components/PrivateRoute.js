import React from 'react';
import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ isLoggedIn, children }) => {
  const token = localStorage.getItem('jwtToken');

  // If either the user is logged in or a valid token exists, render the protected component (children)
  return isLoggedIn || token ? children : <Navigate to="/" />;
};

export default PrivateRoute;
