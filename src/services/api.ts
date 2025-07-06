import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
  Transaction,
  PaymentRequest,
  PaymentResult,
  RefundRequest,
  DashboardStats,
  TransactionFilter,
  PaginatedResponse,
  ApiError,
  TransactionStatus,
  TransactionSituation,
  TransactionType
} from '../types';

class PaymentApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: 'http://localhost:8086', // Payment Executor Service port
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    // Add request interceptor for error handling
    this.api.interceptors.request.use(
      (config) => {
        console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`);
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Add response interceptor for error handling
    this.api.interceptors.response.use(
      (response: AxiosResponse) => response,
      (error) => {
        const apiError: ApiError = {
          message: error.response?.data?.message || error.message || 'An error occurred',
          code: error.response?.status?.toString(),
          details: error.response?.data
        };
        return Promise.reject(apiError);
      }
    );
  }

  // Payment operations
  async createPayment(paymentRequest: PaymentRequest): Promise<PaymentResult> {
    try {
      const response = await this.api.post<PaymentResult>('/payments', paymentRequest);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Transaction operations
  async getTransactions(filter?: TransactionFilter, page: number = 1, pageSize: number = 10): Promise<PaginatedResponse<Transaction>> {
    try {
      const params = new URLSearchParams();
      if (filter?.status) params.append('status', filter.status);
      if (filter?.type) params.append('type', filter.type);
      if (filter?.dateFrom) params.append('dateFrom', filter.dateFrom);
      if (filter?.dateTo) params.append('dateTo', filter.dateTo);
      if (filter?.minAmount) params.append('minAmount', filter.minAmount);
      if (filter?.maxAmount) params.append('maxAmount', filter.maxAmount);
      if (filter?.currency) params.append('currency', filter.currency);
      params.append('page', page.toString());
      params.append('pageSize', pageSize.toString());

      const response = await this.api.get<PaginatedResponse<Transaction>>(`/transactions?${params}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getTransaction(transactionId: string): Promise<Transaction> {
    try {
      const response = await this.api.get<Transaction>(`/transactions/${transactionId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Refund operations
  async createRefund(refundRequest: RefundRequest): Promise<PaymentResult> {
    try {
      const response = await this.api.post<PaymentResult>('/refunds', refundRequest);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getRefunds(page: number = 1, pageSize: number = 10): Promise<PaginatedResponse<Transaction>> {
    try {
      const params = new URLSearchParams({
        type: 'REFUND',
        page: page.toString(),
        pageSize: pageSize.toString()
      });

      const response = await this.api.get<PaginatedResponse<Transaction>>(`/transactions?${params}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Dashboard statistics
  async getDashboardStats(): Promise<DashboardStats> {
    try {
      const response = await this.api.get<DashboardStats>('/dashboard/stats');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Health check
  async healthCheck(): Promise<{ status: string }> {
    try {
      const response = await this.api.get<{ status: string }>('/q/health');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  private handleError(error: any): ApiError {
    if (error.response) {
      return {
        message: error.response.data?.message || 'Server error',
        code: error.response.status?.toString(),
        details: error.response.data
      };
    } else if (error.request) {
      return {
        message: 'Network error - unable to connect to server',
        code: 'NETWORK_ERROR'
      };
    } else {
      return {
        message: error.message || 'An unexpected error occurred',
        code: 'UNKNOWN_ERROR'
      };
    }
  }
}

// Mock service for development/testing when backend is not available
class MockPaymentApiService extends PaymentApiService {
  private mockTransactions: Transaction[] = [
    {
      id: '1',
      paymentOrder: { id: 'order-1' },
      amount: '100.00',
      currency: 'USD',
      buyerInfo: { document: '12345678901', name: 'John Doe' },
      cardInfo: { cardInfo: '**** **** **** 1234', token: 'token123' },
      createdAt: '2024-01-01T10:00:00Z',
      status: TransactionStatus.APPROVED,
      situation: TransactionSituation.PROCESSED,
      type: TransactionType.PAYMENT
    },
    {
      id: '2',
      paymentOrder: { id: 'order-2' },
      amount: '250.00',
      currency: 'USD',
      buyerInfo: { document: '98765432109', name: 'Jane Smith' },
      cardInfo: { cardInfo: '**** **** **** 5678', token: 'token456' },
      createdAt: '2024-01-01T11:00:00Z',
      status: TransactionStatus.DECLINED,
      situation: TransactionSituation.PROCESSED,
      type: TransactionType.PAYMENT
    }
  ];

  async createPayment(paymentRequest: PaymentRequest): Promise<PaymentResult> {
    // Simulate processing delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Simulate random success/failure
    const isSuccess = Math.random() > 0.3;
    
    if (isSuccess) {
      return {
        status: 'APPROVED',
        transactionId: `txn-${Date.now()}`,
        message: 'Payment processed successfully'
      };
    } else {
      return {
        status: 'DECLINED',
        message: 'Payment declined - insufficient funds'
      };
    }
  }

  async getTransactions(filter?: TransactionFilter, page: number = 1, pageSize: number = 10): Promise<PaginatedResponse<Transaction>> {
    await new Promise(resolve => setTimeout(resolve, 500));
    
    let filteredTransactions = [...this.mockTransactions];
    
    if (filter?.status) {
      filteredTransactions = filteredTransactions.filter(t => t.status === filter.status);
    }
    if (filter?.type) {
      filteredTransactions = filteredTransactions.filter(t => t.type === filter.type);
    }
    
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedData = filteredTransactions.slice(startIndex, endIndex);
    
    return {
      data: paginatedData,
      total: filteredTransactions.length,
      page,
      pageSize,
      totalPages: Math.ceil(filteredTransactions.length / pageSize)
    };
  }

  async getTransaction(transactionId: string): Promise<Transaction> {
    await new Promise(resolve => setTimeout(resolve, 300));
    
    const transaction = this.mockTransactions.find(t => t.id === transactionId);
    if (!transaction) {
      throw new Error('Transaction not found');
    }
    return transaction;
  }

  async getDashboardStats(): Promise<DashboardStats> {
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return {
      totalTransactions: 150,
      totalAmount: '15,750.00',
      approvedTransactions: 120,
      declinedTransactions: 25,
      pendingTransactions: 5,
      totalRefunds: 8,
      refundAmount: '980.00'
    };
  }

  async createRefund(refundRequest: RefundRequest): Promise<PaymentResult> {
    await new Promise(resolve => setTimeout(resolve, 800));
    
    return {
      status: 'APPROVED',
      transactionId: `refund-${Date.now()}`,
      message: 'Refund processed successfully'
    };
  }
}

// Export the service instance
export const paymentApiService = new MockPaymentApiService();
export default paymentApiService;