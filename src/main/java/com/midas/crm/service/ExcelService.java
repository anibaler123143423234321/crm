package com.midas.crm.service;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private UserRepository userRepository;

    public List<User> leerUsuariosDesdeExcel(MultipartFile file) throws IOException {
        List<User> usuarios = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Tomamos la primera hoja del Excel
            Iterator<Row> rows = sheet.iterator();
            boolean isFirstRow = true; // Para saltar el encabezado

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (isFirstRow) { // Saltamos la primera fila (encabezado)
                    isFirstRow = false;
                    continue;
                }

                Iterator<Cell> cells = currentRow.iterator();
                User user = new User();

                user.setFechaCreacion(LocalDateTime.now()); // Fecha de creación por defecto
                user.setRole(Role.ASESOR); // Rol predeterminado
                user.setEstado("A"); // Estado activo

                int cellIndex = 0;
                while (cells.hasNext()) {
                    Cell currentCell = cells.next();
                    switch (cellIndex) {
                        case 0 -> user.setUsername(getCellValueAsString(currentCell));
                        case 1 -> user.setPassword(getCellValueAsString(currentCell)); // Recuerda encriptarla al guardar
                        case 2 -> user.setNombre(getCellValueAsString(currentCell));
                        case 3 -> user.setApellido(getCellValueAsString(currentCell));
                        case 4 -> user.setTelefono(getCellValueAsString(currentCell));
                        case 5 -> user.setEmail(getCellValueAsString(currentCell));
                        case 6 -> user.setSede(getCellValueAsString(currentCell));
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                usuarios.add(user);
            }
        }
        return usuarios;
    }

    // Método auxiliar para manejar valores de celda de diferentes tipos
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue()); // Convierte números a String sin notación científica
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
