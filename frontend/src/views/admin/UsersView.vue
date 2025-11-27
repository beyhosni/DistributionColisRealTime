<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userService } from '@/services/userService'
import type { UserResponse } from '@/services/userService'

const route = useRoute()
const router = useRouter()
const users = ref<UserResponse[]>([])
const loading = ref(true)
const error = ref('')
const searchTerm = ref('')
const roleFilter = ref('')
const statusFilter = ref('')

onMounted(async () => {
  try {
    users.value = await userService.getAllUsers()
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des utilisateurs'
  } finally {
    loading.value = false
  }
})

const filteredUsers = ref(users.value)

// Filtrer les utilisateurs selon les critères
const filterUsers = () => {
  filteredUsers.value = users.value.filter(user => {
    const matchesSearch = searchTerm.value === '' || 
      user.firstName.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      user.lastName.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.value.toLowerCase())

    const matchesRole = roleFilter.value === '' || user.role === roleFilter.value
    const matchesStatus = statusFilter.value === '' || 
      (statusFilter.value === 'active' && user.active) ||
      (statusFilter.value === 'inactive' && !user.active)

    return matchesSearch && matchesRole && matchesStatus
  })
}

// Observer les changements de filtres
watch([searchTerm, roleFilter, statusFilter], filterUsers)

// Activer/désactiver un utilisateur
const toggleUserStatus = async (user: UserResponse) => {
  try {
    await userService.toggleUserStatus(user.id, !user.active)
    user.active = !user.active
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la mise à jour du statut'
  }
}

// Supprimer un utilisateur
const deleteUser = async (user: UserResponse) => {
  if (confirm(`Êtes-vous sûr de vouloir supprimer l'utilisateur ${user.firstName} ${user.lastName}?`)) {
    try {
      await userService.deleteUser(user.id)
      users.value = users.value.filter(u => u.id !== user.id)
      filterUsers()
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors de la suppression de l'utilisateur'
    }
  }
}
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-8 flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Gestion des utilisateurs</h1>
        <p class="text-gray-600 mt-2">Gérez les comptes utilisateurs du système</p>
      </div>
      <router-link to="/admin/users/new" class="bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-2 px-4 rounded">
        Créer un utilisateur
      </router-link>
    </div>

    <!-- Filtres -->
    <div class="bg-white shadow overflow-hidden sm:rounded-md mb-6">
      <div class="px-4 py-5 sm:p-6">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label for="search" class="block text-sm font-medium text-gray-700">Recherche</label>
            <input 
              type="text" 
              id="search" 
              v-model="searchTerm"
              placeholder="Nom, email..."
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
          </div>

          <div>
            <label for="role" class="block text-sm font-medium text-gray-700">Rôle</label>
            <select 
              id="role" 
              v-model="roleFilter"
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              <option value="">Tous les rôles</option>
              <option value="CLIENT">Client</option>
              <option value="LIVREUR">Livreur</option>
              <option value="GESTIONNAIRE">Gestionnaire</option>
              <option value="ADMIN">Administrateur</option>
            </select>
          </div>

          <div>
            <label for="status" class="block text-sm font-medium text-gray-700">Statut</label>
            <select 
              id="status" 
              v-model="statusFilter"
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              <option value="">Tous les statuts</option>
              <option value="active">Actif</option>
              <option value="inactive">Inactif</option>
            </select>
          </div>

          <div class="flex items-end">
            <button 
              @click="() => { searchTerm = ''; roleFilter = ''; statusFilter = '' }"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-medium py-2 px-4 rounded w-full"
            >
              Réinitialiser
            </button>
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

    <!-- Chargement -->
    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
      <p class="mt-2 text-gray-600">Chargement des utilisateurs...</p>
    </div>

    <!-- Tableau des utilisateurs -->
    <div v-else class="bg-white shadow overflow-hidden sm:rounded-md">
      <div v-if="filteredUsers.length === 0" class="text-center py-8">
        <p class="text-gray-600">Aucun utilisateur trouvé.</p>
      </div>

      <table v-else class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Utilisateur
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Rôle
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Contact
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Statut
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
              Actions
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in filteredUsers" :key="user.id">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10">
                  <div class="h-10 w-10 rounded-full bg-indigo-500 flex items-center justify-center">
                    <span class="text-white font-medium">
                      {{ user.firstName.charAt(0) }}{{ user.lastName.charAt(0) }}
                    </span>
                  </div>
                </div>
                <div class="ml-4">
                  <div class="text-sm font-medium text-gray-900">
                    {{ user.firstName }} {{ user.lastName }}
                  </div>
                  <div class="text-sm text-gray-500">
                    {{ user.email }}
                  </div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" 
                :class="{
                  'bg-purple-100 text-purple-800': user.role === 'ADMIN',
                  'bg-blue-100 text-blue-800': user.role === 'GESTIONNAIRE',
                  'bg-green-100 text-green-800': user.role === 'LIVREUR',
                  'bg-gray-100 text-gray-800': user.role === 'CLIENT'
                }">
                {{ user.role }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ user.phone || '-' }}<br>
              {{ user.city || '-' }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" 
                :class="user.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                {{ user.active ? 'Actif' : 'Inactif' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <router-link :to="`/admin/users/${user.id}`" class="text-indigo-600 hover:text-indigo-900 mr-3">
                Modifier
              </router-link>
              <button 
                @click="toggleUserStatus(user)"
                class="text-yellow-600 hover:text-yellow-900 mr-3"
              >
                {{ user.active ? 'Désactiver' : 'Activer' }}
              </button>
              <button 
                @click="deleteUser(user)"
                class="text-red-600 hover:text-red-900"
              >
                Supprimer
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
