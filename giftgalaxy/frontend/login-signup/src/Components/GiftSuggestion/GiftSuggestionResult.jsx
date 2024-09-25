import React, {useState}from 'react';
import './GiftSuggestionResult.css'; // Riutilizziamo lo stesso stile
import { useNavigate, useLocation } from 'react-router-dom';
import logo from '../Assets/logo3.png';

const GiftSuggestionsResult = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // Recupera i dati dal navigate (per ora simuliamo dei suggerimenti)
  const { recipientName, event } = location.state || {};

  const [wishlist, setWishlist] = useState([]);

  // Suggerimenti fittizi
  const suggestions = [
    { name: 'Amazon Gift Card', link: 'https://www.amazon.com', source: 'Amazon' },
    { name: 'Concert Ticket', link: 'https://www.ticketone.it', source: 'TicketOne' },
    { name: 'Fitbit Watch', link: 'https://www.amazon.com', source: 'Amazon' },
    { name: 'Bluetooth Headphones', link: 'https://www.amazon.com', source: 'Amazon' },
  ];

  // Aggiunge un regalo alla wishlist
  const handleAddToWishlist = (item) => {
    // Controlla se l'elemento è già nella wishlist
    if (!wishlist.some((wishItem) => wishItem.id === item.id)) {
      setWishlist((prevWishlist) => [...prevWishlist, item]);
      alert(`${item.name} has been added to your wishlist!`);
    }else {
        alert(`${item.name} is already in your wishlist.`);
    }
  };

  const handleBackClick = () => {
    navigate('/gift-suggestion'); // Torna indietro alla pagina di suggerimenti
  };

  return (
    <div className="suggestionlistpage">
      <div className="logo">
        <img src={logo} alt="Logo" className="logo-image"/>
        </div>
    <div className="gift-suggestion-container">
      <div className="form-container">
        <h2 className="title">Gift Suggestions for {recipientName}</h2>
        <p className="event">Event: {event}</p>

        <ul className="suggestion-list">
          {suggestions.map((suggestion, index) => (
            <li key={index} className="suggestion-item">
              <p><strong>{suggestion.name}</strong></p>
              <p>Available on: <a href={suggestion.link} target="_blank" rel="noopener noreferrer">{suggestion.source}</a></p>
              <button className="wishlist-button" onClick={() => handleAddToWishlist(suggestion)}>
                Add to Wishlist
              </button>
            </li>
          ))}
        </ul>

        <button className="back-button" onClick={handleBackClick}>Back</button>
      </div>
    </div>
    </div>
  );
};

export default GiftSuggestionsResult;
