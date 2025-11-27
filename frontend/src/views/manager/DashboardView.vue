<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { routingService } from '@/services/routingService'
import { orderService } from '@/services/orderService'
import type { DeliveryRouteResponse } from '@/services/routingService'
import type { OrderResponse } from '@/services/orderService'

const authStore = useAuthStore()
const user = authStore.user
const today = new Date().toISOString().split('T')[0] // Format YYYY-MM-DD
const routes = ref<DeliveryRouteResponse[]>([])
const pendingOrders = ref<OrderResponse[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    // Charger les tournées du jour
    routes.value = await routingService.getRoutesByDate(today)

    // Charger les commandes en attente d'assignation
    const allOrders = await orderService.getAllOrders()
    pendingOrders.value = allOrders.filter(order => 
      order.status === 'VALIDEE' || order.status === 'PAYEE'
    ).slice(0, 5) // Limiter à 5 commandes
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des données'
  } finally {
    loading.value = false
  }
})

const getStatusClass = (status: string) => {
  switch (status) {
    case 'PLANNED': return 'bg-blue-100 text-blue-800'
    case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800'
    case 'COMPLETED': return 'bg-green-100 text-green-800'
    case 'CANCELLED': return 'bg-red-100 text-red-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'PLANNED': return 'Planifiée'
    case 'IN_PROGRESS': return 'En cours'
    case 'COMPLETED': return 'Terminée'
    case 'CANCELLED': return 'Annulée'
    default: return status
  }
}
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">Tableau de bord Gestionnaire</h1>
      <p class="text-gray-600 mt-2">Bienvenue, {{ user?.firstName }} {{ user?.lastName }}</p>
    </div>

    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
      <p class="mt-2 text-gray-600">Chargement des données...</p>
    </div>

    <div v-else-if="error" class="bg-red-50 border-l-4 border-red-400 p-4 mb-6">
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

    <div v-else>
      <!-- Actions rapides -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <router-link to="/manager/routes/new" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Créer une tournée
        </router-link>
        <router-link to="/manager/zones" class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Gérer les zones
        </router-link>
        <router-link to="/manager/pricing" class="bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Gérer les tarifs
        </router-link>
        <router-link to="/manager/reports" class="bg-gray-600 hover:bg-gray-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Voir les rapports
        </router-link>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Tournées du jour -->
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <div class="px-4 py-5 sm:px-6">
            <h2 class="text-lg font-medium text-gray-900 mb-4">Tournées du jour ({{ today }})</h2>

            <div v-if="routes.length === 0" class="text-center py-4">
              <p class="text-gray-600">Aucune tournée planifiée pour aujourd'hui.</p>
              <router-link to="/manager/routes/new" class="mt-2 inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-2 px-4 rounded">
                Créer une tournée
              </router-link>
            </div>

            <div v-else class="space-y-4">
              <div v-for="route in routes" :key="route.id" class="border-b pb-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm font-medium text-indigo-600">
                      {{ route.zoneName || 'Tournée sans zone' }}
                    </p>
                    <p class="text-sm text-gray-500">
                      Livreur: {{ route.courierName }}
                    </p>
                    <p class="text-sm text-gray-500">
                      Colis: {{ route.tasks?.length || 0 }}
                    </p>
                  </div>
                  <div class="flex items-center">
                    <span :class="`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusClass(route.status)}`">
                      {{ getStatusText(route.status) }}
                    </span>
                    <router-link :to="`/manager/routes/${route.id}`" class="ml-2 text-indigo-600 hover:text-indigo-900">
                      Détails
                    </router-link>
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-4 text-right">
              <router-link to="/manager/routes" class="text-indigo-600 hover:text-indigo-900 font-medium">
                Voir toutes les tournées →
              </router-link>
            </div>
          </div>
        </div>

        <!-- Commandes en attente d'assignation -->
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <div class="px-4 py-5 sm:px-6">
            <h2 class="text-lg font-medium text-gray-900 mb-4">Commandes en attente d'assignation</h2>

            <div v-if="pendingOrders.length === 0" class="text-center py-4">
              <p class="text-gray-600">Toutes les commandes sont assignées.</p>
            </div>

            <div v-else class="space-y-4">
              <div v-for="order in pendingOrders" :key="order.id" class="border-b pb-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm font-medium text-gray-900">
                      N° {{ order.id }} - {{ order.trackingNumber }}
                    </p>
                    <p class="text-sm text-gray-500">
                      {{ order.recipientName }} - {{ order.recipientCity }}
                    </p>
                  </div>
                  <router-link :to="`/manager/parcels/${order.id}`" class="text-indigo-600 hover:text-indigo-900">
                    Assigner
                  </router-link>
                </div>
              </div>
            </div>

            <div class="mt-4 text-right">
              <router-link to="/manager/parcels" class="text-indigo-600 hover:text-indigo-900 font-medium">
                Voir toutes les commandes →
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
