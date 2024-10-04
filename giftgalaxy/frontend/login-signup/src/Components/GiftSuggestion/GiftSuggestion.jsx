import React, { useState } from 'react';
import './GiftSuggestion.css'; 
import { useNavigate } from 'react-router-dom';
import logo from '../Assets/logo3.png';
import axios from 'axios';

const GiftSuggestion = () => {
  const navigate = useNavigate();

  // States for form inputs
  const [recipientName, setRecipientName] = useState('');
  const [age, setAge] = useState('');
  const [sex, setSex] = useState('not specified');
  const [profession, setProfession] = useState('student');
  const [relationship, setRelationship] = useState('friend');
  const [interests, setInterests] = useState([]);
  const [event, setEvent] = useState('birthday');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleInterestChange = (e) => {
    const { value } = e.target;
    setInterests((prev) =>
      prev.includes(value) ? prev.filter((interest) => interest !== value) : [...prev, value]
    );
  };
  const handleBackClick = () => {
    navigate('/dashboard'); 
  };

  // Basic form validation
  const validateForm = () => {
    if (!recipientName.trim()) {
      setError('Recipient name is required.');
      return false;
    }
    if (age <= 0 || isNaN(age)) {
      setError('Please provide a valid age.');
      return false;
    }
    return true;
  };



  const handleSubmit = async () => {

    if (!validateForm()) {
      return;
    }
    
    const requestData ={
      recipientName,
      age: parseInt(age,10),
      sex,
      profession,
      relationship,
      interests,
      event,

    };

    console.log("Sending request data:", requestData);

    setLoading(true);
    setError(null);

    try{
      const token = localStorage.getItem('jwtToken');
      if(!token){
        throw new Error('No authentication token found');
      }
      //send data to the backend
      const response = await axios.post('http://localhost:8083/api/gift-suggestions', requestData, {
        headers:{
          Authorization: `Bearer ${token}`,
        }
      });

      console.log("Response from backend:", response.data);
      //navigate to the result passing the suggestions received
      navigate('/gift-suggestion-result', {
        state: {
          suggestions: response.data, //suggestions received from the backend
          recipientName,
          event,
        },
      });
    } catch(err){
      console.error('Error fetching gift suggestions:', err);
      setError('Failed to fetch gift suggestions.');
    } finally {
      setLoading(false);
    }

    /*// Logic for searching gifts based on input
    console.log({
      recipientName,
      age,
      sex,
      profession,
      relationship,
      interests,
      event,
    });
    navigate('/gift-suggestions', {
        state: {
          //suggestions: response.data.suggestions,
          recipientName,
          event,
        },
      });*/
  };

  

  return (
    <div className="gift-suggestion-container">
      <div className="logo">
        <img src={logo} alt="Logo" className="logo-image" />
      </div>
      <div className="form-container">
        <h2 className="title">Gift Suggestion</h2>

        <label>Name of the recipient</label>
        <input className="cont"
          type="text"
          value={recipientName}
          onChange={(e) => setRecipientName(e.target.value)}
          placeholder="Recipient's name"
        />

        <label>Age</label>
        <input className="cont"
          type="number"
          value={age}
          onChange={(e) => setAge(e.target.value)}
          placeholder="Recipient's age"
        />

        <label>Sex</label>
        <select className="tendina" value={sex} onChange={(e) => setSex(e.target.value)}>
          <option value="male">Male</option>
          <option value="female">Female</option>
          <option value="not specified">Not Specified</option>
        </select>

        <label>Profession</label>
        <select className="tendina" value={profession} onChange={(e) => setProfession(e.target.value)}>
          <option value="student">Student</option>
          <option value="worker">Worker</option>
        </select>

        <label>The recipient is</label>
        <select className="tendina" value={relationship} onChange={(e) => setRelationship(e.target.value)}>
          <option className="option" value="friend">Friend</option>
          <option className="option"  value="girlfriend">Girlfriend</option>
          <option className="option" value="boyfriend">Boyfriend</option>
          <option className="option" value="wife">Wife</option>
          <option className="option" value="husband">Husband</option>
          <option className="option" value="acquaintance">Acquaintance</option>
          <option className="option" value="employee">Employee</option>
          <option className="option" value="family">Family</option>
        </select>

        <label className="interestlabel">Recipient's Interest</label>
        <div className="checkbox-group">
          <label className="sportlabel"><input type="checkbox" value="sport" onChange={handleInterestChange} /> Sport</label>
          <label><input type="checkbox" value="literature" onChange={handleInterestChange} /> Literature</label>
          <label><input type="checkbox" value="travel" onChange={handleInterestChange} /> Travel and Nature</label>
          <label><input type="checkbox" value="music" onChange={handleInterestChange} /> Music</label>
          <label><input type="checkbox" value="videogames" onChange={handleInterestChange} /> Videogames</label>
          <label><input type="checkbox" value="fashion" onChange={handleInterestChange} /> Fashion</label>
          <label><input type="checkbox" value="cinema" onChange={handleInterestChange} /> Cinema</label>
          <label><input type="checkbox" value="theatre" onChange={handleInterestChange} /> Theatre</label>
        </div>

        <label>Type of event</label>
        <select className="tendina" value={event} onChange={(e) => setEvent(e.target.value)}>
          <option value="birthday">Birthday</option>
          <option value="anniversary">Anniversary</option>
          <option value="christmas">Christmas</option>
          <option value="corporate-event">Corporate Event</option>
          <option value="wedding">Wedding</option>
          <option value="special-occasion">Other Special Occasion</option>
          
        </select>

        <div className="button-group">
            <button className="search-button" onClick={handleSubmit} disabled={loading}>
              {loading ? 'Searching...' : 'Search'}</button>
             <button className="back-button" onClick={handleBackClick}>
                  Back to Dashboard
              </button>
        </div>
        {error && <p className="error-message">{error}</p>}
        
      </div>
    </div>
  );
};

export default GiftSuggestion;
