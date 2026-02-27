package com.jaco.cc3d.domain

// Define esta clase en tu paquete de dominio
class SessionExpiredException(message: String = "Token refresh failed. User must log in again.") : Exception(message)