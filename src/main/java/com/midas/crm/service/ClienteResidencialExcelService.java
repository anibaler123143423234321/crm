package com.midas.crm.service;

import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.repository.ClienteResidencialRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteResidencialExcelService {

    @Autowired
    private ClienteResidencialRepository clienteResidencialRepository;

    /**
     * =========================================================================
     *  1) MÉTODO PARA EXPORTAR "MASIVO"
     *     Cada cliente en una fila y cada columna para un campo específico.
     *     Ejemplo de columnas: NÚMERO (movilContacto), CAMPAÑA, NOMBRE...
     * =========================================================================
     */
    public byte[] generarExcelClientesMasivo() {
        List<ClienteResidencial> clientes = clienteResidencialRepository.findAll();
        if (clientes == null || clientes.isEmpty()) {
            return new byte[0];
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes Residenciales");

            // Crear estilo para encabezado
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            // Encabezado (fila 0)
            Row headerRow = sheet.createRow(0);
            // Columnas de ejemplo (puedes añadir/quitar según tus campos)
            crearCeldaConEstilo(headerRow, 0, "NÚMERO", headerStyle);
            crearCeldaConEstilo(headerRow, 1, "CAMPAÑA", headerStyle);
            crearCeldaConEstilo(headerRow, 2, "NOMBRE", headerStyle);
            crearCeldaConEstilo(headerRow, 3, "NIF/NIE", headerStyle);
            crearCeldaConEstilo(headerRow, 4, "FECHA_NAC", headerStyle);
            crearCeldaConEstilo(headerRow, 5, "CORREO", headerStyle);
            crearCeldaConEstilo(headerRow, 6, "DIRECCIÓN", headerStyle);
            crearCeldaConEstilo(headerRow, 7, "TIPO_FIBRA", headerStyle);
            crearCeldaConEstilo(headerRow, 8, "PLAN_ACTUAL", headerStyle);
            crearCeldaConEstilo(headerRow, 9, "MOVILES_A_PORTAR", headerStyle);

            // Rellenar datos fila a fila
            int rowIndex = 1;
            for (ClienteResidencial cliente : clientes) {
                Row dataRow = sheet.createRow(rowIndex++);

                // 0) NÚMERO -> movilContacto
                dataRow.createCell(0)
                        .setCellValue(cliente.getMovilContacto() != null ? cliente.getMovilContacto() : "");

                // 1) CAMPAÑA
                dataRow.createCell(1)
                        .setCellValue(cliente.getCampania() != null ? cliente.getCampania() : "");

                // 2) NOMBRE -> nombresApellidos
                dataRow.createCell(2)
                        .setCellValue(cliente.getNombresApellidos() != null ? cliente.getNombresApellidos() : "");

                // 3) NIF/NIE
                dataRow.createCell(3)
                        .setCellValue(cliente.getNifNie() != null ? cliente.getNifNie() : "");

                // 4) FECHA_NAC
                dataRow.createCell(4)
                        .setCellValue(cliente.getFechaNacimiento() != null
                                ? cliente.getFechaNacimiento().toString()
                                : "");

                // 5) CORREO
                dataRow.createCell(5)
                        .setCellValue(cliente.getCorreoElectronico() != null ? cliente.getCorreoElectronico() : "");

                // 6) DIRECCIÓN
                dataRow.createCell(6)
                        .setCellValue(cliente.getDireccion() != null ? cliente.getDireccion() : "");

                // 7) TIPO_FIBRA
                dataRow.createCell(7)
                        .setCellValue(cliente.getTipoFibra() != null ? cliente.getTipoFibra() : "");

                // 8) PLAN_ACTUAL
                dataRow.createCell(8)
                        .setCellValue(cliente.getPlanActual() != null ? cliente.getPlanActual() : "");

                // 9) MOVILES_A_PORTAR -> Concatenamos la lista de Strings en una sola celda
                if (cliente.getMovilesAPortar() != null && !cliente.getMovilesAPortar().isEmpty()) {
                    // Usamos String.join para concatenar los móviles separados por comas
                    String movilesConcatenados = String.join(", ", cliente.getMovilesAPortar());
                    dataRow.createCell(9).setCellValue(movilesConcatenados);
                } else {
                    dataRow.createCell(9).setCellValue("");
                }
            }

            // Ajustar el ancho de las columnas
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    /**
     * =========================================================================
     *  2) MÉTODO PARA EXPORTAR "INDIVIDUAL"
     *     Un solo cliente en una hoja, con estructura vertical y secciones.
     * =========================================================================
     */
    public byte[] generarExcelClienteIndividual(String movilContacto) {
        Optional<ClienteResidencial> optionalCliente = clienteResidencialRepository.findByMovilContacto(movilContacto);
        if (optionalCliente.isEmpty()) {
            return new byte[0]; // si no existe, array vacío
        }

        ClienteResidencial cliente = optionalCliente.get();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cliente Residencial");

            // Estilo para los encabezados
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            int rowNum = 0;

            // ======================================
            // Sección: DATOS CLIENTE RESIDENCIAL
            // ======================================
            Row headerRow1 = sheet.createRow(rowNum++);
            Cell headerCell1 = headerRow1.createCell(0);
            headerCell1.setCellValue("DATOS CLIENTE RESIDENCIAL");
            headerCell1.setCellStyle(headerStyle);

            agregarFila(sheet, rowNum++, "CAMPAÑA:", cliente.getCampania());
            agregarFila(sheet, rowNum++, "NOMBRES Y APELLIDOS:", cliente.getNombresApellidos());
            agregarFila(sheet, rowNum++, "NIF / NIE:", cliente.getNifNie());
            agregarFila(sheet, rowNum++, "NACIONALIDAD:", cliente.getNacionalidad());
            agregarFila(sheet, rowNum++, "FECHA DE NACIMIENTO:",
                    cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().toString() : "");
            agregarFila(sheet, rowNum++, "GÉNERO:", cliente.getGenero());
            agregarFila(sheet, rowNum++, "CORREO ELECTRÓNICO:", cliente.getCorreoElectronico());
            agregarFila(sheet, rowNum++, "CUENTA BANCARIA:", cliente.getCuentaBancaria());
            agregarFila(sheet, rowNum++, "DIRECCIÓN:", cliente.getDireccion());
            agregarFila(sheet, rowNum++, "TIPO DE FIBRA:", cliente.getTipoFibra());

            // Ejemplo de campo no existente en tu entidad (puedes dejarlo vacío o fijo)
            agregarFila(sheet, rowNum++, "HORA DE INSTALACIÓN:", "");

            rowNum++; // Espacio antes de la siguiente sección

            // ======================================
            // Sección: DATOS DE LA PROMOCIÓN
            // ======================================
            Row headerRow2 = sheet.createRow(rowNum++);
            Cell headerCell2 = headerRow2.createCell(0);
            headerCell2.setCellValue("DATOS DE LA PROMOCIÓN");
            headerCell2.setCellStyle(headerStyle);

            agregarFila(sheet, rowNum++, "PROMOCIÓN:", cliente.getPlanActual());
            agregarFila(sheet, rowNum++, "TV / DECO:", "");
            agregarFila(sheet, rowNum++, "GRABACIÓN OCM:", "");
            agregarFila(sheet, rowNum++, "MOVIL CONTACTO:", cliente.getMovilContacto());
            agregarFila(sheet, rowNum++, "FIJO / COMPAÑÍA:", cliente.getFijoCompania());

            // MOVILES A PORTAR: como es List<String>, iteramos directamente
            if (cliente.getMovilesAPortar() != null && !cliente.getMovilesAPortar().isEmpty()) {
                for (int i = 0; i < cliente.getMovilesAPortar().size(); i++) {
                    agregarFila(sheet, rowNum++,
                            "MOVIL A PORTAR " + (i + 1) + " / COMPAÑÍA:",
                            cliente.getMovilesAPortar().get(i));
                }
            }

            // Otros campos de ejemplo
            agregarFila(sheet, rowNum++, "PRECIO PROMOCIÓN / TIEMPO:", "");
            agregarFila(sheet, rowNum++, "PRECIO REAL O DESPUÉS DE PROMOCIÓN:", "");
            agregarFila(sheet, rowNum++, "SEGMENTO:", "");
            agregarFila(sheet, rowNum++, "COMENTARIOS RELEVANTES CON EL CLIENTE:", "");
            agregarFila(sheet, rowNum++, "COMERCIAL:", "");
            agregarFila(sheet, rowNum++, "ASIGNADO A:", "");
            agregarFila(sheet, rowNum++, "OBSERVACIONES:", "");
            agregarFila(sheet, rowNum++, "TIPO DE USUARIO:", "");

            // Ajustar el ancho de las primeras columnas
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    /**
     * =========================================================================
     *  MÉTODOS AUXILIARES
     * =========================================================================
     */

    // Crea un estilo de encabezado (fondo verde y texto en blanco, negrita)
    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    // Crea una celda con el valor 'texto' y aplica un estilo
    private void crearCeldaConEstilo(Row row, int columnIndex, String texto, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(texto);
        cell.setCellStyle(style);
    }

    // Método auxiliar para agregar filas en el Excel "vertical"
    private void agregarFila(Sheet sheet, int rowNum, String titulo, String valor) {
        if (valor == null) valor = "";
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(titulo);
        row.createCell(1).setCellValue(valor);
    }
}
