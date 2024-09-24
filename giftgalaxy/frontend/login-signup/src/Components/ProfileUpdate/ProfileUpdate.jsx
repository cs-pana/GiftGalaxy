import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance, { switchToProfileService, switchToNotifService } from '../axiosInstance'; 
import './ProfileUpdate.css'; 
import logo from '../Assets/logo3.png'
//import profilePic from '../Assets/profile-image.jpg';

const ProfileUpdate = () => {
  const[userData, setUserData] = useState(null);

  const [events, setEvents] = useState([]);

  const [isAddingEvent, setIsAddingEvent] = useState(false);
  const [newEventName, setNewEventName] = useState('');
  const [newEventDate, setNewEventDate] = useState('');
  const [editingEventId, setEditingEventId] = useState(null);
  const [editingEventName, setEditingEventName] = useState('');
  const [editingEventDate, setEditingEventDate] = useState('');
  const [notificationsEnabled, setNotificationsEnabled] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

useEffect(() => {
  const fetchData = async () => {
      try {
          switchToProfileService();
          const token = localStorage.getItem('jwtToken');
          if (!token) {
              throw new Error('No token found');
          }

          //fetch user data
          const userResponse = await axiosInstance.get(`/profiles/me`, {
              headers: {
                  Authorization: `Bearer ${token}`
              }
          });
          setUserData(userResponse.data);
          console.log(userData);
          
          //fetch user's events
          const eventsResponse = await axiosInstance.get('/profiles/me/events', {
              headers: {
                Authorization: `Bearer ${token}`
              }
          });
          setEvents(eventsResponse.data);

          setLoading(false);
      } catch (error) {
          console.error('Error fetching data:', error);
          setError('Failed to fetch user data.');
          setLoading(false);
      }
  };

  fetchData();
}, []);

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


  // navigate to update event page
  const handleAddEventClick = () => {
    setIsAddingEvent(true);
  };

    // function to Add a new event 
    const handleAddEvent = async (e) => {
      switchToProfileService();

        e.preventDefault();
        if (newEventName && newEventDate) {
          /*const newEvent = {
            id: events.length + 1, // create a fake id 
            name: newEventName,
            date: newEventDate
          };*/
          const token = localStorage.getItem('jwtToken');
          try {
            const newEvent = {
              name: newEventName,
              eventDate: newEventDate,
              notify: notificationsEnabled
            };
            const response = await axiosInstance.post('/profiles/me/events', newEvent, {
              headers: {
                Authorization: `Bearer ${token}`
              }
            });

          setEvents([...events, response.data]);
          setNewEventName('');
          setNewEventDate('');
          setNotificationsEnabled(false);
          setIsAddingEvent(false); // Close the module of add event
        } catch (error) {
          console.error('Error adding event: ', error);
          setError('Failed to add event.');
        }
      }
      };
    
      // Function to modify the event
      const handleEditEventClick = (event) => {
        setEditingEventId(event.id);
        setEditingEventName(event.name);
        setEditingEventDate(event.eventDate);
        setNotificationsEnabled(event.notify);
      };
    
      // Function to save the update of the event
      const handleSaveEventChanges = async (e) => {
        switchToProfileService();

        e.preventDefault();
        if (editingEventName && editingEventDate) {
          const token = localStorage.getItem('jwtToken');
            
          try {
              const updatedEvent = {
                  name: editingEventName,
                  eventDate: editingEventDate, 
                  notify: notificationsEnabled 
                };
              console.log('Updated event being sent: (from FRONTEND) ', updatedEvent);
              const response = await axiosInstance.put(`/profiles/me/events/${editingEventId}`, updatedEvent, {
                  headers: {
                      Authorization: `Bearer ${token}`
                  }
              });
                
              setEvents(events.map(event => 
                  event.id === editingEventId ? response.data : event
              ));
              setEditingEventId(null); // Close modify event module
        } catch (error) {
          console.error('Error updating event:', error);
          setError('Failed to update event.');
        }
      }
    };

      const handleDeleteEvent = async (id) => {
        //setEvents(events.filter(event => event.id !== id));
        switchToProfileService();
        const token = localStorage.getItem('jwtToken');
        try {
          await axiosInstance.delete(`/profiles/me/events/${id}`, {
            headers: {
              Authorization: `Bearer ${token}`
            }
          });
          setEvents(events.filter(event => event.id !== id));
        } catch (error) {
          console.error('Error deleting event:', error);
          setError('Failed to delete event.');
        }
      };

      const handleEditProfileClick = () => {
        navigate('/profile-info-upd'); 
    };

      const handleBackClick = () => {
        navigate('/dashboard'); 
    };

    // go to the notifications page for current user
    const handleNotificationsClick = () => {
      console.log(userData);
      if (userData && userData.userId) {
        console.log("Navigating to notifications for user:", userData.userId);
        navigate(`/notifications/${userData.userId}`);
      } else {
        console.error("User data is not loaded yet or user ID is missing");
      }
    };


  return (
    
    <div className="page" >
     <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
       </div>
    <div className="profile-update-container">

      <h2 className="title">My profile page</h2>

      <div className="profile-info-container">
                   {/* <img src={profilePic} alt="Foto Profilo" className="profile-image" /> */}
                    <div className="profile-details">
                        <p><strong>Name:</strong> {userData.username}</p>
                        <p><strong>Surname:</strong> {userData.surname}</p>
                        <p><strong>Email:</strong> {userData.email}</p>
                    </div>
                </div>

      <button className="edit-button" onClick={handleEditProfileClick}>
        Update profile
      </button>

      <h2 className="eventTitle">Events</h2>
      <div className="button-container">
        <button className="add-event-button" onClick={handleAddEventClick}>
        Add new event
        </button>
        <button className="notifications-button" onClick={handleNotificationsClick}>
         Notifications <span className="notification-badge">!</span>
        </button>
      </div>


      {isAddingEvent && (
        <form onSubmit={handleAddEvent}>
          <div>
            <label>Event name:</label>
            <input
              type="text"
              value={newEventName}
              onChange={(e) => setNewEventName(e.target.value)}
              required
            />
          </div>
          <div>
            <label>Date:</label>
            <input
              type="date"
              value={newEventDate}
              onChange={(e) => setNewEventDate(e.target.value)}
              required
            />
          </div>
          <div>
            <label>Notifications:</label>
              <input
                type="checkbox"
                checked={notificationsEnabled}
                onChange={(e) => setNotificationsEnabled(e.target.checked)}
              />
          </div>
          <button type="submit">Add event</button>
          <button type="button" onClick={() => setIsAddingEvent(false)}>
            Cancel
          </button>
        </form>
      )}

      <div className="events-list">
        {events.length === 0 ? (
          <p>No event found.</p>
        ) : (
          <ul>
            {events.map((event) => (
              <li key={event.id}>
                {editingEventId === event.id ? (
                  <form onSubmit={handleSaveEventChanges}>
                    <div>
                      <label>Event name:</label>
                      <input
                        type="text"
                        value={editingEventName}
                        onChange={(e) => setEditingEventName(e.target.value)}
                        required
                      />
                    </div>
                    <div>
                      <label>Date:</label>
                      <input
                        type="date"
                        value={editingEventDate}
                        onChange={(e) => setEditingEventDate(e.target.value)}
                        required
                      />
                    </div>
                    <div>
                        <label>Notifications:</label>
                        <input className="notification"
                          type="checkbox"
                          checked={notificationsEnabled}
                          onChange={(e) => setNotificationsEnabled(e.target.checked)}
                          />
                    </div>
                    <div className="modify-button-cont">
                    <button type="submit">Save changes</button>
                    <button type="button" onClick={() => setEditingEventId(null)}>
                      Exit
                    </button>
                    </div>
                  </form>
                ) : (
                  <div>
                    <strong>Event Name:</strong> {event.name} <br />
                    <strong>Date:</strong> {event.eventDate} <br />
                    <div className="modify-button-cont">
                    <button className="modify-button" onClick={() => handleEditEventClick(event)}>
                      Modify Event
                    </button>
                    <button className="delete-button" onClick={() => handleDeleteEvent(event.id)}>
                      Delete Event
                    </button>
                    </div>
                  </div>
                )}
              </li>
            ))}
          </ul>
        )}
        <button className="back-button" onClick={handleBackClick}>
                    Back to Dashboard
                    </button>
      </div>
    </div>
    </div>
    
  );
};

export default ProfileUpdate;