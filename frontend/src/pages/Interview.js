import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
  Alert, Box, Button, Chip, CircularProgress,
  Container, LinearProgress, TextField, Typography
} from "@mui/material";
import { submitAnswer } from "../api/interviewApi";

const TOTAL_FALLBACK = 10;

function difficultyColor(d) {
  if (!d) return "#6b7280";
  switch (d.toUpperCase()) {
    case "EASY": return "#22d3a0";
    case "HARD": return "#ff4d6a";
    default: return "#f59e0b";
  }
}

export default function Interview() {
  const location = useLocation();
  const navigate = useNavigate();
  const params = useParams();

  const sessionId = params.sessionId || location.state?.sessionId || null;
  const firstQuestion = location.state?.firstQuestion || null;

  const [currentQuestion, setCurrentQuestion] = useState(firstQuestion);
  const [answerText, setAnswerText] = useState("");
  const [selectedOptionIndex, setSelectedOptionIndex] = useState(null);

  const [lastFeedback, setLastFeedback] = useState(null);
  const [loading, setLoading] = useState(false);
  const [pageError, setPageError] = useState("");

  useEffect(() => { if (!sessionId) navigate("/"); }, [sessionId, navigate]);
  useEffect(() => {
    setAnswerText("");
    setSelectedOptionIndex(null);
    setPageError("");
  }, [currentQuestion?.id]);

  const qType = currentQuestion?.type || "OPEN";
  const options = Array.isArray(currentQuestion?.options) ? currentQuestion.options : [];
  const questionIndex = lastFeedback?.questionIndex ?? 0;
  const totalQuestions = lastFeedback?.totalQuestions ?? TOTAL_FALLBACK;
  const progressValue = useMemo(() =>
    totalQuestions ? Math.min(100, (questionIndex / totalQuestions) * 100) : 0,
    [questionIndex, totalQuestions]);

  const handleSubmit = async (skip = false) => {
    if (!sessionId) return;
    const payload = qType === "MCQ"
      ? { answerText: selectedOptionIndex === null ? "" : String(selectedOptionIndex) }
      : { answerText: skip ? "" : answerText.trim() };

    if (!skip && qType !== "MCQ" && !payload.answerText) {
      setPageError("Type an answer or hit Skip.");
      return;
    }

    setLoading(true);
    setPageError("");
    try {
      const response = await submitAnswer(sessionId, payload);
      setLastFeedback(response);
      if (response?.status === "COMPLETED" || response?.status === "FAILED") {
        navigate(`/summary/${sessionId}`);
        return;
      }
      if (response?.question) {
        setCurrentQuestion(response.question);
      } else {
        navigate(`/summary/${sessionId}`);
      }
    } catch {
      setPageError("Failed to submit. Try again.");
    } finally {
      setLoading(false);
    }
  };

  if (!sessionId || !currentQuestion) return null;

  const qNum = String(questionIndex + 1).padStart(2, "0");
  const qTotal = String(totalQuestions).padStart(2, "0");

  return (
    <Container maxWidth="md" sx={{ pt: 4, pb: 8 }}>

      {/* Header bar */}
      <Box sx={{
        display: "flex", justifyContent: "space-between", alignItems: "center",
        mb: 3, pb: 2, borderBottom: "1px solid #262626"
      }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>
          Interview<Box component="span" sx={{ color: "primary.main" }}>IQ</Box>
        </Typography>
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Typography sx={{
            fontFamily: "monospace", fontWeight: 700, fontSize: "1rem",
            color: "primary.main", letterSpacing: "0.05em"
          }}>
            {qNum} / {qTotal}
          </Typography>
          <Button size="small" variant="text" sx={{ color: "text.secondary", fontSize: "0.75rem" }}
            onClick={() => navigate(`/summary/${sessionId}`)}>
            End →
          </Button>
        </Box>
      </Box>

      {/* Progress */}
      <LinearProgress variant="determinate" value={progressValue}
        sx={{ mb: 3, "& .MuiLinearProgress-bar": { background: "#3b82f6" } }} />

      {/* Question */}
      <Box sx={{
        backgroundColor: "#141414", border: "1px solid #262626",
        borderRadius: 3, p: 3, mb: 2,
      }}>
        <Box sx={{ display: "flex", gap: 1, mb: 2, flexWrap: "wrap" }}>
          <Chip label={qType} size="small" variant="outlined"
            sx={{ borderColor: "#333", color: "text.secondary", fontFamily: "monospace" }} />
          {currentQuestion.difficulty && (
            <Chip label={currentQuestion.difficulty} size="small" variant="outlined"
              sx={{ borderColor: difficultyColor(currentQuestion.difficulty),
                    color: difficultyColor(currentQuestion.difficulty) }} />
          )}
          {currentQuestion.tags && [...currentQuestion.tags].slice(0, 3).map(t => (
            <Chip key={t} label={t} size="small" variant="outlined"
              sx={{ borderColor: "#333", color: "text.secondary" }} />
          ))}
        </Box>

        <Typography variant="h6" sx={{ lineHeight: 1.5, fontWeight: 700, color: "text.primary" }}>
          {currentQuestion.text}
        </Typography>

        {qType === "CODE" && currentQuestion.starterCode && (
          <Box sx={{
            mt: 2, p: 2, borderRadius: 2, backgroundColor: "#0d0d0d",
            border: "1px solid #333",
          }}>
            <Typography variant="caption" sx={{ color: "text.secondary", display: "block", mb: 1 }}>
              Starter code
            </Typography>
            <pre style={{ margin: 0, fontFamily: "monospace", fontSize: "0.85rem", color: "#93c5fd", whiteSpace: "pre-wrap" }}>
              {currentQuestion.starterCode}
            </pre>
          </Box>
        )}
      </Box>

      {/* Answer */}
      <Box sx={{
        backgroundColor: "#141414", border: "1px solid #262626",
        borderRadius: 3, p: 3, mb: 2,
      }}>
        <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
          Your answer
        </Typography>

        {qType === "MCQ" ? (
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1, mt: 1.5 }}>
            {options.map((opt, idx) => (
              <Button
                key={idx}
                variant={selectedOptionIndex === idx ? "contained" : "outlined"}
                onClick={() => setSelectedOptionIndex(idx)}
                disabled={loading}
                sx={{
                  justifyContent: "flex-start", textAlign: "left",
                  py: 1.25, px: 2,
                  border: selectedOptionIndex === idx ? "none" : "1px solid #333",
                  background: selectedOptionIndex === idx
                    ? "#3b82f6"
                    : "transparent",
                }}>
                <Box component="span" sx={{
                  fontFamily: "monospace", mr: 1.5, opacity: 0.6, fontWeight: 700
                }}>
                  {String.fromCharCode(65 + idx)}.
                </Box>
                {opt}
              </Button>
            ))}
          </Box>
        ) : (
          <TextField
            fullWidth multiline
            minRows={qType === "CODE" ? 7 : 4}
            value={answerText}
            onChange={e => setAnswerText(e.target.value)}
            placeholder={qType === "CODE" ? "Write your code here..." : "Type your answer..."}
            disabled={loading}
            sx={{
              mt: 1.5,
              "& textarea": qType === "CODE" ? {
                fontFamily: "monospace", fontSize: "0.875rem", color: "#93c5fd"
              } : {},
            }}
          />
        )}

        {pageError && (
          <Alert severity="error" sx={{ mt: 1.5 }}>{pageError}</Alert>
        )}

        <Box sx={{ display: "flex", gap: 1.5, mt: 2 }}>
          <Button
            variant="contained" onClick={() => handleSubmit(false)} disabled={loading}
            sx={{
              px: 3, background: "#3b82f6",
              "&:hover": { background: "#4f9cff" },
            }}>
            {loading ? <CircularProgress size={20} color="inherit" /> : "Submit"}
          </Button>
          <Button variant="outlined" onClick={() => handleSubmit(true)} disabled={loading}
            sx={{ color: "text.secondary", borderColor: "#333" }}>
            Skip
          </Button>
        </Box>
      </Box>

      {/* Feedback from last answer */}
      {lastFeedback && (
        <Box sx={{
          backgroundColor: "#141414", border: "1px solid #262626",
          borderRadius: 3, p: 3,
        }}>
          <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
            Last answer feedback
          </Typography>

          <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", mt: 1.5, mb: 1.5 }}>
            <Chip
              label={lastFeedback.status}
              size="small"
              sx={{
                fontWeight: 800,
                background: lastFeedback.status === "IN_PROGRESS" ? "#1f1f1f" : "transparent",
                color: lastFeedback.status?.includes("FAIL") ? "#ff4d6a" : "#22d3a0",
                border: "1px solid",
                borderColor: lastFeedback.status?.includes("FAIL") ? "#ff4d6a" : "#22d3a0",
              }}
            />
            {typeof lastFeedback.scoreSoFar === "number" && (
              <Chip
                label={`Score ${lastFeedback.scoreSoFar}/${lastFeedback.maxScoreSoFar}`}
                size="small" variant="outlined"
                sx={{ borderColor: "#333", color: "text.secondary", fontFamily: "monospace" }}
              />
            )}
            {lastFeedback.evaluationSource && (
              <Chip
                label={lastFeedback.evaluationSource === "AI" ? "🤖 AI" : "⚙ Keyword"}
                size="small" variant="outlined"
                sx={{ borderColor: lastFeedback.evaluationSource === "AI" ? "#60a5fa" : "#333",
                      color: lastFeedback.evaluationSource === "AI" ? "#93c5fd" : "text.secondary" }}
              />
            )}
          </Box>

          {lastFeedback.feedback && (
            <Typography variant="body2" sx={{ color: "text.secondary", mb: 1, lineHeight: 1.6 }}>
              {lastFeedback.feedback}
            </Typography>
          )}

          {Array.isArray(lastFeedback.strengths) && lastFeedback.strengths.length > 0 && (
            <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap", mb: 1 }}>
              {lastFeedback.strengths.map((s, i) => (
                <Chip key={i} label={s} size="small"
                  sx={{ backgroundColor: "rgba(34,211,160,0.1)", color: "#22d3a0", border: "1px solid rgba(34,211,160,0.2)" }} />
              ))}
            </Box>
          )}

          {Array.isArray(lastFeedback.missingKeywords) && lastFeedback.missingKeywords.length > 0 && (
            <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap" }}>
              {lastFeedback.missingKeywords.map((k, i) => (
                <Chip key={i} label={k} size="small"
                  sx={{ backgroundColor: "rgba(255,77,106,0.1)", color: "#ff4d6a", border: "1px solid rgba(255,77,106,0.2)" }} />
              ))}
            </Box>
          )}
        </Box>
      )}
    </Container>
  );
}
