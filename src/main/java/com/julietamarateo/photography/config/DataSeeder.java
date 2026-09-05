package com.julietamarateo.photography.config;

import com.julietamarateo.photography.entity.Album;
import com.julietamarateo.photography.entity.CoverPhoto;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.entity.Profile;
import com.julietamarateo.photography.entity.SiteContent;
import com.julietamarateo.photography.entity.User;
import com.julietamarateo.photography.entity.AlbumPhoto;
import com.julietamarateo.photography.repository.AlbumPhotoRepository;
import com.julietamarateo.photography.repository.AlbumRepository;
import com.julietamarateo.photography.repository.CoverPhotoRepository;
import com.julietamarateo.photography.repository.PhotoRepository;
import com.julietamarateo.photography.repository.ProfileRepository;
import com.julietamarateo.photography.repository.SiteContentRepository;
import com.julietamarateo.photography.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ProfileRepository profileRepository;
    private final AlbumRepository albumRepository;
    private final AlbumPhotoRepository albumPhotoRepository;
    private final CoverPhotoRepository coverPhotoRepository;
    private final SiteContentRepository siteContentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      PhotoRepository photoRepository,
                      ProfileRepository profileRepository,
                      AlbumRepository albumRepository,
                      AlbumPhotoRepository albumPhotoRepository,
                      CoverPhotoRepository coverPhotoRepository,
                      SiteContentRepository siteContentRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.profileRepository = profileRepository;
        this.albumRepository = albumRepository;
        this.albumPhotoRepository = albumPhotoRepository;
        this.coverPhotoRepository = coverPhotoRepository;
        this.siteContentRepository = siteContentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedDefaultSiteContent();
        seedDefaultProfile();
        seedDefaultAlbums();
        seedDefaultPhotos();
        seedDefaultCoverPhoto();
    }

    private void seedAdminUser() {
        String[] adminEmails = {"admin@denniswanderlight.com", "julietamarateo4@gmail.com"};
        for (String email : adminEmails) {
            if (!userRepository.existsByEmail(email)) {
                User admin = new User();
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode("12345678"));
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println(">>> [DataSeeder] Administrador sembrado: " + email);
            }
        }
    }

    private void seedDefaultSiteContent() {
        if (siteContentRepository.count() == 0) {
            SiteContent sc = new SiteContent();
            sc.setBrandName("Dennis Wanderlight");
            sc.setBrandTagline("Travel & Documentary");
            sc.setHeroTitle("The World, Unfiltered");
            sc.setHeroSubtitle("Journeys captured beyond the postcard view");
            sc.setHeroButtonText("Explore Projects");
            sc.setHeroBgUrl("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=2000&q=85");

            sc.setMenuHome("Home");
            sc.setMenuPortfolio("Portfolio");
            sc.setMenuAbout("About");
            sc.setMenuContact("Contact");

            sc.setVignettesKicker("Vignettes from the edge");
            sc.setVignettesTitle("A curated selection of recent expeditions and untold stories");
            sc.setVignettesLabel1("Tokyo's Neon Pulse");
            sc.setVignettesImage1("https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1200&q=85");
            sc.setVignettesImage2("https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1200&q=85");

            sc.setStoryKickerLeft("Beyond the frame");
            sc.setStoryKickerRight("Stories in motion");
            sc.setStoryButtonText("My Story");
            sc.setStoryBgUrl("https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=2000&q=85");
            sc.setStoryPortraitUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=85");

            sc.setAboutTitle("Dennis Wanderlight");
            sc.setAboutSubtitle("Travel & Documentary Photographer");
            sc.setAboutBio("Dennis Wanderlight is an independent travel and documentary photographer focused on capturing the raw, unscripted beauty of remote landscapes and human culture. From high alpine passes in the Andes to rainy twilight streets in Tokyo, Dennis seeks stories that exist beyond the conventional postcard perspective.");
            sc.setAboutQuote("Photography is not about documenting places; it's about holding on to the ephemeral light and silent narratives that define who we are.");
            sc.setAboutImageUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=85");

            sc.setContactTitle("Get in Touch");
            sc.setContactSubtitle("Available for worldwide expeditions, editorial assignments and fine art print commissions.");
            sc.setContactEmail("hello@denniswanderlight.com");
            sc.setContactPhone("+1 (555) 349-2810");
            sc.setContactLocation("Tokyo · Patagonia · Worldwide");
            sc.setInstagramHandle("@denniswanderlight");
            sc.setWhatsappNumber("+15553492810");

            sc.setFooterText("Journeys captured beyond the postcard view. All images shot on location worldwide.");
            sc.setCopyrightText("© 2026 Dennis Wanderlight. All rights reserved.");
            sc.setUpdatedAt(LocalDateTime.now());

            siteContentRepository.save(sc);
            System.out.println(">>> [DataSeeder] SiteContent Dennis Wanderlight sembrado en SQLite.");
        }
    }

    private void seedDefaultProfile() {
        if (profileRepository.count() == 0) {
            Profile profile = new Profile(
                    "Dennis Wanderlight",
                    "Travel & Documentary Photographer",
                    "Tokyo · Patagonia · Worldwide",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=85",
                    "Dennis Wanderlight is an independent travel and documentary photographer focused on capturing the raw, unscripted beauty of remote landscapes and human culture. From high alpine passes in the Andes to rainy twilight streets in Tokyo, Dennis seeks stories that exist beyond the conventional postcard perspective.",
                    "+15553492810",
                    "hello@denniswanderlight.com",
                    "@denniswanderlight",
                    Arrays.asList("Expeditions", "Tokyo Street", "Alpine Wilderness", "Editorial Print")
            );
            profileRepository.save(profile);
            System.out.println(">>> [DataSeeder] Perfil inicial de Dennis Wanderlight sembrado en SQLite.");
        }
    }

    private void seedDefaultAlbums() {
        if (albumPhotoRepository.count() == 0) {
            // Limpiar si había álbumes antiguos sin fotos para sincronizar con la plantilla Wix Dennis Wanderlight
            if (albumRepository.count() > 0 && albumPhotoRepository.count() == 0) {
                albumRepository.deleteAll();
            }

            // 1. Tokyo's Neon Pulse (Screenshot 1 & Screenshot 2)
            Album tokyo = new Album();
            tokyo.setId("tokyo-neon-pulse");
            tokyo.setName("Tokyo's Neon Pulse");
            tokyo.setSubtitle("Tokyo, Japan");
            tokyo.setCategory("Tokyo's Neon Pulse");
            tokyo.setDescription("This is the space to provide an in-depth look at the visual narrative and the details within the frame. Show the inspiration that led to this moment, and what you hope to communicate to your audience through this specific piece. You can use this section to share a particular feature that sets it apart from others or highlight a unique part of the creative process.");
            tokyo.setCoverImage("https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1200&q=85");
            tokyo.setDisplayOrder(1);
            albumRepository.save(tokyo);

            List<AlbumPhoto> tokyoPhotos = Arrays.asList(
                    new AlbumPhoto("tokyo-p1", tokyo, "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1200&q=85", "Tokyo Street Pedestrian Crossing in Rain", "portrait", 1),
                    new AlbumPhoto("tokyo-p2", tokyo, "https://images.unsplash.com/photo-1542051841857-5f90071e7989?auto=format&fit=crop&w=1200&q=85", "Japanese Red Kanji Neon Sign in Night Alleyway", "portrait", 2),
                    new AlbumPhoto("tokyo-p3", tokyo, "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=85", "Akihabara Neon Green Sign Reflection", "landscape", 3),
                    new AlbumPhoto("tokyo-p4", tokyo, "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=1200&q=85", "Traditional Izakaya Lanterns in Midnight Alley", "landscape", 4),
                    new AlbumPhoto("tokyo-p5", tokyo, "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?auto=format&fit=crop&w=1200&q=85", "Neon Corridors and Rain Slicked Tarmac", "portrait", 5),
                    new AlbumPhoto("tokyo-p6", tokyo, "https://images.unsplash.com/photo-1528164344705-475426879c0d?auto=format&fit=crop&w=1200&q=85", "Twilight Shinjuku Tower Light Trails", "landscape", 6)
            );
            albumPhotoRepository.saveAll(tokyoPhotos);

            // 2. The Crimson Sands of Wadi Rum (Screenshot 1)
            Album wadi = new Album();
            wadi.setId("the-crimson-sands-of-wadi-rum");
            wadi.setName("The Crimson Sands of Wadi Rum");
            wadi.setSubtitle("Jordanian Desert");
            wadi.setCategory("The Crimson Sands of Wadi Rum");
            wadi.setDescription("Sculpted sandstone monoliths and shifting red dunes stretching to the Martian horizon. An expedition into quiet solitude where wind and millennia have carved monuments of timeless silence.");
            wadi.setCoverImage("https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1200&q=85");
            wadi.setDisplayOrder(2);
            albumRepository.save(wadi);

            List<AlbumPhoto> wadiPhotos = Arrays.asList(
                    new AlbumPhoto("wadi-p1", wadi, "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1200&q=85", "Tall Sandstone Rock Tower Formation", "portrait", 1),
                    new AlbumPhoto("wadi-p2", wadi, "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1200&q=85", "Highway Through Red Desert Canyons", "landscape", 2),
                    new AlbumPhoto("wadi-p3", wadi, "https://images.unsplash.com/photo-1547234935-80c7145ec969?auto=format&fit=crop&w=1200&q=85", "Desert Palm Oasis and Long Evening Shadows", "landscape", 3),
                    new AlbumPhoto("wadi-p4", wadi, "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1200&q=85", "Sunlight Striking Eroded Sandstone Gorges", "portrait", 4)
            );
            albumPhotoRepository.saveAll(wadiPhotos);

            // 3. Echoes of the Andean Peaks (Screenshot 1)
            Album andes = new Album();
            andes.setId("echoes-of-the-andean-peaks");
            andes.setName("Echoes of the Andean Peaks");
            andes.setSubtitle("Patagonia & Andes");
            andes.setCategory("Echoes of the Andean Peaks");
            andes.setDescription("Jagged granite spires rising above glacial moraines. Traversing treacherous winds and unyielding terrain to capture the fleeting golden light across high altitude summits.");
            andes.setCoverImage("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1200&q=85");
            andes.setDisplayOrder(3);
            albumRepository.save(andes);

            List<AlbumPhoto> andesPhotos = Arrays.asList(
                    new AlbumPhoto("andes-p1", andes, "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1200&q=85", "Summit Ridge Trekkers Against Jagged Mountain Crests", "landscape", 1),
                    new AlbumPhoto("andes-p2", andes, "https://images.unsplash.com/photo-1486870591958-9b9d0d1dda99?auto=format&fit=crop&w=1200&q=85", "Deep Blue Glacier Ice Formations", "portrait", 2),
                    new AlbumPhoto("andes-p3", andes, "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=85", "Glacial Valley Basin at Dawn", "landscape", 3),
                    new AlbumPhoto("andes-p4", andes, "https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?auto=format&fit=crop&w=1200&q=85", "Alpenglow Illuminating Alpine Snowfields", "portrait", 4)
            );
            albumPhotoRepository.saveAll(andesPhotos);

            System.out.println(">>> [DataSeeder] 3 Álbumes Dennis Wanderlight y 14 Fotos asociadas sembrados con éxito.");
        }
    }

    private void seedDefaultPhotos() {
        if (photoRepository.count() == 0) {
            List<Photo> initialPhotos = Arrays.asList(
                    new Photo("photo-1", "Amanecer en las Agujas de Granito", "Wilderness & Peaks", 220.0,
                            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1200&q=85",
                            "Primera luz sobre los picos escarpados de los Alpes. Siluetas de pinos y niebla matinal.",
                            "90 x 60 cm · Archival Pigment Print",
                            "Sony Alpha 7R V · FE 24-70mm f/2.8 GM II · f/8.0 · 1/320s · ISO 100",
                            "Sony Alpha 7R V", "FE 24-70mm f/2.8 GM II", "f/8.0", "1/320s", "ISO 100",
                            true, "Alpes Suizos", true),

                    new Photo("photo-2", "Tokyo's Neon Pulse", "Tokyo Neon Pulse", 260.0,
                            "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1200&q=85",
                            "Cruce peatonal bajo la lluvia en Shinjuku. Reflejos policromáticos sobre el asfalto mojado.",
                            "100 x 70 cm · Metallic Acrylic Mount",
                            "Leica M11 · Summilux-M 35mm f/1.4 ASPH · f/1.4 · 1/125s · ISO 800",
                            "Leica M11", "Summilux-M 35mm f/1.4 ASPH", "f/1.4", "1/125s", "ISO 800",
                            true, "Tokyo, Japón", true),

                    new Photo("photo-3", "Arenas Doradas & Silencio", "Silent Deserts", 195.0,
                            "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1200&q=85",
                            "Curvas esculpidas por el viento al atardecer en el desierto rojo.",
                            "75 x 50 cm · Hahnemühle Photo Rag",
                            "Hasselblad 907X 50C · XCD 45mm f/4 P · f/11 · 1/60s · ISO 64",
                            "Hasselblad 907X 50C", "XCD 45mm f/4 P", "f/11", "1/60s", "ISO 64",
                            true, "Monumento Valley, USA", true),

                    new Photo("photo-4", "Mirada del Valle Nevado", "Portraits of the Edge", 210.0,
                            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=1200&q=85",
                            "Retrato espontáneo de una habitante de aldea de montaña bajo la luz difusa de la tarde.",
                            "60 x 60 cm · Fine Art Baryta",
                            "Canon EOS R5 · RF 85mm f/1.2L USM · f/1.4 · 1/800s · ISO 100",
                            "Canon EOS R5", "RF 85mm f/1.2L USM", "f/1.4", "1/800s", "ISO 100",
                            true, "Kashmir", true),

                    new Photo("photo-5", "Ruta Hacia la Inmensidad", "Wilderness & Peaks", 180.0,
                            "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1200&q=85",
                            "Carretera solitaria cruzando cañones ocres hacia cordilleras lejanas.",
                            "80 x 50 cm · Lienzo Montado",
                            "Sony Alpha 7 IV · FE 16-35mm f/2.8 GM · f/9.0 · 1/250s · ISO 100",
                            "Sony Alpha 7 IV", "FE 16-35mm f/2.8 GM", "f/9.0", "1/250s", "ISO 100",
                            true, "Patagonia Argentina", false),

                    new Photo("photo-6", "Callejón de Linternas en Kioto", "Tokyo Neon Pulse", 240.0,
                            "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=1200&q=85",
                            "La serenidad de Gion a medianoche con linternas rojas iluminando madera tradicional.",
                            "80 x 55 cm · Giclée Print",
                            "Leica Q2 · Summilux 28mm f/1.7 · f/2.0 · 1/160s · ISO 400",
                            "Leica Q2", "Summilux 28mm f/1.7", "f/2.0", "1/160s", "ISO 400",
                            true, "Kioto, Japón", false),

                    new Photo("photo-7", "Cresta Glaciar al Mediodía", "Wilderness & Peaks", 215.0,
                            "https://images.unsplash.com/photo-1486870591958-9b9d0d1dda99?auto=format&fit=crop&w=1200&q=85",
                            "Formaciones de hielo azul profundo contrastando con la roca negra.",
                            "90 x 60 cm · Impresión en Aluminio",
                            "Nikon Z8 · NIKKOR Z 70-200mm f/2.8 VR S · f/8.0 · 1/1000s · ISO 64",
                            "Nikon Z8", "NIKKOR Z 70-200mm f/2.8 VR S", "f/8.0", "1/1000s", "ISO 64",
                            true, "Islandia", false),

                    new Photo("photo-8", "El Tiempo Suspendido en el Oasis", "Silent Deserts", 190.0,
                            "https://images.unsplash.com/photo-1547234935-80c7145ec969?auto=format&fit=crop&w=1200&q=85",
                            "Sombras alargadas de palmeras solitarias sobre arena virgen.",
                            "70 x 50 cm · Papel Museo 310g",
                            "Sony Alpha 7R V · FE 50mm f/1.2 GM · f/5.6 · 1/500s · ISO 100",
                            "Sony Alpha 7R V", "FE 50mm f/1.2 GM", "f/5.6", "1/500s", "ISO 100",
                            true, "Sahara", false)
            );

            photoRepository.saveAll(initialPhotos);
            System.out.println(">>> [DataSeeder] 8 Fotografías de portafolio Dennis Wanderlight sembradas en SQLite.");
        }
    }

    private void seedDefaultCoverPhoto() {
        if (coverPhotoRepository.count() == 0) {
            CoverPhoto defaultCover = new CoverPhoto(
                    "photo-1",
                    "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=2000&q=85",
                    "The World, Unfiltered",
                    "Wilderness & Peaks",
                    "Journeys captured beyond the postcard view. High altitude alpine expedition."
            );
            coverPhotoRepository.save(defaultCover);
            System.out.println(">>> [DataSeeder] Foto de portada Hero Dennis Wanderlight sembrada en SQLite.");
        }
    }
}
