import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Container,
  Divider,
  LinearProgress,
  Paper,
  TextField,
  Typography
} from "@mui/material";
import { submitAnswer } from "../api/interviewApi";

const TOTAL_QUESTIONS_FALLBACK = 10;

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

  useEffect(() => {
    if (!sessionId) {
      navigate("/");
    }
  }, [sessionId, navigate]);

  useEffect(() => {
    setAnswerText("");
    setSelectedOptionIndex(null);
  }, [currentQuestion?.id]);

  const qType = currentQuestion?.type || "OPEN";
  const options = Array.isArray(currentQuestion?.options)
    ? currentQuestion.options
    : [];

  const questionIndex = lastFeedback?.questionIndex ?? 0;
  const totalQuestions =
    lastFeedback?.totalQuestions ?? TOTAL_QUESTIONS_FALLBACK;

  const progressValue = useMemo(() => {
    if (!totalQuestions) return 0;
    return Math.min(100, (questionIndex / totalQuestions) * 100);
  }, [questionIndex, totalQuestions]);

  const handleSubmitAnswer = async (skip = false) => {
    if (!sessionId) return;

    const payload =
      qType === "MCQ"
        ? {
            answerText:
              selectedOptionIndex === null ? "" : String(selectedOptionIndex)
          }
        : {
            answerText: skip ? "" : answerText.trim()
          };

    if (!skip && !payload.answerText) {
      setPageError("Please enter an answer before submitting.");
      return;
    }

    try {
      setLoading(true);
      setPageError("");

      const response = await submitAnswer(sessionId, payload);

      setLastFeedback(response);

      if (
        response?.status === "COMPLETED" ||
        response?.status === "FAILED"
      ) {
        navigate(`/summary/${sessionId}`);
        return;
      }

      if (response?.question) {
        setCurrentQuestion(response.question);
      } else {
        navigate(`/summary/${sessionId}`);
      }
    } catch (error) {
      console.error("Failed to submit answer", error);
      setPageError("Failed to submit answer.");
    } finally {
      setLoading(false);
    }
  };

  if (!sessionId) {
    return null;
  }

  if (!currentQuestion) {
    return (
      <Container maxWidth="md" sx={{ mt: 5, mb: 6 }}>
        <Alert severity="warning" sx={{ mb: 2 }}>
          No question was loaded for this session.
        </Alert>

        <Button variant="contained" onClick={() => navigate("/")}>
          Back Home
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 5, mb: 6 }}>
      <Paper elevation={4} sx={{ p: 2.5, mb: 2.5, borderRadius: 4 }}>
        <Typography variant="h5" fontWeight={900} sx={{ mb: 1 }}>
          Interview Session
        </Typography>

        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 1,
            flexWrap: "wrap",
            gap: 1
          }}
        >
          <Typography variant="body2" sx={{ opacity: 0.8 }}>
            Question <b>{Math.min(questionIndex + 1, totalQuestions)}</b> /{" "}
            {totalQuestions}
          </Typography>

          <Typography variant="body2" sx={{ opacity: 0.8 }}>
            Session: <b>{sessionId}</b>
          </Typography>
        </Box>

        <LinearProgress
          variant="determinate"
          value={progressValue}
          sx={{ height: 10, borderRadius: 999 }}
        />
      </Paper>

      <Paper elevation={6} sx={{ p: 3, mb: 2.5, borderRadius: 4 }}>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 2,
            mb: 1,
            flexWrap: "wrap"
          }}
        >
          <Typography variant="h6" fontWeight={900}>
            {currentQuestion.text}
          </Typography>

          <Chip label={qType} />
        </Box>

        {qType === "CODE" && currentQuestion?.starterCode && (
          <Paper
            variant="outlined"
            sx={{
              p: 2,
              mt: 2,
              borderRadius: 2,
              backgroundColor: "rgba(0,0,0,0.02)"
            }}
          >
            <Typography variant="subtitle2" sx={{ mb: 1 }}>
              Starter Code
            </Typography>
            <pre style={{ margin: 0, whiteSpace: "pre-wrap" }}>
              {currentQuestion.starterCode}
            </pre>
          </Paper>
        )}
      </Paper>

      <Paper elevation={6} sx={{ p: 3, mb: 2.5, borderRadius: 4 }}>
        <Typography variant="h6" fontWeight={900} sx={{ mb: 1 }}>
          Your answer
        </Typography>

        {qType === "MCQ" ? (
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
            {options.length === 0 && (
              <Typography color="error">
                No options provided for this MCQ.
              </Typography>
            )}

            {options.map((opt, idx) => (
              <Button
                key={idx}
                variant={selectedOptionIndex === idx ? "contained" : "outlined"}
                onClick={() => setSelectedOptionIndex(idx)}
                disabled={loading}
                sx={{ justifyContent: "flex-start", textTransform: "none" }}
              >
                {opt}
              </Button>
            ))}
          </Box>
        ) : (
          <TextField
            fullWidth
            multiline
            minRows={qType === "CODE" ? 7 : 4}
            value={answerText}
            onChange={(e) => setAnswerText(e.target.value)}
            placeholder={
              qType === "CODE"
                ? "Type your code / query here..."
                : "Type your answer..."
            }
            sx={{
              mt: 1,
              "& textarea":
                qType === "CODE" ? { fontFamily: "monospace" } : undefined
            }}
          />
        )}

        {pageError && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {pageError}
          </Alert>
        )}

        <Box sx={{ display: "flex", gap: 2, mt: 2, flexWrap: "wrap" }}>
          <Button
            variant="contained"
            onClick={() => handleSubmitAnswer(false)}
            disabled={loading}
          >
            {loading ? <CircularProgress size={22} /> : "Submit"}
          </Button>

          <Button
            variant="outlined"
            onClick={() => handleSubmitAnswer(true)}
            disabled={loading}
          >
            Skip
          </Button>

          <Button
            variant="text"
            onClick={() => navigate(`/summary/${sessionId}`)}
            disabled={loading}
          >
            End interview
          </Button>
        </Box>
      </Paper>

      {lastFeedback && (
        <Paper elevation={4} sx={{ p: 2.5, mb: 2.5, borderRadius: 4 }}>
          <Typography variant="h6" fontWeight={900}>
            Previous answer feedback
          </Typography>

          <Typography variant="body2" sx={{ opacity: 0.75, mt: 0.5 }}>
            This feedback is for the last submitted answer.
          </Typography>

          <Divider sx={{ my: 1.5 }} />

          <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", mb: 1 }}>
            <Chip label={`Status: ${lastFeedback.status}`} />
            {typeof lastFeedback.scoreSoFar === "number" &&
              typeof lastFeedback.maxScoreSoFar === "number" && (
                <Chip
                  label={`Score so far: ${lastFeedback.scoreSoFar}/${lastFeedback.maxScoreSoFar}`}
                  variant="outlined"
                />
              )}
            {lastFeedback.evaluationSource && (
              <Chip
                label={`Source: ${lastFeedback.evaluationSource}`}
                variant="outlined"
              />
            )}
            {lastFeedback.agentAction && (
              <Chip
                label={`Agent: ${lastFeedback.agentAction}`}
                variant="outlined"
              />
            )}
          </Box>

          {lastFeedback.feedback && (
            <Typography sx={{ mt: 1 }}>
              <b>Feedback:</b> {lastFeedback.feedback}
            </Typography>
          )}

          {lastFeedback.failReason && (
            <Typography sx={{ mt: 1 }}>
              <b>Reason:</b> {lastFeedback.failReason}
            </Typography>
          )}

          {lastFeedback.agentReason && (
            <Typography sx={{ mt: 1 }}>
              <b>Agent reason:</b> {lastFeedback.agentReason}
            </Typography>
          )}

          {Array.isArray(lastFeedback.strengths) &&
            lastFeedback.strengths.length > 0 && (
              <Typography sx={{ mt: 1 }}>
                <b>Strengths:</b> {lastFeedback.strengths.join(", ")}
              </Typography>
            )}

          {Array.isArray(lastFeedback.missingKeywords) &&
            lastFeedback.missingKeywords.length > 0 && (
              <Typography sx={{ mt: 1 }}>
                <b>Missing Keywords:</b>{" "}
                {lastFeedback.missingKeywords.join(", ")}
              </Typography>
            )}

          {lastFeedback.weakTags &&
            Object.keys(lastFeedback.weakTags).length > 0 && (
              <Typography sx={{ mt: 1 }}>
                <b>Weak Tags:</b> {Object.keys(lastFeedback.weakTags).join(", ")}
              </Typography>
            )}
        </Paper>
      )}
    </Container>
  );
}