import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom";

import Home from "./pages/Home";
import Interview from "./pages/Interview";
import SummaryPage from "./pages/SummaryPage";
import HistoryPage from "./pages/HistoryPage";
import SessionDetailsPage from "./pages/SessionDetailsPage";
import AuthPage from "./pages/AuthPage";

function PrivateRoute({ children }) {
  const location = useLocation();
  return localStorage.getItem("token")
    ? children
    : <Navigate to="/auth" state={{ from: location.pathname }} replace />;
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/auth" element={<AuthPage />} />
        <Route path="/" element={<Home />} />
        <Route path="/interview" element={<Interview />} />
        <Route path="/interview/:sessionId" element={<Interview />} />
        <Route path="/summary/:sessionId" element={<SummaryPage />} />
        <Route path="/history" element={<PrivateRoute><HistoryPage /></PrivateRoute>} />
        <Route path="/history/:sessionId" element={<PrivateRoute><SessionDetailsPage /></PrivateRoute>} />
      </Routes>
    </Router>
  );
}

export default App;
