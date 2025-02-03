# Role-Based Appointment Booking System

## Overview
A comprehensive healthcare appointment management system with role-based access control for Users , Doctors, and Administrators. The system provides tailored interfaces and functionalities for each role while maintaining data security and booking integrity.

### Core Entities
- Users 
- Doctors
- Administrators
- Appointments

## System Architecture

### 1. Authentication
- **Login Requirements**
  - Email
  - Password
  - Role Selection (User/Doctor/Admin)
- **Security Flow**
  - ✓ Credential validation
  - ✓ Role-specific dashboard routing
  - ✓ Password reset functionality
  - ✓ Email-based registration

### 2. User (Patient) Portal
#### Features
- **Doctor Directory**
  - Browse all registered doctors
  - View detailed doctor profiles
  - Search by specialty or name

- **Appointment Booking**
  - Select doctor and time slot
  - Real-time availability checking
  - Booking confirmation system

- **Patient Dashboard**
  - Personal information management
  - Appointment history
  - Upcoming appointments

### 3. Doctor Portal
#### Features
- **Profile Management**
  - Professional details
  - Specialty information
  - Contact details

- **Appointment Management**
  - View scheduled appointments
  - Patient information access
  - Schedule management

### 4. Administrator Portal
#### Features
- **User Management**
  - Create/Read/Update/Delete users
  - Manage user profiles
  - Reset user credentials

- **Doctor Management**
  - Onboard new doctors
  - Update doctor information
  - Manage specialties

- **System Administration**
  - Appointment oversight
  - System configuration
  - Access control management

## Technical Stack

### Frontend
- **Platform**: Android Native
- **Language**: Java
- **UI**: XML Layouts
- **Key Components**:
  - Activities for screen management
  - Fragments for modular UI
  - RecyclerViews for list displays
  - Custom DialogFragments for pop-ups
  - XML-based layouts for responsive design

### Backend
- **Database**: Firebase Firestore
- **Key Features**:
  - Real-time data synchronization
  - Cloud-based NoSQL database
  - Automatic offline persistence
  - Scalable data structure
  - Built-in security rules

### Data Structure (Firestore Collections)
- **users/**
  - `userId`
    - profile_data: {...}
    - appointments: [...]
    
- **doctors/**
  - `doctorId`
    - profile_data: {...}
    - specialty: string
    - appointments: [...]
    
- **appointments/**
  - `appointmentId`
    - userId: string
    - doctorId: string
    - dateTime: timestamp
    - status: string

### Authentication
- Firebase Authentication
  - Email/password authentication
  - Role-based user management
  - Secure token handling

## Technical Requirements

### Data Validation
- Prevent double-booking
- Ensure user registration before booking
- Maintain data isolation between users
- Enforce doctor-specific appointment visibility

### Security Measures
- Role-based access control
- Data privacy protection
- Secure authentication
- Session management

### User Interface
- Responsive design
- Intuitive navigation
- Clear error messaging
- Search optimization

## Data Models

### User Profile
- Name
- Email
- Password (encrypted)
- Gender
- Birth Date
- Blood Type
- Height
- Weight
- CONSTRAINTS:
  - Blood Type must be one of: 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'
  - Gender must be one of: 'Male', 'Female', 'Other'

### Doctor Profile
- Name
- Email
- Password (encrypted)
- Gender
- Birth Date
- Blood Type
- Height
- Weight
- Specialty
- CONSTRAINTS:
  - Blood Type must be one of: 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'
  - Gender must be one of: 'Male', 'Female', 'Other'

### Admin Profile
- AdminID
- Name
- Email
- Password (encrypted)
- Gender
- Blood Type
- CONSTRAINTS:
  - Blood Type must be one of: 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'
  - Gender must be one of: 'Male', 'Female', 'Other'

### Appointment
- User ID
- Doctor ID
- Date
- Time
- Status
- Completion DateTime (null until completed)

## Business Rules
1. Users can only view their own appointments
2. Doctors can only access their assigned appointments
3. Administrators have full system access
4. No duplicate appointments allowed
5. Only registered users can book appointments
6. Appointments can be marked as 'Completed' with a completion timestamp
7. Blood type must be one of the standard types (A+, A-, B+, B-, AB+, AB-, O+, O-)
8. Gender must be one of: Male, Female, Other
9. Appointments are cascade deleted if associated user or doctor is deleted

## Error Handling
- Invalid credentials: "Please check your credentials again"
- Booking conflict: "Time slot already taken"
- Registration required: "Please register to book appointments"
- Success message: "Booked successfully"

---

## Database Schema

### Collections Structure

#### users

```json
{
"userId": {
"name": "string",
"email": "string",
"password": "string (encrypted)",
"gender": "string (enum: Male, Female, Other)",
"birthDate": "timestamp",
"bloodType": "string (enum: A+, A-, B+, B-, AB+, AB-, O+, O-)",
"height": "number",
"weight": "number",
"createdAt": "timestamp",
"updatedAt": "timestamp"
}
}
```

#### doctors
```json
{
  "doctorId": {
    "name": "string",
    "email": "string",
    "password": "string (encrypted)",
    "gender": "string (enum: Male, Female, Other)",
    "birthDate": "timestamp",
    "bloodType": "string (enum: A+, A-, B+, B-, AB+, AB-, O+, O-)",
    "height": "number",
    "weight": "number",
    "specialty": "string",
    "createdAt": "timestamp",
    "updatedAt": "timestamp"
  }
}
```

#### admins
```json
{
  "adminId": {
    "name": "string",
    "email": "string",
    "password": "string (encrypted)",
    "gender": "string (enum: Male, Female, Other)",
    "bloodType": "string (enum: A+, A-, B+, B-, AB+, AB-, O+, O-)",
    "createdAt": "timestamp",
    "updatedAt": "timestamp"
  }
}
```

#### appointments
```json
{
  "appointmentId": {
    "userId": "string (reference: users)",
    "doctorId": "string (reference: doctors)",
    "dateTime": "timestamp",
    "status": "string (enum: SCHEDULED, COMPLETED, CANCELLED)",
    "completionDateTime": "timestamp | null",
    "createdAt": "timestamp",
    "updatedAt": "timestamp"
  }
}
```

## Project Structure

```plaintext
appointment-booking-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/appointmentbooking/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   │   └── RegisterActivity.java
│   │   │   │   │   ├── user/
│   │   │   │   │   │   ├── UserDashboardActivity.java
│   │   │   │   │   │   ├── BookAppointmentActivity.java
│   │   │   │   │   │   └── UserProfileActivity.java
│   │   │   │   │   ├── doctor/
│   │   │   │   │   │   ├── DoctorDashboardActivity.java
│   │   │   │   │   │   └── DoctorProfileActivity.java
│   │   │   │   │   ├── admin/
│   │   │   │   │   │   ├── AdminDashboardActivity.java
│   │   │   │   │   │   └── ManagementActivity.java
│   │   │   │   │   └── MainActivity.java
│   │   │   │   │
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── AppointmentAdapter.java
│   │   │   │   │   ├── DoctorListAdapter.java
│   │   │   │   │   └── UserListAdapter.java
│   │   │   │   │
│   │   │   │   ├── fragments/
│   │   │   │   │   ├── AppointmentFragment.java
│   │   │   │   │   ├── ProfileFragment.java
│   │   │   │   │   └── DoctorListFragment.java
│   │   │   │   │
│   │   │   │   ├── models/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Doctor.java
│   │   │   │   │   ├── Admin.java
│   │   │   │   │   └── Appointment.java
│   │   │   │   │
│   │   │   │   ├── utils/
│   │   │   │   │   ├── Constants.java
│   │   │   │   │   ├── FirebaseUtils.java
│   │   │   │   │   ├── DateUtils.java
│   │   │   │   │   └── ValidationUtils.java
│   │   │   │   │
│   │   │   │   └── services/
│   │   │   │       ├── AuthService.java
│   │   │   │       ├── AppointmentService.java
│   │   │   │       └── UserService.java
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── fragment_appointment.xml
│   │   │   │   │   └── item_appointment.xml
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── themes.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   │
│   │   │   │   ├── drawable/
│   │   │   │   └── mipmap/
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/
│   │
│   ├── build.gradle
│   └── google-services.json
│
├── gradle/
├── build.gradle
└── settings.gradle
```
docs/Context.md

### Key Components

#### Activities
- **auth/**: Authentication-related activities
- **user/**: Patient-specific activities
- **doctor/**: Doctor-specific activities
- **admin/**: Administrator activities

#### Core Components
- **adapters/**: RecyclerView adapters for lists
- **fragments/**: Reusable UI components
- **models/**: Data classes matching Firestore schema
- **utils/**: Helper classes and utilities
- **services/**: Firebase and business logic

#### Resources
- **layout/**: UI layout files
- **values/**: App resources
- **drawable/**: Images and icons
- **mipmap/**: App launcher icons

### Build Configuration

```gradle
// app/build.gradle
dependencies {
    // Firebase
    implementation 'com.google.firebase:firebase-firestore:24.9.1'
    implementation 'com.google.firebase:firebase-auth:22.3.0'
    
    // AndroidX
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    
    // Material Design
    implementation 'com.google.android.material:material:1.10.0'
}
```

// ... existing code ...

### UI/UX Implementation

#### Empty Views
Each list-based screen includes an empty view state that is displayed when no data is available:

- **Appointment List**
  ```xml
  <!-- Empty view for no appointments -->
  <LinearLayout
      android:id="@+id/empty_appointments_view"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:gravity="center"
      android:orientation="vertical"
      android:visibility="gone">
      
      <ImageView
          android:layout_width="120dp"
          android:layout_height="120dp"
          android:src="@drawable/ic_empty_appointments"/>
          
      <TextView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="No appointments yet"
          android:textSize="18sp"/>
  </LinearLayout>
  ```

- **Doctor List**
  - Empty state when no doctors are registered
  - "No doctors available" message with illustration

- **User List (Admin)**
  - Empty state for no registered users
  - "No users found" message with illustration

#### Empty State Triggers
- Initial app state with no data
- Search results with no matches
- Filtered lists with no results
- Deleted or removed items leaving empty list

#### Implementation Pattern
Each RecyclerView adapter should:
1. Check for empty data sets
2. Toggle empty view visibility
3. Handle transitions between empty and populated states

Example Implementation:

java
public void updateList(List<Item> items) {
    this.items = items;
    emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    recyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    notifyDataSetChanged();
}

This structure follows Android best practices and provides a clear separation of concerns. Each component has its specific responsibility and location in the project hierarchy.

*Note: This documentation is subject to updates and modifications as the system evolves.*

This addition emphasizes that the app properly handles empty states across all list views, which is an important UX consideration. Would you like me to expand on any particular aspect of the empty views implementation?

# Development Plan - Role-Based Appointment Booking System

## Phase 1: Project Setup & Authentication
1. **Initial Project Setup**
   - Create Android project with required dependencies
   - Configure Firebase project
   - Add google-services.json
   - Setup basic project structure

2. **Authentication Implementation**
   - Create LoginActivity and layouts
   - Implement Firebase Authentication
   - Create RegisterActivity for user signup
   - Add role selection (User/Doctor/Admin)
   - Implement password reset functionality

## Phase 2: Core Models & Database
1. **Data Models**
   - Create User.java model
   - Create Doctor.java model
   - Create Admin.java model
   - Create Appointment.java model

2. **Firebase Setup**
   - Setup Firestore collections structure
   - Implement FirebaseUtils helper class
   - Create basic CRUD operations
   - Setup security rules

## Phase 3: User (Patient) Features
1. **User Dashboard**
   - Create UserDashboardActivity
   - Implement profile management
   - Show appointment history
   - Display upcoming appointments

2. **Doctor Browsing**
   - Create doctor listing interface
   - Implement doctor search functionality
   - Add doctor profile viewing
   - Create DoctorListAdapter

3. **Appointment Booking**
   - Create BookAppointmentActivity
   - Implement date/time selection
   - Add real-time availability checking
   - Implement booking confirmation

## Phase 4: Doctor Features
1. **Doctor Dashboard**
   - Create DoctorDashboardActivity
   - Implement profile management
   - Show scheduled appointments
   - Add appointment management features

2. **Schedule Management**
   - Create schedule setting interface
   - Implement availability management
   - Add patient information access
   - Create appointment status updates

## Phase 5: Admin Features
1. **Admin Dashboard**
   - Create AdminDashboardActivity
   - Implement user management interface
   - Add doctor management features
   - Create system configuration options

2. **Management Features**
   - Implement CRUD for users
   - Add doctor onboarding
   - Create specialty management
   - Add system oversight tools

## Phase 6: Testing & Refinement
1. **Testing**
   - Unit tests for core functionality
   - Integration tests for Firebase
   - UI/UX testing
   - Role-based access testing

2. **Refinement**
   - Implement error handling
   - Add loading states
   - Create empty states
   - Polish UI/UX

## Phase 7: Final Steps
1. **Documentation**
   - Code documentation
   - User manual
   - API documentation
   - Deployment guide

2. **Deployment**
   - Final testing
   - Performance optimization
   - Release preparation
   - Play Store submission

## Development Guidelines
1. Follow one phase at a time
2. Test each component before moving to next
3. Maintain consistent code style
4. Regular commits with clear messages
5. Document as you develop

## Priority Order
1. Authentication (Critical)
2. Core Models & Database (Foundation)
3. User Features (Basic Functionality)
4. Doctor Features (Service Provider)
5. Admin Features (Management)
6. Testing & Refinement (Quality)
7. Documentation & Deployment (Release)

This plan provides a structured approach to building the application. Each phase builds upon the previous one, ensuring a solid foundation and manageable development process. Would you like to start with Phase 1?
