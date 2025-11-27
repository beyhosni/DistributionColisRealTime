<script setup lang="ts">
import { ref } from 'vue'
import { parcelService } from '@/services/parcelService'
import type { ParcelResponse } from '@/services/parcelService'

const trackingNumber = ref('')
const parcel = ref<ParcelResponse | null>(null)
const loading = ref(false)
const error = ref('')
const notFound = ref(false)

// Fonction pour rechercher un colis
const trackParcel = async () => {
  if (!trackingNumber.value.trim()) {
    error.value = 'Veuillez entrer un numéro de suivi valide'
    return
  }

  loading.value = true
  error.value = ''
  notFound.value = false

  try {
    parcel.value = await parcelService.trackParcel(trackingNumber.value.trim())
  } catch (err: any) {
    if (err.response?.status === 404) {
      notFound.value = true
      error.value = 'Aucun colis trouvé avec ce numéro de suivi'
    } else {
      error.value = err.response?.data?.message || 'Erreur lors du suivi du colis'
    }
  } finally {
    loading.value = false
  }
}

// Obtenir la classe CSS selon le statut
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

// Obtenir le texte du statut
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

// Obtenir l'icône du statut
const getStatusIcon = (status: string) => {
  switch (status) {
    case 'BORNE': return 'M9 12h6l6 6v2a2 2 0 002 2v2a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2h6a2 2 0 002-2v-2a2 2 0 00-2-2z'
    case 'VALIDEE': return 'M5 13l4 4L8.5 7.5 11 11l-6 6z'
    case 'PAYEE': return 'M9 12l2 2 4-4-6-6z'
    case 'ASSIGNEE': return 'M17 20h-2v-2a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2h6a2 2 0 002-2v-2a2 2 0 00-2-2z'
    case 'EN_COURS': return 'M13 7l5 5-5-5 5v2a2 2 0 002 2h6a2 2 0 002-2v-2a2 2 0 00-2-2z'
    case 'LIVREE': return 'M5 13l4 4L8.5 7.5 11 11l-6 6z'
    case 'ANNULEE': return 'M10 18a8 8 0 100-16 0 8 8 0 000-16zM8.707 7.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L8 11.414l2.293 2.293a1 1 0 001.414-1.414l-3-3z'
    case 'RETOUR': return 'M3 4a1 1 0 011-1V2a1 1 0 011-1h12a1 1 0 011 1v1a1 1 0 01-1 1H4a1 1 0 01-1-1V2a1 1 0 011-1z'
    default: return 'M13 16h-1v-4h-1v1h1v4h1v-1h1z'
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <div class="mb-6">
          <h2 class="text-center text-2xl font-extrabold text-gray-900">Suivi de colis</h2>
          <p class="mt-2 text-center text-sm text-gray-600">
            Entrez le numéro de suivi pour suivre votre colis
          </p>
        </div>

        <!-- Formulaire de suivi -->
        <form @submit.prevent="trackParcel" class="space-y-6">
          <div>
            <label for="tracking-number" class="sr-only">Numéro de suivi</label>
            <input
              id="tracking-number"
              v-model="trackingNumber"
              type="text"
              required
              placeholder="Numéro de suivi (ex: TRK123456789)"
              class="appearance-none block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
          </div>

          <div v-if="error" class="rounded-md bg-red-50 p-4">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 0 8 8 0 000-16zM8.707 7.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L8 11.414l2.293 2.293a1 1 0 001.414-1.414l-3-3z" clip-rule="evenodd" />
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">
                  Erreur de suivi
                </h3>
                <div class="mt-2 text-sm text-red-700">
                  {{ error }}
                </div>
              </div>
            </div>
          </div>

          <div>
            <button
              type="submit"
              :disabled="loading"
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              <span v-if="loading" class="absolute left-0 inset-y-0 flex items-center pl-3">
                <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              </span>
              {{ loading ? 'Recherche en cours...' : 'Suivre mon colis' }}
            </button>
          </div>
        </form>

        <!-- Résultat de suivi -->
        <div v-if="parcel && !loading" class="mt-8">
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="px-4 py-5 sm:px-6">
              <!-- Informations principales -->
              <div class="border-b pb-4 mb-4">
                <h3 class="text-lg leading-6 font-medium text-gray-900">
                  Colis n° {{ parcel.trackingNumber }}
                </h3>
                <div class="mt-2 flex items-center">
                  <span :class="`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusClass(parcel.status)}`">
                    {{ getStatusText(parcel.status) }}
                  </span>
                </div>
              </div>

              <!-- Détails du colis -->
              <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Expéditeur</h4>
                  <p class="mt-1 text-sm text-gray-900">{{ parcel.senderName }}</p>
                </div>
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Destinataire</h4>
                  <p class="mt-1 text-sm text-gray-900">{{ parcel.recipientName }}</p>
                </div>
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Adresse de livraison</h4>
                  <p class="mt-1 text-sm text-gray-900">
                    {{ parcel.recipientAddressLine1 }}<br>
                    <span v-if="parcel.recipientAddressLine2">{{ parcel.recipientAddressLine2 }}<br></span>
                    {{ parcel.recipientPostalCode }} {{ parcel.recipientCity }}
                    <span v-if="parcel.recipientCountry">, {{ parcel.recipientCountry }}</span>
                  </p>
                </div>
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Type de service</h4>
                  <p class="mt-1 text-sm text-gray-900">
                    {{ parcel.serviceType === 'STANDARD' ? 'Standard' : 'Express' }}
                  </p>
                </div>
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Poids</h4>
                  <p class="mt-1 text-sm text-gray-900">{{ parcel.weight }} kg</p>
                </div>
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Dimensions</h4>
                  <p class="mt-1 text-sm text-gray-900">
                    {{ parcel.length }} × {{ parcel.width }} × {{ parcel.height }} cm
                  </p>
                </div>
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Valeur déclarée</h4>
                  <p class="mt-1 text-sm text-gray-900">{{ parcel.declaredValue }} €</p>
                </div>
                <div>
                  <h4 class="text-sm font-medium text-gray-500">Options</h4>
                  <p class="mt-1 text-sm text-gray-900">
                    <span v-if="parcel.insurance" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      Assurance
                    </span>
                    <span v-if="parcel.signatureRequired" class="ml-1 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      Signature requise
                    </span>
                  </p>
                </div>
              </div>

              <!-- Timeline des événements -->
              <div class="mt-6">
                <h4 class="text-sm font-medium text-gray-500 mb-4">Historique du suivi</h4>
                <div class="flow-root">
                  <ul class="-mb-8">
                    <!-- Événement initial -->
                    <li>
                      <div class="relative pb-8">
                        <div class="relative flex items-start space-x-3">
                          <div class="relative">
                            <span class="h-8 w-8 rounded-full bg-gray-400 flex items-center justify-center ring-8 ring-white">
                              <svg class="h-5 w-5 text-white" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 001-1v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                              </svg>
                            </span>
                            <div class="absolute top-0 right-0 h-8 w-px bg-gray-300"></div>
                          </div>
                          <div class="min-w-0 flex-1 pt-1.5 pb-4 flex justify-between space-x-4">
                            <div>
                              <p class="text-sm text-gray-900">Commande créée</p>
                              <p class="mt-0.5 text-xs text-gray-500">{{ parcel.createdAt }}</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </li>

                    <!-- Autres événements -->
                    <li>
                      <div class="relative pb-8">
                        <div class="relative flex items-start space-x-3">
                          <div class="relative">
                            <span class="h-8 w-8 rounded-full bg-blue-400 flex items-center justify-center ring-8 ring-white">
                              <svg class="h-5 w-5 text-white" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 0 8 8 0 000-16zm3.707-8.293l-3-3a1 1 0 00-1.414 1.414L10 12.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L8 11.414l2.293 2.293a1 1 0 001.414-1.414l-3-3z" clip-rule="evenodd" />
                              </svg>
                            </span>
                            <div class="absolute top-0 right-0 h-8 w-px bg-blue-300"></div>
                          </div>
                          <div class="min-w-0 flex-1 pt-1.5 pb-4 flex justify-between space-x-4">
                            <div>
                              <p class="text-sm text-gray-900">En préparation</p>
                              <p class="mt-0.5 text-xs text-gray-500">Votre colis est en cours de préparation</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </li>

                    <!-- État actuel -->
                    <li>
                      <div class="relative">
                        <div class="relative flex items-start space-x-3">
                          <div class="relative">
                            <span :class="`h-8 w-8 rounded-full flex items-center justify-center ring-8 ring-white ${getStatusClass(parcel.status).split(' ')[0]}`">
                              <svg class="h-5 w-5 text-white" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" :d="getStatusIcon(parcel.status)" clip-rule="evenodd" />
                              </svg>
                            </span>
                          </div>
                          <div class="min-w-0 flex-1 pt-1.5 flex justify-between space-x-4">
                            <div>
                              <p class="text-sm text-gray-900">{{ getStatusText(parcel.status) }}</p>
                              <p class="mt-0.5 text-xs text-gray-500">{{ parcel.updatedAt }}</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
