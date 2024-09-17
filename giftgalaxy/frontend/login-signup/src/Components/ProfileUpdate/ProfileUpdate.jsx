import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance, { switchToProfileService } from '../axiosInstance'; 
import './ProfileUpdate.css'; 
import logo from '../Assets/logo3.png'
import profilePic from '../Assets/profile-image.jpg';

const ProfileUpdate = () => {
    //states for the user data and events
  /*const [userData] = useState({
    username: 'Mario' ,
    surname: 'Rossi',
    email: 'mario.rossi@example.com'
  }); */
  const[userData, setUserData] = useState(null);
  const [events, setEvents] = useState([
    {id:1, name: 'Compleanno di Maria', date: '2024-09-20'},
    { id:2, name: 'Anniversario di Paolo', date: '2024-12-15'}

  ]);         

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

 /* THIS PART IS USEFUL FOR THE RECOVERY OF THE REAL USER DATA
 
 useEffect(() => {
    // Simula una richiesta API per ottenere i dati dell'utente e gli eventi
    const fetchData = async () => {
      try {
        const token = localStorage.getItem('jwtToken'); // Verifica il token salvato

        // Ottenere i dati dell'utente
        const userResponse = await axiosInstance.get('/user/profile', {
          headers: {
            Authorization: `Bearer ${token}` // Usa il token per autenticare la richiesta
          }
        });
        setUserData(userResponse.data);

        // Ottenere gli eventi dell'utente
        const eventsResponse = await axiosInstance.get('/user/events', {
          headers: {
            Authorization: `Bearer ${token}` // Usa il token per autenticare la richiesta
          }
        });
        setEvents(eventsResponse.data);

        setLoading(false);
      } catch (error) {
        console.error('Errore nel recupero dei dati:', error);
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <p>Caricamento dati...</p>;
  }

  if (!userData) {
    return <p>Errore nel recupero dei dati dell'utente.</p>;
  }
*/

useEffect(() => {
  const fetchData = async () => {
      try {
          switchToProfileService();
        //const userId = 1; // replace with the actual user ID, if needed
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

          //  fetching events for this user
          // const eventsResponse = await axios.get(`/user/events/${userId}`, {
          //     headers: {
          //         Authorization: `Bearer ${token}`
          //     }
          // });
          // setEvents(eventsResponse.data);

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
  return <p>Loading data...</p>;
}

if (error) {
  return <p>{error}</p>;
}

  // navigate to update event page
  const handleAddEventClick = () => {
    setIsAddingEvent(true);
  };

    // function to Add a new event 
    const handleAddEvent = (e) => {
        e.preventDefault();
        if (newEventName && newEventDate) {
          const newEvent = {
            id: events.length + 1, // create a fake id 
            name: newEventName,
            date: newEventDate
          };
          setEvents([...events, newEvent]);
          setNewEventName('');
          setNewEventDate('');
          setIsAddingEvent(false); // Close the module of add event
        }
      };
    
      // Function to modify the event
      const handleEditEventClick = (event) => {
        setEditingEventId(event.id);
        setEditingEventName(event.name);
        setEditingEventDate(event.date);
        setNotificationsEnabled(event.notificationsEnabled);
      };
    
      // Function to save the update of the event
      const handleSaveEventChanges = (e) => {
        e.preventDefault();
        if (editingEventName && editingEventDate) {
          const updatedEvents = events.map((event) =>
            event.id === editingEventId
              ? { ...event, name: editingEventName, date: editingEventDate }
              : event
          );
          setEvents(updatedEvents);
          setEditingEventId(null); // Close modify event module
        }
      };

      const handleDeleteEvent = (id) => {
        setEvents(events.filter(event => event.id !== id));
      };

      const handleEditProfileClick = () => {
        navigate('/profile-info-upd'); 
    };

      const handleBackClick = () => {
        navigate('/dashboard'); 
    };


  return (
    
    <div className="page" >
     <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
       </div>
    <div className="profile-update-container">

      <h2 className="title">My profile page</h2>

      <div className="profile-info-container">
                    <img src={profilePic} alt="Foto Profilo" className="profile-image" />
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
      <button className="add-event-button" onClick={handleAddEventClick}>
        Add new event
      </button>

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
                    <strong>Date:</strong> {event.date} <br />
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