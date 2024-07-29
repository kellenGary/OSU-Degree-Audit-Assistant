package mainPKG;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

@WebServlet(name = "FileServlet", urlPatterns = "/fileServlet")
public class FileServlet extends HttpServlet {

    private static final String inputDirectory = "uploaded-files";
    private static final String outputDirectory = "output-htmls";


    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        String uploadDirPath = getServletContext().getRealPath("/") + inputDirectory;
        String outputDirPath = getServletContext().getRealPath("/") + outputDirectory;
        File uploadDir = new File(uploadDirPath);

        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new ServletException("Failed to create upload directory: " + uploadDirPath);
            }
        }

        // Process each file part
        String uploadedFileName = null;
        for (Part part : request.getParts()) {
            String fileName = getFileName(part);
            if (fileName != null && !fileName.isEmpty()) {
                File file = new File(uploadDir, fileName);
                try (InputStream inputStream = part.getInputStream()) {
                    // Write the file to the specified location
                    java.nio.file.Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                uploadedFileName = fileName; // Capture the name of the uploaded file
            }
        }

        // If a file was uploaded, call your method with the uploaded file
        if (uploadedFileName != null) {
            File uploadedFile = new File(uploadDir, uploadedFileName);
            Main.execute(uploadedFile, outputDirPath);
        }

        // Construct the path to the output HTML file
        String contextPath = request.getContextPath();
        String htmlPath = contextPath + "/" + outputDirectory + "/output.html";


        // Redirect to the output HTML file
        response.sendRedirect(htmlPath);
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String cd : contentDisposition.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}