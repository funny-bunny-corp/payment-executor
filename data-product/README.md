# Transaction Processed Events Data Product

## Overview

### Owner & Domain
- **Owner**: Payment Processing Team
- **Domain**: Payments
- **Business Context**: Core payment processing and refund transactions
- **Data Product Name**: Transaction Processed Events

### Description & Purpose
This data product provides real-time analytical access to payment transaction processing events from the Paymentic payment processing system. It captures the complete lifecycle of payment and refund transactions, including their final status (approved/declined), participant information, and financial details.

**Primary Use Cases:**
- Real-time payment success rate monitoring and alerting
- Revenue and transaction volume analytics
- Fraud detection and risk analysis
- Customer behavior analysis and segmentation
- Payment method optimization
- Financial reconciliation and reporting
- Cross-border payment analysis

**Target Consumers:**
- Business Intelligence teams for dashboard creation
- Risk Management teams for fraud detection
- Finance teams for revenue reporting
- Product teams for payment method optimization
- Operations teams for real-time monitoring

## Data Product Architecture

### Event Source
- **System**: Paymentic Payment Executor Service
- **Event Type**: TransactionProcessedEvent
- **Publishing Pattern**: Domain Event (fired after successful transaction processing)
- **Kafka Topic**: `payment-processing`
- **CloudEvent Types**: 
  - `funny-bunny.xyz.payment-processing.v1.payment-order.approved`
  - `funny-bunny.xyz.payment-processing.v1.payment-order.declined`

### Data Flow
1. Payment/Refund request received by Payment Executor
2. PSP integration called for transaction processing
3. Transaction result persisted in operational database
4. TransactionProcessedEvent published to Kafka topic
5. Apache Pinot ingests events in real-time
6. Data available for analytical queries within seconds

## Schema Definition

### Core Entity
**Table**: `transaction_processed_events`
**Type**: Apache Pinot Real-time Table
**Retention**: 365 days
**Partitioning**: Time-based (processed_at)

### Field Specifications

| Field Name | Data Type | Category | Description | Example | Nullable |
|------------|-----------|----------|-------------|---------|----------|
| `transaction_id` | STRING | Dimension | Unique identifier for the transaction | "550e8400-e29b-41d4-a716-446655440000" | No |
| `payment_order_id` | STRING | Dimension | Unique identifier for the payment order | "550e8400-e29b-41d4-a716-446655440001" | No |
| `checkout_id` | STRING | Dimension | Checkout session identifier (payments only) | "550e8400-e29b-41d4-a716-446655440002" | Yes |
| `refund_id` | STRING | Dimension | Refund request identifier (refunds only) | "550e8400-e29b-41d4-a716-446655440003" | Yes |
| `transaction_type` | STRING | Dimension | Type of transaction (PAYMENT/REFUND) | "PAYMENT" | No |
| `transaction_status` | STRING | Dimension | Final transaction status (APPROVED/DECLINED) | "APPROVED" | No |
| `currency` | STRING | Dimension | ISO currency code | "USD" | No |
| `seller_id` | STRING | Dimension | Merchant/seller identifier | "merchant_123" | No |
| `buyer_document` | STRING | Dimension | Customer identifier/document | "12345678901" | No |
| `buyer_name` | STRING | Dimension | Customer name | "John Doe" | No |
| `amount` | BIG_DECIMAL | Metric | Transaction amount | "100.50" | No |
| `processed_at` | TIMESTAMP | Time | Event processing timestamp (epoch milliseconds) | 1704067200000 | No |

### Data Quality Notes
- **Uniqueness**: `transaction_id` is unique per event
- **Completeness**: All required fields are guaranteed to be present based on domain validation
- **Referential Integrity**: `payment_order_id` references the originating payment order
- **Business Rules**: 
  - Payments have `checkout_id` populated, `refund_id` is null
  - Refunds have `refund_id` populated, `checkout_id` is null
  - All monetary amounts are positive decimal values
  - Timestamps are in UTC

## Data Quality Guarantees (SLA/SLO)

### Freshness
- **Target**: < 5 seconds from event publication to query availability
- **Measurement**: 95th percentile end-to-end latency
- **Monitoring**: Real-time latency dashboard

### Completeness
- **Target**: 99.9% of published events successfully ingested
- **Measurement**: Event count reconciliation (source vs sink)
- **Monitoring**: Daily completeness reports

### Accuracy
- **Target**: 100% data accuracy (no transformation errors)
- **Measurement**: Schema validation and data type checks
- **Monitoring**: Automated data quality validation

### Availability
- **Target**: 99.9% query availability during business hours
- **Measurement**: Query success rate
- **Monitoring**: Pinot cluster health checks

## Indexing Strategy

### Performance Optimizations
- **Inverted Indexes**: `transaction_status`, `transaction_type`, `currency`, `seller_id`, `buyer_document`
- **Range Indexes**: `processed_at`, `amount`
- **Sorted Column**: `processed_at` (for time-series queries)
- **Bloom Filters**: `transaction_id`, `payment_order_id`, `checkout_id`, `refund_id`

### Query Patterns Supported
- Time-range filtering (last 24h, last 7d, etc.)
- Status-based filtering (approved vs declined)
- Currency-based analysis
- Seller/merchant performance analysis
- Customer transaction history

## Example Queries

### 1. Real-time Payment Success Rate (Last 24 Hours)
```sql
SELECT 
    DATE_TRUNC('hour', FROM_UNIXTIME(processed_at/1000)) as hour,
    COUNT(*) as total_transactions,
    SUM(CASE WHEN transaction_status = 'APPROVED' THEN 1 ELSE 0 END) as approved_count,
    (SUM(CASE WHEN transaction_status = 'APPROVED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as success_rate
FROM transaction_processed_events 
WHERE processed_at >= (UNIX_TIMESTAMP(NOW()) - 86400) * 1000
    AND transaction_type = 'PAYMENT'
GROUP BY DATE_TRUNC('hour', FROM_UNIXTIME(processed_at/1000))
ORDER BY hour DESC;
```

### 2. Revenue by Currency (Last 7 Days)
```sql
SELECT 
    currency,
    COUNT(*) as transaction_count,
    SUM(amount) as total_revenue,
    AVG(amount) as avg_transaction_value
FROM transaction_processed_events 
WHERE processed_at >= (UNIX_TIMESTAMP(NOW()) - 604800) * 1000
    AND transaction_status = 'APPROVED'
    AND transaction_type = 'PAYMENT'
GROUP BY currency
ORDER BY total_revenue DESC;
```

### 3. Top Performing Merchants (Current Month)
```sql
SELECT 
    seller_id,
    COUNT(*) as total_transactions,
    SUM(CASE WHEN transaction_status = 'APPROVED' THEN 1 ELSE 0 END) as successful_transactions,
    SUM(CASE WHEN transaction_status = 'APPROVED' THEN amount ELSE 0 END) as total_revenue,
    (SUM(CASE WHEN transaction_status = 'APPROVED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as success_rate
FROM transaction_processed_events 
WHERE processed_at >= UNIX_TIMESTAMP(DATE_TRUNC('month', NOW())) * 1000
    AND transaction_type = 'PAYMENT'
GROUP BY seller_id
HAVING COUNT(*) >= 10
ORDER BY total_revenue DESC
LIMIT 20;
```

## Access & Usage

### Connection Details
- **Pinot Broker**: `localhost:8099` (default)
- **Query Console**: `http://localhost:9000` (Pinot UI)
- **JDBC URL**: `jdbc:pinot://localhost:8099/transaction_processed_events`

### Recommended Tools
- **BI Tools**: Grafana, Tableau, Power BI
- **SQL Clients**: DBeaver, DataGrip, Pinot Query Console
- **Programmatic Access**: Pinot Java Client, Python Client

### Query Best Practices
1. **Always include time filters** to limit data scan (use `processed_at` column)
2. **Use indexed columns** in WHERE clauses for better performance
3. **Aggregate data** for dashboard queries rather than row-level access
4. **Limit result sets** using appropriate LIMIT clauses
5. **Use EXPLAIN PLAN** to understand query execution

## Monitoring & Observability

### Key Metrics
- **Ingestion Rate**: Events per second
- **Query Latency**: P95 query response time
- **Error Rate**: Failed ingestion percentage
- **Data Freshness**: Event-to-query latency

### Alerting
- **High Error Rate**: > 1% ingestion failures
- **High Latency**: > 10s query response time
- **Data Freshness**: > 30s event lag
- **Missing Data**: > 5% completeness gap

### Dashboards
- Real-time ingestion monitoring
- Query performance metrics
- Business KPI dashboard
- Data quality dashboard

## Support & Contact

### Data Product Owner
- **Team**: Payment Processing Team
- **Contact**: payment-processing-team@paymentic.com
- **On-call**: #payments-oncall Slack channel

### Technical Support
- **Documentation**: Internal wiki `/data-products/transaction-processed-events`
- **Issue Tracking**: JIRA project key: PAYMENT-DATA
- **Emergency Contact**: #data-platform-emergency

### Change Management
- **Schema Changes**: Require 2-week notice and migration plan
- **Breaking Changes**: Require business stakeholder approval
- **Deprecation**: 6-month advance notice required

---

**Last Updated**: January 2024  
**Version**: 1.0  
**Data Product Classification**: Internal Use  
**Compliance**: PCI-DSS Level 1 compliant for payment data