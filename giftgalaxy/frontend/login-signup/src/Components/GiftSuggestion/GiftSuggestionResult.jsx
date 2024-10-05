import React, {useEffect, useState}from 'react';
import './GiftSuggestionResult.css'; // Riutilizziamo lo stesso stile
import { useNavigate, useLocation } from 'react-router-dom';
import logo from '../Assets/logo3.png';
import axiosInstance, { switchToProfileService, switchToWishlistService } from '../axiosInstance';

const GiftSuggestionsResult = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // Recupera i dati dal navigate and other info
  const { suggestions,recipientName, event } = location.state || {};

  const[userData, setUserData] = useState(null);
  const [wishlist, setWishlist] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addedItems, setAddedItems] = useState([]);


  console.log("Suggestions received:", suggestions);

  // Suggerimenti fittizi
 // const suggestions = [
   // { name: 'Amazon Gift Card', link: 'https://www.amazon.com', source: 'Amazon' },
    //{ name: 'Concert Ticket', link: 'https://www.ticketone.it', source: 'TicketOne' },
    //{ name: 'Fitbit Watch', link: 'https://www.amazon.com', source: 'Amazon' },
    //{ name: 'Bluetooth Headphones', link: 'https://www.amazon.com', source: 'Amazon' },
  //];

  // Aggiunge un regalo alla wishlist
  /*const handleAddToWishlist = (item) => {
    // Controlla se l'elemento è già nella wishlist
    if (!wishlist.some((wishItem) => wishItem.id === item.id)) {
      setWishlist((prevWishlist) => [...prevWishlist, item]);
      alert(`${item.name} has been added to your wishlist!`);
    }else {
        alert(`${item.name} is already in your wishlist.`);
    }
  };*/


  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const jwtToken = localStorage.getItem('jwtToken');
        if (!jwtToken) {
          setError('Invalid token.');
          setLoading(false);
          return;
        }

        // Switch to profile service to fetch user data        
        switchToProfileService();

        const userResponse = await axiosInstance.get(`/profiles/me`, {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        });

        setUserData(userResponse.data);
        setLoading(false);
        console.log("User data fetched:", userResponse.data);
      } catch (err) {
        console.error('Error fetching user data:', err);
        setError('Error fetching user data.');
        setLoading(false);
      }
    };

    // Call fetchUserData on component mount
    fetchUserData();
  }, []);

  const handleAddToWishlist = async (suggestion) => {

    if (!userData) {
      alert('User data is not available yet.');
      return;
    }
    try {
      const jwtToken = localStorage.getItem('jwtToken');
      if (!jwtToken) {
        alert('invalid token.');
        return;
      }

      console.log("ADD TO WISHLIST: Switching to wishlist service...");
      switchToWishlistService();

      const newWish = {
        giftName: suggestion.name,
        giftLink: suggestion.link,
        recipientName: recipientName,
        eventType: event,
        userId: userData.userId
      };

      //POST request to wishlist service
      console.log("Adding to wishlist: " + newWish);

      const response = await axiosInstance.post('/wishlist/add', newWish, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      console.log("Item successfully added to wishlist: ", response.data);
      setWishlist((prevWishlist) => [...prevWishlist, response.data]);
      const unique = `${suggestion.name}-${suggestion.link}`;
      setAddedItems((prevAddedItems) => [...prevAddedItems, unique]); 

    } catch (error) {
      console.error('Error adding to wishlist:', error);
      setError('Error adding to wishlist.');

    }

  }

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

        {suggestions && suggestions.length > 0 ? (
        <ul className="suggestion-list">
         {suggestions.map((suggestion, index) => {
              const unique = `${suggestion.name}-${suggestion.link}`;
              return (
                <li key={index} className="suggestion-item">
                  <p><strong>{suggestion.name}</strong></p>
                  <p>Available on: <a href={suggestion.link} target="_blank" rel="noopener noreferrer">{suggestion.source}</a></p>
                  {addedItems.includes(unique) ? (
                    <button className="added-button" disabled>
                      Added to Wishlist
                    </button>
                  ) : (
                    <button className="wishlist-button" onClick={() => handleAddToWishlist(suggestion)}>
                      Add to Wishlist
                    </button>
                  )}
                </li>
              );
            })}
        </ul>
        ) : (
          <p> No suggestions found. </p>
        )}

        <button className="back-button" onClick={handleBackClick}>Back</button>
      </div>
    </div>
    </div>
  );
};

export default GiftSuggestionsResult;
