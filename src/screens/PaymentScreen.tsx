import React, { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  TextField,
  Button,
  Grid,
  Alert,
  CircularProgress,
  Divider,
  InputAdornment,
  MenuItem,
  Stepper,
  Step,
  StepLabel,
  StepContent
} from '@mui/material';
import {
  Payment,
  Person,
  CreditCard,
  Business,
  AttachMoney
} from '@mui/icons-material';
import { PaymentRequest, PaymentResult, BuyerInfo, CardInfo, SellerInfo } from '../types';
import { paymentApiService } from '../services/api';

const currencies = [
  { value: 'USD', label: 'USD - US Dollar' },
  { value: 'EUR', label: 'EUR - Euro' },
  { value: 'GBP', label: 'GBP - British Pound' },
  { value: 'BRL', label: 'BRL - Brazilian Real' }
];

const PaymentScreen: React.FC = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<PaymentResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  
  const [formData, setFormData] = useState<PaymentRequest>({
    amount: '',
    currency: 'USD',
    buyerInfo: {
      document: '',
      name: ''
    },
    cardInfo: {
      cardInfo: '',
      token: ''
    },
    sellerInfo: {
      document: '',
      name: ''
    }
  });

  const handleInputChange = (field: string, value: string) => {
    const keys = field.split('.');
    if (keys.length === 1) {
      setFormData(prev => ({
        ...prev,
        [field]: value
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [keys[0]]: {
          ...prev[keys[0] as keyof PaymentRequest],
          [keys[1]]: value
        }
      }));
    }
  };

  const handleNext = () => {
    setActiveStep(prev => prev + 1);
  };

  const handleBack = () => {
    setActiveStep(prev => prev - 1);
  };

  const handleSubmit = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const result = await paymentApiService.createPayment(formData);
      setResult(result);
      setActiveStep(3);
    } catch (err: any) {
      setError(err.message || 'Failed to process payment');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      amount: '',
      currency: 'USD',
      buyerInfo: {
        document: '',
        name: ''
      },
      cardInfo: {
        cardInfo: '',
        token: ''
      },
      sellerInfo: {
        document: '',
        name: ''
      }
    });
    setActiveStep(0);
    setResult(null);
    setError(null);
  };

  const isStepValid = (step: number) => {
    switch (step) {
      case 0:
        return formData.amount && formData.currency;
      case 1:
        return formData.buyerInfo.document && formData.buyerInfo.name;
      case 2:
        return formData.cardInfo.cardInfo && formData.cardInfo.token && formData.sellerInfo.document && formData.sellerInfo.name;
      default:
        return true;
    }
  };

  const steps = [
    {
      label: 'Payment Details',
      content: (
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Amount"
              type="number"
              value={formData.amount}
              onChange={(e) => handleInputChange('amount', e.target.value)}
              InputProps={{
                startAdornment: <InputAdornment position="start"><AttachMoney /></InputAdornment>,
              }}
              required
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              select
              label="Currency"
              value={formData.currency}
              onChange={(e) => handleInputChange('currency', e.target.value)}
              required
            >
              {currencies.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
        </Grid>
      )
    },
    {
      label: 'Buyer Information',
      content: (
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Buyer Document"
              value={formData.buyerInfo.document}
              onChange={(e) => handleInputChange('buyerInfo.document', e.target.value)}
              InputProps={{
                startAdornment: <InputAdornment position="start"><Person /></InputAdornment>,
              }}
              required
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Buyer Name"
              value={formData.buyerInfo.name}
              onChange={(e) => handleInputChange('buyerInfo.name', e.target.value)}
              required
            />
          </Grid>
        </Grid>
      )
    },
    {
      label: 'Payment & Seller Details',
      content: (
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Card Information"
              value={formData.cardInfo.cardInfo}
              onChange={(e) => handleInputChange('cardInfo.cardInfo', e.target.value)}
              InputProps={{
                startAdornment: <InputAdornment position="start"><CreditCard /></InputAdornment>,
              }}
              placeholder="**** **** **** 1234"
              required
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Card Token"
              value={formData.cardInfo.token}
              onChange={(e) => handleInputChange('cardInfo.token', e.target.value)}
              required
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Seller Document"
              value={formData.sellerInfo.document}
              onChange={(e) => handleInputChange('sellerInfo.document', e.target.value)}
              InputProps={{
                startAdornment: <InputAdornment position="start"><Business /></InputAdornment>,
              }}
              required
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Seller Name"
              value={formData.sellerInfo.name}
              onChange={(e) => handleInputChange('sellerInfo.name', e.target.value)}
              required
            />
          </Grid>
        </Grid>
      )
    }
  ];

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        New Payment
      </Typography>
      
      <Card>
        <CardContent>
          <Stepper activeStep={activeStep} orientation="vertical">
            {steps.map((step, index) => (
              <Step key={step.label}>
                <StepLabel>{step.label}</StepLabel>
                <StepContent>
                  {step.content}
                  <Box sx={{ mb: 2, mt: 2 }}>
                    <div>
                      <Button
                        variant="contained"
                        onClick={index === steps.length - 1 ? handleSubmit : handleNext}
                        disabled={!isStepValid(index) || loading}
                        sx={{ mt: 1, mr: 1 }}
                      >
                        {index === steps.length - 1 ? 'Process Payment' : 'Continue'}
                      </Button>
                      <Button
                        disabled={index === 0}
                        onClick={handleBack}
                        sx={{ mt: 1, mr: 1 }}
                      >
                        Back
                      </Button>
                    </div>
                  </Box>
                </StepContent>
              </Step>
            ))}
          </Stepper>
          
          {activeStep === steps.length && (
            <Box>
              {loading && (
                <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
                  <CircularProgress />
                  <Typography variant="h6" sx={{ ml: 2 }}>
                    Processing payment...
                  </Typography>
                </Box>
              )}
              
              {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {error}
                </Alert>
              )}
              
              {result && (
                <Box>
                  <Alert severity={result.status === 'APPROVED' ? 'success' : 'error'} sx={{ mb: 2 }}>
                    <Typography variant="h6">
                      Payment {result.status}
                    </Typography>
                    <Typography>
                      {result.message}
                    </Typography>
                    {result.transactionId && (
                      <Typography variant="body2">
                        Transaction ID: {result.transactionId}
                      </Typography>
                    )}
                  </Alert>
                  
                  <Box textAlign="center" mt={3}>
                    <Button
                      variant="outlined"
                      onClick={resetForm}
                      size="large"
                    >
                      Create New Payment
                    </Button>
                  </Box>
                </Box>
              )}
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default PaymentScreen;