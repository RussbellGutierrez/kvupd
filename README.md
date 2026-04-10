## 🔐 Configuración de credenciales (desde v1.5.11)

A partir de la versión **1.5.11**, el proyecto utiliza archivos locales para manejar credenciales sensibles como API keys y configuración de firma.

Estos archivos **NO están incluidos en el repositorio** por seguridad.

---

### 📁 Archivos requeridos

Debes crear los siguientes archivos a partir de los ejemplos:

```bash
cp secrets.properties.example secrets.properties
cp keystore.properties.example keystore.properties