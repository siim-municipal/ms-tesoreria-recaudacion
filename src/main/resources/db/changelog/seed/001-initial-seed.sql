INSERT INTO cat_rubros_ingresos (codigo_cri, descripcion, nivel, cuenta_contable) VALUES
            ('1', 'IMPUESTOS', 1, '4.1.1'),
            ('1.2', 'IMPUESTOS SOBRE EL PATRIMONIO', 2, '4.1.1.2'),
            ('1.2.1', 'Impuesto Predial', 3, '4.1.1.2.1'),
            ('1.7', 'ACCESORIOS DE IMPUESTOS', 2, '4.1.1.7'),
            ('4', 'DERECHOS', 1, '4.1.4'),
            ('4.3', 'DERECHOS POR PRESTACIÓN DE SERVICIOS', 2, '4.1.4.3'),
            ('4.3.1', 'Alumbrado Público', 3, '4.1.4.3.1'),
            ('4.3.10', 'Licencias de Construcción', 3, '4.1.4.3.10'),
            ('5', 'PRODUCTOS', 1, '4.1.5'),
            ('6', 'APROVECHAMIENTOS', 1, '4.1.6');

-- Asegurar que existe el mapeo para el Predial
UPDATE cat_rubros_ingresos
SET clave_concepto = 'IMP_PREDIAL_URBANO'
WHERE codigo_cri = '1.2.1';

-- Asegurar que existe el mapeo para Agua (ajusta el código CRI según tu seed)
UPDATE cat_rubros_ingresos
SET clave_concepto = 'DERECHO_AGUA'
WHERE codigo_cri = '4.3.1';

-- Insertar 3 Cajas de prueba
INSERT INTO cat_cajas (id, nombre, ubicacion, activa)
    VALUES
      ('11111111-1111-1111-1111-111111111111', 'Caja 1 - Ventanilla Principal', 'Planta Baja', true),
      ('22222222-2222-2222-2222-222222222222', 'Caja 2 - AutoPago', 'Estacionamiento', true),
      ('33333333-3333-3333-3333-333333333333', 'Caja 3 - Tesorería', 'Planta Alta', true);