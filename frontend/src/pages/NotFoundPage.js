import React from "react";
import { Box, Button, Container, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <Container maxWidth="sm" sx={{ pt: 12, pb: 8, textAlign: "center" }}>
      <Typography sx={{
        fontFamily: "monospace", fontSize: "5rem", fontWeight: 900,
        color: "primary.main", lineHeight: 1, mb: 2,
      }}>
        404
      </Typography>
      <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
        Page not found
      </Typography>
      <Typography variant="body2" sx={{ color: "text.secondary", mb: 4 }}>
        This route doesn't exist.
      </Typography>
      <Button
        variant="contained"
        sx={{ background: "linear-gradient(135deg, #7c6fff, #5a4fd4)" }}
        onClick={() => navigate("/")}
      >
        ← Go Home
      </Button>
    </Container>
  );
}
