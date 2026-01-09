### 💰 MS Tesorería y Recaudación (`ms-tesoreria-recaudacion/README.md`)

Enfoque transaccional y financiero.

```markdown
# 💰 MS Tesorería y Recaudación

Módulo financiero encargado del **ciclo de vida del ingreso**, desde la emisión de la línea de captura hasta la conciliación y el corte de caja diario.

![Status](https://img.shields.io/badge/Status-Transactional-red)
![Finance](https://img.shields.io/badge/Domain-Finance-blue)

## 🚀 Capacidades

* **Cajas de Cobro:** Apertura, operación y cierre de cajas físicas.
* **Procesamiento de Pagos:** Registro de pagos (Efectivo, Tarjeta, Cheque).
* **Emisión de Recibos:** Generación de recibos oficiales (CFDI o simples).
* **Corte de Caja:** Arqueo diario y reporte de ingresos por partida presupuestal.

## 🔄 Flujo de Integración

1.  Recibe la orden de cobro (importe calculado) desde **MS Cálculo**.
2.  Registra la transacción y actualiza el saldo del contribuyente en **MS Padrón**.
3.  Genera la póliza contable (simulada o integración con ERP).

## ⚙️ Variables de Entorno

| Variable | Descripción |
| :--- | :--- |
| `URL_BD_TESORERIA` | Conexión a `recaudacion_db` |
| `MS_CALCULO_CLIENT` | URL para verificar montos con MS Cálculo |
| `MS_PADRON_CLIENT` | URL para actualizar estatus de pago |
