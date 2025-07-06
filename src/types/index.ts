// Payment Processing API Types

export interface BuyerInfo {
  document: string;
  name: string;
}

export interface CardInfo {
  cardInfo: string;
  token: string;
}

export interface SellerInfo {
  document: string;
  name: string;
}

export interface PaymentOrderId {
  id: string;
}

export interface TransactionId {
  id: string;
}

export interface CheckoutId {
  id: string;
}

export interface RefundId {
  id: string;
}

export enum TransactionStatus {
  APPROVED = 'APPROVED',
  DECLINED = 'DECLINED',
  UNDEFINED = 'UNDEFINED'
}

export enum TransactionSituation {
  RECEIVED = 'RECEIVED',
  PROCESSED = 'PROCESSED'
}

export enum TransactionType {
  PAYMENT = 'PAYMENT',
  REFUND = 'REFUND'
}

export enum RiskLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export interface Transaction {
  id: string;
  paymentOrder: PaymentOrderId;
  amount: string;
  currency: string;
  buyerInfo: BuyerInfo;
  cardInfo: CardInfo;
  createdAt: string;
  status: TransactionStatus;
  situation: TransactionSituation;
  type: TransactionType;
}

export interface PaymentRequest {
  amount: string;
  currency: string;
  buyerInfo: BuyerInfo;
  cardInfo: CardInfo;
  sellerInfo: SellerInfo;
}

export interface PaymentResult {
  status: string;
  transactionId?: string;
  message?: string;
}

export interface RefundRequest {
  transactionId: string;
  amount: string;
  currency: string;
  reason: string;
}

export interface TransactionProcessedEvent {
  transaction: TransactionId;
  seller: SellerInfo;
  payment: PaymentOrderId;
  checkoutId?: CheckoutId;
  refundId?: RefundId;
  amount: string;
  currency: string;
  at: string;
  buyer: BuyerInfo;
  status: TransactionStatus;
}

export interface PaymentCreatedEvent {
  specversion: string;
  type: string;
  source: string;
  id: string;
  time: string;
  datacontenttype: string;
  data: {
    status: string;
    level: RiskLevel;
    transaction: {
      payment: {
        id: string;
        amount: string;
        currency: string;
        status: string;
      };
      order: {
        id: string;
      };
      participants: {
        seller: SellerInfo;
        buyer: BuyerInfo;
      };
    };
  };
}

export interface PaymentOrderStartedEvent {
  specversion: string;
  type: string;
  source: string;
  id: string;
  time: string;
  subject: string;
  datacontenttype: string;
  data: {
    paymentOrderId: string;
    amount: string;
    currency: string;
    seller: SellerInfo;
    processedAt: string;
  };
}

export interface DashboardStats {
  totalTransactions: number;
  totalAmount: string;
  approvedTransactions: number;
  declinedTransactions: number;
  pendingTransactions: number;
  totalRefunds: number;
  refundAmount: string;
}

export interface TransactionFilter {
  status?: TransactionStatus;
  type?: TransactionType;
  dateFrom?: string;
  dateTo?: string;
  minAmount?: string;
  maxAmount?: string;
  currency?: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface ApiError {
  message: string;
  code?: string;
  details?: any;
}