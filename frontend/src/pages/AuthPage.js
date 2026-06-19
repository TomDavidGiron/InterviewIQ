import React, { useState } from "react";
import {
  Box,
  Button,
  Container,
  Tab,
  Tabs,
  TextField,
  Typography,
  Alert,
  Paper
} from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import apiClient from "../api/client";

export default function AuthPage() {
  const [tab, setTab] = useState(0);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from || "/";

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    const endpoint = tab === 0 ? "/api/auth/login" : "/api/auth/register";
    try {
      const { data } = await apiClient.post(endpoint, { username, password });
      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("username", data.username);
      navigate(from, { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ||
        (tab === 0 ? "Invalid username or password." : "Registration failed. Username may already be taken.")
      );
    }
  };

  return (
    <Container maxWidth="xs" sx={{ mt: 10 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h5" align="center" gutterBottom>
          InterviewIQ
        </Typography>

        <Tabs value={tab} onChange={(_, v) => { setTab(v); setError(""); }} centered sx={{ mb: 3 }}>
          <Tab label="Login" />
          <Tab label="Register" />
        </Tabs>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        <Box component="form" onSubmit={handleSubmit} display="flex" flexDirection="column" gap={2}>
          <TextField
            label="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            fullWidth
            autoFocus
          />
          <TextField
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            fullWidth
          />
          <Button type="submit" variant="contained" fullWidth>
            {tab === 0 ? "Login" : "Register"}
          </Button>
          <Button variant="text" fullWidth onClick={() => navigate("/")} sx={{ mt: 0.5 }}>
            Continue without signing in
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}
