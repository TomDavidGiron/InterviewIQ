import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    mode: "dark",
    background: {
      default: "#0d0d0d",
      paper: "#141414",
    },
    primary: {
      main: "#60a5fa",
      light: "#93c5fd",
      dark: "#3b82f6",
      contrastText: "#fff",
    },
    secondary: {
      main: "#22d3a0",
    },
    error: {
      main: "#f87171",
    },
    warning: {
      main: "#fbbf24",
    },
    success: {
      main: "#22d3a0",
    },
    text: {
      primary: "#e2e8f0",
      secondary: "#6b7280",
    },
    divider: "#262626",
  },
  typography: {
    fontFamily: "'Inter', 'Segoe UI', 'Arial', sans-serif",
    h3: { fontWeight: 800, letterSpacing: "-0.02em" },
    h4: { fontWeight: 800, letterSpacing: "-0.02em" },
    h5: { fontWeight: 700, letterSpacing: "-0.01em" },
    h6: { fontWeight: 700 },
    subtitle1: { color: "#6b7280" },
  },
  shape: { borderRadius: 4 },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          border: "1px solid #262626",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: "none",
          fontWeight: 600,
          borderRadius: 4,
          letterSpacing: "0.01em",
          boxShadow: "none",
          "&:hover": { boxShadow: "none" },
        },
        contained: {
          "&:hover": { filter: "brightness(1.12)" },
        },
        outlined: {
          borderColor: "#333",
          "&:hover": { borderColor: "#60a5fa", backgroundColor: "rgba(96,165,250,0.06)" },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          fontWeight: 600,
          fontSize: "0.72rem",
          borderRadius: 3,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          "& .MuiOutlinedInput-root": {
            backgroundColor: "#111",
            borderRadius: 4,
            "& fieldset": { borderColor: "#2a2a2a" },
            "&:hover fieldset": { borderColor: "#3f3f3f" },
            "&.Mui-focused fieldset": { borderColor: "#60a5fa" },
          },
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          borderRadius: 2,
          backgroundColor: "#1f1f1f",
          height: 5,
        },
        bar: { borderRadius: 2 },
      },
    },
    MuiDivider: {
      styleOverrides: {
        root: { borderColor: "#262626" },
      },
    },
    MuiAlert: {
      styleOverrides: {
        root: {
          borderRadius: 4,
          border: "1px solid",
        },
      },
    },
  },
});

export default theme;
