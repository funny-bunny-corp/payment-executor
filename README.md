# Payment Processing Frontend

A modern React TypeScript frontend application for the Paymentic Payment Processing System. This application provides a beautiful and intuitive interface for managing payments, transactions, and refunds with a light blue and light purple color scheme.

## 🚀 Features

### Core Functionality
- **Dashboard**: Overview with payment statistics and recent transactions
- **Payment Processing**: Step-by-step payment creation form
- **Transaction Management**: Advanced filtering, pagination, and detailed views
- **Refund Processing**: Handle refund requests and tracking
- **Analytics**: Payment insights and reporting (coming soon)

### UI/UX Features
- **Responsive Design**: Optimized for desktop, tablet, and mobile devices
- **Modern Material Design**: Clean and professional interface using Material-UI
- **Beautiful Color Scheme**: Light blue (#87CEEB) and light purple (#DDA0DD) gradient theme
- **Interactive Components**: Real-time feedback and smooth animations
- **Accessibility**: WCAG compliant with proper contrast ratios and keyboard navigation

### Technical Features
- **TypeScript**: Full type safety and enhanced development experience
- **React Router**: Client-side routing for seamless navigation
- **API Integration**: RESTful API communication with error handling
- **Mock Data**: Development-ready with mock service for testing
- **Real-time Updates**: Live dashboard statistics and transaction status

## 🛠️ Technology Stack

- **Frontend Framework**: React 18 with TypeScript
- **UI Library**: Material-UI (MUI) v5
- **Styling**: Styled-components + Material-UI theme system
- **Routing**: React Router v6
- **HTTP Client**: Axios for API communication
- **State Management**: React Hooks (useState, useEffect)
- **Icons**: Material-UI Icons
- **Build Tool**: Create React App with TypeScript template

## 📋 Prerequisites

- Node.js (v16 or higher)
- npm or yarn package manager
- Payment Executor Service running on port 8086 (for production use)

## 🚀 Getting Started

### Installation

1. **Clone the repository** (if not already in the workspace):
   ```bash
   git clone <repository-url>
   cd frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm start
   ```

4. **Open your browser** and navigate to `http://localhost:3000`

### Available Scripts

- `npm start` - Runs the app in development mode
- `npm test` - Launches the test runner
- `npm run build` - Builds the app for production
- `npm run eject` - Ejects from Create React App (not recommended)

## 🏗️ Project Structure

```
frontend/
├── src/
│   ├── components/         # Reusable UI components
│   │   └── Layout.tsx      # Main layout with navigation
│   │   ├── Dashboard.tsx   # Dashboard overview
│   │   ├── PaymentScreen.tsx # Payment creation form
│   │   └── TransactionsScreen.tsx # Transaction management
│   ├── services/          # API service layer
│   │   └── api.ts         # Payment API service with mock data
│   ├── types/             # TypeScript type definitions
│   │   └── index.ts       # API and component types
│   ├── utils/             # Utility functions and configurations
│   │   └── theme.ts       # Material-UI theme configuration
│   ├── App.tsx            # Main application component
│   └── index.tsx          # Application entry point
├── public/                # Static assets
├── package.json           # Project dependencies
└── README.md              # This file
```

## 🎨 Design System

### Color Palette
- **Primary**: Light Blue (`#87CEEB`) to Sky Blue (`#ADD8E6`)
- **Secondary**: Light Purple (`#DDA0DD`) to Lavender (`#E6E6FA`)
- **Background**: Light Gray (`#F8F9FA`)
- **Success**: Green (`#48BB78`)
- **Error**: Red (`#F56565`)
- **Warning**: Yellow (`#ECC94B`)

### Typography
- **Font Family**: Inter, Roboto, Helvetica, Arial
- **Headings**: Various weights (400-700) with appropriate sizing
- **Body Text**: Consistent line-height and spacing

### Components
- **Cards**: Rounded corners (12px) with subtle shadows
- **Buttons**: Gradient backgrounds with hover effects
- **Forms**: Clean input fields with proper validation
- **Tables**: Responsive with pagination and sorting

## 🔌 API Integration

The application connects to the Payment Executor Service API (default: `http://localhost:8086`).

### API Endpoints Used
- `GET /dashboard/stats` - Dashboard statistics
- `GET /transactions` - Transaction listing with filters
- `GET /transactions/:id` - Individual transaction details
- `POST /payments` - Create new payment
- `POST /refunds` - Process refund requests
- `GET /q/health` - Health check

### Mock Service
For development and testing, the application includes a mock service that simulates API responses:
- Realistic data generation
- Simulated network delays
- Success/failure scenarios
- Pagination support

## 📱 Screens Overview

### Dashboard
- **Payment Statistics**: Total transactions, amounts, success rates
- **Recent Transactions**: Quick overview of latest payments
- **Interactive Cards**: Click-through navigation to detailed screens
- **Real-time Updates**: Auto-refreshing statistics

### Payment Processing
- **Step-by-Step Form**: Guided payment creation process
- **Validation**: Real-time form validation with error messages
- **Multi-currency Support**: USD, EUR, GBP, BRL
- **Progress Tracking**: Visual stepper showing completion status
- **Result Feedback**: Clear success/failure messaging

### Transaction Management
- **Advanced Filtering**: Status, type, date range, currency, amount
- **Pagination**: Configurable page sizes with navigation
- **Detailed Views**: Modal dialogs with complete transaction information
- **Export Capabilities**: Ready for CSV/PDF export features
- **Search Functionality**: Quick transaction lookup

### Future Screens
- **Refunds**: Dedicated refund processing and tracking
- **Analytics**: Charts and insights for payment trends
- **Settings**: User preferences and system configuration

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the root directory:

```env
REACT_APP_API_BASE_URL=http://localhost:8086
REACT_APP_MOCK_MODE=true
```

### API Service Configuration
Edit `src/services/api.ts` to customize:
- Base URL for the Payment Executor Service
- Request/response interceptors
- Error handling behavior
- Mock data responses

## 🚀 Deployment

### Production Build
```bash
npm run build
```

This creates an optimized production build in the `build/` folder.

### Deployment Options
- **Static Hosting**: Netlify, Vercel, GitHub Pages
- **CDN**: AWS CloudFront, Cloudflare
- **Server**: Nginx, Apache, Express.js
- **Containerization**: Docker with multi-stage builds

### Docker Deployment
```dockerfile
FROM node:16-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 🧪 Testing

### Running Tests
```bash
npm test
```

### Test Coverage
- Component unit tests
- API service tests
- Integration testing
- End-to-end testing (coming soon)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🛠️ Troubleshooting

### Common Issues

1. **API Connection Errors**
   - Ensure the Payment Executor Service is running on port 8086
   - Check network connectivity and CORS settings
   - Verify API endpoint URLs in the service configuration

2. **Build Errors**
   - Clear node_modules and reinstall dependencies
   - Check for TypeScript errors in the console
   - Ensure all required dependencies are installed

3. **Styling Issues**
   - Clear browser cache and reload
   - Check for Material-UI theme conflicts
   - Verify CSS import order

### Performance Optimization
- Enable code splitting for larger applications
- Implement React.memo for expensive components
- Use useMemo and useCallback for heavy computations
- Optimize bundle size with webpack-bundle-analyzer

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation in the main project
- Review the Payment Executor Service logs for backend issues

---

**Built with ❤️ for the Paymentic Payment Processing System**
