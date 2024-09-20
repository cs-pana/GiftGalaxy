import React, { useEffect, useState } from 'react';
import axiosInstanceTemp from '../axiosInstanceTemp';
import { useParams } from 'react-router-dom';
import './Notifications.css';
import logo from '../Assets/logo3.png'

const Notifications = () => {
    const { userId } = useParams();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                //const token = localStorage.getItem('jwtToken');
                const response = await axiosInstanceTemp.get(`/notifications/${userId}`);
                setNotifications(response.data);
                console.log("Notifications fetched");
                console.log(notifications);
            } catch (error) {
                console.error('Error fetching notifications:', error);
                setError('Failed to fetch notifications.');
            } finally {
                setLoading(false);
            }
        };

        fetchNotifications();
    }, [userId]);

    if (loading) {
        return <p>Loading notifications...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div>
        <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
       </div>
        <div className='notifications-container'>
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
