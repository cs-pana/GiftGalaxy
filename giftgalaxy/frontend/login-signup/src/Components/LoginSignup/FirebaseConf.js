// Import the functions you need from the SDKs you need
import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider, signInWithPopup as firebaseSignInWithPopup } from 'firebase/auth';

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyDiHpowSVEOKqxJyB6WXRLYOXbOVALkeaI",
  authDomain: "gift-galaxy-6baae.firebaseapp.com",
  projectId: "gift-galaxy-6baae",
  storageBucket: "gift-galaxy-6baae.appspot.com",
  messagingSenderId: "93423380264",
  appId: "1:93423380264:web:26410d4242374c52ad121d"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

// Re-export the signInWithPopup function from firebase/auth
const signInWithPopup = (provider) => {
  return firebaseSignInWithPopup(auth, provider);
};

export { auth, googleProvider, signInWithPopup };
