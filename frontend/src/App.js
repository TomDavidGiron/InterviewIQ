import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Home from "./pages/Home";
import Interview from "./pages/Interview";
import SummaryPage from "./pages/SummaryPage";
import HistoryPage from "./pages/HistoryPage";
import SessionDetailsPage from "./pages/SessionDetailsPage";
import ErrorBoundary from "./components/ErrorBoundary";
import NotFoundPage from "./pages/NotFoundPage";

function App() {
  return (
    <ErrorBoundary>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/interview" element={<Interview />} />
          <Route path="/interview/:sessionId" element={<Interview />} />
          <Route path="/summary/:sessionId" element={<SummaryPage />} />
          <Route path="/history" element={<HistoryPage />} />
          <Route path="/history/:sessionId" element={<SessionDetailsPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Router>
    </ErrorBoundary>
  );
}

export default App;
