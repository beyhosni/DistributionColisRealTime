<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { routingService } from '@/services/routingService'
import type { DeliveryRouteResponse } from '@/services/routingService'

const authStore = useAuthStore()
const user = authStore.user
const routes = ref<DeliveryRouteResponse[]>([])
const loading = ref(true)
const error = ref('')
const today = new Date().toISOString().split('T')[0] // Format YYYY-MM-DD

onMounted(async () => {
  try {
    routes.value = await routingService.getRoutesForCourier(user.id, today)
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des tournées'
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
      <h1 class="text-2xl font-bold text-gray-900">Tableau de bord Livreur</h1>
      <p class="text-gray-600 mt-2">Bienvenue, {{ user?.firstName }} {{ user?.lastName }}</p>
    </div>

    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
      <p class="mt-2 text-gray-600">Chargement de vos tournées...</p>
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
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <router-link :to="`/courier/routes`" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Voir mes tournées
        </router-link>
        <router-link to="/courier/profile" class="bg-gray-600 hover:bg-gray-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Mon profil
        </router-link>
      </div>

      <!-- Tournées du jour -->
      <div>
        <h2 class="text-xl font-semibold text-gray-900 mb-4">Mes tournées du jour</h2>

        <div v-if="routes.length === 0" class="bg-gray-50 p-6 rounded-lg text-center">
          <p class="text-gray-600">Vous n'avez pas de tournée prévue pour aujourd'hui.</p>
          <p class="text-gray-500 text-sm mt-2">Contactez votre gestionnaire pour plus d'informations.</p>
        </div>

        <div v-else class="bg-white shadow overflow-hidden sm:rounded-md">
          <ul class="divide-y divide-gray-200">
            <li v-for="route in routes" :key="route.id" class="px-6 py-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-indigo-600">
                    Tournée du {{ route.routeDate }}
                  </p>
                  <p class="text-sm text-gray-500">
                    Zone: {{ route.zoneName || 'Non spécifiée' }}
                  </p>
                  <p class="text-sm text-gray-500">
                    Heure de début: {{ route.startTime || 'Non spécifiée' }}
                  </p>
                  <p class="text-sm text-gray-500">
                    Nombre de colis: {{ route.tasks?.length || 0 }}
                  </p>
                </div>
                <div class="flex items-center">
                  <span :class="`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusClass(route.status)}`">
                    {{ getStatusText(route.status) }}
                  </span>
                  <router-link :to="`/courier/routes/${route.id}`" class="ml-2 text-indigo-600 hover:text-indigo-900">
                    Détails
                  </router-link>
                </div>
              </div>
            </li>
          </ul>
        </div>

        <div class="mt-4 text-right">
          <router-link to="/courier/routes" class="text-indigo-600 hover:text-indigo-900 font-medium">
            Voir toutes les tournées →
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>
