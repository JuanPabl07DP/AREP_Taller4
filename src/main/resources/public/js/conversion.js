document.addEventListener('DOMContentLoaded', () => {
    const origenSelect = document.getElementById('origen');
    const destinoSelect = document.getElementById('destino');
    const montoInput = document.getElementById('monto');
    const resultadoInput = document.getElementById('resultado');
    const convertirBtn = document.getElementById('convertir');
    const historialDiv = document.createElement('div');

    // Configuración del historial
    historialDiv.className = 'mt-6 p-4 bg-white/80 backdrop-blur-md rounded-lg shadow-lg';
    historialDiv.innerHTML = `
        <div id="lista-historial" class="space-y-2"></div>
    `;
    document.querySelector('.calculator-container').appendChild(historialDiv);

    // Agregar botón de invertir
    const invertirBtn = document.createElement('button');
    invertirBtn.innerHTML = '🔄 Invertir monedas';
    invertirBtn.className = 'w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 transition-colors mt-2';
    convertirBtn.parentNode.insertBefore(invertirBtn, convertirBtn.nextSibling);

    // Función para invertir monedas
    invertirBtn.addEventListener('click', () => {
        const tempOrigen = origenSelect.value;
        origenSelect.value = destinoSelect.value;
        destinoSelect.value = tempOrigen;

        // Animación de rotación
        invertirBtn.style.transform = 'rotate(180deg)';
        setTimeout(() => invertirBtn.style.transform = '', 500);
    });

    // Validación en tiempo real
    montoInput.addEventListener('input', () => {
        const monto = parseFloat(montoInput.value);
        convertirBtn.disabled = isNaN(monto) || monto <= 0;
    });

    // Evitar monedas iguales
    destinoSelect.addEventListener('change', () => {
        if (origenSelect.value === destinoSelect.value) {
            alert('Por favor seleccione monedas diferentes');
            destinoSelect.value = destinoSelect.value === 'USD' ? 'EUR' : 'USD';
        }
    });

    // Función para agregar al historial
    function agregarAlHistorial(origen, destino, monto, resultado) {
        const historicoItem = document.createElement('div');
        historicoItem.className = 'bg-gray-50 p-2 rounded flex justify-between items-center';

        const fecha = new Date().toLocaleTimeString();
        historicoItem.innerHTML = `
            <span>${monto} ${origen} → ${resultado} ${destino}</span>
            <span class="text-sm text-gray-500">${fecha}</span>
        `;

        const listaHistorial = document.getElementById('lista-historial');
        listaHistorial.insertBefore(historicoItem, listaHistorial.firstChild);

        // Limitar a 5 registros
        if (listaHistorial.children.length > 5) {
            listaHistorial.removeChild(listaHistorial.lastChild);
        }

        // Animación de entrada
        historicoItem.style.opacity = '0';
        historicoItem.style.transform = 'translateY(-10px)';
        setTimeout(() => {
            historicoItem.style.transition = 'all 0.3s ease';
            historicoItem.style.opacity = '1';
            historicoItem.style.transform = 'translateY(0)';
        }, 50);
    }

    // Función de conversión mejorada
    async function convertirMoneda(origen, destino, monto) {
        try {
            console.log('Enviando petición:', { origen, destino, monto });
            const response = await fetch(`/api/convertir?origen=${origen}&destino=${destino}&monto=${monto}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('Respuesta recibida:', data);

            if (data.error) {
                throw new Error(data.error);
            }

            return data.resultado;
        } catch (error) {
            console.error('Error en la conversión:', error);
            throw error;
        }
    }

    // Animación del resultado
    function animarResultado(elemento) {
        elemento.style.transform = 'scale(1.05)';
        elemento.style.transition = 'transform 0.3s ease';

        setTimeout(() => {
            elemento.style.transform = 'scale(1)';
        }, 300);
    }

    // Manejador del botón mejorado
    convertirBtn.addEventListener('click', async () => {
        const origen = origenSelect.value;
        const destino = destinoSelect.value;
        const monto = parseFloat(montoInput.value);

        if (isNaN(monto) || monto <= 0) {
            resultadoInput.value = 'Por favor, ingrese un monto válido';
            animarResultado(resultadoInput);
            return;
        }

        // Mostrar estado de carga con animación
        resultadoInput.value = 'Calculando...';
        convertirBtn.disabled = true;
        convertirBtn.innerHTML = '<span class="animate-spin">↻</span> Convirtiendo...';

        try {
            const resultado = await convertirMoneda(origen, destino, monto);
            const resultadoFormateado = `${resultado.toFixed(2)} ${destino}`;
            resultadoInput.value = resultadoFormateado;

            // Animar el resultado
            animarResultado(resultadoInput);

            // Agregar al historial
            agregarAlHistorial(origen, destino, monto, resultado.toFixed(2));
        } catch (error) {
            console.error('Error detallado:', error);
            resultadoInput.value = 'Error en la conversión. Intente nuevamente.';
            animarResultado(resultadoInput);
        } finally {
            convertirBtn.disabled = false;
            convertirBtn.innerHTML = 'Convertir';
        }
    });

    // Agregar manejo de tecla Enter
    montoInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !convertirBtn.disabled) {
            convertirBtn.click();
        }
    });
});