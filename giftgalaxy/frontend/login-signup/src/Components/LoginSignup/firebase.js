// firebase.js
import { initializeApp } from 'firebase/app';
import { getAuth, signInWithPopup, GoogleAuthProvider } from 'firebase/auth';

const firebaseConfig = {
  apiKey: "AIzaSyDiHpowSVEOKqxJyB6WXRLYOXbOVALkeaI",
  authDomain: "gift-galaxy-6baae.firebaseapp.com",
  projectId: "gift-galaxy-6baae",
  storageBucket: "gift-galaxy-6baae.appspot.com",
  messagingSenderId: "93423380264",
  appId: "1:93423380264:web:26410d4242374c52ad121d"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const provider = new GoogleAuthProvider();

export { auth, provider };
