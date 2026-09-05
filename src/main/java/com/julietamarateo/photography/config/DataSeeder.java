package com.julietamarateo.photography.config;

import com.julietamarateo.photography.entity.Album;
import com.julietamarateo.photography.entity.CoverPhoto;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.entity.Profile;
import com.julietamarateo.photography.entity.ServiceItem;
import com.julietamarateo.photography.entity.User;
import com.julietamarateo.photography.repository.AlbumRepository;
import com.julietamarateo.photography.repository.CoverPhotoRepository;
import com.julietamarateo.photography.repository.PhotoRepository;
import com.julietamarateo.photography.repository.ProfileRepository;
import com.julietamarateo.photography.repository.ServiceRepository;
import com.julietamarateo.photography.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ServiceRepository serviceRepository;
    private final ProfileRepository profileRepository;
    private final AlbumRepository albumRepository;
    private final CoverPhotoRepository coverPhotoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      PhotoRepository photoRepository,
                      ServiceRepository serviceRepository,
                      ProfileRepository profileRepository,
                      AlbumRepository albumRepository,
                      CoverPhotoRepository coverPhotoRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.serviceRepository = serviceRepository;
        this.profileRepository = profileRepository;
        this.albumRepository = albumRepository;
        this.coverPhotoRepository = coverPhotoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedDefaultProfile();
        seedDefaultServices();
        seedDefaultPhotos();
        seedDefaultAlbums();
        seedDefaultCoverPhoto();
    }

    private void seedAdminUser() {
        String adminEmail = "julietamarateo4@gmail.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("12345678"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println(">>> [DataSeeder] Administrador creado por defecto: " + adminEmail);
        }
    }

    private void seedDefaultProfile() {
        if (profileRepository.count() == 0) {
            Profile profile = new Profile(
                    "Julieta Marateo",
                    "Técnica en Fotografía",
                    "Mar del Plata, Argentina",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=85",
                    "Hola, mi nombre es Julieta Marateo. Soy Técnica en Fotografía radicada en Mar del Plata. Me apasiona capturar momentos únicos, encargándome con máxima dedicación tanto de la toma fotográfica como de la postproducción y edición profesional. Ofrezco coberturas para casamientos, cumpleaños de XV y eventos en general, garantizando un recuerdo imborrable con la mejor calidad visual.",
                    "2281311917",
                    "julietamarateo4@gmail.com",
                    "@julietamph_"
            );
            profileRepository.save(profile);
            System.out.println(">>> [DataSeeder] Perfil inicial de Julieta Marateo sembrado en SQLite.");
        }
    }

    private void seedDefaultServices() {
        if (serviceRepository.count() == 0) {
            String defaultWhatsApp = "https://wa.me/5492281311917?text=Hola%20Julieta,%20vengo%20de%20tu%20sitio%20web%20y%20me%20gustar%C3%ADa%20agendar%20una%20cita.";

            ServiceItem s1 = new ServiceItem(
                    "serv-1",
                    "Casamientos",
                    "Cobertura fotográfica integral y sensible para el día de tu boda. Acompañamos desde los preparativos hasta el último baile, capturando emociones genuinas con estética cinematográfica.",
                    "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800&q=80",
                    Arrays.asList(
                            "Preparativos (Getting Ready) de los novios",
                            "Ceremonia religiosa o civil & sesión de pareja",
                            "Cobertura de fiesta y momentos espontáneos",
                            "Galería digital privada en alta resolución & fotos editadas"
                    ),
                    defaultWhatsApp,
                    450.0
            );

            ServiceItem s2 = new ServiceItem(
                    "serv-2",
                    "Cumpleaños de XV",
                    "Un recuerdo mágico para celebrar los 15 años. Realizamos sesiones de exteriores previas llenas de frescura y estilo, además de la cobertura completa de la fiesta.",
                    "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?auto=format&fit=crop&w=800&q=80",
                    Arrays.asList(
                            "Sesión previa en locaciones de Mar del Plata",
                            "Cobertura completa de la fiesta y vals",
                            "Retoque estético profesional individual",
                            "Entrega ágil en pendrive y galería web protegida"
                    ),
                    defaultWhatsApp,
                    350.0
            );

            ServiceItem s3 = new ServiceItem(
                    "serv-3",
                    "Eventos en General",
                    "Coberturas para celebraciones corporativas, aniversarios, cumpleaños familiares, bautismos y recitales. Registro dinámico y profesional con máxima fidelidad visual.",
                    "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800&q=80",
                    Arrays.asList(
                            "Eventos sociales, culturales y corporativos",
                            "Tomas espontáneas, detalles y fotos grupales",
                            "Postproducción y corrección de color profesional",
                            "Planificación personalizada de tiempos y momentos clave"
                    ),
                    defaultWhatsApp,
                    250.0
            );

            serviceRepository.saveAll(Arrays.asList(s1, s2, s3));
            System.out.println(">>> [DataSeeder] 3 Servicios fotográficos iniciales sembrados en SQLite.");
        }
    }

    private void seedDefaultPhotos() {
        if (photoRepository.count() == 0) {
            List<Photo> initialPhotos = Arrays.asList(
                    new Photo("photo-1", "Amanecer en los Acantilados", "Paisajismo", 130.0,
                            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=85",
                            "Luz dorada matutina sobre la costa marítima de Mar del Plata, capturando la inmensidad del océano Atlántico y el romper de las olas.",
                            "75 x 50 cm · Impresión Fine Art",
                            "Sony Alpha 7 IV · FE 24-70mm f/2.8 GM II · f/8.0 · 1/250s · ISO 100",
                            "Sony Alpha 7 IV", "FE 24-70mm f/2.8 GM II", "f/8.0", "1/250s", "ISO 100",
                            true, "Mar del Plata", true),

                    new Photo("photo-2", "Esencia Botánica & Vidrio", "Foto Producto", 90.0,
                            "https://images.unsplash.com/photo-1608248597359-28c049e048ea?auto=format&fit=crop&w=1200&q=85",
                            "Fotografía comercial publicitaria de cosmética natural con iluminación controlada de estudio, texturas acuáticas y reflejos sutiles.",
                            "40 x 40 cm · Alta Definición",
                            "Canon EOS R5 · RF 100mm f/2.8L Macro IS · f/11 · 1/160s · ISO 64",
                            "Canon EOS R5", "RF 100mm f/2.8L Macro IS", "f/11", "1/160s", "ISO 64",
                            true, "Editorial", true),

                    new Photo("photo-3", "Promesa al Atardecer", "Eventos", 150.0,
                            "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=1200&q=85",
                            "Momento íntimo de casamiento al aire libre. La calidez del atardecer abrazando la complicidad y el amor de la pareja.",
                            "60 x 40 cm · Fine Art 310g",
                            "Sony Alpha 7R V · FE 85mm f/1.4 GM · f/1.8 · 1/800s · ISO 125",
                            "Sony Alpha 7R V", "FE 85mm f/1.4 GM", "f/1.8", "1/800s", "ISO 125",
                            true, "Casamiento", true),

                    new Photo("photo-4", "Dunas & Horizonte Costero", "Paisajismo", 115.0,
                            "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=85",
                            "Paisaje salvaje de dunas y cielo despejado. Tonos naturales y sensación de calma infinita.",
                            "80 x 50 cm · Lienzo Montado",
                            "Nikon Z8 · NIKKOR Z 14-30mm f/4 S · f/9.0 · 1/125s · ISO 64",
                            "Nikon Z8", "NIKKOR Z 14-30mm f/4 S", "f/9.0", "1/125s", "ISO 64",
                            true, null, false),

                    new Photo("photo-5", "Brillo de Quinceañera", "Eventos", 140.0,
                            "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?auto=format&fit=crop&w=1200&q=85",
                            "Sesión fotográfica de XV años en exteriores. Captura natural de la emoción y el protagonismo de la homenajeada.",
                            "50 x 75 cm · Papel Lustre",
                            "Fujifilm X-T5 · XF 56mm f/1.2 R WR · f/1.4 · 1/1000s · ISO 160",
                            "Fujifilm X-T5", "XF 56mm f/1.2 R WR", "f/1.4", "1/1000s", "ISO 160",
                            true, "Quince Años", false),

                    new Photo("photo-6", "Café de Especialidad & Cerámica", "Foto Producto", 85.0,
                            "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=1200&q=85",
                            "Fotografía gastronómica y de producto para cafetería de especialidad en Mar del Plata. Textura de café filtrado y luz natural cenital.",
                            "45 x 30 cm · Giclée Print",
                            "Sony Alpha 7 IV · FE 50mm f/1.2 GM · f/2.2 · 1/400s · ISO 200",
                            "Sony Alpha 7 IV", "FE 50mm f/1.2 GM", "f/2.2", "1/400s", "ISO 200",
                            true, null, false),

                    new Photo("photo-7", "Viento & Olas en Playa Grande", "Paisajismo", 125.0,
                            "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=1200&q=85",
                            "La fuerza del mar marplatense en una tarde de brisa marina. Tonos verde agua, espuma blanca y cielo límpido.",
                            "90 x 60 cm · Aluminio Mate",
                            "Sony Alpha 7 IV · FE 70-200mm f/2.8 GM OSS II · f/5.6 · 1/1000s · ISO 160",
                            "Sony Alpha 7 IV", "FE 70-200mm f/2.8 GM OSS II", "f/5.6", "1/1000s", "ISO 160",
                            true, "Costa Atlántica", false),

                    new Photo("photo-8", "Celebración & Luz Cálida", "Eventos", 135.0,
                            "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1200&q=85",
                            "Detalle de mesa principal y ambientación de evento social con guirnaldas de luces y flores frescas.",
                            "50 x 50 cm · Papel Museo 310g",
                            "Canon EOS R6 Mark II · RF 35mm f/1.8 IS Macro · f/2.0 · 1/160s · ISO 800",
                            "Canon EOS R6 Mark II", "RF 35mm f/1.8 IS Macro", "f/2.0", "1/160s", "ISO 800",
                            true, null, false),

                    new Photo("photo-9", "Reloj Cronógrafo de Lujo", "Foto Producto", 110.0,
                            "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=1200&q=85",
                            "Macrofotografía de alta relojería. Detalles minuciosos de titanio, bisel cerámico y reflejos de luz rasante.",
                            "40 x 40 cm · Fine Art Baritado",
                            "Hasselblad 907X · XCD 120mm Macro · f/16 · 1/125s · ISO 100",
                            "Hasselblad 907X", "XCD 120mm Macro", "f/16", "1/125s", "ISO 100",
                            true, null, false)
            );

            photoRepository.saveAll(initialPhotos);
            System.out.println(">>> [DataSeeder] 9 Fotografías de portafolio sembradas en SQLite.");
        }
    }

    private void seedDefaultAlbums() {
        if (albumRepository.count() == 0) {
            List<Album> defaultAlbums = Arrays.asList(
                    new Album(
                            "casamientos",
                            "Casamientos",
                            "Casamientos",
                            "Historias de amor, ceremonias íntimas y momentos espontáneos de bodas.",
                            "",
                            1
                    ),
                    new Album(
                            "cumpleanos-xv",
                            "Cumpleaños XV",
                            "Cumpleaños XV",
                            "Sesiones previas llenas de estilo, fiesta y vals de 15 años.",
                            "",
                            2
                    ),
                    new Album(
                            "eventos",
                            "Eventos",
                            "Eventos",
                            "Celebraciones sociales, aniversarios y registros culturales.",
                            "",
                            3
                    ),
                    new Album(
                            "paisajismo",
                            "Paisajismo",
                            "Paisajismo",
                            "Horizontes, dunas y la inmensidad del océano en Mar del Plata.",
                            "",
                            4
                    ),
                    new Album(
                            "foto-producto",
                            "Foto Producto",
                            "Foto Producto",
                            "Composiciones gastronómicas y comerciales con iluminación de estudio.",
                            "",
                            5
                    )
            );
            albumRepository.saveAll(defaultAlbums);
            System.out.println(">>> [DataSeeder] 5 Álbumes temáticos sembrados en SQLite.");
        }
    }

    private void seedDefaultCoverPhoto() {
        if (coverPhotoRepository.count() == 0) {
            CoverPhoto defaultCover = new CoverPhoto(
                    "photo-1",
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=85",
                    "Amanecer en los Acantilados",
                    "Paisajismo",
                    "Luz dorada matutina sobre la costa marítima de Mar del Plata, capturando la inmensidad del océano Atlántico y el romper de las olas."
            );
            coverPhotoRepository.save(defaultCover);
            System.out.println(">>> [DataSeeder] Foto de portada Hero sembrada en SQLite.");
        }
    }
}
