import api from './api'

export interface UserRequest {
  firstName: string
  lastName: string
  email: string
  password?: string
  phone?: string
  address?: string
  city?: string
  postalCode?: string
  country?: string
  role: string
  active?: boolean
}

export interface UserResponse {
  id: number
  firstName: string
  lastName: string
  email: string
  phone?: string
  address?: string
  city?: string
  postalCode?: string
  country?: string
  role: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export const userService = {
  // Obtenir tous les utilisateurs
  async getAllUsers(): Promise<UserResponse[]> {
    const response = await api.get('/admin/users')
    return response.data
  },

  // Obtenir un utilisateur par son ID
  async getUserById(id: number): Promise<UserResponse> {
    const response = await api.get(`/admin/users/${id}`)
    return response.data
  },

  // Créer un nouvel utilisateur
  async createUser(userData: UserRequest): Promise<UserResponse> {
    const response = await api.post('/admin/users', userData)
    return response.data
  },

  // Mettre à jour un utilisateur
  async updateUser(id: number, userData: Partial<UserRequest>): Promise<UserResponse> {
    const response = await api.put(`/admin/users/${id}`, userData)
    return response.data
  },

  // Supprimer un utilisateur
  async deleteUser(id: number): Promise<void> {
    await api.delete(`/admin/users/${id}`)
  },

  // Activer/désactiver un utilisateur
  async toggleUserStatus(id: number, active: boolean): Promise<UserResponse> {
    const response = await api.put(`/admin/users/${id}/status`, null, {
      params: { active }
    })
    return response.data
  },

  // Obtenir les livreurs
  async getCouriers(): Promise<UserResponse[]> {
    const response = await api.get('/admin/users/couriers')
    return response.data
  },

  // Obtenir les gestionnaires
  async getManagers(): Promise<UserResponse[]> {
    const response = await api.get('/admin/users/managers')
    return response.data
  },

  // Obtenir les clients
  async getClients(): Promise<UserResponse[]> {
    const response = await api.get('/admin/users/clients')
    return response.data
  }
}
