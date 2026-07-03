import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Home from "./pages/Home";
import Interview from "./pages/Interview";
import SummaryPage from "./pages/SummaryPage";
import HistoryPage from "./pages/HistoryPage";
import SessionDetailsPage from "./pages/SessionDetailsPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/interview" element={<Interview />} />
        <Route path="/interview/:sessionId" element={<Interview />} />
        <Route path="/summary/:sessionId" element={<SummaryPage />} />
        <Route path="/history" element={<HistoryPage />} />
        <Route path="/history/:sessionId" element={<SessionDetailsPage />} />
      </Routes>
    </Router>
  );
}

export default App;
