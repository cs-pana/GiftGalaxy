import React, { useEffect, useState } from 'react';
import axiosInstance, { switchToNotifService } from '../axiosInstance';
import { useParams, useNavigate } from 'react-router-dom';
import './Notifications.css';
import logo from '../Assets/logo3.png'

const Notifications = () => {
    const { userId } = useParams();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        switchToNotifService();
        const fetchNotifications = async () => {
            console.log("current URL: " + axiosInstance.baseURL);
            try {
                
                const token = localStorage.getItem('jwtToken');
                if (!token) {
                    throw new Error('No token found');
                }
                console.log(token);
                const response = await axiosInstance.get(`/notifications/${userId}`, {
                    headers: {
                        Authorization: `Bearer `
                    }
                });
                setNotifications(response.data);
                console.log("Notifications fetched: ", response.data);
            } catch (error) {
                console.log("Switched to: " + axiosInstance.baseURL);
                console.error('Error fetching notifications:', error);
                setError('Failed to fetch notifications.');
            } finally {
                setLoading(false);
            }
        };

        fetchNotifications();
    }, [userId]);

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
       <div className="notifpage">
        <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
       </div>
        <div className='notifications-container'>
        <button className="back-button" onClick={() => navigate('/profile-update')}>
                Go back
            </button>
            <h2 className='title'>Your Notifications</h2>
            <ul className='notifications-list'>
                {notifications.length === 0 ? (
                    <li>No notifications available.</li>
                ) : (
                    notifications.map(notification => (
                        <li key={notification.id}>
                            <strong>Message:</strong> {notification.message} <br />
                            <em>Event Date: {new Date(notification.notificationDate).toLocaleString()}</em>
                        </li>
                    ))
                )}
            </ul>
        </div>
        </div>
    );
};

export default Notifications;
