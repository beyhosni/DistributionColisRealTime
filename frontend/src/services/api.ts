import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// Création de l'instance axios
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercepteur pour ajouter le token d'authentification
api.interceptors.request.use(config => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  response => response,
  error => {
    const authStore = useAuthStore()

    // Gérer les erreurs 401 (non autorisé)
    if (error.response?.status === 401) {
      authStore.logout()
      window.location.href = '/login'
    }

    return Promise.reject(error)
  }
)

export default api
