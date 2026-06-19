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
import { getSkillGraphBySession, getSummary } from "../api/interviewApi";

export default function SummaryPage() {
  const navigate = useNavigate();
  const { sessionId } = useParams();

  const [summary, setSummary] = useState(null);
  const [skillGraph, setSkillGraph] = useState(null);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        setPageError("");

        const [summaryData, skillGraphData] = await Promise.all([
          getSummary(sessionId),
          getSkillGraphBySession(sessionId).catch(() => null)
        ]);

        setSummary(summaryData);
        setSkillGraph(skillGraphData);
      } catch (error) {
        console.error("Failed to load summary", error);
        setPageError("Failed to load summary.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [sessionId]);

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 5, textAlign: "center" }}>
        <CircularProgress />
      </Container>
    );
  }

  if (pageError || !summary) {
    return (
      <Container maxWidth="md" sx={{ mt: 5, mb: 6 }}>
        <Alert severity="error">{pageError || "Summary not found."}</Alert>
        <Box sx={{ mt: 2 }}>
          <Button variant="contained" onClick={() => navigate("/")}>
            Back Home
          </Button>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 5, mb: 6 }}>
      <Paper elevation={4} sx={{ p: 4, borderRadius: 4 }}>
        <Typography variant="h5" fontWeight={900} sx={{ mb: 2 }}>
          Interview Summary
        </Typography>

        <Typography variant="body1" sx={{ mb: 2 }}>
          Session ID: <b>{summary.sessionId}</b>
        </Typography>

        <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", mb: 3 }}>
          <Chip label={`Score: ${summary.totalScore}/${summary.maxScore}`} />
          <Chip
            label={`Percentage: ${Number(summary.percentage || 0).toFixed(1)}%`}
            variant="outlined"
          />
          {summary.overallScore && <Chip label={summary.overallScore} />}
          {typeof summary.jobFitScore === "number" && (
            <Chip label={`Job fit: ${summary.jobFitScore}`} variant="outlined" />
          )}
        </Box>

        {summary.feedbackSummary && (
          <Alert severity="info" sx={{ mb: 3 }}>
            {summary.feedbackSummary}
          </Alert>
        )}

        {summary.studyPlan && (
          <Box sx={{ mb: 3 }}>
            <Typography fontWeight={800} sx={{ mb: 1 }}>
              Study Plan
            </Typography>
            <Typography>{summary.studyPlan}</Typography>
          </Box>
        )}

        <Divider sx={{ my: 2 }} />

        <Box sx={{ mb: 3 }}>
          <Typography fontWeight={800} sx={{ mb: 1 }}>
            Strengths
          </Typography>
          {summary.strengths?.length ? (
            summary.strengths.map((item, index) => (
              <Chip
                key={`${item}-${index}`}
                label={item}
                color="success"
                sx={{ mr: 1, mb: 1 }}
              />
            ))
          ) : (
            <Typography variant="body2" sx={{ opacity: 0.75 }}>
              No strengths returned.
            </Typography>
          )}
        </Box>

        <Box sx={{ mb: 3 }}>
          <Typography fontWeight={800} sx={{ mb: 1 }}>
            Weaknesses
          </Typography>
          {summary.weaknesses?.length ? (
            summary.weaknesses.map((item, index) => (
              <Chip
                key={`${item}-${index}`}
                label={item}
                variant="outlined"
                color="warning"
                sx={{ mr: 1, mb: 1 }}
              />
            ))
          ) : (
            <Typography variant="body2" sx={{ opacity: 0.75 }}>
              No weaknesses returned.
            </Typography>
          )}
        </Box>

        <Box sx={{ mb: 3 }}>
          <Typography fontWeight={800} sx={{ mb: 1 }}>
            Weak Topics
          </Typography>
          {summary.weakTopics?.length ? (
            summary.weakTopics.map((item, index) => (
              <Chip
                key={`${item}-${index}`}
                label={item}
                variant="outlined"
                sx={{ mr: 1, mb: 1 }}
              />
            ))
          ) : (
            <Typography variant="body2" sx={{ opacity: 0.75 }}>
              No weak topics returned.
            </Typography>
          )}
        </Box>

        {summary.matchedJobSkills?.length > 0 && (
          <Box sx={{ mb: 3 }}>
            <Typography fontWeight={800} sx={{ mb: 1 }}>
              Matched Job Skills
            </Typography>
            {summary.matchedJobSkills.map((item, index) => (
              <Chip
                key={`${item}-${index}`}
                label={item}
                color="success"
                sx={{ mr: 1, mb: 1 }}
              />
            ))}
          </Box>
        )}

        {summary.missingJobSkills?.length > 0 && (
          <Box sx={{ mb: 3 }}>
            <Typography fontWeight={800} sx={{ mb: 1 }}>
              Missing Job Skills
            </Typography>
            {summary.missingJobSkills.map((item, index) => (
              <Chip
                key={`${item}-${index}`}
                label={item}
                variant="outlined"
                color="warning"
                sx={{ mr: 1, mb: 1 }}
              />
            ))}
          </Box>
        )}

        {summary.diagnosis && (
          <Box sx={{ mb: 3 }}>
            <Typography fontWeight={800} sx={{ mb: 1 }}>
              Diagnosis
            </Typography>

            {summary.diagnosis.overallAssessment && (
              <Typography sx={{ mb: 1 }}>
                <b>Assessment:</b> {summary.diagnosis.overallAssessment}
              </Typography>
            )}

            <Typography sx={{ mb: 1 }}>
              <b>Overall score:</b> {summary.diagnosis.overallScore}
            </Typography>

            {summary.diagnosis.primaryWeakAreas?.length > 0 && (
              <Typography sx={{ mb: 1 }}>
                <b>Primary weak areas:</b>{" "}
                {summary.diagnosis.primaryWeakAreas.join(", ")}
              </Typography>
            )}

            {summary.diagnosis.criticalIssues?.length > 0 && (
              <Typography sx={{ mb: 1 }}>
                <b>Critical issues:</b>{" "}
                {summary.diagnosis.criticalIssues.join(", ")}
              </Typography>
            )}

            {summary.diagnosis.suggestedStudyPlan?.length > 0 && (
              <Typography sx={{ mb: 1 }}>
                <b>Suggested study plan:</b>{" "}
                {summary.diagnosis.suggestedStudyPlan.join(", ")}
              </Typography>
            )}
          </Box>
        )}

        {skillGraph && (
          <Box sx={{ mb: 3 }}>
            <Typography fontWeight={800} sx={{ mb: 1 }}>
              Skill Graph
            </Typography>

            <Typography sx={{ mb: 1 }}>
              <b>Overall average:</b> {skillGraph.overallAverage}
            </Typography>

            {skillGraph.strongestSkills?.length > 0 && (
              <Typography sx={{ mb: 1 }}>
                <b>Strongest skills:</b> {skillGraph.strongestSkills.join(", ")}
              </Typography>
            )}

            {skillGraph.weakestSkills?.length > 0 && (
              <Typography sx={{ mb: 1 }}>
                <b>Weakest skills:</b> {skillGraph.weakestSkills.join(", ")}
              </Typography>
            )}

            {skillGraph.skills?.length > 0 && (
              <Box sx={{ mt: 1 }}>
                {skillGraph.skills.map((skill, index) => (
                  <Chip
                    key={`${skill.skill}-${index}`}
                    label={`${skill.skill}: ${skill.score}`}
                    variant="outlined"
                    sx={{ mr: 1, mb: 1 }}
                  />
                ))}
              </Box>
            )}
          </Box>
        )}

        <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap" }}>
          <Button variant="contained" onClick={() => navigate("/")}>
            Back Home
          </Button>

          <Button variant="outlined" onClick={() => navigate("/history")}>
            Go to History
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}