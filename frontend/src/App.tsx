import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import type { RootState } from './app/store'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './features/auth/pages/LoginPage'
import RegisterPage from './features/auth/pages/RegisterPage'
import VerifyEmailPage from './features/auth/pages/VerifyEmailPage'
import ForgotPasswordPage from './features/auth/pages/ForgotPasswordPage'
import VerifyResetOtpPage from './features/auth/pages/VerifyResetOtpPage'
import ResetPasswordPage from './features/auth/pages/ResetPasswordPage'
import DashboardLayout from './layouts/DashboardLayout'
import DashboardPage from './features/dashboard/pages/DashboardPage'
import ClientsPage from './features/clients/pages/ClientsPage'
import ClientDetailPage from './features/clients/pages/ClientDetailPage'
import ProjectsPage from './features/projects/pages/ProjectsPage'
import ProjectDetailPage from './features/projects/pages/ProjectDetailPage'
import ComingSoonPage from './pages/ComingSoonPage'

function App() {
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated)

  return (
    <BrowserRouter>
      <Routes>
        {/* Auth Routes */}
        <Route
          path="/login"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />}
        />
        <Route
          path="/register"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <RegisterPage />}
        />
        <Route
          path="/verify-email"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <VerifyEmailPage />}
        />
        <Route
          path="/forgot-password"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <ForgotPasswordPage />}
        />
        <Route
          path="/verify-reset-otp"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <VerifyResetOtpPage />}
        />
        <Route
          path="/reset-password"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <ResetPasswordPage />}
        />

        {/* Protected Routes */}
        <Route element={<ProtectedRoute isAuthenticated={isAuthenticated} />}>
          <Route element={<DashboardLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/clients" element={<ClientsPage />} />
            <Route path="/clients/:id" element={<ClientDetailPage />} />
            <Route path="/projects" element={<ProjectsPage />} />
            <Route path="/projects/:id" element={<ProjectDetailPage />} />
            <Route path="/organizations" element={<ComingSoonPage title="Organizations" description="Organization management is coming soon. Manage teams, members, and roles from here." />} />
            <Route path="/settings" element={<ComingSoonPage title="Settings" description="Account and workspace settings are being built for FreelanceHub." />} />
          </Route>
        </Route>

        {/* Redirect */}
        <Route path="/" element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
