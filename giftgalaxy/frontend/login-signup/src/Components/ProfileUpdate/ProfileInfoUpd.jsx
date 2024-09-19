import React, { useState,useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ProfileInfoUpd.css';
import axiosInstance, { switchToProfileService } from '../axiosInstance'; 
import logo from '../Assets/logo3.png'
//import profilePic from '../Assets/profile-image.jpg';

const ProfileInfoUpd = () => {
    const [userData, setUserData] = useState({
       // profilePic: profilePic, // existant image (placeholder)
        username: '',
        surname: '',
        email: '',
    });

    //const [newProfilePic, setNewProfilePic] = useState(null);
    const [newUsername, setNewUsername] = useState('');
    const [newSurname, setNewSurname] = useState('');
    const [newEmail, setNewEmail] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            try {
                switchToProfileService();
                console.log('userData iniziale:', userData);
                
                const jwtToken = localStorage.getItem('jwtToken');
                console.log('Token JWT:', jwtToken); 
                if (!jwtToken) {
                    throw new Error('No token found');
                }
    
                // Chiamata API per ottenere i dati attuali dell'utente
                const response = await fetch('http://localhost:8081/profiles/me', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${jwtToken}`,
                        'Content-Type': 'application/json',
                    },
                });
    
                if (!response.ok) {
                    throw new Error('Errore nel caricamento del profilo');
                }
    
                const data = await response.json();
                console.log('Dati utente recuperati:', data);
    
                // Popola userData con i dati recuperati
                setUserData(data);
                setNewUsername(data.username);
                setNewSurname(data.surname);
                setNewEmail(data.email);
            } catch (error) {
                console.error('Errore nel caricamento del profilo:', error);
            }
        };
        fetchData();
    },[]);

   // const handleProfilePicChange = (e) => {
    //    setNewProfilePic(URL.createObjectURL(e.target.files[0]));
   // };

    const handleSaveChanges = async (e) => {
        e.preventDefault();

        const jwtToken = localStorage.getItem('jwtToken');

        const updatedUserData = {
            username: newUsername,
            surname: newSurname,
            email: newEmail,
           // profilePic: newProfilePic || userData.profilePic,
        };

        // Effettua una richiesta PUT per aggiornare il profilo
        fetch('http://localhost:8081/profiles/update-profile', {
            method: 'PUT', // Metodo PUT per aggiornare i dati
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtToken}`, // Aggiungi il token JWT
            },
            body: JSON.stringify(updatedUserData), // I dati da aggiornare
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error('Errore nell\'aggiornamento del profilo');
            }
            return response.json(); // Ricevi i dati aggiornati
        })
        .then((data) => {
            // Aggiorna lo stato con i dati restituiti
            setUserData({
                ...userData,
                username: data.username,
                surname: data.surname,
                email: data.email,
                // Aggiorna anche profilePic se è incluso nella risposta
            });
    
            // Naviga indietro o mostra un messaggio di successo
            navigate('/profile-update'); // Torna alla pagina del profilo
        })
        .catch((error) => {
            console.error('Errore durante il salvataggio del profilo:', error);
        });
    };


    const handleCancel = () => {
        navigate('/profile-update');
    };

    

    return (
        <div className="mainpage">
            <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
       </div>
        <div className="edit-profile-container">
            <button className="back-button" onClick={handleCancel}>
                Go back
            </button>
            <h2 className="title">Update profile information</h2>
            <form onSubmit={handleSaveChanges} className="edit-profile-form">
              {/* <div className="profile-pic-section">
                    <label>Profile image:</label>
                    <input type="file" onChange={handleProfilePicChange} />
                    <img
                        src={newProfilePic || userData.profilePic}
                        alt="Profile Preview"
                        className="profile-pic-preview"
                    />
                </div> */}

                <div>
                    <label htmlFor="username">Name:</label>
                    <input
                        type="text"
                        value={newUsername}
                        name="username"
                        onChange={(e) => setNewUsername(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="surname">Surname:</label>
                    <input
                        type="text"
                        value={newSurname}
                        id ="surname"
                        onChange={(e) => setNewSurname(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="email">Email:</label>
                    <input
                        type="email"
                        value={newEmail}
                        id="email"
                        onChange={(e) => setNewEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="button-container">
                <button type="submit" className="save-button">
                    Save 
                </button>
                <button type="button" className="cancel-button" onClick={handleCancel}>
                    Cancel
                </button>
                </div>
            </form>
        </div>
        </div>
    );
};

export default ProfileInfoUpd;
