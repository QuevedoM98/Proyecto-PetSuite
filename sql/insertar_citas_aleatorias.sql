-- Inserción de visitas veterinarias
INSERT INTO VisitaVeterinaria (fecha, fecha_hora, motivo, observaciones, mascota_id, veterinaria_id)
SELECT
    DATE(t.fecha_hora) AS fecha,
    t.fecha_hora,

    ELT(
    FLOOR(1 + RAND() * 20),
    'Vacunación',
    'Consulta general',
    'Desparasitación',
    'Control post-operatorio',
    'Chequeo de rutina',
    'Urgencia médica',
    'Revisión dental',
    'Problema dermatológico',
    'Cojea de una pata',
    'Fiebre persistente',
    'Otitis',
    'Corte de uñas',
    'Inapetencia',
    'Vómitos y diarrea',
    'Cambio de alimentación',
    'Revisión prequirúrgica',
    'Revisión ocular',
    'Castración programada',
    'Examen geriátrico',
    'Extracción de cuerpo extraño'
    ) AS motivo,

    ELT(
    FLOOR(1 + RAND() * 20),
    'Paciente estable y sin signos clínicos alarmantes',
    'Requiere medicación durante 5 días',
    'Buen comportamiento durante la atención',
    'Se agenda control en 3 días',
    'Se detecta inflamación leve',
    'Peso dentro del rango ideal',
    'Requiere análisis de sangre',
    'Aplicada vacuna según calendario',
    'Recomendación: dieta blanda por 2 días',
    'Se indica radiografía',
    'No se detectan parásitos externos',
    'Consulta por comportamiento agresivo',
    'Hidratación subcutánea administrada',
    'Posible alergia alimentaria',
    'Se sugiere limpieza dental en próxima visita',
    'Ligera mejoría respecto a consulta anterior',
    'Requiere seguimiento nutricional',
    'Sin alteraciones detectadas al examen físico',
    'Alta médica sin complicaciones',
    'Se entrega receta y recomendaciones por escrito'
    ) AS observaciones,

    m.id AS mascota_id,
    3 AS veterinaria_id

FROM (
    SELECT id FROM Mascota ORDER BY RAND() LIMIT 10
    ) m
    CROSS JOIN (
    SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    ) n
    CROSS JOIN (
    SELECT TIMESTAMP(
    CURDATE() + INTERVAL FLOOR(RAND() * 14) DAY,
    SEC_TO_TIME(FLOOR(RAND() * (18 - 9 + 1) + 9) * 3600)
    ) AS fecha_hora
    ) t;

-- Inserción de servicios de peluquería
INSERT INTO ServicioPeluqueria (fecha, tipoServicio, precio, mascota_id, peluqueria_id)
SELECT
    CURDATE() + INTERVAL FLOOR(RAND() * 14) DAY AS fecha,

    ELT(
    FLOOR(1 + RAND() * 20),
    'Baño completo',
    'Corte de pelo',
    'Limpieza de oídos',
    'Corte de uñas',
    'Deslanado',
    'Perfume',
    'Desparasitación externa',
    'Baño medicado',
    'Cepillado dental',
    'Tratamiento para pulgas',
    'Aplicación de loción antiparasitaria',
    'Baño con shampoo hipoalergénico',
    'Baño antipulgas',
    'Tinte estético',
    'Secado con turbina',
    'Hidratación capilar',
    'Corte estilo cachorro',
    'Revisión de piel y pelo',
    'Corte sanitario',
    'Servicio express'
    ) AS tipoServicio,

    ROUND(10 + (RAND() * 50), 2) AS precio,

    m.id AS mascota_id,
    3 AS peluqueria_id

FROM (
    SELECT id FROM mascota WHERE id BETWEEN 1 AND 10
    ) m
    CROSS JOIN (
    SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    ) n;
