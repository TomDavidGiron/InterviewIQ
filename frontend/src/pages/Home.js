import React, { useEffect, useMemo, useState } from "react";
import {
  Box, Button, Chip, CircularProgress, Container,
  Divider, Link, TextField, Typography
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { getTopics, startInterview, startJobSpecificInterview } from "../api/interviewApi";
import { extractTextFromImage } from "../api/ocrApi";
import { getGuestId } from "../utils/guestId";

function Header() {
  return (
    <Box sx={{ mb: 5 }}>
      <Typography variant="h4" sx={{ fontWeight: 900, letterSpacing: "-0.03em", color: "#e5e7eb" }}>
        Interview<Box component="span" sx={{ color: "primary.main" }}>IQ</Box>
      </Typography>
      <Typography variant="caption" sx={{ color: "text.secondary", letterSpacing: "0.06em", textTransform: "uppercase" }}>
        AI Interview Simulator
      </Typography>
    </Box>
  );
}

export default function Home() {
  const navigate = useNavigate();

  const [jobUrl, setJobUrl] = useState("");
  const [jobText, setJobText] = useState("");
  const [jobImage, setJobImage] = useState(null);

  const [showText, setShowText] = useState(false);
  const [showOcr, setShowOcr] = useState(false);
  const [showAllTopics, setShowAllTopics] = useState(false);

  const [topics, setTopics] = useState([]);
  const [selectedTopic, setSelectedTopic] = useState("");

  const [topicsLoading, setTopicsLoading] = useState(false);
  const [startLoading, setStartLoading] = useState(false);
  const [ocrLoading, setOcrLoading] = useState(false);
  const [pageError, setPageError] = useState("");

  useEffect(() => {
    const load = async () => {
      setTopicsLoading(true);
      try {
        const data = await getTopics();
        setTopics(Array.isArray(data) ? data : []);
      } catch {
        setTopics([]);
      } finally {
        setTopicsLoading(false);
      }
    };
    load();
  }, []);

  const topTopics = useMemo(() => topics.slice(0, 16), [topics]);

  const isBlockedJobSite = useMemo(() => {
    const url = jobUrl.trim().toLowerCase();
    if (!url) return false;
    return url.includes("linkedin.com") || url.includes("indeed.com");
  }, [jobUrl]);

  const handleExtractTextFromImage = async () => {
    if (!jobImage) return;
    setOcrLoading(true);
    try {
      const text = await extractTextFromImage(jobImage);
      setJobText(typeof text === "string" ? text : "");
      setShowText(true);
    } catch {
      setPageError("OCR failed. Please try again.");
    } finally {
      setOcrLoading(false);
    }
  };

  const handleStart = async () => {
    setStartLoading(true);
    setPageError("");
    try {
      const url = jobUrl.trim();
      const text = jobText.trim();
      const userId = getGuestId();

      let response;
      if (text) {
        response = await startJobSpecificInterview({ sourceType: "TEXT", payload: text, topic: selectedTopic || null, userId });
      } else if (url) {
        response = await startJobSpecificInterview({ sourceType: "URL", payload: url, topic: selectedTopic || null, userId });
      } else {
        response = await startInterview({ sourceType: null, payload: "", topic: selectedTopic || null, userId });
      }

      const { sessionId, firstQuestion } = response || {};
      if (!sessionId) { setPageError("No session returned. Try again."); return; }
      navigate(`/interview/${sessionId}`, { state: { sessionId, firstQuestion } });
    } catch {
      setPageError("Failed to start session. Check backend connection.");
    } finally {
      setStartLoading(false);
    }
  };

  return (
    <Container maxWidth="lg" sx={{ pt: 6, pb: 8 }}>
      <Header />

      {/* Hero */}
      <Box sx={{ mb: 5 }}>
        <Typography variant="h3" sx={{ mb: 1, lineHeight: 1.1 }}>
          Practice like it&apos;s{" "}
          <Box component="span" sx={{ color: "#60a5fa" }}>
            the real thing.
          </Box>
        </Typography>
        <Typography variant="body1" sx={{ color: "text.secondary" }}>
          10 adaptive questions · AI evaluation · instant feedback
        </Typography>
      </Box>

      {/* Two-column cards */}
      <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", md: "1fr 1fr" }, gap: 3, mb: 3 }}>

        {/* Job Input */}
        <Box sx={{ backgroundColor: "#141414", border: "1px solid #262626", borderRadius: 3, p: 3 }}>
          <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
            Tailor to a job (optional)
          </Typography>

          <TextField
            fullWidth size="small"
            placeholder="Paste a job URL..."
            value={jobUrl}
            onChange={e => setJobUrl(e.target.value)}
            sx={{ mt: 1.5, mb: 1 }}
          />

          {isBlockedJobSite && (
            <Typography variant="caption" sx={{ display: "block", color: "#f59e0b", mb: 1 }}>
              LinkedIn and Indeed block automated scraping, this link probably won&apos;t work.
              Use{" "}
              <Link component="button" type="button" onClick={() => setShowOcr(true)}
                sx={{ color: "#f59e0b", fontWeight: 700 }}>
                a screenshot (OCR)
              </Link>
              {" "}or{" "}
              <Link component="button" type="button" onClick={() => setShowText(true)}
                sx={{ color: "#f59e0b", fontWeight: 700 }}>
                paste the text
              </Link>
              {" "}instead.
            </Typography>
          )}

          <Box sx={{ display: "flex", gap: 1.5 }}>
            <Button size="small" variant="text" sx={{ color: "text.secondary", fontSize: "0.78rem" }}
              onClick={() => setShowText(p => !p)}>
              {showText ? "Hide text" : "Paste text instead"}
            </Button>
            <Button size="small" variant="text" sx={{ color: "text.secondary", fontSize: "0.78rem" }}
              onClick={() => setShowOcr(p => !p)}>
              {showOcr ? "Hide OCR" : "Upload screenshot"}
            </Button>
          </Box>

          {showText && (
            <TextField
              fullWidth multiline rows={4} size="small"
              placeholder="Paste the job description..."
              value={jobText}
              onChange={e => setJobText(e.target.value)}
              sx={{ mt: 1.5 }}
            />
          )}

          {showOcr && (
            <Box sx={{ display: "flex", gap: 1.5, alignItems: "center", mt: 1.5, flexWrap: "wrap" }}>
              <Button variant="outlined" size="small" component="label">
                Choose image
                <input type="file" accept="image/*" hidden onChange={e => setJobImage(e.target.files?.[0] || null)} />
              </Button>
              <Button variant="outlined" size="small" onClick={handleExtractTextFromImage}
                disabled={!jobImage || ocrLoading}>
                {ocrLoading ? "Extracting..." : "Extract text"}
              </Button>
              {jobImage && (
                <Typography variant="caption" sx={{ color: "text.secondary" }}>{jobImage.name}</Typography>
              )}
            </Box>
          )}
        </Box>

        {/* Topic Picker */}
        <Box sx={{ backgroundColor: "#141414", border: "1px solid #262626", borderRadius: 3, p: 3 }}>
          <Typography variant="overline" sx={{ color: "text.secondary", letterSpacing: "0.1em" }}>
            Topic (optional)
          </Typography>

          {topicsLoading ? (
            <Box sx={{ display: "flex", alignItems: "center", gap: 1, mt: 1.5 }}>
              <CircularProgress size={14} />
              <Typography variant="caption" sx={{ color: "text.secondary" }}>Loading topics...</Typography>
            </Box>
          ) : (
            <>
              <Box sx={{ display: "flex", gap: 0.75, flexWrap: "wrap", mt: 1.5 }}>
                <Chip
                  label="General" size="small" clickable
                  onClick={() => setSelectedTopic("")}
                  color={selectedTopic === "" ? "primary" : "default"}
                  variant={selectedTopic === "" ? "filled" : "outlined"}
                />
                {topTopics.map(t => (
                  <Chip
                    key={t} label={t} size="small" clickable
                    onClick={() => setSelectedTopic(t)}
                    color={selectedTopic === t ? "primary" : "default"}
                    variant={selectedTopic === t ? "filled" : "outlined"}
                  />
                ))}
              </Box>

              {topics.length > 16 && (
                <>
                  <Button
                    size="small" variant="text"
                    sx={{ mt: 1, color: "text.secondary", fontSize: "0.78rem" }}
                    onClick={() => setShowAllTopics(p => !p)}>
                    {showAllTopics ? "Show less" : `+ ${topics.length - 16} more topics`}
                  </Button>
                  {showAllTopics && (
                    <Box sx={{
                      display: "flex", gap: 0.75, flexWrap: "wrap", mt: 1,
                      maxHeight: 140, overflowY: "auto", p: 1,
                      border: "1px solid #262626", borderRadius: 2,
                    }}>
                      {topics.slice(16).map(t => (
                        <Chip
                          key={t} label={t} size="small" clickable
                          onClick={() => setSelectedTopic(t)}
                          color={selectedTopic === t ? "primary" : "default"}
                          variant={selectedTopic === t ? "filled" : "outlined"}
                        />
                      ))}
                    </Box>
                  )}
                </>
              )}
            </>
          )}
        </Box>

      </Box>

      {pageError && (
        <Typography variant="body2" sx={{ color: "error.main", mb: 2 }}>{pageError}</Typography>
      )}

      <Button
        fullWidth variant="contained" size="large"
        onClick={handleStart} disabled={startLoading}
        sx={{
          py: 1.5, fontSize: "1rem", fontWeight: 800,
          background: "#3b82f6",
          "&:hover": { background: "#4f9cff" },
        }}>
        {startLoading ? <CircularProgress size={22} color="inherit" /> : "Start session →"}
      </Button>

      <Divider sx={{ my: 4 }} />

      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <Typography variant="caption" sx={{ color: "text.secondary" }}>
          Built by{" "}
          <Link href="https://www.linkedin.com/in/tom-giron-733921198/" target="_blank"
            sx={{ color: "primary.main", textDecoration: "none", fontWeight: 700 }}>
            Tom Giron
          </Link>
        </Typography>
        <Button size="small" variant="text" sx={{ color: "text.secondary", fontSize: "0.75rem" }}
          onClick={() => navigate("/history")}>
          View history
        </Button>
      </Box>
    </Container>
  );
}
