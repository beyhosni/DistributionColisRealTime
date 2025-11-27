<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userService } from '@/services/userService'
import type { UserRequest } from '@/services/userService'

const router = useRouter()
const loading = ref(false)
const error = ref('')
const success = ref(false)

// Formulaire d'utilisateur
const formData = ref<UserRequest>({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  phone: '',
  address: '',
  city: '',
  postalCode: '',
  country: '',
  role: 'CLIENT',
  active: true
})

// Soumettre le formulaire
const submitUser = async () => {
  // Validation simple
  if (!formData.value.firstName || !formData.value.lastName || !formData.value.email || !formData.value.password) {
    error.value = 'Veuillez remplir tous les champs obligatoires'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await userService.createUser(formData.value)
    success.value = true
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la création de l'utilisateur'
  } finally {
    loading.value = false
  }
}

// Réinitialiser le formulaire
const resetForm = () => {
  formData.value = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: '',
    address: '',
    city: '',
    postalCode: '',
    country: '',
    role: 'CLIENT',
    active: true
  }
  success.value = false
  error.value = ''
}
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">Créer un nouvel utilisateur</h1>
      <p class="text-gray-600 mt-2">Ajoutez un nouvel utilisateur au système</p>
    </div>

    <!-- Message de succès -->
    <div v-if="success" class="bg-green-50 border-l-4 border-green-400 p-4 mb-6">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 0 8 8 0 000-16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L8 11.414l2.293 2.293a1 1 0 001.414-1.414l-3-3z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <p class="text-sm text-green-700">
            L'utilisateur a été créé avec succès !
          </p>
          <div class="mt-2">
            <button @click="resetForm" class="bg-green-600 hover:bg-green-700 text-white font-medium py-1 px-3 rounded">
              Créer un autre utilisateur
            </button>
            <router-link to="/admin/users" class="ml-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-1 px-3 rounded inline-block">
              Voir tous les utilisateurs
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
        <form @submit.prevent="submitUser">
          <!-- Informations personnelles -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Informations personnelles</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="firstName" class="block text-sm font-medium text-gray-700">Prénom *</label>
                <input 
                  type="text" 
                  id="firstName" 
                  v-model="formData.firstName"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="lastName" class="block text-sm font-medium text-gray-700">Nom *</label>
                <input 
                  type="text" 
                  id="lastName" 
                  v-model="formData.lastName"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="email" class="block text-sm font-medium text-gray-700">Email *</label>
                <input 
                  type="email" 
                  id="email" 
                  v-model="formData.email"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="password" class="block text-sm font-medium text-gray-700">Mot de passe *</label>
                <input 
                  type="password" 
                  id="password" 
                  v-model="formData.password"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>

              <div>
                <label for="phone" class="block text-sm font-medium text-gray-700">Téléphone</label>
                <input 
                  type="tel" 
                  id="phone" 
                  v-model="formData.phone"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>
          </div>

          <!-- Adresse -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Adresse</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="md:col-span-2">
                <label for="address" class="block text-sm font-medium text-gray-700">Adresse</label>
                <input 
                  type="text" 
                  id="address" 
                  v-model="formData.address"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>

              <div>
                <label for="city" class="block text-sm font-medium text-gray-700">Ville</label>
                <input 
                  type="text" 
                  id="city" 
                  v-model="formData.city"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>

              <div>
                <label for="postalCode" class="block text-sm font-medium text-gray-700">Code postal</label>
                <input 
                  type="text" 
                  id="postalCode" 
                  v-model="formData.postalCode"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>

              <div>
                <label for="country" class="block text-sm font-medium text-gray-700">Pays</label>
                <input 
                  type="text" 
                  id="country" 
                  v-model="formData.country"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>
          </div>

          <!-- Rôle et statut -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Rôle et statut</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="role" class="block text-sm font-medium text-gray-700">Rôle *</label>
                <select 
                  id="role" 
                  v-model="formData.role"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                >
                  <option value="CLIENT">Client</option>
                  <option value="LIVREUR">Livreur</option>
                  <option value="GESTIONNAIRE">Gestionnaire</option>
                  <option value="ADMIN">Administrateur</option>
                </select>
              </div>

              <div class="flex items-center">
                <input 
                  type="checkbox" 
                  id="active" 
                  v-model="formData.active"
                  class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                />
                <label for="active" class="ml-2 block text-sm text-gray-900">
                  Compte actif
                </label>
              </div>
            </div>
          </div>

          <!-- Boutons -->
          <div class="flex justify-end">
            <button 
              type="button" 
              @click="router.back()"
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
                Création
              </span>
              <span v-else>Créer l'utilisateur</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
