import React, { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Container,
  Divider,
  Paper,
  Typography
} from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";
import { getSessionDetails } from "../api/interviewApi";

function formatDate(value) {
  if (!value) return "—";
  try {
    return new Date(value).toLocaleString();
  } catch {
    return value;
  }
}

export default function SessionDetailsPage() {
  const navigate = useNavigate();
  const { sessionId } = useParams();

  const [attempts, setAttempts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");

  useEffect(() => {
    const loadDetails = async () => {
      try {
        setLoading(true);
        setPageError("");

        const data = await getSessionDetails(sessionId);
        setAttempts(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Failed to load session details", error);
        setPageError("Failed to load session details.");
      } finally {
        setLoading(false);
      }
    };

    loadDetails();
  }, [sessionId]);

  return (
    <Container maxWidth="md" sx={{ mt: 5, mb: 6 }}>
      <Paper elevation={4} sx={{ p: 4, borderRadius: 4 }}>
        <Typography variant="h5" fontWeight={900} sx={{ mb: 2 }}>
          Session Details
        </Typography>

        <Typography variant="body1" sx={{ mb: 3 }}>
          Session ID: <b>{sessionId}</b>
        </Typography>

        {loading ? (
          <Box sx={{ textAlign: "center", py: 4 }}>
            <CircularProgress />
          </Box>
        ) : pageError ? (
          <Alert severity="error">{pageError}</Alert>
        ) : attempts.length === 0 ? (
          <Alert severity="info">No attempts found for this session.</Alert>
        ) : (
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            {attempts.map((attempt) => (
              <Paper
                key={attempt.attemptId}
                variant="outlined"
                sx={{ p: 2.5, borderRadius: 3 }}
              >
                <Typography variant="h6" fontWeight={800}>
                  {attempt.questionText}
                </Typography>

                <Typography variant="body2" sx={{ opacity: 0.75, mt: 0.5 }}>
                  {formatDate(attempt.createdAt)}
                </Typography>

                <Divider sx={{ my: 1.5 }} />

                <Typography fontWeight={800} sx={{ mb: 0.5 }}>
                  Answer
                </Typography>
                <Typography sx={{ mb: 2, whiteSpace: "pre-wrap" }}>
                  {attempt.answerText || "—"}
                </Typography>

                <Typography fontWeight={800} sx={{ mb: 0.5 }}>
                  Feedback
                </Typography>
                <Typography sx={{ mb: 2, whiteSpace: "pre-wrap" }}>
                  {attempt.feedback || "—"}
                </Typography>

                <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", mb: 1 }}>
                  <Chip
                    label={attempt.passed ? "Passed" : "Not passed"}
                    color={attempt.passed ? "success" : "warning"}
                  />
                  <Chip
                    label={`${attempt.earnedPoints}/${attempt.maxPoints}`}
                    variant="outlined"
                  />
                </Box>

                {attempt.missingKeywords?.length > 0 && (
                  <Typography variant="body2">
                    <b>Missing keywords:</b>{" "}
                    {attempt.missingKeywords.join(", ")}
                  </Typography>
                )}
              </Paper>
            ))}
          </Box>
        )}

        <Box sx={{ display: "flex", gap: 2, mt: 3 }}>
          <Button variant="contained" onClick={() => navigate("/history")}>
            Back to History
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}