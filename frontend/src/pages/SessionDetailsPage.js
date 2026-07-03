import React, { useEffect, useState } from "react";
import {
  Alert, Box, Button, Chip, CircularProgress,
  Container, Divider, Typography
} from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";
import { getSessionDetails } from "../api/interviewApi";

function formatDate(value) {
  if (!value) return "—";
  try { return new Date(value).toLocaleString(); }
  catch { return value; }
}

export default function SessionDetailsPage() {
  const navigate = useNavigate();
  const { sessionId } = useParams();
  const [attempts, setAttempts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await getSessionDetails(sessionId);
        setAttempts(Array.isArray(data) ? data : []);
      } catch {
        setPageError("Failed to load session details.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [sessionId]);

  return (
    <Container maxWidth="md" sx={{ pt: 5, pb: 8 }}>

      {/* Header */}
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 4 }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 900 }}>
            Interview<Box component="span" sx={{ color: "primary.main" }}>IQ</Box>
            <Box component="span" sx={{ color: "text.secondary", fontWeight: 400, fontSize: "0.9rem", ml: 2 }}>
              Session
            </Box>
          </Typography>
          <Typography sx={{ fontFamily: "monospace", fontSize: "0.7rem", color: "text.secondary", mt: 0.25 }}>
            {sessionId}
          </Typography>
        </Box>
        <Button size="small" variant="text" sx={{ color: "text.secondary", fontSize: "0.75rem" }}
          onClick={() => navigate("/history")}>
          ← History
        </Button>
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", py: 8 }}><CircularProgress /></Box>
      ) : pageError ? (
        <Alert severity="error">{pageError}</Alert>
      ) : attempts.length === 0 ? (
        <Alert severity="info">No attempts found.</Alert>
      ) : (
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          {attempts.map((attempt, idx) => (
            <Box key={attempt.attemptId} sx={{
              backgroundColor: "#0f0f1a", border: "1px solid #1e1e30",
              borderRadius: 3, p: 3,
            }}>
              {/* Question header */}
              <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", mb: 1 }}>
                <Box sx={{ display: "flex", gap: 1, alignItems: "center" }}>
                  <Typography sx={{ fontFamily: "monospace", color: "primary.main", fontWeight: 700, fontSize: "0.8rem" }}>
                    Q{String(idx + 1).padStart(2, "0")}
                  </Typography>
                  <Chip
                    label={attempt.passed ? "Passed" : "Missed"}
                    size="small"
                    sx={{
                      fontWeight: 800,
                      color: attempt.passed ? "#22d3a0" : "#ff4d6a",
                      border: "1px solid",
                      borderColor: attempt.passed ? "rgba(34,211,160,0.3)" : "rgba(255,77,106,0.3)",
                      backgroundColor: attempt.passed ? "rgba(34,211,160,0.08)" : "rgba(255,77,106,0.08)",
                    }}
                  />
                  <Chip
                    label={`${attempt.earnedPoints} / ${attempt.maxPoints} pts`}
                    size="small"
                    sx={{ fontFamily: "monospace", border: "1px solid #2a2a40", color: "text.secondary" }}
                  />
                </Box>
                <Typography variant="caption" sx={{ color: "text.secondary" }}>
                  {formatDate(attempt.createdAt)}
                </Typography>
              </Box>

              <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2, lineHeight: 1.4 }}>
                {attempt.questionText}
              </Typography>

              <Divider sx={{ my: 1.5, borderColor: "#1e1e30" }} />

              {/* Answer */}
              <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.08em" }}>
                Answer
              </Typography>
              <Box sx={{
                mt: 0.75, mb: 2, p: 2, borderRadius: 2,
                backgroundColor: "#08080f", border: "1px solid #1a1a2e",
              }}>
                <Typography variant="body2" sx={{ whiteSpace: "pre-wrap", color: "text.secondary", lineHeight: 1.6 }}>
                  {attempt.answerText || "—"}
                </Typography>
              </Box>

              {/* Feedback */}
              {attempt.feedback && (
                <>
                  <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.08em" }}>
                    Feedback
                  </Typography>
                  <Typography variant="body2" sx={{ mt: 0.75, mb: 2, color: "text.secondary", lineHeight: 1.6 }}>
                    {attempt.feedback}
                  </Typography>
                </>
              )}

              {/* Missing keywords */}
              {attempt.missingKeywords?.length > 0 && (
                <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap" }}>
                  {attempt.missingKeywords.map((k, i) => (
                    <Chip key={i} label={k} size="small"
                      sx={{ backgroundColor: "rgba(255,77,106,0.08)", color: "#ff4d6a", border: "1px solid rgba(255,77,106,0.2)" }} />
                  ))}
                </Box>
              )}
            </Box>
          ))}
        </Box>
      )}
    </Container>
  );
}
