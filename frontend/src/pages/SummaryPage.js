import React, { useEffect, useState } from "react";
import {
  Alert, Box, Button, Chip, CircularProgress,
  Container, Divider, Typography
} from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";
import { getSkillGraphBySession, getSummary, resetSkillGraph } from "../api/interviewApi";
import { getGuestId } from "../utils/guestId";

function ScoreRing({ percent }) {
  const color = percent >= 70 ? "#22d3a0" : percent >= 50 ? "#f59e0b" : "#ff4d6a";
  const label = percent >= 70 ? "Passed" : percent >= 50 ? "Needs Work" : "Failed";
  return (
    <Box sx={{ textAlign: "center", mb: 4 }}>
      <Box sx={{
        display: "inline-flex", flexDirection: "column", alignItems: "center",
        justifyContent: "center", width: 140, height: 140, borderRadius: "50%",
        border: `3px solid ${color}`,
        boxShadow: `0 0 30px ${color}33`,
        mb: 1.5,
      }}>
        <Typography sx={{
          fontFamily: "monospace", fontSize: "2.8rem",
          fontWeight: 900, color, lineHeight: 1
        }}>
          {percent}
        </Typography>
        <Typography sx={{ fontSize: "0.75rem", color: "text.secondary", fontWeight: 700 }}>/ 100</Typography>
      </Box>
      <Box>
        <Chip label={label} size="small"
          sx={{ fontWeight: 800, color, border: `1px solid ${color}`, backgroundColor: `${color}15` }} />
      </Box>
    </Box>
  );
}

export default function SummaryPage() {
  const navigate = useNavigate();
  const { sessionId } = useParams();

  const [summary, setSummary] = useState(null);
  const [skillGraph, setSkillGraph] = useState(null);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");
  const [resetDone, setResetDone] = useState(false);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const [s, g] = await Promise.all([
          getSummary(sessionId),
          getSkillGraphBySession(sessionId).catch(() => null),
        ]);
        setSummary(s);
        setSkillGraph(g);
      } catch {
        setPageError("Failed to load summary.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [sessionId]);

  const handleReset = async () => {
    try {
      await resetSkillGraph(getGuestId());
      setSkillGraph(null);
      setResetDone(true);
    } catch {}
  };

  if (loading) return (
    <Container maxWidth="md" sx={{ pt: 8, textAlign: "center" }}>
      <CircularProgress />
    </Container>
  );

  if (pageError || !summary) return (
    <Container maxWidth="md" sx={{ pt: 6 }}>
      <Alert severity="error">{pageError || "Summary not found."}</Alert>
      <Button variant="contained" sx={{ mt: 2 }} onClick={() => navigate("/")}>Back Home</Button>
    </Container>
  );

  const percent = Number(summary.percentage || 0);
  const passed = percent >= 70;

  return (
    <Container maxWidth="md" sx={{ pt: 5, pb: 8 }}>

      {/* Header */}
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 4 }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>
          Interview<Box component="span" sx={{ color: "primary.main" }}>IQ</Box>
          <Box component="span" sx={{ color: "text.secondary", fontWeight: 400, fontSize: "0.9rem", ml: 2 }}>
            Summary
          </Box>
        </Typography>
        <Typography sx={{ fontFamily: "monospace", fontSize: "0.75rem", color: "text.secondary" }}>
          {sessionId}
        </Typography>
      </Box>

      {/* Score */}
      <ScoreRing percent={Math.round(percent)} />

      {/* Score chips */}
      <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", justifyContent: "center", mb: 4 }}>
        <Chip label={`${summary.totalScore} / ${summary.maxScore} pts`}
          sx={{ fontFamily: "monospace", fontWeight: 700, border: "1px solid #2a2a40", color: "text.secondary" }} />
        {summary.feedbackSource && (
          <Chip
            label={summary.feedbackSource === "ai" ? "🤖 AI Feedback" : "⚙ Rule-based"}
            sx={{ fontWeight: 700, border: "1px solid #2a2a40", color: "text.secondary" }} />
        )}
      </Box>

      {/* AI Summary */}
      {summary.feedbackSummary && (
        <Box sx={{
          backgroundColor: "#0f0f1a", border: "1px solid #1e1e30",
          borderRadius: 3, p: 3, mb: 3,
        }}>
          <Typography variant="overline" sx={{ color: "primary.main", letterSpacing: "0.1em" }}>
            AI Assessment
          </Typography>
          <Typography variant="body1" sx={{ mt: 1, color: "text.primary", lineHeight: 1.7 }}>
            {summary.feedbackSummary}
          </Typography>
        </Box>
      )}

      {/* Study Plan */}
      {summary.studyPlan && (
        <Box sx={{
          backgroundColor: "#0f0f1a", border: "1px solid #1e1e30",
          borderRadius: 3, p: 3, mb: 3,
        }}>
          <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
            Study Plan
          </Typography>
          <Typography variant="body2" sx={{ mt: 1, color: "text.secondary", lineHeight: 1.7, whiteSpace: "pre-line" }}>
            {summary.studyPlan}
          </Typography>
        </Box>
      )}

      <Divider sx={{ my: 3 }} />

      {/* Strengths */}
      {summary.strengths?.length > 0 && (
        <Box sx={{ mb: 3 }}>
          <Typography variant="overline" sx={{ color: "#22d3a0", letterSpacing: "0.1em" }}>
            Strengths
          </Typography>
          <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap", mt: 1 }}>
            {summary.strengths.map((s, i) => (
              <Chip key={i} label={s} size="small"
                sx={{ backgroundColor: "rgba(34,211,160,0.1)", color: "#22d3a0", border: "1px solid rgba(34,211,160,0.25)" }} />
            ))}
          </Box>
        </Box>
      )}

      {/* Weaknesses */}
      {summary.weaknesses?.length > 0 && (
        <Box sx={{ mb: 3 }}>
          <Typography variant="overline" sx={{ color: "#f59e0b", letterSpacing: "0.1em" }}>
            Needs Work
          </Typography>
          <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap", mt: 1 }}>
            {summary.weaknesses.map((w, i) => (
              <Chip key={i} label={w} size="small"
                sx={{ backgroundColor: "rgba(245,158,11,0.1)", color: "#f59e0b", border: "1px solid rgba(245,158,11,0.25)" }} />
            ))}
          </Box>
        </Box>
      )}

      {/* Weak Topics */}
      {summary.weakTopics?.length > 0 && (
        <Box sx={{ mb: 3 }}>
          <Typography variant="overline" sx={{ color: "#ff4d6a", letterSpacing: "0.1em" }}>
            Weak Topics
          </Typography>
          <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap", mt: 1 }}>
            {summary.weakTopics.map((t, i) => (
              <Chip key={i} label={t} size="small"
                sx={{ backgroundColor: "rgba(255,77,106,0.1)", color: "#ff4d6a", border: "1px solid rgba(255,77,106,0.2)" }} />
            ))}
          </Box>
        </Box>
      )}

      {/* Diagnosis details */}
      {summary.diagnosis && (
        <Box sx={{
          backgroundColor: "#0f0f1a", border: "1px solid #1e1e30",
          borderRadius: 3, p: 3, mb: 3,
        }}>
          <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
            Diagnosis
          </Typography>
          {summary.diagnosis.overallAssessment && (
            <Typography variant="body2" sx={{ mt: 1, color: "text.secondary", lineHeight: 1.7 }}>
              {summary.diagnosis.overallAssessment}
            </Typography>
          )}
          {summary.diagnosis.suggestedStudyPlan?.length > 0 && (
            <Box sx={{ mt: 1.5 }}>
              {summary.diagnosis.suggestedStudyPlan.map((line, i) => (
                <Typography key={i} variant="body2" sx={{ color: "text.secondary", display: "flex", gap: 1, mb: 0.5 }}>
                  <Box component="span" sx={{ color: "primary.main", fontWeight: 800 }}>→</Box>
                  {line}
                </Typography>
              ))}
            </Box>
          )}
        </Box>
      )}

      {/* Skill Graph */}
      {skillGraph?.skills?.length > 0 && (
        <Box sx={{
          backgroundColor: "#0f0f1a", border: "1px solid #1e1e30",
          borderRadius: 3, p: 3, mb: 3,
        }}>
          <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 1.5 }}>
            <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
              Skill Graph — avg {skillGraph.overallAverage}
            </Typography>
          </Box>
          <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap" }}>
            {skillGraph.skills.map((s, i) => {
              const c = s.score >= 70 ? "#22d3a0" : s.score >= 50 ? "#f59e0b" : "#ff4d6a";
              return (
                <Chip key={i}
                  label={`${s.skill} · ${s.score}`}
                  size="small"
                  sx={{ fontFamily: "monospace", color: c, border: `1px solid ${c}33`, backgroundColor: `${c}10` }}
                />
              );
            })}
          </Box>
        </Box>
      )}

      {/* Job fit */}
      {(summary.matchedJobSkills?.length > 0 || summary.missingJobSkills?.length > 0) && (
        <Box sx={{
          backgroundColor: "#0f0f1a", border: "1px solid #1e1e30",
          borderRadius: 3, p: 3, mb: 3,
        }}>
          <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
            Job Fit
          </Typography>
          {summary.matchedJobSkills?.length > 0 && (
            <Box sx={{ mt: 1, display: "flex", gap: 0.75, flexWrap: "wrap" }}>
              {summary.matchedJobSkills.map((s, i) => (
                <Chip key={i} label={s} size="small"
                  sx={{ backgroundColor: "rgba(34,211,160,0.1)", color: "#22d3a0", border: "1px solid rgba(34,211,160,0.25)" }} />
              ))}
            </Box>
          )}
          {summary.missingJobSkills?.length > 0 && (
            <Box sx={{ mt: 1, display: "flex", gap: 0.75, flexWrap: "wrap" }}>
              {summary.missingJobSkills.map((s, i) => (
                <Chip key={i} label={s} size="small"
                  sx={{ backgroundColor: "rgba(245,158,11,0.1)", color: "#f59e0b", border: "1px solid rgba(245,158,11,0.25)" }} />
              ))}
            </Box>
          )}
        </Box>
      )}

      {/* Actions */}
      <Box sx={{ display: "flex", gap: 1.5, flexWrap: "wrap", mt: 2 }}>
        <Button variant="contained" onClick={() => navigate("/")}
          sx={{ background: "linear-gradient(135deg, #7c6fff, #5a4fd4)" }}>
          New session
        </Button>
        <Button variant="outlined" onClick={() => navigate("/history")}
          sx={{ borderColor: "#2a2a40", color: "text.secondary" }}>
          History
        </Button>
        {(skillGraph || resetDone) && (
          <Button variant="outlined" color="error" onClick={handleReset} disabled={resetDone}
            sx={{ borderColor: resetDone ? "#2a2a40" : "#ff4d6a33", color: resetDone ? "text.secondary" : "#ff4d6a" }}>
            {resetDone ? "Graph reset" : "Reset skill graph"}
          </Button>
        )}
      </Box>
    </Container>
  );
}
