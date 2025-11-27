import api from './api'

export interface ParcelRequest {
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

export interface ParcelResponse {
  id: number
  orderId: number
  trackingNumber: string
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
  status: string
  price: number
  insurance: boolean
  signatureRequired: boolean
  createdAt: string
  updatedAt: string
}

export const parcelService = {
  // Créer un nouveau colis
  async createParcel(parcelData: ParcelRequest): Promise<ParcelResponse> {
    const response = await api.post('/parcels', parcelData)
    return response.data
  },

  // Obtenir tous les colis (selon le rôle)
  async getAllParcels(): Promise<ParcelResponse[]> {
    const response = await api.get('/parcels')
    return response.data
  },

  // Obtenir un colis par son ID
  async getParcelById(id: number): Promise<ParcelResponse> {
    const response = await api.get(`/parcels/${id}`)
    return response.data
  },

  // Suivre un colis par numéro de suivi
  async trackParcel(trackingNumber: string): Promise<ParcelResponse> {
    const response = await api.get(`/parcels/tracking/${trackingNumber}`)
    return response.data
  },

  // Mettre à jour un colis
  async updateParcel(id: number, parcelData: Partial<ParcelRequest>): Promise<ParcelResponse> {
    const response = await api.put(`/parcels/${id}`, parcelData)
    return response.data
  },

  // Supprimer un colis
  async deleteParcel(id: number): Promise<void> {
    await api.delete(`/parcels/${id}`)
  },

  // Calculer le prix d'un colis
  async calculatePrice(parcelData: ParcelRequest): Promise<number> {
    const response = await api.post('/parcels/calculate-price', parcelData)
    return response.data.price
  }
}
