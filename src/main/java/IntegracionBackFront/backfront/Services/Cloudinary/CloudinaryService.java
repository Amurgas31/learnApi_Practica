package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.netty.util.internal.ObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    //Constante que define el tamaño máximo permitido para los archivos de (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    //Constante para definir los tipos de archivos admitidos
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};
    //Cliente de Cloudinary inyectado como dependencia
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Subir imagenes a la raiz de Cloudinary
     * @param file
     * @return String URL de la imagen
     * @throws IOException
     */
    public String uploadImage(MultipartFile file) throws IOException {
        //1. Validamos el archivo
        validateImage(file);

        // Sube el archivo a Cloudinary con configuraciones básicas
        // Tipo de recurso auto-detectado
        // Calidad automática con nivel "good"
        Map<?, ?> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.asMap(
                        "resource_type", "auto",
                        "quality", "auto:good"
                ));

        //Retorna la URL segura de la imagen
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Sube una imagen a una carpeta en especifico
     * @param file
     * @param folder caréta destino
     * @return URL segura (HTTPS) de la imagen subida
     * @throws IOException Si ocurre un error duantes la subida
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        validateImage(file);
        // Generar un nombre unico para el archivo
        // Conservar la extensión original
        // Agregarun prefijo y un UUID para evitar colisiones

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = "img_" + UUID.randomUUID() + fileExtension;

        //Configuración para subir la imagen
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder, //Carpeta de destino
                "public_id", uniqueFilename,  //Nombre unico para el archivp
                "use_filename", false,  //No usar el nombre original
                "unique_filename", false,  //No generar nombre unico (proceso hecho anteriormente)
                "overwrite", false,  //No sobreescribir archivos
                "resource_type", "auto", //Auto-detectar tipo de recurso
                "quality", "auto:good" //Optimización de calidad automática
        );

        //Subir el archivo
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        //Retornamos la URL segura
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Valida la imagen
     * @param file
     */
    private void validateImage(MultipartFile file){
        //1. verificar si el archivo esta vacío
        if (file.isEmpty()){
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }

        //2. Verificar el tamaño de l imagen
        if (file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException("El archivo no puede ser mayor a 5MB");
        }

        //3.Obtener y validar el nombre original del archivo
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null){
            throw new IllegalArgumentException("Nombre del archivo inválido");
        }

        //4. Extraer y validar la extensión
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains((extension))){
            throw new IllegalArgumentException("Solo se permiten archivos JPG, JPEG y PNG");
        }

        //Verifica el tipo de MIME sea una imagen
        if (!file.getContentType().startsWith("imagen/")){
            throw new IllegalArgumentException("El archivo dede ser una imagen válida");
        }
    }
}