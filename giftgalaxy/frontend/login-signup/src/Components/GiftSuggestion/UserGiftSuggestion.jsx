import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import logo from '../Assets/logo3.png';

import './UserGiftSuggestion.css';

const UserGiftSuggestion = () => {
  const [itemName, setItemName] = useState('');
  const [link, setLink] = useState('');
  const [ageRange, setAgeRange] = useState('');
  const [interest, setInterest] = useState('');
  const [sex, setSex] = useState('not specified');
  const [profession, setProfession] = useState('student');
  const [relationship, setRelationship] = useState('friend');
  const [eventType, setEventType] = useState('birthday');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  // Validation error states
  const [itemNameError, setItemNameError] = useState('');
  const [ageRangeError, setAgeRangeError] = useState('');
  const [interestError, setInterestError] = useState('');
  const [linkError, setLinkError] = useState('');

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Reset validation error states
    setItemNameError('');
    setAgeRangeError('');
    setInterestError('');
    setLinkError('');
    setSuccess(false);  // Clear the success message if any validation error occurs

    // Check if required fields are empty
    let hasError = false;

    if (!itemName.trim()) {
      setItemNameError('Item name is required');
      hasError = true;
    }

    if (!ageRange) {
      setAgeRangeError('Age range is required');
      hasError = true;
    }

    if (!interest) {
      setInterestError('Interest is required');
      hasError = true;
    }

    if (link && !link.startsWith('https://')) {
        setLinkError('Invalid link format: must start with https://');
        hasError = true;
      }

    // If any validation error exists, prevent submission
    if (hasError) {
      return;
    }

    const suggestionData = {
      itemName,
      link,
      ageRange,
      sex,
      interest,
      profession,
      relationship,
      eventType,
    };

    console.log("Submitting suggestion:", suggestionData);
    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      const token = localStorage.getItem('jwtToken');
      if (!token) {
        throw new Error('No authentication token found');
      }

      await axios.post('http://localhost:8083/api/gift-suggestions/user-suggest', suggestionData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      // Show success message and reset fields
      setSuccess(true);
      setItemName('');
      setLink('');
      setAgeRange('');
      setSex('not specified');
      setInterest('');
      setProfession('student');
      setRelationship('friend');
      setEventType('birthday');
    } catch (err) {
        if (err.response && err.response.status === 409) { //409 is CONFLICT error
            // Handle duplicate suggestion error
            setError('A similar gift suggestion already exists.');
          } else {
            console.error('Error submitting gift suggestion:', err);
            setError('Failed to submit gift suggestion.');
          }
    } finally {
      setLoading(false);
    }
  };

  const handleInterestChange = (e) => {
    setInterest(e.target.value);
  };

  const handleBackClick = () => {
    navigate('/gift-suggestion');
  };

  return (
    <div className="user-gift-suggestion-container">
      <div className="logo">
        <img src={logo} alt="Logo" className="logo-image" />
      </div>
      <div className="form-container">
        <h2 className="title">Suggest a gift</h2>

        <label>
          Name of Item:
          <input
            className="cont"
            type="text"
            value={itemName}
            onChange={(e) => setItemName(e.target.value)}
            required
          />
          {itemNameError && <p className="error-message">{itemNameError}</p>}
        </label>

        <label>
          Link (optional):
          <input
            className="cont"
            type="url"
            value={link}
            onChange={(e) => setLink(e.target.value)}
            pattern="https?://.+"
            placeholder="https://example.com"
          />
            {linkError && <p className="error-message">{linkError}</p>}

        </label>

        <label>
          Age Range:
          <select className="tendina" value={ageRange} onChange={(e) => setAgeRange(e.target.value)} required>
            <option value="">Select age range</option>
            <option value="0-12">0-12</option>
            <option value="13-18">13-18</option>
            <option value="19-25">19-25</option>
            <option value="26-35">26-35</option>
            <option value="36+">36+</option>
          </select>
          {ageRangeError && <p className="error-message">{ageRangeError}</p>}
        </label>

        <label>
          Sex:
          <select className="tendina" value={sex} onChange={(e) => setSex(e.target.value)}>
            <option value="male">Male</option>
            <option value="female">Female</option>
            <option value="not specified">Not Specified</option>
          </select>
        </label>

        <label className="interestlabel">Recipient's Interest:</label>
        <div className="checkbox-group">
          <label className="sportlabel">
            <input
              type="radio"
              value="sport"
              checked={interest === 'sport'}
              onChange={handleInterestChange}
            /> Sport
          </label>
          <label>
            <input
              type="radio"
              value="literature"
              checked={interest === 'literature'}
              onChange={handleInterestChange}
            /> Literature
          </label>
          <label>
            <input
              type="radio"
              value="travel"
              checked={interest === 'travel'}
              onChange={handleInterestChange}
            /> Travel and Nature
          </label>
          <label>
            <input
              type="radio"
              value="music"
              checked={interest === 'music'}
              onChange={handleInterestChange}
            /> Music
          </label>
          <label>
            <input
              type="radio"
              value="videogames"
              checked={interest === 'videogames'}
              onChange={handleInterestChange}
            /> Videogames
          </label>
          <label>
            <input
              type="radio"
              value="fashion"
              checked={interest === 'fashion'}
              onChange={handleInterestChange}
            /> Fashion
          </label>
          <label>
            <input
              type="radio"
              value="cinema"
              checked={interest === 'cinema'}
              onChange={handleInterestChange}
            /> Cinema
          </label>
          <label>
            <input
              type="radio"
              value="theatre"
              checked={interest === 'theatre'}
              onChange={handleInterestChange}
            /> Theatre
          </label>
          </div>
        {interestError && <p className="error-message">{interestError}</p>}

        <label>
          Profession:
          <select className="tendina" value={profession} onChange={(e) => setProfession(e.target.value)} required>
            <option value="student">Student</option>
            <option value="worker">Worker</option>
          </select>
        </label>

        <label>
          Relationship:
          <select className="tendina" value={relationship} onChange={(e) => setRelationship(e.target.value)} required>
            <option value="friend">Friend</option>
            <option value="girlfriend">Girlfriend</option>
            <option value="boyfriend">Boyfriend</option>
            <option value="wife">Wife</option>
            <option value="husband">Husband</option>
            <option value="acquaintance">Acquaintance</option>
            <option value="employee">Employee</option>
            <option value="family">Family</option>
          </select>
        </label>

        <label>
          Type of Event:
          <select className="tendina" value={eventType} onChange={(e) => setEventType(e.target.value)} required>
            <option value="birthday">Birthday</option>
            <option value="anniversary">Anniversary</option>
            <option value="christmas">Christmas</option>
            <option value="corporate-event">Corporate Event</option>
            <option value="special-occasion">Other Special Occasion</option>
          </select>
        </label>

        <div className="button-group-usergift">
          <button className="submit-button" onClick={handleSubmit} disabled={loading}>
            {loading ? 'Submitting...' : 'Submit'}
          </button>
          <button className="back-button-usergift" onClick={handleBackClick}>
            Go back
          </button>
          {error && <p className="error-message">{error}</p>}
        </div>
        {success && <p className="success-message">Gift suggestion submitted successfully!</p>}
      </div>
    </div>
  );
};

export default UserGiftSuggestion;
