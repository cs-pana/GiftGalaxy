import React, { useEffect, useState } from 'react';
import './Wishlist.css';
import { useParams, useNavigate } from 'react-router-dom';
import logo from '../Assets/logo3.png';
import axiosInstance, { switchToWishlistService } from '../axiosInstance';

const Wishlist = () => {
  const { userId } = useParams();
  const [wishlist, setWishlist] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingItemId, setEditingItemId] = useState(null);
  const [editingItem, setEditingItem] = useState({ giftName: '', giftLink: '', recipientName: '', eventType: '' });
  const navigate = useNavigate();

  useEffect(() => {
    const fetchWishlist = async () => {
      switchToWishlistService();
      try {
        const jwtToken = localStorage.getItem('jwtToken');
        if (!jwtToken) {
          throw new Error('No token found');
        }

        const response = await axiosInstance.get(`/wishlist/${userId}`, {
          headers: {
            Authorization: `Bearer ${jwtToken}` 
          }
        });
        
        setWishlist(response.data);
      } catch (error) {
        console.error('Error fetching wishlist:', error);
        setError('Failed to fetch wishlist.');
      } finally {
        setLoading(false);
      }
    };

    fetchWishlist();
  }, [userId]);

  const handleRemove = async (id) => {
    try {
      const jwtToken = localStorage.getItem('jwtToken');
      switchToWishlistService();
      await axiosInstance.delete(`/wishlist/delete/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      setWishlist((prevWishlist) => prevWishlist.filter((item) => item.id !== id));
    } catch (error) {
      console.error('Error removing item:', error);
      setError('Failed to remove item.');
    }
  };

  const handleEdit = (item) => {
    setEditingItemId(item.id);
    setEditingItem({ 
      giftName: item.giftName, 
      giftLink: item.giftLink, 
      recipientName: item.recipientName, 
      eventType: item.eventType });
  };

  const handleSaveChanges = async (e) => {
    e.preventDefault();
    const updatedItem = { ...editingItem, id: editingItemId };
    switchToWishlistService();

    try {
      const jwtToken = localStorage.getItem('jwtToken');
      await axiosInstance.put(`/wishlist/update/${editingItemId}`, updatedItem, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      setWishlist((prevWishlist) =>
        prevWishlist.map((item) => (item.id === editingItemId ? updatedItem : item))
      );
      setEditingItemId(null);
    } catch (error) {
      console.error('Error updating item:', error);
      setError('Failed to update item.');
    }
  };

  const handleBackClick = () => {
    navigate('/dashboard');
  };

  //shorten long links to fit page
  const shortenLink = (link, maxLength = 30) => {
    return link.length > maxLength ? `${link.substring(0, maxLength)}...` : link;
  };

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
    <div className="wishlistpage">
      <div className="logo">
        <img src={logo} alt="Logo" className="logo-image" />
      </div>
      <div className="wishlist-items-container">
        <h2 className="title">Your Wishlist</h2>
        <ul className="suggestion-list">
          {wishlist.length === 0 ? (
            <p>No items found in your wishlist.</p>
          ) : (
            wishlist.map((item) => (
              <li key={item.id} className="suggestion-item">
                {editingItemId === item.id ? (
                  <form onSubmit={handleSaveChanges}>
                    <div>
                      <label>Item:</label>
                      <input
                        type="text"
                        value={editingItem.giftName}
                        onChange={(e) => setEditingItem({ ...editingItem, giftName: e.target.value })}
                        required
                      />
                    </div>
                    <div>
                      <label>Recipient:</label>
                      <input
                        type="text"
                        value={editingItem.recipientName}
                        onChange={(e) => setEditingItem({ ...editingItem, recipientName: e.target.value })}
                        required
                      />
                    </div>
                    <div>
                      <label>Event Type:</label>
                      <input
                        type="text"
                        value={editingItem.eventType}
                        onChange={(e) => setEditingItem({ ...editingItem, eventType: e.target.value })}
                        required
                      />
                    </div>
                    <div className="modify-button-cont">
                      <button type="submit">Save</button>
                      <button type="button" onClick={() => setEditingItemId(null)}>
                        Cancel
                      </button>
                    </div>
                  </form>
                ) : (
                  <div>
                    <strong>Item:</strong> {item.giftName} <br />
                    <strong>Link:</strong>
                      <a
                        href={item.giftLink}
                        target="_blank"
                        rel="noopener noreferrer"
                        title={item.giftLink} // Full link on hover
                      >
                        {shortenLink(item.giftLink)}
                      </a> <br />
                    <strong>Recipient:</strong> {item.recipientName} <br />
                    <strong>Event:</strong> {item.eventType} <br />
                    <div className="modify-button-cont">
                      <button className="modify-button" onClick={() => handleEdit(item)}>
                        Edit
                      </button>
                      <button className="delete-button" onClick={() => handleRemove(item.id)}>
                        Remove
                      </button>
                    </div>
                  </div>
                )}
              </li>
            ))
          )}
        </ul>
        <button className="back-button" onClick={handleBackClick}>
          Back
        </button>
      </div>
    </div>
  );
};

export default Wishlist;
