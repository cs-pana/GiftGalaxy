import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ProfileInfoUpd.css';
import logo from '../Assets/logo3.png'
import profilePic from '../Assets/profile-image.jpg';

const ProfileInfoUpd = () => {
    const [userData, setUserData] = useState({
        profilePic: profilePic, // existant image (placeholder)
        username: 'Mario',
        surname: 'Rossi',
        email: 'mario.rossi@example.com',
    });

    const [newProfilePic, setNewProfilePic] = useState(null);
    const [newUsername, setNewUsername] = useState(userData.username);
    const [newSurname, setNewSurname] = useState(userData.surname);
    const [newEmail, setNewEmail] = useState(userData.email);
    const navigate = useNavigate();

    const handleProfilePicChange = (e) => {
        setNewProfilePic(URL.createObjectURL(e.target.files[0]));
    };

    const handleSaveChanges = (e) => {
        e.preventDefault();
    
            setUserData({
                profilePic: newProfilePic || userData.profilePic, // if the profile image change
                username: newUsername,
                surname: newSurname,
                email: newEmail
        });
        navigate('/profile-update'); // go back 
        
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
                <div className="profile-pic-section">
                    <label>Profile image:</label>
                    <input type="file" onChange={handleProfilePicChange} />
                    <img
                        src={newProfilePic || userData.profilePic}
                        alt="Profile Preview"
                        className="profile-pic-preview"
                    />
                </div>

                <div>
                    <label>Name:</label>
                    <input
                        type="text"
                        value={newUsername}
                        onChange={(e) => setNewUsername(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Surname:</label>
                    <input
                        type="text"
                        value={newSurname}
                        onChange={(e) => setNewSurname(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={newEmail}
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
