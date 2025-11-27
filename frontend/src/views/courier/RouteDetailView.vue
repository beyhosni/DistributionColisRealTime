<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { routingService } from '@/services/routingService'
import type { DeliveryRouteResponse } from '@/services/routingService'

const route = useRoute()
const router = useRouter()
const routeId = Number(route.params.id)
const routeData = ref<DeliveryRouteResponse | null>(null)
const loading = ref(true)
const error = ref('')
const selectedTaskId = ref<number | null>(null)
const statusUpdate = ref('')
const notesUpdate = ref('')
const updatingStatus = ref(false)

onMounted(async () => {
  try {
    routeData.value = await routingService.getRouteById(routeId)
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement de la tournée'
  } finally {
    loading.value = false
  }
})

const getStatusClass = (status: string) => {
  switch (status) {
    case 'PENDING': return 'bg-gray-100 text-gray-800'
    case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800'
    case 'COMPLETED': return 'bg-green-100 text-green-800'
    case 'FAILED': return 'bg-red-100 text-red-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'PENDING': return 'En attente'
    case 'IN_PROGRESS': return 'En cours'
    case 'COMPLETED': return 'Terminé'
    case 'FAILED': return 'Échec'
    default: return status
  }
}

const updateTaskStatus = async (taskId: number, newStatus: string) => {
  if (!newStatus || (newStatus === 'FAILED' && !notesUpdate.value.trim())) {
    error.value = newStatus === 'FAILED' 
      ? 'Veuillez indiquer la raison de l'échec' 
      : 'Veuillez sélectionner un statut'
    return
  }

  updatingStatus.value = true
  error.value = ''

  try {
    await routingService.updateTaskStatus(routeId, taskId, newStatus, notesUpdate.value)

    // Mettre à jour les données localement pour éviter de recharger
    if (routeData.value && routeData.value.tasks) {
      const taskIndex = routeData.value.tasks.findIndex(t => t.id === taskId)
      if (taskIndex !== -1) {
        routeData.value.tasks[taskIndex].status = newStatus
        if (notesUpdate.value.trim()) {
          routeData.value.tasks[taskIndex].notes = notesUpdate.value
        }
      }
    }

    // Réinitialiser le formulaire
    selectedTaskId.value = null
    statusUpdate.value = ''
    notesUpdate.value = ''
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la mise à jour du statut'
  } finally {
    updatingStatus.value = false
  }
}

const openTaskDetails = (taskId: number) => {
  selectedTaskId.value = selectedTaskId.value === taskId ? null : taskId
  statusUpdate.value = ''
  notesUpdate.value = ''
}

// Calculer le pourcentage de progression
const completionPercentage = computed(() => {
  if (!routeData.value || !routeData.value.tasks || routeData.value.tasks.length === 0) {
    return 0
  }

  const completedTasks = routeData.value.tasks.filter(t => t.status === 'COMPLETED').length
  return Math.round((completedTasks / routeData.value.tasks.length) * 100)
})
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <!-- En-tête -->
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">Détails de la tournée</h1>
      <button 
        @click="router.back()" 
        class="mt-2 text-indigo-600 hover:text-indigo-900 font-medium"
      >
        ← Retour à mes tournées
      </button>
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

    <!-- Chargement -->
    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
      <p class="mt-2 text-gray-600">Chargement des détails de la tournée...</p>
    </div>

    <!-- Détails de la tournée -->
    <div v-else-if="routeData" class="bg-white shadow overflow-hidden sm:rounded-md">
      <div class="px-4 py-5 sm:p-6">
        <!-- Informations générales -->
        <div class="mb-6 pb-6 border-b">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Informations de la tournée</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-gray-500">Date</p>
              <p class="mt-1 text-sm text-gray-900">{{ routeData.routeDate }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Zone</p>
              <p class="mt-1 text-sm text-gray-900">{{ routeData.zoneName || 'Non spécifiée' }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Heure de début</p>
              <p class="mt-1 text-sm text-gray-900">{{ routeData.startTime || 'Non spécifiée' }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Heure de fin</p>
              <p class="mt-1 text-sm text-gray-900">{{ routeData.endTime || 'Non spécifiée' }}</p>
            </div>
          </div>
          <div v-if="routeData.notes" class="mt-4">
            <p class="text-sm text-gray-500">Notes</p>
            <p class="mt-1 text-sm text-gray-900">{{ routeData.notes }}</p>
          </div>
        </div>

        <!-- Barre de progression -->
        <div class="mb-6">
          <div class="flex items-center justify-between mb-2">
            <h3 class="text-lg font-medium text-gray-900">Progression de la tournée</h3>
            <span class="text-sm text-gray-500">{{ completionPercentage }}% complété</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-2.5">
            <div 
              class="bg-indigo-600 h-2.5 rounded-full" 
              :style="`width: ${completionPercentage}%`"
            ></div>
          </div>
        </div>

        <!-- Liste des tâches -->
        <div>
          <h3 class="text-lg font-medium text-gray-900 mb-4">Colis à livrer</h3>

          <div v-if="!routeData.tasks || routeData.tasks.length === 0" class="bg-gray-50 p-6 rounded-lg text-center">
            <p class="text-gray-600">Aucun colis assigné à cette tournée.</p>
          </div>

          <div v-else class="space-y-4">
            <div 
              v-for="task in routeData.tasks" 
              :key="task.id" 
              class="bg-white border border-gray-200 rounded-lg p-4"
            >
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-gray-900">
                    #{{ task.taskOrder }} - {{ task.recipientName }}
                  </p>
                  <p class="text-sm text-gray-500">
                    {{ task.recipientAddress }}
                  </p>
                  <p class="text-sm text-gray-500">
                    Numéro de suivi: {{ task.trackingNumber }}
                  </p>
                </div>
                <div class="flex items-center">
                  <span :class="`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusClass(task.status)}`">
                    {{ getStatusText(task.status) }}
                  </span>
                  <button 
                    @click="openTaskDetails(task.id)" 
                    class="ml-2 text-indigo-600 hover:text-indigo-900 font-medium"
                  >
                    {{ selectedTaskId === task.id ? 'Annuler' : 'Mettre à jour' }}
                  </button>
                </div>
              </div>

              <!-- Formulaire de mise à jour du statut -->
              <div v-if="selectedTaskId === task.id" class="mt-4 pt-4 border-t">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label for="status" class="block text-sm font-medium text-gray-700">Nouveau statut</label>
                    <select 
                      id="status" 
                      v-model="statusUpdate"
                      class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                    >
                      <option value="">Sélectionner un statut</option>
                      <option value="IN_PROGRESS">En cours</option>
                      <option value="COMPLETED">Livré</option>
                      <option value="FAILED">Échec de livraison</option>
                    </select>
                  </div>

                  <div>
                    <label for="notes" class="block text-sm font-medium text-gray-700">Notes</label>
                    <textarea 
                      id="notes" 
                      v-model="notesUpdate"
                      rows="3"
                      class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                      placeholder="Notes optionnelles (obligatoires en cas d'échec)"
                    ></textarea>
                  </div>
                </div>

                <div class="mt-4 flex justify-end">
                  <button 
                    @click="openTaskDetails(task.id)" 
                    class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-medium py-2 px-4 rounded mr-2"
                  >
                    Annuler
                  </button>
                  <button 
                    @click="updateTaskStatus(task.id, statusUpdate)" 
                    :disabled="updatingStatus"
                    class="bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-2 px-4 rounded disabled:opacity-50"
                  >
                    <span v-if="updatingStatus" class="flex items-center">
                      <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Mise à jour
                    </span>
                    <span v-else>Mettre à jour</span>
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
