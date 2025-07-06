import { createTheme } from '@mui/material/styles';

// Light blue and light purple color palette
export const colors = {
  primary: {
    light: '#ADD8E6',      // Light blue
    main: '#87CEEB',       // Sky blue
    dark: '#4682B4',       // Steel blue
    contrastText: '#ffffff'
  },
  secondary: {
    light: '#E6E6FA',      // Lavender
    main: '#DDA0DD',       // Plum
    dark: '#B19CD9',       // Medium slate blue
    contrastText: '#ffffff'
  },
  background: {
    default: '#F8F9FA',
    paper: '#FFFFFF',
    light: '#F5F7FF'
  },
  text: {
    primary: '#2D3748',
    secondary: '#718096',
    disabled: '#A0AEC0'
  },
  success: {
    light: '#9AE6B4',
    main: '#48BB78',
    dark: '#38A169'
  },
  error: {
    light: '#FEB2B2',
    main: '#F56565',
    dark: '#E53E3E'
  },
  warning: {
    light: '#F6E05E',
    main: '#ECC94B',
    dark: '#D69E2E'
  },
  info: {
    light: '#90CDF4',
    main: '#4299E1',
    dark: '#3182CE'
  }
};

export const theme = createTheme({
  palette: {
    primary: {
      light: colors.primary.light,
      main: colors.primary.main,
      dark: colors.primary.dark,
      contrastText: colors.primary.contrastText
    },
    secondary: {
      light: colors.secondary.light,
      main: colors.secondary.main,
      dark: colors.secondary.dark,
      contrastText: colors.secondary.contrastText
    },
    background: {
      default: colors.background.default,
      paper: colors.background.paper
    },
    text: {
      primary: colors.text.primary,
      secondary: colors.text.secondary,
      disabled: colors.text.disabled
    },
    success: {
      light: colors.success.light,
      main: colors.success.main,
      dark: colors.success.dark
    },
    error: {
      light: colors.error.light,
      main: colors.error.main,
      dark: colors.error.dark
    },
    warning: {
      light: colors.warning.light,
      main: colors.warning.main,
      dark: colors.warning.dark
    },
    info: {
      light: colors.info.light,
      main: colors.info.main,
      dark: colors.info.dark
    }
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontSize: '2.5rem',
      fontWeight: 700,
      color: colors.text.primary
    },
    h2: {
      fontSize: '2rem',
      fontWeight: 600,
      color: colors.text.primary
    },
    h3: {
      fontSize: '1.5rem',
      fontWeight: 600,
      color: colors.text.primary
    },
    h4: {
      fontSize: '1.25rem',
      fontWeight: 600,
      color: colors.text.primary
    },
    h5: {
      fontSize: '1.125rem',
      fontWeight: 600,
      color: colors.text.primary
    },
    h6: {
      fontSize: '1rem',
      fontWeight: 600,
      color: colors.text.primary
    },
    body1: {
      fontSize: '1rem',
      lineHeight: 1.5,
      color: colors.text.primary
    },
    body2: {
      fontSize: '0.875rem',
      lineHeight: 1.43,
      color: colors.text.secondary
    }
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: 'none',
          fontWeight: 500,
          padding: '8px 16px',
          boxShadow: 'none',
          '&:hover': {
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
          }
        },
        contained: {
          background: `linear-gradient(135deg, ${colors.primary.main}, ${colors.secondary.main})`,
          '&:hover': {
            background: `linear-gradient(135deg, ${colors.primary.dark}, ${colors.secondary.dark})`
          }
        }
      }
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
          border: '1px solid rgba(0, 0, 0, 0.05)',
          '&:hover': {
            boxShadow: '0 6px 20px rgba(0, 0, 0, 0.15)'
          }
        }
      }
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)'
        }
      }
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
            '&:hover fieldset': {
              borderColor: colors.primary.main
            },
            '&.Mui-focused fieldset': {
              borderColor: colors.primary.main
            }
          }
        }
      }
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          fontWeight: 500
        }
      }
    }
  }
});

export default theme;