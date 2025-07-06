import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  TextField,
  Button,
  Grid,
  MenuItem,
  TablePagination,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  CircularProgress,
  Tooltip
} from '@mui/material';
import {
  Visibility,
  FilterList,
  Refresh,
  Search,
  CheckCircle,
  Cancel,
  Assessment,
  Download
} from '@mui/icons-material';
import { 
  Transaction, 
  TransactionStatus, 
  TransactionType, 
  TransactionFilter,
  PaginatedResponse
} from '../types';
import { paymentApiService } from '../services/api';

const TransactionsScreen: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedTransaction, setSelectedTransaction] = useState<Transaction | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  
  const [filters, setFilters] = useState<TransactionFilter>({
    status: undefined,
    type: undefined,
    dateFrom: '',
    dateTo: '',
    currency: ''
  });

  useEffect(() => {
    loadTransactions();
  }, [page, rowsPerPage, filters]);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const response = await paymentApiService.getTransactions(
        filters,
        page + 1,
        rowsPerPage
      );
      
      setTransactions(response.data);
      setTotalCount(response.total);
    } catch (err: any) {
      setError(err.message || 'Failed to load transactions');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (field: keyof TransactionFilter, value: string) => {
    setFilters(prev => ({
      ...prev,
      [field]: value || undefined
    }));
    setPage(0);
  };

  const handleViewTransaction = async (transactionId: string) => {
    try {
      const transaction = await paymentApiService.getTransaction(transactionId);
      setSelectedTransaction(transaction);
      setDialogOpen(true);
    } catch (err: any) {
      setError(err.message || 'Failed to load transaction details');
    }
  };

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
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

  const formatDateTime = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  const clearFilters = () => {
    setFilters({
      status: undefined,
      type: undefined,
      dateFrom: '',
      dateTo: '',
      currency: ''
    });
    setPage(0);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Transactions
      </Typography>

      {/* Filters */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            <FilterList sx={{ mr: 1 }} />
            Filters
          </Typography>
          
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                fullWidth
                select
                label="Status"
                value={filters.status || ''}
                onChange={(e) => handleFilterChange('status', e.target.value)}
                size="small"
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value="APPROVED">Approved</MenuItem>
                <MenuItem value="DECLINED">Declined</MenuItem>
                <MenuItem value="UNDEFINED">Undefined</MenuItem>
              </TextField>
            </Grid>
            
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                fullWidth
                select
                label="Type"
                value={filters.type || ''}
                onChange={(e) => handleFilterChange('type', e.target.value)}
                size="small"
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value="PAYMENT">Payment</MenuItem>
                <MenuItem value="REFUND">Refund</MenuItem>
              </TextField>
            </Grid>
            
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                fullWidth
                label="Currency"
                value={filters.currency || ''}
                onChange={(e) => handleFilterChange('currency', e.target.value)}
                size="small"
              />
            </Grid>
            
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                fullWidth
                label="From Date"
                type="date"
                value={filters.dateFrom || ''}
                onChange={(e) => handleFilterChange('dateFrom', e.target.value)}
                size="small"
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                fullWidth
                label="To Date"
                type="date"
                value={filters.dateTo || ''}
                onChange={(e) => handleFilterChange('dateTo', e.target.value)}
                size="small"
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            
            <Grid item xs={12} sm={6} md={2}>
              <Box display="flex" gap={1}>
                <Button
                  variant="outlined"
                  onClick={clearFilters}
                  size="small"
                  fullWidth
                >
                  Clear
                </Button>
                <IconButton onClick={loadTransactions} color="primary">
                  <Refresh />
                </IconButton>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {/* Transactions Table */}
      <Card>
        <CardContent>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Amount</TableCell>
                  <TableCell>Currency</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Buyer</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {loading ? (
                  <TableRow>
                    <TableCell colSpan={8} align="center">
                      <CircularProgress />
                    </TableCell>
                  </TableRow>
                ) : transactions.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={8} align="center">
                      <Typography color="textSecondary">
                        No transactions found
                      </Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  transactions.map((transaction) => (
                    <TableRow key={transaction.id} hover>
                      <TableCell>{transaction.id}</TableCell>
                      <TableCell>{formatCurrency(transaction.amount)}</TableCell>
                      <TableCell>{transaction.currency}</TableCell>
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
                      <TableCell>
                        <Tooltip title="View Details">
                          <IconButton 
                            onClick={() => handleViewTransaction(transaction.id)}
                            size="small"
                          >
                            <Visibility />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
          
          <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={totalCount}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangeRowsPerPage}
          />
        </CardContent>
      </Card>

      {/* Transaction Details Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          Transaction Details
        </DialogTitle>
        <DialogContent>
          {selectedTransaction && (
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Transaction ID</Typography>
                <Typography variant="body1">{selectedTransaction.id}</Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Payment Order</Typography>
                <Typography variant="body1">{selectedTransaction.paymentOrder.id}</Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Amount</Typography>
                <Typography variant="body1">{formatCurrency(selectedTransaction.amount)} {selectedTransaction.currency}</Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Type</Typography>
                <Typography variant="body1">{selectedTransaction.type}</Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Status</Typography>
                <Chip 
                  icon={getStatusIcon(selectedTransaction.status)}
                  label={selectedTransaction.status} 
                  color={getStatusColor(selectedTransaction.status) as any}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Situation</Typography>
                <Typography variant="body1">{selectedTransaction.situation}</Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Created At</Typography>
                <Typography variant="body1">{formatDateTime(selectedTransaction.createdAt)}</Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Buyer</Typography>
                <Typography variant="body1">{selectedTransaction.buyerInfo.name}</Typography>
                <Typography variant="body2" color="textSecondary">
                  Document: {selectedTransaction.buyerInfo.document}
                </Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2">Card Info</Typography>
                <Typography variant="body1">{selectedTransaction.cardInfo.cardInfo}</Typography>
                <Typography variant="body2" color="textSecondary">
                  Token: {selectedTransaction.cardInfo.token}
                </Typography>
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default TransactionsScreen;