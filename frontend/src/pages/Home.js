import React, { useEffect, useMemo, useState } from "react";
import {
  Container,
  Typography,
  Button,
  TextField,
  Box,
  Paper,
  Divider,
  Link,
  Chip,
  CircularProgress
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import {
  getTopics,
  startInterview,
  startJobSpecificInterview
} from "../api/interviewApi";
import { extractTextFromImage } from "../api/ocrApi";

function Header() {
  const navigate = useNavigate();
  const username = localStorage.getItem("username");

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");
    navigate("/");
  };

  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        mb: 4
      }}
    >
      <Box>
        <Typography variant="h4" fontWeight={900}>
          InterviewIQ
        </Typography>
        <Typography variant="body2" sx={{ opacity: 0.8, mt: 0.5 }}>
          10 questions • instant feedback • summary at the end
        </Typography>
      </Box>
      <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
        {username ? (
          <>
            <Typography variant="body2" sx={{ opacity: 0.7 }}>{username}</Typography>
            <Button size="small" variant="outlined" onClick={handleLogout}>Logout</Button>
          </>
        ) : (
          <Button size="small" variant="outlined" onClick={() => navigate("/auth")}>Login</Button>
        )}
      </Box>
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
    const loadTopics = async () => {
      try {
        setTopicsLoading(true);
        setPageError("");

        const data = await getTopics();
        setTopics(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Failed to fetch topics", error);
        setTopics([]);
      } finally {
        setTopicsLoading(false);
      }
    };

    loadTopics();
  }, []);

  const topTopics = useMemo(() => {
    return Array.isArray(topics) ? topics.slice(0, 16) : [];
  }, [topics]);

  const handleJobImageChange = (event) => {
    setJobImage(event.target.files?.[0] || null);
  };

  const handleExtractTextFromImage = async () => {
    if (!jobImage) {
      alert("Please select an image first.");
      return;
    }

    try {
      setOcrLoading(true);
      setPageError("");

      const extractedText = await extractTextFromImage(jobImage);

      setShowText(true);
      setJobText(typeof extractedText === "string" ? extractedText : "");
    } catch (error) {
      console.error("OCR failed", error);
      alert("Failed to extract text from image.");
    } finally {
      setOcrLoading(false);
    }
  };

  const handleStartInterview = async () => {
    try {
      setStartLoading(true);
      setPageError("");

      const trimmedText = jobText.trim();
      const trimmedUrl = jobUrl.trim();

      let response;

      if (trimmedText) {
        response = await startJobSpecificInterview({
          sourceType: "TEXT",
          payload: trimmedText,
          topic: selectedTopic || null,
          userId: null
        });
      } else if (trimmedUrl) {
        response = await startJobSpecificInterview({
          sourceType: "URL",
          payload: trimmedUrl,
          topic: selectedTopic || null,
          userId: null
        });
      } else {
        response = await startInterview({
          sourceType: null,
          payload: "",
          topic: selectedTopic || null,
          userId: null
        });
      }

      const sessionId = response?.sessionId;
      const firstQuestion = response?.firstQuestion || null;

      if (!sessionId) {
        setPageError("Backend did not return a sessionId.");
        return;
      }

      navigate(`/interview/${sessionId}`, {
        state: {
          sessionId,
          firstQuestion
        }
      });
    } catch (error) {
      console.error("Failed to start interview", error);
      setPageError("Failed to start session. Please try again.");
    } finally {
      setStartLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 5, mb: 6 }}>
      <Header />

      <Paper elevation={6} sx={{ p: { xs: 3, sm: 4 }, borderRadius: 4 }}>
        <Typography variant="h6" fontWeight={900}>
          Start practicing in seconds
        </Typography>

        <Typography variant="body2" sx={{ opacity: 0.8, mt: 0.5, mb: 3 }}>
          Paste a job link to tailor questions — or start instantly with a topic.
        </Typography>

        <TextField
          fullWidth
          label="Job URL (optional)"
          placeholder="Paste a job posting link..."
          value={jobUrl}
          onChange={(e) => setJobUrl(e.target.value)}
          sx={{ mb: 1.5 }}
        />

        <Box sx={{ display: "flex", gap: 2, mb: 2, flexWrap: "wrap" }}>
          <Button
            variant="text"
            onClick={() => setShowText((prev) => !prev)}
            sx={{ textTransform: "none" }}
          >
            {showText ? "Hide pasted text" : "Prefer to paste text?"}
          </Button>

          <Button
            variant="text"
            onClick={() => setShowOcr((prev) => !prev)}
            sx={{ textTransform: "none" }}
          >
            {showOcr ? "Hide OCR" : "Have an image?"}
          </Button>
        </Box>

        {showText && (
          <TextField
            fullWidth
            multiline
            rows={5}
            label="Job description text"
            placeholder="Paste job description..."
            value={jobText}
            onChange={(e) => setJobText(e.target.value)}
            sx={{ mb: 2 }}
          />
        )}

        {showOcr && (
          <Box
            sx={{
              display: "flex",
              gap: 2,
              mb: 2,
              flexWrap: "wrap",
              alignItems: "center"
            }}
          >
            <Button variant="outlined" component="label">
              Upload image
              <input
                type="file"
                accept="image/*"
                hidden
                onChange={handleJobImageChange}
              />
            </Button>

            <Button
              variant="text"
              onClick={handleExtractTextFromImage}
              disabled={!jobImage || ocrLoading}
            >
              {ocrLoading ? "Extracting..." : "OCR image"}
            </Button>

            {jobImage && (
              <Typography variant="body2" sx={{ opacity: 0.8 }}>
                {jobImage.name}
              </Typography>
            )}
          </Box>
        )}

        <Divider sx={{ my: 2.5 }} />

        <Typography fontWeight={900} sx={{ mb: 1 }}>
          Quick topic (optional)
        </Typography>

        {topicsLoading ? (
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, mb: 2 }}>
            <CircularProgress size={18} />
            <Typography variant="body2">Loading topics...</Typography>
          </Box>
        ) : (
          <>
            <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", mb: 1.5 }}>
              <Chip
                label="General"
                clickable
                onClick={() => setSelectedTopic("")}
                variant={selectedTopic === "" ? "filled" : "outlined"}
              />

              {topTopics.map((topic) => (
                <Chip
                  key={topic}
                  label={topic}
                  clickable
                  onClick={() => setSelectedTopic(topic)}
                  variant={selectedTopic === topic ? "filled" : "outlined"}
                />
              ))}
            </Box>

            <Button
              variant="text"
              onClick={() => setShowAllTopics((prev) => !prev)}
              sx={{ textTransform: "none", mb: 2 }}
              disabled={topics.length === 0}
            >
              {showAllTopics ? "Hide topics" : `More topics (${topics.length})`}
            </Button>

            {showAllTopics && (
              <Paper
                variant="outlined"
                sx={{
                  p: 2,
                  borderRadius: 3,
                  maxHeight: 160,
                  overflow: "auto",
                  mb: 2
                }}
              >
                <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap" }}>
                  {topics.map((topic) => (
                    <Chip
                      key={topic}
                      label={topic}
                      clickable
                      onClick={() => setSelectedTopic(topic)}
                      variant={selectedTopic === topic ? "filled" : "outlined"}
                    />
                  ))}
                </Box>
              </Paper>
            )}
          </>
        )}

        {pageError && (
          <Typography color="error" variant="body2" sx={{ mb: 2 }}>
            {pageError}
          </Typography>
        )}

        <Box textAlign="center" sx={{ mt: 2 }}>
          <Button
            variant="contained"
            size="large"
            onClick={handleStartInterview}
            disabled={startLoading}
            sx={{ px: 4.5, py: 1.25, fontWeight: 900, borderRadius: 3 }}
          >
            {startLoading ? "Starting..." : "Start session"}
          </Button>

          <Typography
            variant="caption"
            sx={{ display: "block", mt: 1.2, opacity: 0.75 }}
          >
            Leave everything empty for a general interview flow.
          </Typography>
        </Box>
      </Paper>

      <Box textAlign="center" mt={5}>
        <Typography variant="body2" sx={{ opacity: 0.8 }}>
          Built by{" "}
          <Link
            href="https://www.linkedin.com/in/tom-giron-733921198/"
            target="_blank"
          >
            Tom Giron
          </Link>
        </Typography>
      </Box>
    </Container>
  );
}