import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { theme } from './utils/theme';
import Layout from './components/Layout';
import Dashboard from './screens/Dashboard';
import PaymentScreen from './screens/PaymentScreen';
import TransactionsScreen from './screens/TransactionsScreen';
import './App.css';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Layout>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/payment" element={<PaymentScreen />} />
            <Route path="/transactions" element={<TransactionsScreen />} />
            <Route path="/refunds" element={<div>Refunds Screen (Coming Soon)</div>} />
            <Route path="/analytics" element={<div>Analytics Screen (Coming Soon)</div>} />
          </Routes>
        </Layout>
      </Router>
    </ThemeProvider>
  );
}

export default App;