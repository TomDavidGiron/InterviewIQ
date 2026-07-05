import React from "react";

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, message: "" };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, message: error?.message || "Something went wrong." };
  }

  render() {
    if (!this.state.hasError) return this.props.children;

    return (
      <div style={{
        minHeight: "100vh",
        background: "#08080f",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        fontFamily: "monospace",
        color: "#fff",
        padding: "2rem",
        textAlign: "center",
      }}>
        <div style={{ fontSize: "2.5rem", marginBottom: "1rem" }}>⚠</div>
        <div style={{ fontSize: "1.25rem", fontWeight: 800, marginBottom: "0.5rem" }}>
          Something went wrong
        </div>
        <div style={{
          fontSize: "0.85rem", color: "#ff4d6a",
          background: "rgba(255,77,106,0.08)", border: "1px solid rgba(255,77,106,0.2)",
          borderRadius: "8px", padding: "0.75rem 1.25rem",
          maxWidth: "480px", marginBottom: "2rem", wordBreak: "break-word",
        }}>
          {this.state.message}
        </div>
        <a href="/" style={{
          background: "linear-gradient(135deg, #7c6fff, #5a4fd4)",
          color: "#fff", textDecoration: "none",
          padding: "0.6rem 1.5rem", borderRadius: "8px",
          fontWeight: 700, fontSize: "0.9rem",
        }}>
          ← Go Home
        </a>
      </div>
    );
  }
}
