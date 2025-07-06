import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Tooltip,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  TrendingUp,
  Payment,
  MoneyOff,
  Assessment,
  CheckCircle,
  Cancel,
  Refresh,
  Visibility
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { DashboardStats, Transaction, TransactionStatus } from '../types';
import { paymentApiService } from '../services/api';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Load stats and recent transactions in parallel
      const [statsResponse, transactionsResponse] = await Promise.all([
        paymentApiService.getDashboardStats(),
        paymentApiService.getTransactions(undefined, 1, 5)
      ]);
      
      setStats(statsResponse);
      setRecentTransactions(transactionsResponse.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: TransactionStatus) => {
    switch (status) {
      case 'APPROVED':
        return 'success';
      case 'DECLINED':
        return 'error';
      case 'UNDEFINED':
        return 'warning';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status: TransactionStatus) => {
    switch (status) {
      case 'APPROVED':
        return <CheckCircle />;
      case 'DECLINED':
        return <Cancel />;
      case 'UNDEFINED':
        return <Assessment />;
      default:
        return null;
    }
  };

  const formatCurrency = (amount: string) => {
    return `$${parseFloat(amount).toLocaleString()}`;
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Alert severity="error" action={
          <IconButton color="inherit" size="small" onClick={loadDashboardData}>
            <Refresh />
          </IconButton>
        }>
          {error}
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      
      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Transactions
                  </Typography>
                  <Typography variant="h4">
                    {stats?.totalTransactions || 0}
                  </Typography>
                </Box>
                <TrendingUp color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Amount
                  </Typography>
                  <Typography variant="h4">
                    {formatCurrency(stats?.totalAmount || '0')}
                  </Typography>
                </Box>
                <Payment color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Approved
                  </Typography>
                  <Typography variant="h4" color="success.main">
                    {stats?.approvedTransactions || 0}
                  </Typography>
                </Box>
                <CheckCircle color="success" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Declined
                  </Typography>
                  <Typography variant="h4" color="error.main">
                    {stats?.declinedTransactions || 0}
                  </Typography>
                </Box>
                <Cancel color="error" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Transactions */}
      <Card>
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h6">
              Recent Transactions
            </Typography>
            <IconButton onClick={() => navigate('/transactions')}>
              <Visibility />
            </IconButton>
          </Box>
          
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Amount</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Buyer</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {recentTransactions.map((transaction) => (
                  <TableRow key={transaction.id} hover>
                    <TableCell>{transaction.id}</TableCell>
                    <TableCell>{formatCurrency(transaction.amount)}</TableCell>
                    <TableCell>
                      <Chip 
                        label={transaction.type} 
                        size="small" 
                        variant="outlined"
                        color={transaction.type === 'PAYMENT' ? 'primary' : 'secondary'}
                      />
                    </TableCell>
                    <TableCell>
                      <Chip 
                        icon={getStatusIcon(transaction.status)}
                        label={transaction.status} 
                        size="small"
                        color={getStatusColor(transaction.status) as any}
                      />
                    </TableCell>
                    <TableCell>{formatDate(transaction.createdAt)}</TableCell>
                    <TableCell>{transaction.buyerInfo.name}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          
          {recentTransactions.length === 0 && (
            <Box textAlign="center" py={4}>
              <Typography color="textSecondary">
                No recent transactions found
              </Typography>
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default Dashboard;