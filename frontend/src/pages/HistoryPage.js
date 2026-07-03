import React, { useEffect, useState } from "react";
import {
  Alert, Box, Button, Chip, CircularProgress,
  Container, Typography
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { getHistory } from "../api/interviewApi";
import { getGuestId } from "../utils/guestId";

function formatDate(value) {
  if (!value) return "—";
  try { return new Date(value).toLocaleString(); }
  catch { return value; }
}

function scoreColor(pct) {
  if (pct >= 70) return "#22d3a0";
  if (pct >= 50) return "#f59e0b";
  return "#ff4d6a";
}

export default function HistoryPage() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await getHistory(0, 20, getGuestId());
        setItems(Array.isArray(data) ? data : []);
      } catch {
        setPageError("Failed to load history.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  return (
    <Container maxWidth="md" sx={{ pt: 5, pb: 8 }}>

      {/* Header */}
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 4 }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>
          Interview<Box component="span" sx={{ color: "primary.main" }}>IQ</Box>
          <Box component="span" sx={{ color: "text.secondary", fontWeight: 400, fontSize: "0.9rem", ml: 2 }}>
            History
          </Box>
        </Typography>
        <Button size="small" variant="text" sx={{ color: "text.secondary", fontSize: "0.75rem" }}
          onClick={() => navigate("/")}>
          ← Home
        </Button>
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", py: 8 }}><CircularProgress /></Box>
      ) : pageError ? (
        <Alert severity="error">{pageError}</Alert>
      ) : items.length === 0 ? (
        <Box sx={{
          textAlign: "center", py: 8,
          color: "text.secondary", border: "1px dashed #1e1e30", borderRadius: 3,
        }}>
          <Typography variant="h6" sx={{ mb: 1, fontWeight: 700 }}>No sessions yet</Typography>
          <Typography variant="body2">Complete your first interview to see history here.</Typography>
          <Button variant="contained" sx={{ mt: 3, background: "linear-gradient(135deg, #7c6fff, #5a4fd4)" }}
            onClick={() => navigate("/")}>
            Start now
          </Button>
        </Box>
      ) : (
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          {items.map(item => {
            const pct = item.totalPossibleScore > 0
              ? Math.round((item.score / item.totalPossibleScore) * 100)
              : 0;
            const c = scoreColor(pct);

            return (
              <Box key={item.sessionId} sx={{
                backgroundColor: "#0f0f1a", border: "1px solid #1e1e30",
                borderRadius: 3, p: 3,
                "&:hover": { borderColor: "#2a2a40" },
              }}>
                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", mb: 1.5 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "text.primary" }}>
                    {item.roleHint || "General interview"}
                  </Typography>
                  <Typography sx={{
                    fontFamily: "monospace", fontWeight: 900, fontSize: "1.25rem",
                    color: c, lineHeight: 1
                  }}>
                    {pct}%
                  </Typography>
                </Box>

                <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap", mb: 1.5 }}>
                  <Chip label={item.status || "UNKNOWN"} size="small"
                    sx={{
                      fontWeight: 700,
                      color: item.status === "PASSED" ? "#22d3a0" : item.status === "FAILED" ? "#ff4d6a" : "text.secondary",
                      border: "1px solid",
                      borderColor: item.status === "PASSED" ? "rgba(34,211,160,0.3)" : item.status === "FAILED" ? "rgba(255,77,106,0.3)" : "#2a2a40",
                      backgroundColor: item.status === "PASSED" ? "rgba(34,211,160,0.08)" : item.status === "FAILED" ? "rgba(255,77,106,0.08)" : "transparent",
                    }} />
                  <Chip label={`${item.score} / ${item.totalPossibleScore} pts`} size="small"
                    sx={{ fontFamily: "monospace", border: "1px solid #2a2a40", color: "text.secondary" }} />
                  {item.levelHint && (
                    <Chip label={item.levelHint} size="small"
                      sx={{ border: "1px solid #2a2a40", color: "text.secondary" }} />
                  )}
                </Box>

                <Box sx={{ display: "flex", gap: 3, flexWrap: "wrap", mb: 1.5 }}>
                  <Typography variant="caption" sx={{ color: "text.secondary" }}>
                    <Box component="span" sx={{ color: "#3d3d5a", mr: 0.5 }}>started</Box>
                    {formatDate(item.createdAt)}
                  </Typography>
                  {item.endedAt && (
                    <Typography variant="caption" sx={{ color: "text.secondary" }}>
                      <Box component="span" sx={{ color: "#3d3d5a", mr: 0.5 }}>ended</Box>
                      {formatDate(item.endedAt)}
                    </Typography>
                  )}
                </Box>

                {item.failReason && (
                  <Typography variant="caption" sx={{ color: "#ff4d6a", display: "block", mb: 1.5 }}>
                    {item.failReason}
                  </Typography>
                )}

                <Box sx={{ display: "flex", gap: 1 }}>
                  <Button size="small" variant="contained"
                    sx={{ background: "linear-gradient(135deg, #7c6fff, #5a4fd4)", fontSize: "0.78rem" }}
                    onClick={() => navigate(`/summary/${item.sessionId}`)}>
                    Summary
                  </Button>
                  <Button size="small" variant="outlined"
                    sx={{ borderColor: "#2a2a40", color: "text.secondary", fontSize: "0.78rem" }}
                    onClick={() => navigate(`/history/${item.sessionId}`)}>
                    Details
                  </Button>
                </Box>
              </Box>
            );
          })}
        </Box>
      )}
    </Container>
  );
}
