import React from 'react';
import { Navigate } from 'react-router-dom';

function PrivateRoute({ children }) {
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';

  if (isAuthenticated) {
    return children; 
  } else {
    return <Navigate to="/" replace />; 
  }
}

export default PrivateRoute;