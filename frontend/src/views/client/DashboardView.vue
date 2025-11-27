<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { orderService } from '@/services/orderService'
import type { OrderResponse } from '@/services/orderService'

const authStore = useAuthStore()
const user = authStore.user
const recentOrders = ref<OrderResponse[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    recentOrders.value = await orderService.getAllOrders()
    // Ne garder que les 5 commandes les plus récentes
    recentOrders.value = recentOrders.value.slice(0, 5)
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des commandes'
  } finally {
    loading.value = false
  }
})

const getStatusClass = (status: string) => {
  switch (status) {
    case 'BORNE': return 'bg-gray-100 text-gray-800'
    case 'VALIDEE': return 'bg-blue-100 text-blue-800'
    case 'PAYEE': return 'bg-green-100 text-green-800'
    case 'ASSIGNEE': return 'bg-purple-100 text-purple-800'
    case 'EN_COURS': return 'bg-yellow-100 text-yellow-800'
    case 'LIVREE': return 'bg-green-100 text-green-800'
    case 'ANNULEE': return 'bg-red-100 text-red-800'
    case 'RETOUR': return 'bg-orange-100 text-orange-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'BORNE': return 'Brouillon'
    case 'VALIDEE': return 'Validée'
    case 'PAYEE': return 'Payée'
    case 'ASSIGNEE': return 'Assignée'
    case 'EN_COURS': return 'En cours de livraison'
    case 'LIVREE': return 'Livrée'
    case 'ANNULEE': return 'Annulée'
    case 'RETOUR': return 'Retournée'
    default: return status
  }
}
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">Tableau de bord</h1>
      <p class="text-gray-600 mt-2">Bienvenue, {{ user?.firstName }} {{ user?.lastName }}</p>
    </div>

    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
      <p class="mt-2 text-gray-600">Chargement de vos commandes...</p>
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
        <router-link to="/client/parcels/new" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Créer une nouvelle livraison
        </router-link>
        <router-link to="/tracking" class="bg-gray-600 hover:bg-gray-700 text-white font-bold py-4 px-6 rounded-lg text-center transition duration-300">
          Suivre un colis
        </router-link>
      </div>

      <!-- Commandes récentes -->
      <div>
        <h2 class="text-xl font-semibold text-gray-900 mb-4">Vos commandes récentes</h2>

        <div v-if="recentOrders.length === 0" class="bg-gray-50 p-6 rounded-lg text-center">
          <p class="text-gray-600">Vous n'avez pas encore de commandes.</p>
          <router-link to="/client/parcels/new" class="mt-4 inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-2 px-4 rounded transition duration-300">
            Créer votre première commande
          </router-link>
        </div>

        <div v-else class="bg-white shadow overflow-hidden sm:rounded-md">
          <ul class="divide-y divide-gray-200">
            <li v-for="order in recentOrders" :key="order.id" class="px-6 py-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-indigo-600 truncate">
                    N° {{ order.id }} - {{ order.trackingNumber }}
                  </p>
                  <p class="text-sm text-gray-500">
                    {{ order.recipientName }} - {{ order.recipientCity }}
                  </p>
                </div>
                <div class="flex items-center">
                  <span :class="`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusClass(order.status)}`">
                    {{ getStatusText(order.status) }}
                  </span>
                  <router-link :to="`/client/orders/${order.id}`" class="ml-2 text-indigo-600 hover:text-indigo-900">
                    Détails
                  </router-link>
                </div>
              </div>
            </li>
          </ul>
        </div>

        <div class="mt-4 text-right">
          <router-link to="/client/orders" class="text-indigo-600 hover:text-indigo-900 font-medium">
            Voir toutes les commandes →
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>
