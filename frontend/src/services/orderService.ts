import api from './api'

export interface OrderRequest {
  parcelId?: number
  weight: number
  length?: number
  width?: number
  height?: number
  contentDescription?: string
  declaredValue?: number
  recipientName: string
  recipientPhone?: string
  recipientAddressLine1: string
  recipientAddressLine2?: string
  recipientCity: string
  recipientPostalCode: string
  recipientCountry?: string
  serviceType: 'STANDARD' | 'EXPRESS'
  insurance?: boolean
  signatureRequired?: boolean
}

export interface OrderResponse {
  id: number
  parcelId: number
  trackingNumber: string
  senderId: number
  senderName: string
  recipientName: string
  recipientPhone?: string
  recipientAddressLine1: string
  recipientAddressLine2?: string
  recipientCity: string
  recipientPostalCode: string
  recipientCountry?: string
  serviceType: 'STANDARD' | 'EXPRESS'
  status: string
  price: number
  insurance: boolean
  signatureRequired: boolean
  createdAt: string
  updatedAt: string
}

export const orderService = {
  // Créer une nouvelle commande
  async createOrder(orderData: OrderRequest): Promise<OrderResponse> {
    const response = await api.post('/orders', orderData)
    return response.data
  },

  // Obtenir toutes les commandes (selon le rôle)
  async getAllOrders(): Promise<OrderResponse[]> {
    const response = await api.get('/orders')
    return response.data
  },

  // Obtenir une commande par son ID
  async getOrderById(id: number): Promise<OrderResponse> {
    const response = await api.get(`/orders/${id}`)
    return response.data
  },

  // Mettre à jour une commande
  async updateOrder(id: number, orderData: Partial<OrderRequest>): Promise<OrderResponse> {
    const response = await api.put(`/orders/${id}`, orderData)
    return response.data
  },

  // Supprimer une commande
  async deleteOrder(id: number): Promise<void> {
    await api.delete(`/orders/${id}`)
  },

  // Payer une commande
  async payOrder(id: number): Promise<OrderResponse> {
    const response = await api.post(`/orders/${id}/pay`)
    return response.data
  },

  // Obtenir le statut d'une commande
  async getOrderStatus(id: number): Promise<string> {
    const response = await api.get(`/orders/${id}/status`)
    return response.data
  },

  // Mettre à jour le statut d'une commande
  async updateOrderStatus(id: number, status: string): Promise<OrderResponse> {
    const response = await api.put(`/orders/${id}/status`, null, {
      params: { status }
    })
    return response.data
  }
}
