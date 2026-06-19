import React, { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Container,
  Paper,
  Typography
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { getHistory } from "../api/interviewApi";

function formatDate(value) {
  if (!value) return "—";
  try {
    return new Date(value).toLocaleString();
  } catch {
    return value;
  }
}

export default function HistoryPage() {
  const navigate = useNavigate();

  const [historyItems, setHistoryItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");

  useEffect(() => {
    const loadHistory = async () => {
      try {
        setLoading(true);
        setPageError("");

        const data = await getHistory(0, 20);
        setHistoryItems(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Failed to load history", error);
        setPageError("Failed to load history.");
      } finally {
        setLoading(false);
      }
    };

    loadHistory();
  }, []);

  return (
    <Container maxWidth="md" sx={{ mt: 5, mb: 6 }}>
      <Paper elevation={4} sx={{ p: 4, borderRadius: 4 }}>
        <Typography variant="h5" fontWeight={900} sx={{ mb: 2 }}>
          Interview History
        </Typography>

        <Typography variant="body2" sx={{ opacity: 0.75, mb: 3 }}>
          Recent interview sessions
        </Typography>

        {loading ? (
          <Box sx={{ textAlign: "center", py: 4 }}>
            <CircularProgress />
          </Box>
        ) : pageError ? (
          <Alert severity="error">{pageError}</Alert>
        ) : historyItems.length === 0 ? (
          <Alert severity="info">No interview history yet.</Alert>
        ) : (
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            {historyItems.map((item) => {
              const percent =
                item.totalPossibleScore > 0
                  ? ((item.score / item.totalPossibleScore) * 100).toFixed(1)
                  : "0.0";

              return (
                <Paper
                  key={item.sessionId}
                  variant="outlined"
                  sx={{ p: 2.5, borderRadius: 3 }}
                >
                  <Typography variant="h6" fontWeight={800} sx={{ mb: 1 }}>
                    {item.roleHint || "General interview"}
                  </Typography>

                  <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", mb: 1 }}>
                    <Chip label={item.status || "UNKNOWN"} />
                    <Chip
                      label={`${item.score}/${item.totalPossibleScore}`}
                      variant="outlined"
                    />
                    <Chip label={`${percent}%`} variant="outlined" />
                    {item.levelHint && (
                      <Chip label={item.levelHint} variant="outlined" />
                    )}
                  </Box>

                  <Typography variant="body2" sx={{ mb: 0.5 }}>
                    <b>Session ID:</b> {item.sessionId}
                  </Typography>
                  <Typography variant="body2" sx={{ mb: 0.5 }}>
                    <b>Created:</b> {formatDate(item.createdAt)}
                  </Typography>
                  <Typography variant="body2" sx={{ mb: 0.5 }}>
                    <b>Ended:</b> {formatDate(item.endedAt)}
                  </Typography>

                  {item.failReason && (
                    <Typography variant="body2" color="error" sx={{ mt: 1 }}>
                      <b>Fail reason:</b> {item.failReason}
                    </Typography>
                  )}

                  <Box sx={{ display: "flex", gap: 2, mt: 2, flexWrap: "wrap" }}>
                    <Button
                      variant="contained"
                      onClick={() => navigate(`/summary/${item.sessionId}`)}
                    >
                      Open Summary
                    </Button>

                    <Button
                      variant="outlined"
                      onClick={() => navigate(`/history/${item.sessionId}`)}
                    >
                      View Details
                    </Button>
                  </Box>
                </Paper>
              );
            })}
          </Box>
        )}

        <Box sx={{ display: "flex", gap: 2, mt: 3 }}>
          <Button variant="outlined" onClick={() => navigate("/")}>
            Back Home
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}