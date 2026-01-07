# BitDue - Personal Finance Management App

<div align="center">
  <h3>💰 Take Control of Your Finances 💰</h3>
  <p>A modern, feature-rich Android finance management application built with Jetpack Compose and Material Design 3</p>
</div>

---

## 📱 About

**BitDue** is a comprehensive personal finance management application designed to help users track expenses, manage budgets, set savings goals, and gain insights into their spending habits. Built using the latest Android development best practices with Kotlin, Jetpack Compose, and Material Design 3.

## ✨ Features

### 🏠 Core Features
- **Dashboard Overview** - Beautiful home screen with personalized greetings and quick financial summary
- **Transaction Management** - Track income and expenses with detailed categorization
- **Budget Tracking** - Set and monitor budgets by category with real-time progress
- **Savings Goals** - Create and track progress toward financial goals
- **Reports & Analytics** - Comprehensive financial reports with visual charts

### 📊 Enhanced Reports
- **Dynamic Date Range Filtering**
  - Monthly view for current month data
  - Yearly view for annual financial overview
  - Custom date range picker for flexible reporting
- **Visual Analytics**
  - Pie charts for spending breakdown by category
  - Real-time income vs expense summaries
  - Category-wise spending details with progress indicators

### 💳 Premium Balance Display
- **Gradient-Enhanced Balance Card**
  - Beautiful horizontal gradient design
  - Elevated card with 8dp shadow for depth
  - Available balance calculation (Total - Savings)
  - Color-coded income/expense indicators
  - Savings breakdown with contextual information

### 🔐 Security & Permissions
- **Biometric Authentication** - Secure app access with fingerprint/face ID
- **Runtime Permission Management**
  - Smart permission requests for biometric features
  - Android 13+ notification permissions
  - User-friendly rationale dialogs for denied permissions

### 🎨 Theming & Appearance
- **Dark/Light Mode** - System-integrated theme switching
- **Dynamic Colors** - Material You color theming (Android 12+)
- **Modern UI Components** - Polished Material Design 3 components

---

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Dependency Injection**: Manual injection via Application class
- **Asynchronous**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Compose

### Project Structure
```
app/
├── data/
│   ├── local/
│   │   ├── dao/          # Room Data Access Objects
│   │   ├── database/     # Database configuration
│   │   └── entity/       # Database entities
│   ├── models/           # Data models
│   ├── preferences/      # User preferences (DataStore)
│   └── repository/       # Repository pattern implementations
├── ui/
│   ├── components/       # Reusable UI components
│   │   └── charts/       # Chart components (Pie, Bar)
│   ├── screens/          # Screen composables
│   │   └── auth/         # Authentication screens
│   ├── theme/            # Material Design theming
│   └── viewmodel/        # ViewModels for state management
├── utils/                # Utility classes (e.g., PermissionUtils)
└── navigation/           # Navigation configuration
```

### Key Components

#### Data Layer
- **Entities**: `TransactionEntity`, `CategoryEntity`, `BudgetEntity`, `GoalEntity`
- **DAOs**: Type-safe database queries using Room
- **Repositories**: Single source of truth for data operations

#### UI Layer
- **Screens**: All major app screens (Home, Transactions, Budgets, Goals, Reports, Settings)
- **Components**: Reusable UI elements (BalanceCard, TransactionItem, Charts)
- **ViewModels**: State management with StateFlow

#### Features
- **Date Range Filtering**: `DateRangeFilter` enum with MONTHLY, YEARLY, CUSTOM options
- **Permission Handling**: `PermissionUtils` for runtime permission checks
- **User Preferences**: DataStore for persistent settings

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog or later
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Gradle**: 8.0+
- **Kotlin**: 1.9+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/cloneyjay/bitdue.git
   cd bitdue
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Wait for Gradle to sync dependencies
   - Resolve any dependency conflicts if necessary

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

---

## 📖 Usage Guide

### Adding Transactions
1. Tap the **"+"** button on the home screen
2. Select transaction type (Income/Expense)
3. Choose a category and enter amount
4. Add optional notes and date
5. Save the transaction

### Creating Budgets
1. Navigate to the Budgets screen
2. Tap "Add Budget"
3. Select a category and set amount
4. Choose the budget period
5. Monitor spending progress in real-time

### Setting Goals
1. Go to the Goals screen
2. Create a new savings goal
3. Set target amount and deadline
4. Add progress incrementally
5. Track completion percentage

### Viewing Reports
1. Open the Reports screen
2. Select date range (Monthly/Yearly/Custom)
3. View income vs expense summaries
4. Analyze spending breakdown by category
5. Review detailed category statistics

### Configuring Settings
1. Access Settings from the navigation menu
2. Toggle dark mode or dynamic colors
3. Enable biometric authentication (requires permission)
4. Manage notification preferences
5. Update profile information

---

## 🎨 UI Highlights

### Enhanced Balance Card
- **Gradient Background**: Smooth transition between primary and secondary colors
- **Available Balance**: Shows money available after savings commitments
- **Smart Layout**: Income and expense displayed in color-coded cards
- **Responsive Design**: Adapts to different screen sizes

### Modern Reports Design
- **Filter Chips**: Easy-to-use date range selection
- **Color-Coded Metrics**: Green for income, red for expenses
- **Interactive Charts**: Pie chart for spending visualization
- **Progress Indicators**: Visual progress bars for each category

---

## 🔒 Permissions

### Required Permissions
- **INTERNET**: For potential cloud sync features
- **ACCESS_NETWORK_STATE**: Network connectivity checks

### Optional Permissions
- **USE_BIOMETRIC**: Biometric authentication (requested at runtime)
- **POST_NOTIFICATIONS**: Push notifications on Android 13+ (requested at runtime)
- **VIBRATE**: Haptic feedback

---

## 🛠️ Built With

| Technology | Purpose |
|-----------|---------|
| Kotlin | Primary programming language |
| Jetpack Compose | Modern declarative UI framework |
| Material Design 3 | UI design system |
| Room | Local database persistence |
| Coroutines | Asynchronous programming |
| Flow | Reactive data streams |
| ViewModel | UI state management |
| Navigation Compose | In-app navigation |
| DataStore | Preferences storage |
| Firebase (Optional) | Authentication & cloud storage |

---

## 📝 Recent Updates

### Version 1.0.0
- ✅ Initial release with core features
- ✅ Transaction and budget management
- ✅ Savings goals tracking
- ✅ Basic reports and analytics

### Latest Enhancements
- ✅ **Enhanced Balance Card** with gradient design and available balance display
- ✅ **Dynamic Date Range Filtering** for reports (Monthly, Yearly, Custom)
- ✅ **Runtime Permission Management** for biometric auth and notifications
- ✅ **Improved Reports UI** with streamlined chart displays
- ✅ **Permission Utility Class** for centralized permission handling

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Maintain consistent formatting

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---


<div align="center">
  <p>Made with ❤️ using Jetpack Compose</p>
  <p>⭐ Star this repo if you find it helpful!</p>
</div>