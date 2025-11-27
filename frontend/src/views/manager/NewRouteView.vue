<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { routingService } from '@/services/routingService'
import { orderService } from '@/services/orderService'
import type { RouteRequest } from '@/services/routingService'
import type { OrderResponse } from '@/services/orderService'

const authStore = useAuthStore()
const loading = ref(false)
const error = ref('')
const success = ref(false)
const routeId = ref<number | null>(null)
const unassignedOrders = ref<OrderResponse[]>([])

// Formulaire de tournée
const formData = ref<RouteRequest>({
  courierId: 0,
  zoneId: null,
  routeDate: new Date().toISOString().split('T')[0], // Format YYYY-MM-DD
  startTime: '',
  endTime: '',
  notes: ''
})

// Liste des livreurs et zones (à charger depuis l'API)
const couriers = ref([])
const zones = ref([])

// Charger les données initiales
onMounted(async () => {
  try {
    // Charger les commandes non assignées
    const allOrders = await orderService.getAllOrders()
    unassignedOrders.value = allOrders.filter(order => 
      order.status === 'VALIDEE' || order.status === 'PAYEE'
    )

    // Charger les livreurs et zones (à implémenter)
    // couriers.value = await userService.getCouriers()
    // zones.value = await zoneService.getAllZones()
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des données'
  }
})

// Soumettre le formulaire
const submitRoute = async () => {
  // Validation simple
  if (!formData.value.courierId || !formData.value.routeDate) {
    error.value = 'Veuillez remplir tous les champs obligatoires'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const response = await routingService.createRoute(formData.value)
    routeId.value = response.id
    success.value = true
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la création de la tournée'
  } finally {
    loading.value = false
  }
}

// Réinitialiser le formulaire
const resetForm = () => {
  formData.value = {
    courierId: 0,
    zoneId: null,
    routeDate: new Date().toISOString().split('T')[0],
    startTime: '',
    endTime: '',
    notes: ''
  }
  routeId.value = null
  success.value = false
  error.value = ''
}

// Ajouter une commande à la tournée
const addOrderToRoute = async (orderId: number) => {
  if (!routeId.value) {
    error.value = 'Veuillez d'abord créer la tournée'
    return
  }

  try {
    // Trouver le prochain ordre de livraison
    const taskOrder = unassignedOrders.value.findIndex(o => o.id === orderId) + 1

    await routingService.addTaskToRoute(routeId.value, orderId, taskOrder)

    // Retirer la commande de la liste des commandes non assignées
    unassignedOrders.value = unassignedOrders.value.filter(o => o.id !== orderId)
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de l'assignation de la commande'
  }
}
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">Créer une nouvelle tournée</h1>
      <p class="text-gray-600 mt-2">Planifiez une tournée pour un livreur</p>
    </div>

    <!-- Message de succès -->
    <div v-if="success && routeId" class="bg-green-50 border-l-4 border-green-400 p-4 mb-6">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 0 8 8 0 000-16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L8 11.414l2.293 2.293a1 1 0 001.414-1.414l-3-3z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <p class="text-sm text-green-700">
            La tournée a été créée avec succès ! ID: <strong>{{ routeId }}</strong>
          </p>
          <div class="mt-2">
            <button @click="resetForm" class="bg-green-600 hover:bg-green-700 text-white font-medium py-1 px-3 rounded">
              Créer une autre tournée
            </button>
            <router-link to="/manager/routes" class="ml-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-1 px-3 rounded inline-block">
              Voir mes tournées
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

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Formulaire de tournée -->
      <div class="lg:col-span-2">
        <div v-if="!success" class="bg-white shadow overflow-hidden sm:rounded-md">
          <div class="px-4 py-5 sm:p-6">
            <form @submit.prevent="submitRoute">
              <!-- Livreur -->
              <div class="mb-6">
                <label for="courier" class="block text-sm font-medium text-gray-700">Livreur *</label>
                <select 
                  id="courier" 
                  v-model="formData.courierId"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                >
                  <option value="">Sélectionner un livreur</option>
                  <option v-for="courier in couriers" :key="courier.id" :value="courier.id">
                    {{ courier.firstName }} {{ courier.lastName }}
                  </option>
                </select>
              </div>

              <!-- Zone -->
              <div class="mb-6">
                <label for="zone" class="block text-sm font-medium text-gray-700">Zone de livraison</label>
                <select 
                  id="zone" 
                  v-model="formData.zoneId"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                >
                  <option value="">Sélectionner une zone (optionnel)</option>
                  <option v-for="zone in zones" :key="zone.id" :value="zone.id">
                    {{ zone.name }}
                  </option>
                </select>
              </div>

              <!-- Date -->
              <div class="mb-6">
                <label for="routeDate" class="block text-sm font-medium text-gray-700">Date de la tournée *</label>
                <input 
                  type="date" 
                  id="routeDate" 
                  v-model="formData.routeDate"
                  :min="new Date().toISOString().split('T')[0]"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <!-- Heures -->
              <div class="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <label for="startTime" class="block text-sm font-medium text-gray-700">Heure de début</label>
                  <input 
                    type="time" 
                    id="startTime" 
                    v-model="formData.startTime"
                    class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  />
                </div>
                <div>
                  <label for="endTime" class="block text-sm font-medium text-gray-700">Heure de fin</label>
                  <input 
                    type="time" 
                    id="endTime" 
                    v-model="formData.endTime"
                    class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  />
                </div>
              </div>

              <!-- Notes -->
              <div class="mb-6">
                <label for="notes" class="block text-sm font-medium text-gray-700">Notes</label>
                <textarea 
                  id="notes" 
                  v-model="formData.notes"
                  rows="3"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  placeholder="Notes optionnelles sur la tournée..."
                ></textarea>
              </div>

              <!-- Boutons -->
              <div class="flex justify-end">
                <button 
                  type="button" 
                  @click="resetForm"
                  class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-medium py-2 px-4 rounded mr-2"
                >
                  Annuler
                </button>
                <button 
                  type="submit"
                  :disabled="loading"
                  class="bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-2 px-4 rounded disabled:opacity-50"
                >
                  <span v-if="loading" class="flex items-center">
                    <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Créer la tournée
                  </span>
                  <span v-else>Créer la tournée</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Commandes non assignées -->
      <div class="lg:col-span-1">
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <div class="px-4 py-5 sm:p-6">
            <h2 class="text-lg font-medium text-gray-900 mb-4">Commandes à assigner</h2>

            <div v-if="unassignedOrders.length === 0" class="text-center py-4">
              <p class="text-gray-600">Aucune commande en attente d'assignation.</p>
            </div>

            <div v-else class="space-y-4">
              <div v-for="order in unassignedOrders" :key="order.id" class="border-b pb-4">
                <div class="flex justify-between">
                  <div>
                    <p class="text-sm font-medium text-gray-900">
                      N° {{ order.id }} - {{ order.trackingNumber }}
                    </p>
                    <p class="text-sm text-gray-500">
                      {{ order.recipientName }} - {{ order.recipientCity }}
                    </p>
                  </div>
                  <button 
                    v-if="routeId"
                    @click="addOrderToRoute(order.id)"
                    class="bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-1 px-3 rounded"
                  >
                    Ajouter
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
