import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/tracking',
      name: 'tracking',
      component: () => import('@/views/public/TrackingView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    // Client routes
    {
      path: '/client',
      name: 'clientLayout',
      component: () => import('@/layouts/ClientLayout.vue'),
      meta: { requiresAuth: true, role: 'CLIENT' },
      children: [
        {
          path: '',
          name: 'clientDashboard',
          component: () => import('@/views/client/DashboardView.vue')
        },
        {
          path: 'parcels',
          name: 'clientParcels',
          component: () => import('@/views/client/ParcelsView.vue')
        },
        {
          path: 'parcels/new',
          name: 'newParcel',
          component: () => import('@/views/client/NewParcelView.vue')
        },
        {
          path: 'orders',
          name: 'clientOrders',
          component: () => import('@/views/client/OrdersView.vue')
        },
        {
          path: 'profile',
          name: 'clientProfile',
          component: () => import('@/views/client/ProfileView.vue')
        }
      ]
    },
    // Livreur routes
    {
      path: '/courier',
      name: 'courierLayout',
      component: () => import('@/layouts/CourierLayout.vue'),
      meta: { requiresAuth: true, role: 'LIVREUR' },
      children: [
        {
          path: '',
          name: 'courierDashboard',
          component: () => import('@/views/courier/DashboardView.vue')
        },
        {
          path: 'routes',
          name: 'courierRoutes',
          component: () => import('@/views/courier/RoutesView.vue')
        },
        {
          path: 'routes/:id',
          name: 'courierRouteDetail',
          component: () => import('@/views/courier/RouteDetailView.vue')
        },
        {
          path: 'profile',
          name: 'courierProfile',
          component: () => import('@/views/courier/ProfileView.vue')
        }
      ]
    },
    // Gestionnaire routes
    {
      path: '/manager',
      name: 'managerLayout',
      component: () => import('@/layouts/ManagerLayout.vue'),
      meta: { requiresAuth: true, role: 'GESTIONNAIRE' },
      children: [
        {
          path: '',
          name: 'managerDashboard',
          component: () => import('@/views/manager/DashboardView.vue')
        },
        {
          path: 'routes',
          name: 'managerRoutes',
          component: () => import('@/views/manager/RoutesView.vue')
        },
        {
          path: 'routes/new',
          name: 'newRoute',
          component: () => import('@/views/manager/NewRouteView.vue')
        },
        {
          path: 'routes/:id',
          name: 'managerRouteDetail',
          component: () => import('@/views/manager/RouteDetailView.vue')
        },
        {
          path: 'parcels',
          name: 'managerParcels',
          component: () => import('@/views/manager/ParcelsView.vue')
        },
        {
          path: 'zones',
          name: 'managerZones',
          component: () => import('@/views/manager/ZonesView.vue')
        },
        {
          path: 'pricing',
          name: 'managerPricing',
          component: () => import('@/views/manager/PricingView.vue')
        },
        {
          path: 'reports',
          name: 'managerReports',
          component: () => import('@/views/manager/ReportsView.vue')
        }
      ]
    },
    // Admin routes
    {
      path: '/admin',
      name: 'adminLayout',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, role: 'ADMIN' },
      children: [
        {
          path: '',
          name: 'adminDashboard',
          component: () => import('@/views/admin/DashboardView.vue')
        },
        {
          path: 'users',
          name: 'adminUsers',
          component: () => import('@/views/admin/UsersView.vue')
        },
        {
          path: 'users/new',
          name: 'newUser',
          component: () => import('@/views/admin/NewUserView.vue')
        },
        {
          path: 'users/:id',
          name: 'editUser',
          component: () => import('@/views/admin/EditUserView.vue')
        },
        {
          path: 'zones',
          name: 'adminZones',
          component: () => import('@/views/admin/ZonesView.vue')
        },
        {
          path: 'pricing',
          name: 'adminPricing',
          component: () => import('@/views/admin/PricingView.vue')
        },
        {
          path: 'reports',
          name: 'adminReports',
          component: () => import('@/views/admin/ReportsView.vue')
        },
        {
          path: 'settings',
          name: 'adminSettings',
          component: () => import('@/views/admin/SettingsView.vue')
        }
      ]
    },
    // Catch-all route
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      component: () => import('@/views/NotFoundView.vue')
    }
  ]
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Check if route requires authentication
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
    return
  }

  // Check if route requires guest (not authenticated)
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next({ name: 'dashboard' })
    return
  }

  // Check if route requires specific role
  if (to.meta.role && authStore.user?.role !== to.meta.role) {
    next({ name: 'dashboard' })
    return
  }

  next()
})

export default router
