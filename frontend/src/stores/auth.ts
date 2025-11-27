import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || null)
  const isClient = computed(() => userRole.value === 'CLIENT')
  const isCourier = computed(() => userRole.value === 'LIVREUR')
  const isManager = computed(() => userRole.value === 'GESTIONNAIRE')
  const isAdmin = computed(() => userRole.value === 'ADMIN')

  // Actions
  async function login(credentials: { email: string; password: string }) {
    try {
      const response = await api.post('/auth/login', credentials)
      const { token: authToken, user: userData } = response.data

      token.value = authToken
      user.value = userData

      // Store token in localStorage
      localStorage.setItem('token', authToken)

      // Set default auth header
      api.defaults.headers.common['Authorization'] = `Bearer ${authToken}`

      return { success: true }
    } catch (error) {
      console.error('Login error:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || 'Erreur de connexion' 
      }
    }
  }

  async function register(userData: {
    firstName: string
    lastName: string
    email: string
    password: string
    phone?: string
    address?: string
    city?: string
    postalCode?: string
    country?: string
  }) {
    try {
      await api.post('/auth/register', userData)
      return { success: true }
    } catch (error) {
      console.error('Registration error:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || 'Erreur d'inscription' 
      }
    }
  }

  function logout() {
    user.value = null
    token.value = null

    // Remove token from localStorage
    localStorage.removeItem('token')

    // Remove auth header
    delete api.defaults.headers.common['Authorization']
  }

  async function fetchUser() {
    try {
      const response = await api.get('/auth/profile')
      user.value = response.data
      return response.data
    } catch (error) {
      console.error('Fetch user error:', error)
      return null
    }
  }

  function initializeAuth() {
    const storedToken = localStorage.getItem('token')
    if (storedToken) {
      token.value = storedToken
      api.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`
      fetchUser()
    }
  }

  return {
    // State
    user,
    token,

    // Getters
    isAuthenticated,
    userRole,
    isClient,
    isCourier,
    isManager,
    isAdmin,

    // Actions
    login,
    register,
    logout,
    fetchUser,
    initializeAuth
  }
})
