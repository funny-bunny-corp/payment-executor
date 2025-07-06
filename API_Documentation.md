# Payment Executor Service - API Documentation

## Overview

The Payment Executor Service is a Quarkus-based microservice that serves as the core payment processing engine in the Paymentic payment ecosystem. It handles payment transactions, refunds, and coordinates with Payment Service Providers (PSPs) through a clean, event-driven architecture.

### Technology Stack
- **Framework**: Quarkus 3.6.4 (Java 17)
- **Database**: PostgreSQL with Hibernate ORM Panache
- **Messaging**: Apache Kafka with CloudEvents
- **Architecture**: Clean Architecture / Hexagonal Architecture
- **Observability**: OpenTelemetry, Micrometer with Prometheus
- **Health Checks**: Quarkus SmallRye Health

## Service Configuration

### Default Port
- **HTTP Port**: 8086

### Key Dependencies
- REST API with Jackson JSON processing
- Kafka messaging for event-driven communication
- PostgreSQL database for transaction persistence
- OpenTelemetry for distributed tracing
- Prometheus metrics for monitoring

## Domain Model

### Core Entities

#### Transaction
The central entity representing a payment or refund transaction.

**Location**: `com.paymentic.domain.transaction.Transaction`

**Properties**:
- `id`: UUID - Unique transaction identifier
- `paymentOrder`: PaymentOrderId - Associated payment order
- `amount`: String - Transaction amount
- `currency`: String - Currency code
- `buyerInfo`: BuyerInfo - Buyer information
- `cardInfo`: CardInfo - Card/payment method information
- `createdAt`: LocalDateTime - Transaction creation timestamp
- `status`: TransactionStatus - Current transaction status
- `situation`: TransactionSituation - Processing situation
- `type`: TransactionType - Transaction type (PAYMENT/REFUND)

**Methods**:
```java
// Create new transaction when payment order is received
public static Transaction newTransactionReceived(
    PaymentOrderId paymentOrder, 
    String amount, 
    String currency,
    BuyerInfo buyerInfo, 
    CardInfo cardInfo,
    TransactionType type
)

// Create new transaction when processing is complete
public static Transaction newTransactionProcessed(
    PaymentOrderId paymentOrder, 
    String amount, 
    String currency,
    BuyerInfo buyerInfo, 
    CardInfo cardInfo,
    String result,
    TransactionType type
)
```

#### Shared Value Objects

##### BuyerInfo
**Location**: `com.paymentic.domain.shared.BuyerInfo`

**Properties**:
- `document`: String - Buyer's document/ID
- `name`: String - Buyer's name

**Usage**:
```java
BuyerInfo buyer = new BuyerInfo("12345678901", "John Doe");
```

##### CardInfo
**Location**: `com.paymentic.domain.shared.CardInfo`

**Properties**:
- `cardInfo`: String - Card information
- `token`: String - Tokenized card reference

**Usage**:
```java
CardInfo card = new CardInfo("MASKED_CARD_INFO", "token_abc123");
```

##### PaymentOrderId
**Location**: `com.paymentic.domain.shared.PaymentOrderId`

**Properties**:
- `id`: UUID - Payment order unique identifier

### Transaction Enums

#### TransactionStatus
**Location**: `com.paymentic.domain.transaction.TransactionStatus`

**Values**:
- `APPROVED` - Transaction approved
- `DECLINED` - Transaction declined
- `UNDEFINED` - Status not yet determined

#### TransactionSituation
**Location**: `com.paymentic.domain.transaction.TransactionSituation`

**Values**:
- `RECEIVED` - Transaction received for processing
- `PROCESSED` - Transaction processing completed

#### TransactionType
**Location**: `com.paymentic.domain.transaction.TransactionType`

**Values**:
- `PAYMENT` - Payment transaction
- `REFUND` - Refund transaction

## Event-Driven Architecture

### Domain Events

#### TransactionProcessedEvent
**Location**: `com.paymentic.domain.transaction.events.TransactionProcessedEvent`

**Properties**:
- `transaction`: TransactionId - Transaction reference
- `seller`: SellerInfo - Seller information
- `payment`: PaymentOrderId - Payment order reference
- `checkoutId`: CheckoutId - Checkout session reference (for payments)
- `refundId`: RefundId - Refund reference (for refunds)
- `amount`: String - Transaction amount
- `currency`: String - Currency code
- `at`: LocalDateTime - Event timestamp
- `buyer`: BuyerInfo - Buyer information
- `status`: TransactionStatus - Final transaction status

**Factory Methods**:
```java
// Create event for checkout payment
public static TransactionProcessedEvent ofCheckout(
    TransactionId transaction,
    SellerInfo seller,
    PaymentOrderId payment,
    CheckoutId checkoutId,
    String amount,
    String currency,
    LocalDateTime at,
    BuyerInfo buyer,
    TransactionStatus status
)

// Create event for refund
public static TransactionProcessedEvent ofRefund(
    TransactionId transaction,
    SellerInfo seller,
    PaymentOrderId payment,
    RefundId refundId,
    String amount,
    String currency,
    LocalDateTime at,
    BuyerInfo buyer,
    TransactionStatus status
)
```

## Messaging APIs

### Kafka Topics Configuration

#### Incoming Events

##### Payment Created Events
- **Topic**: `risk-management`
- **Channel**: `payment-created`
- **CloudEvent Type**: `funny-bunny.xyz.risk-management.v1.risk.decision.approved`
- **Processor**: `PaymentCreatedProcessor`

**Event Structure**:
```json
{
  "specversion": "1.0",
  "type": "funny-bunny.xyz.risk-management.v1.risk.decision.approved",
  "source": "risk-management",
  "id": "event-uuid",
  "time": "2024-01-01T12:00:00Z",
  "datacontenttype": "application/json",
  "data": {
    "status": "APPROVED",
    "level": "LOW",
    "transaction": {
      "payment": {
        "id": "payment-uuid",
        "amount": "100.00",
        "currency": "USD",
        "status": "PENDING"
      },
      "order": {
        "id": "order-uuid"
      },
      "participants": {
        "seller": {
          "document": "seller-doc",
          "name": "Seller Name"
        },
        "buyer": {
          "document": "buyer-doc",
          "name": "Buyer Name"
        }
      }
    }
  }
}
```

##### Refund Created Events
- **Topic**: `payment-processing`
- **Channel**: `refund-created`
- **Processor**: `RefundCreatedProcessor`

#### Outgoing Events

##### Payment Order Started
- **Topic**: `payment-processing`
- **Channel**: `payment-order-started`
- **CloudEvent Type**: `funny-bunny.xyz.payment-processing.v1.payment-order.started`
- **Source**: `payment-executor`

**Event Structure**:
```json
{
  "specversion": "1.0",
  "type": "funny-bunny.xyz.payment-processing.v1.payment-order.started",
  "source": "payment-executor",
  "id": "event-uuid",
  "time": "2024-01-01T12:00:00Z",
  "subject": "payment-order-started",
  "datacontenttype": "application/json",
  "data": {
    "paymentOrderId": "order-uuid",
    "amount": "100.00",
    "currency": "USD",
    "seller": {
      "document": "seller-doc",
      "name": "Seller Name"
    },
    "processedAt": "2024-01-01"
  }
}
```

##### Transaction Approved
- **Topic**: `payment-processing`
- **Channel**: `transaction-approved`
- **CloudEvent Type**: `funny-bunny.xyz.payment-processing.v1.payment-order.approved`
- **Source**: `payment-executor`

##### Transaction Failed
- **Topic**: `payment-processing`
- **Channel**: `transaction-failed`
- **CloudEvent Type**: `funny-bunny.xyz.payment-processing.v1.payment-order.declined`
- **Source**: `payment-executor`

##### Refund Started
- **Topic**: `payment-processing`
- **Channel**: `refund-started`
- **CloudEvent Type**: `funny-bunny.xyz.payment-processing.v1.refund.started`
- **Source**: `payment-executor`

### Event Processors

#### PaymentCreatedProcessor
**Location**: `com.paymentic.adapter.kafka.in.PaymentCreatedProcessor`

**Purpose**: Processes incoming payment creation events from the risk management system.

**Method**:
```java
@Incoming("payment-created")
@Blocking
public CompletionStage<Void> process(Message<PaymentCreatedEvent> message)
```

**Processing Flow**:
1. Validates CloudEvent metadata
2. Checks if event should be handled (idempotency)
3. Fires `PaymentOrderReceived` domain event
4. Acknowledges message processing

#### RefundCreatedProcessor
**Location**: `com.paymentic.adapter.kafka.in.RefundCreatedProcessor`

**Purpose**: Processes incoming refund creation events.

**Method**:
```java
@Incoming("refund-created")
@Blocking
public CompletionStage<Void> process(Message<RefundCreatedEvent> message)
```

### Event Publishers

#### PaymentOrderStartedProcessor
**Location**: `com.paymentic.adapter.kafka.out.PaymentOrderStartedProcessor`

**Purpose**: Publishes payment order started events to downstream services.

#### TransactionResultProcessor
**Location**: `com.paymentic.adapter.kafka.out.TransactionResultProcessor`

**Purpose**: Publishes transaction result events (approved/declined).

## HTTP APIs

### External Service Integration

#### PSP REST Client
**Location**: `com.paymentic.adapter.http.PspRestClient`

**Interface**: REST client for Payment Service Provider integration

**Endpoint**: `POST /payments`

**Method**:
```java
@POST
PaymentResult pay(PaymentRequest request);
```

**Request Structure**:
```java
public class PaymentRequest {
    private String amount;
    // Constructor and getters
}
```

**Response Structure**:
```java
public class PaymentResult {
    private String status; // "APPROVED" or "DECLINED"
    // Constructor and getters
}
```

**Configuration**:
```properties
quarkus.rest-client."com.paymentic.adapter.http.PspRestClient".url=http://localhost:8082
```

**Usage Example**:
```java
@RestClient
PspRestClient pspRestClient;

PaymentRequest request = new PaymentRequest("100.00");
PaymentResult result = pspRestClient.pay(request);
```

### Health Check APIs

The service includes Quarkus SmallRye Health endpoints:

#### Liveness Probe
- **Endpoint**: `GET /q/health/live`
- **Purpose**: Indicates if the service is running

#### Readiness Probe
- **Endpoint**: `GET /q/health/ready`
- **Purpose**: Indicates if the service is ready to accept requests

#### Health Check
- **Endpoint**: `GET /q/health`
- **Purpose**: Overall health status

## Data Access Layer

### Transaction Repository
**Location**: `com.paymentic.domain.transaction.repositories.TransactionRepository`

**Interface**: Extends `PanacheRepository<Transaction>`

**Methods**:
```java
// Store transaction and return ID
public TransactionId store(Transaction transaction)

// Inherited from PanacheRepository
public void persist(Transaction transaction)
public Transaction findById(UUID id)
public List<Transaction> findAll()
// ... other Panache methods
```

**Usage Examples**:
```java
// Persist a new transaction
Transaction transaction = Transaction.newTransactionReceived(
    paymentOrderId, amount, currency, buyerInfo, cardInfo, TransactionType.PAYMENT
);
transactionRepository.persist(transaction);

// Store and get ID
TransactionId id = transactionRepository.store(transaction);

// Find by ID
Transaction found = transactionRepository.findById(transactionId);
```

## Business Logic Layer

### Domain Event Listeners

#### PaymentOrderReceivedListener
**Location**: `com.paymentic.domain.transaction.listeners.PaymentOrderReceivedListener`

**Purpose**: Handles payment order received events and orchestrates payment processing.

**Method**:
```java
@Transactional
void paymentOrderReceived(@Observes PaymentOrderReceived paymentOrder)
```

**Processing Flow**:
1. Creates and persists initial transaction record
2. Fires `PaymentOrderStartedEvent`
3. Calls PSP integration for payment processing
4. Creates and persists processed transaction record
5. Fires `TransactionProcessedEvent`

#### RefundReceivedListener
**Location**: `com.paymentic.domain.transaction.listeners.RefundReceivedListener`

**Purpose**: Handles refund received events and orchestrates refund processing.

**Method**:
```java
@Transactional
void refundReceived(@Observes RefundReceived refund)
```

## Configuration

### Application Properties

#### Database Configuration
```properties
# PostgreSQL datasource
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/postgres

# Hibernate ORM
quarkus.hibernate-orm.database.generation=update
```

#### Kafka Configuration
```properties
# Kafka bootstrap servers
kafka.bootstrap.servers=localhost:9092

# Incoming channels
mp.messaging.incoming.payment-created.connector=smallrye-kafka
mp.messaging.incoming.payment-created.topic=risk-management
mp.messaging.incoming.refund-created.connector=smallrye-kafka
mp.messaging.incoming.refund-created.topic=payment-processing

# Outgoing channels
mp.messaging.outgoing.payment-order-started.connector=smallrye-kafka
mp.messaging.outgoing.payment-order-started.topic=payment-processing
mp.messaging.outgoing.transaction-approved.connector=smallrye-kafka
mp.messaging.outgoing.transaction-approved.topic=payment-processing
```

#### OpenTelemetry Configuration
```properties
# Datasource telemetry
quarkus.datasource.jdbc.telemetry=true

# OTLP endpoint
quarkus.otel.exporter.otlp.traces.endpoint=http://localhost:4317
```

## Monitoring and Observability

### Metrics
- **Prometheus metrics**: Available at `/q/metrics`
- **Database metrics**: Automatically instrumented via OpenTelemetry
- **HTTP client metrics**: REST client calls to PSP are traced

### Tracing
- **OpenTelemetry tracing**: Distributed tracing across service boundaries
- **Database tracing**: SQL queries are automatically traced
- **Kafka tracing**: Message publishing and consumption are traced

### Logging
- **Framework**: JBoss Logging
- **Key log points**:
  - Payment order processing start/completion
  - PSP integration calls
  - Event processing status
  - Transaction persistence operations

## Development and Deployment

### Running the Service

#### Development Mode
```bash
./mvnw compile quarkus:dev
```
- Enables live reload
- Dev UI available at `http://localhost:8086/q/dev/`

#### Production Build
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

#### Native Build
```bash
./mvnw package -Pnative
./target/payment-executor-1.0-SNAPSHOT-runner
```

#### Docker Build
```bash
docker build -f Dockerfile -t payment-executor .
docker run -p 8086:8086 payment-executor
```

### Environment Profiles

The service supports multiple configuration profiles:
- `application.properties` - Default configuration
- `application-prod.properties` - Production configuration
- `application-kind.properties` - Kubernetes deployment configuration

### Dependencies

#### Required External Services
- **PostgreSQL**: Database for transaction persistence
- **Apache Kafka**: Message broker for event streaming
- **Payment Service Provider**: External PSP service for payment processing
- **OpenTelemetry Collector**: For distributed tracing (optional)

#### Health Dependencies
The service health depends on:
- Database connectivity
- Kafka broker availability
- PSP service availability

## Error Handling and Resilience

### Event Processing
- **Idempotency**: Events are processed only once using event repository
- **Dead Letter Queue**: Failed messages can be configured for retry
- **Blocking Processing**: Payment processing is synchronous to ensure consistency

### Database Transactions
- **Transactional Boundaries**: All business operations are wrapped in transactions
- **Rollback on Failure**: Database changes are rolled back on processing failures

### PSP Integration
- **Synchronous Calls**: Payment processing waits for PSP response
- **Error Propagation**: PSP failures are propagated to transaction status

## Security Considerations

### Data Protection
- **Card Information**: Stored in tokenized format
- **Buyer Information**: Stored with document and name fields
- **Transaction Tracing**: Full audit trail via OpenTelemetry

### API Security
- **Internal Service**: No external HTTP endpoints exposed
- **Event-Driven**: Communication via secure Kafka topics
- **PSP Integration**: Configured REST client with proper authentication

## Integration Examples

### Processing a Payment

1. **Risk Management Service** publishes a payment created event:
```json
{
  "specversion": "1.0",
  "type": "funny-bunny.xyz.risk-management.v1.risk.decision.approved",
  "source": "risk-management",
  "data": {
    "transaction": {
      "payment": {
        "id": "payment-123",
        "amount": "50.00",
        "currency": "USD"
      }
    }
  }
}
```

2. **Payment Executor** processes the event:
   - Creates transaction record
   - Calls PSP for payment processing
   - Updates transaction with result
   - Publishes result event

3. **Downstream Services** receive transaction result:
```json
{
  "specversion": "1.0",
  "type": "funny-bunny.xyz.payment-processing.v1.payment-order.approved",
  "source": "payment-executor",
  "data": {
    "transactionId": "txn-456",
    "paymentOrderId": "payment-123",
    "status": "APPROVED"
  }
}
```

### Monitoring Integration

Query transaction metrics:
```promql
# Transaction processing rate
rate(transaction_processed_total[5m])

# Transaction success rate
rate(transaction_approved_total[5m]) / rate(transaction_processed_total[5m])

# Average processing time
histogram_quantile(0.95, rate(transaction_processing_duration_seconds_bucket[5m]))
```

This comprehensive documentation covers all public APIs, functions, and components of the Payment Executor service, providing developers with the necessary information to integrate with and extend the service.