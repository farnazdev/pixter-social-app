# Pixter – Social Media Android App

## Getting Started

Pixter is a fully functional Android-based social media platform, built to connect users through messaging, content sharing, and interactive features like following, liking, and commenting.
It combines a native mobile client with a real-time backend to deliver a seamless social experience.

Note: This is my associate's thesis project.

---


## Features

🔐 User Authentication
Secure registration and login system with session handling

💬 Private Messaging
One-on-one real-time chat with message persistence

🖼️ Media Upload
Share images via posts or messages with optimized delivery

❤️ Engagement Tools
Like and comment system similar to modern social apps

👥 Follow System
Follow/unfollow users and get updates on their activities

🧾 Profile Management
Edit your personal profile and browse others’ profiles

🔔 Notification System
Get notified of new likes, comments, followers, and more

---

##  How It Works

1. Users sign up or log in with secure credentials
2. After authentication, users can:
3. Upload and share image posts
4. Start private chats with other users
5. Like or comment on posts from their feed
6. Follow or unfollow other users
7. The app communicates with a backend server via HTTP API (written in PHP)
8. All user data, images, and chat logs are stored and served dynamically from the backend

---

## Logs

While this app doesn’t maintain traditional logs, the backend handles and stores:

- 📅 User login/logout sessions
- 💬 Message history for chats
- 🖼️ Image upload metadata
- 🔁 API request/response logs for debugging (server-side)

---

## Requirements

Frontend: Android Studio (Java)
Backend: PHP server with API endpoints for auth, post, and chat
Database: MySQL or equivalent
Image Handling: Glide library for smooth media loading
API Communication: RESTful API calls over HTTP

---

## Testing

Create a new account or use test credentials
Upload an image post and verify it appears on the feed
Initiate a private chat between two users
Like/comment on posts and validate interactions
Test follow/unfollow and view post filtering
Simulate network delays and confirm app handles API errors gracefully

---

## Demo

Video demo

📎 [View]()


Execute

📎 [View](https://drive.google.com/drive/folders/1jcLYNyGRzP5QNjmCe4ZjFo0kQ9PrYZ6S?usp=sharing)


---
