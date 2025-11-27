<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { orderService, parcelService } from '@/services/orderService'
import type { OrderRequest, OrderResponse } from '@/services/orderService'
import type { ParcelRequest } from '@/services/parcelService'

const authStore = useAuthStore()
const loading = ref(false)
const error = ref('')
const success = ref(false)
const orderId = ref<number | null>(null)

// Formulaire de commande
const formData = reactive<OrderRequest>({
  weight: 0,
  length: null,
  width: null,
  height: null,
  contentDescription: '',
  declaredValue: 0,
  recipientName: '',
  recipientPhone: '',
  recipientAddressLine1: '',
  recipientAddressLine2: '',
  recipientCity: '',
  recipientPostalCode: '',
  recipientCountry: '',
  serviceType: 'STANDARD',
  insurance: false,
  signatureRequired: false
})

// Calcul du prix en temps réel
const calculatedPrice = ref(0)

// Calculer le prix lorsque les données changent
const calculatePrice = async () => {
  if (formData.weight > 0 && formData.recipientPostalCode && formData.recipientName) {
    try {
      calculatedPrice.value = await parcelService.calculatePrice(formData as ParcelRequest)
    } catch (err: any) {
      console.error('Erreur de calcul du prix:', err)
    }
  }
}

// Observer les changements pour recalculer le prix
const watchedFields = [
  'weight', 'length', 'width', 'height', 
  'recipientPostalCode', 'serviceType', 
  'insurance', 'signatureRequired', 'declaredValue'
]

watchedFields.forEach(field => {
  computed(() => formData[field as keyof OrderRequest]).watch(() => {
    calculatePrice()
  })
})

// Soumettre le formulaire
const submitOrder = async () => {
  // Validation simple
  if (!formData.recipientName || !formData.recipientAddressLine1 || 
      !formData.recipientCity || !formData.recipientPostalCode || formData.weight <= 0) {
    error.value = 'Veuillez remplir tous les champs obligatoires'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const response = await orderService.createOrder(formData)
    orderId.value = response.id
    success.value = true
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la création de la commande'
  } finally {
    loading.value = false
  }
}

// Réinitialiser le formulaire
const resetForm = () => {
  Object.assign(formData, {
    weight: 0,
    length: null,
    width: null,
    height: null,
    contentDescription: '',
    declaredValue: 0,
    recipientName: '',
    recipientPhone: '',
    recipientAddressLine1: '',
    recipientAddressLine2: '',
    recipientCity: '',
    recipientPostalCode: '',
    recipientCountry: '',
    serviceType: 'STANDARD',
    insurance: false,
    signatureRequired: false
  })
  calculatedPrice.value = 0
  orderId.value = null
  success.value = false
  error.value = ''
}
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">Créer une nouvelle livraison</h1>
      <p class="text-gray-600 mt-2">Remplissez les informations ci-dessous pour créer une demande de livraison</p>
    </div>

    <!-- Message de succès -->
    <div v-if="success && orderId" class="bg-green-50 border-l-4 border-green-400 p-4 mb-6">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 0 8 8 0 000-16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L8 11.414l2.293 2.293a1 1 0 001.414-1.414l-3-3z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <p class="text-sm text-green-700">
            Votre commande a été créée avec succès ! Votre numéro de suivi est : <strong>{{ orderId }}</strong>
          </p>
          <div class="mt-2">
            <button @click="resetForm" class="bg-green-600 hover:bg-green-700 text-white font-medium py-1 px-3 rounded">
              Créer une autre commande
            </button>
            <router-link to="/client/orders" class="ml-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-1 px-3 rounded inline-block">
              Voir mes commandes
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Message d'erreur -->
    <div v-if="error" class="bg-red-50 border-l-4 border-red-400 p-4 mb-6">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 0 8 8 0 000-16zM8.707 7.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L8 11.414l2.293 2.293a1 1 0 001.414-1.414l-3-3z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <p class="text-sm text-red-700">
            {{ error }}
          </p>
        </div>
      </div>
    </div>

    <!-- Formulaire -->
    <div v-if="!success" class="bg-white shadow overflow-hidden sm:rounded-md">
      <div class="px-4 py-5 sm:p-6">
        <form @submit.prevent="submitOrder">
          <!-- Informations du destinataire -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Informations du destinataire</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="recipientName" class="block text-sm font-medium text-gray-700">Nom complet *</label>
                <input 
                  type="text" 
                  id="recipientName" 
                  v-model="formData.recipientName"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="recipientPhone" class="block text-sm font-medium text-gray-700">Téléphone</label>
                <input 
                  type="tel" 
                  id="recipientPhone" 
                  v-model="formData.recipientPhone"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>

              <div class="md:col-span-2">
                <label for="recipientAddressLine1" class="block text-sm font-medium text-gray-700">Adresse *</label>
                <input 
                  type="text" 
                  id="recipientAddressLine1" 
                  v-model="formData.recipientAddressLine1"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="recipientAddressLine2" class="block text-sm font-medium text-gray-700">Complément d'adresse</label>
                <input 
                  type="text" 
                  id="recipientAddressLine2" 
                  v-model="formData.recipientAddressLine2"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>

              <div>
                <label for="recipientCity" class="block text-sm font-medium text-gray-700">Ville *</label>
                <input 
                  type="text" 
                  id="recipientCity" 
                  v-model="formData.recipientCity"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="recipientPostalCode" class="block text-sm font-medium text-gray-700">Code postal *</label>
                <input 
                  type="text" 
                  id="recipientPostalCode" 
                  v-model="formData.recipientPostalCode"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>
            </div>
          </div>

          <!-- Informations du colis -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Informations du colis</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="weight" class="block text-sm font-medium text-gray-700">Poids (kg) *</label>
                <input 
                  type="number" 
                  id="weight" 
                  v-model.number="formData.weight"
                  step="0.1"
                  min="0.1"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="declaredValue" class="block text-sm font-medium text-gray-700">Valeur déclarée (€)</label>
                <input 
                  type="number" 
                  id="declaredValue" 
                  v-model.number="formData.declaredValue"
                  step="0.01"
                  min="0"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>

              <div class="md:col-span-2 grid grid-cols-3 gap-4">
                <div>
                  <label for="length" class="block text-sm font-medium text-gray-700">Longueur (cm)</label>
                  <input 
                    type="number" 
                    id="length" 
                    v-model.number="formData.length"
                    step="0.1"
                    min="0"
                    class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  />
                </div>

                <div>
                  <label for="width" class="block text-sm font-medium text-gray-700">Largeur (cm)</label>
                  <input 
                    type="number" 
                    id="width" 
                    v-model.number="formData.width"
                    step="0.1"
                    min="0"
                    class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  />
                </div>

                <div>
                  <label for="height" class="block text-sm font-medium text-gray-700">Hauteur (cm)</label>
                  <input 
                    type="number" 
                    id="height" 
                    v-model.number="formData.height"
                    step="0.1"
                    min="0"
                    class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  />
                </div>
              </div>

              <div class="md:col-span-2">
                <label for="contentDescription" class="block text-sm font-medium text-gray-700">Description du contenu</label>
                <textarea 
                  id="contentDescription" 
                  v-model="formData.contentDescription"
                  rows="3"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Options de livraison -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Options de livraison</h3>

            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">Type de service</label>
                <div class="mt-2 space-x-4">
                  <label class="inline-flex items-center">
                    <input 
                      type="radio" 
                      v-model="formData.serviceType"
                      value="STANDARD"
                      class="form-radio h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300"
                    />
                    <span class="ml-2">Standard (3-5 jours)</span>
                  </label>
                  <label class="inline-flex items-center">
                    <input 
                      type="radio" 
                      v-model="formData.serviceType"
                      value="EXPRESS"
                      class="form-radio h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300"
                    />
                    <span class="ml-2">Express (24h)</span>
                  </label>
                </div>
              </div>

              <div class="space-x-6">
                <label class="inline-flex items-center">
                  <input 
                    type="checkbox" 
                    v-model="formData.insurance"
                    class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                  />
                  <span class="ml-2">Assurance (+{{ (formData.declaredValue * 0.01).toFixed(2) }}€ minimum 2€)</span>
                </label>

                <label class="inline-flex items-center">
                  <input 
                    type="checkbox" 
                    v-model="formData.signatureRequired"
                    class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                  />
                  <span class="ml-2">Signature requise (+1.50€)</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Prix calculé -->
          <div class="mb-6 p-4 bg-gray-50 rounded-md">
            <div class="flex justify-between">
              <span class="text-lg font-medium text-gray-900">Prix estimé:</span>
              <span class="text-lg font-bold text-indigo-600">{{ calculatedPrice.toFixed(2) }}€</span>
            </div>
          </div>

          <!-- Boutons de soumission -->
          <div class="flex justify-end">
            <button 
              type="button" 
              @click="resetForm"
              class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Annuler
            </button>
            <button 
              type="submit" 
              :disabled="loading"
              class="ml-3 inline-flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              <span v-if="loading" class="mr-2">
                <svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              </span>
              Créer la commande
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
