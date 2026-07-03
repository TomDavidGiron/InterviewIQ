import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    mode: "dark",
    background: {
      default: "#08080f",
      paper: "#0f0f1a",
    },
    primary: {
      main: "#7c6fff",
      light: "#a99fff",
      dark: "#5a4fd4",
      contrastText: "#fff",
    },
    secondary: {
      main: "#22d3a0",
    },
    error: {
      main: "#ff4d6a",
    },
    warning: {
      main: "#f59e0b",
    },
    success: {
      main: "#22d3a0",
    },
    text: {
      primary: "#ededf5",
      secondary: "#7a7a9e",
    },
    divider: "#1e1e30",
  },
  typography: {
    fontFamily: "'Inter', 'Segoe UI', 'Arial', sans-serif",
    h3: { fontWeight: 900, letterSpacing: "-0.03em" },
    h4: { fontWeight: 900, letterSpacing: "-0.02em" },
    h5: { fontWeight: 800, letterSpacing: "-0.01em" },
    h6: { fontWeight: 800 },
    subtitle1: { color: "#7a7a9e" },
  },
  shape: { borderRadius: 10 },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          border: "1px solid #1e1e30",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: "none",
          fontWeight: 700,
          borderRadius: 8,
          letterSpacing: "0.01em",
        },
        contained: {
          boxShadow: "none",
          "&:hover": { boxShadow: "0 0 0 3px rgba(124,111,255,0.2)" },
        },
        outlined: {
          borderColor: "#2a2a40",
          "&:hover": { borderColor: "#7c6fff", backgroundColor: "rgba(124,111,255,0.06)" },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          fontWeight: 700,
          fontSize: "0.72rem",
          borderRadius: 6,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          "& .MuiOutlinedInput-root": {
            backgroundColor: "#0b0b16",
            "& fieldset": { borderColor: "#1e1e30" },
            "&:hover fieldset": { borderColor: "#3a3a55" },
            "&.Mui-focused fieldset": { borderColor: "#7c6fff" },
          },
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          borderRadius: 999,
          backgroundColor: "#1e1e30",
          height: 6,
        },
        bar: { borderRadius: 999 },
      },
    },
    MuiDivider: {
      styleOverrides: {
        root: { borderColor: "#1e1e30" },
      },
    },
    MuiAlert: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          border: "1px solid",
        },
      },
    },
  },
});

export default theme;
