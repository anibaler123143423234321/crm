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

@Service
public class ClienteResidencialExcelService {

    @Autowired
    private ClienteResidencialRepository clienteResidencialRepository;

    // ✅ Generar Excel de TODOS los clientes con formato específico
    public byte[] generarExcelClientes() {
        List<ClienteResidencial> clientes = clienteResidencialRepository.findAll();
        return generarExcelConFormato(clientes, "Clientes Residenciales");
    }

    // ✅ Método principal para generar el archivo Excel con formato específico
    private byte[] generarExcelConFormato(List<ClienteResidencial> clientes, String sheetName) {
        if (clientes == null || clientes.isEmpty()) {
            return new byte[0]; // Si no hay datos, devolvemos un array vacío
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // ✅ Crear estilo para los encabezados (fondo verde y texto en blanco)
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            int rowNum = 0;

            for (ClienteResidencial cliente : clientes) {
                // ✅ Sección DATOS CLIENTE RESIDENCIAL
                Row headerRow1 = sheet.createRow(rowNum++);
                Cell headerCell1 = headerRow1.createCell(0);
                headerCell1.setCellValue("DATOS CLIENTE RESIDENCIAL");
                headerCell1.setCellStyle(headerStyle);

                // Agregar datos del cliente
                agregarFila(sheet, rowNum++, "CAMPAÑA:", cliente.getCampania());
                agregarFila(sheet, rowNum++, "NOMBRES Y APELLIDOS:", cliente.getNombresApellidos());
                agregarFila(sheet, rowNum++, "NIF / NIE:", cliente.getNifNie());
                agregarFila(sheet, rowNum++, "NACIONALIDAD:", cliente.getNacionalidad());
                agregarFila(sheet, rowNum++, "FECHA DE NACIMIENTO:", cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().toString() : "");
                agregarFila(sheet, rowNum++, "GÉNERO:", cliente.getGenero());
                agregarFila(sheet, rowNum++, "CORREO ELECTRÓNICO:", cliente.getCorreoElectronico());
                agregarFila(sheet, rowNum++, "CUENTA BANCARIA:", cliente.getCuentaBancaria());
                agregarFila(sheet, rowNum++, "PERMANENCIA:", cliente.getPermanencia());
                agregarFila(sheet, rowNum++, "DIRECCIÓN:", cliente.getDireccion());
                agregarFila(sheet, rowNum++, "TIPO DE FIBRA:", cliente.getTipoFibra());
                agregarFila(sheet, rowNum++, "MOVIL CONTACTO:", cliente.getMovilContacto());

                rowNum++; // Espacio entre secciones

                // ✅ Sección DATOS DE LA PROMOCIÓN
                Row headerRow2 = sheet.createRow(rowNum++);
                Cell headerCell2 = headerRow2.createCell(0);
                headerCell2.setCellValue("DATOS DE LA PROMOCIÓN");
                headerCell2.setCellStyle(headerStyle);

                // Agregar datos de la promoción
                agregarFila(sheet, rowNum++, "PROMOCIÓN:", cliente.getPlanActual());
                agregarFila(sheet, rowNum++, "TIPO PLAN:", cliente.getTipoPlan());
                agregarFila(sheet, rowNum++, "MOVIL A PORTAR 1 / COMPAÑÍA:", cliente.getMovilesAPortar() != null && !cliente.getMovilesAPortar().isEmpty() ? cliente.getMovilesAPortar().get(0) : "");
                agregarFila(sheet, rowNum++, "MOVIL A PORTAR 2 / COMPAÑÍA:", cliente.getMovilesAPortar() != null && cliente.getMovilesAPortar().size() > 1 ? cliente.getMovilesAPortar().get(1) : "");
                agregarFila(sheet, rowNum++, "PRECIO PROMOCIÓN / TIEMPO:", ""); // Ejemplo estático
                agregarFila(sheet, rowNum++, "PRECIO REAL O DESPUÉS DE PROMOCIÓN:", ""); // Ejemplo estático
                agregarFila(sheet, rowNum++, "OPERADOR:", ""); // Ejemplo estático
                agregarFila(sheet, rowNum++, "SEGMENTO:", ""); // Ejemplo estático

                rowNum += 2; // Espacio entre clientes
            }

            // Ajustar el ancho de las columnas
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convertir el workbook a un array de bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0]; // Si hay error, devolvemos un array vacío
        }
    }

    // ✅ Método para crear el estilo de los encabezados
    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex()); // Texto blanco
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex()); // Fondo verde
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    // ✅ Método auxiliar para agregar filas
    private void agregarFila(Sheet sheet, int rowNum, String titulo, String valor) {
        if (valor == null || valor.isEmpty()) return;
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(titulo);
        row.createCell(1).setCellValue(valor);
    }
}