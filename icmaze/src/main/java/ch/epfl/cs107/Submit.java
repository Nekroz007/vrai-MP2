package ch.epfl.cs107;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Submit {

    private static final Path PROJECT_ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path ICMAZE = PROJECT_ROOT.resolve("icmaze");
    private static final Path TARGET_FOLDER = ICMAZE.resolve("target");
    private static final Path SUBMISSION_FILE = TARGET_FOLDER.resolve("submission.zip");

    private static final Path ICMAZE_RESOURCE_FOLDER =
            ICMAZE.resolve("src")
                    .resolve("main")
                    .resolve("resources");

    /** ??? */
    private static final Set<Path> DIRECTORIES_TO_IGNORE = Set.of(
            Path.of("out"), // output folder in Eclipse
            TARGET_FOLDER,
            Path.of(".idea"),
            Path.of("src", ".idea")
    );

    // Les fichiers dont le nom commence par l'un de ces préfixes sont inclus dans le rendu.
    private static final Set<String> PREFIXES_TO_SUBMIT = Set.of("readme", "conception");
    // Les fichiers dont le nom se termine par l'un de ces suffixes sont inclus dans le rendu.
    private static final Set<String> SUFFIXES_TO_SUBMIT = Set.of(
            ".java", ".png", ".ttf",
            ".wav", ".xml", ".txt", ".md", ".pdf"
            );

    private static final Set<Path> RESOURCE_FILES_TO_IGNORE = Set.of(
            "fonts/Dragonfly.ttf",
            "fonts/OpenSans-Bold.ttf",
            "fonts/OpenSans-BoldItalic.ttf",
            "fonts/OpenSans-ExtraBold.ttf",
            "fonts/OpenSans-ExtraBoldItalic.ttf",
            "fonts/OpenSans-Italic.ttf",
            "fonts/OpenSans-Light.ttf",
            "fonts/OpenSans-LightItalic.ttf",
            "fonts/OpenSans-Regular.ttf",
            "fonts/OpenSans-Semibold.ttf",
            "fonts/OpenSans-SemiboldItalic.ttf"
    ).stream().map(ICMAZE_RESOURCE_FOLDER::resolve).collect(Collectors.toSet());

    // ============================================================================================
    // ========================================= MAIN =============================================
    // ============================================================================================

    public static void main(String[] args) {
        try {
           // var root = ICMAZE.resolve("src").resolve("main");
            var root = ICMAZE.resolve("src");
            Set<Path> pathsToIgnore = new HashSet<>();
            pathsToIgnore.addAll(DIRECTORIES_TO_IGNORE);
            pathsToIgnore.addAll(RESOURCE_FILES_TO_IGNORE);
            var paths = filesToSubmit(root, path -> {
                var fileName = path.getFileName().toString().toLowerCase();
                return pathsToIgnore.stream().noneMatch(path::startsWith)
                        && !fileName.equals("submit.java")
                        && (PREFIXES_TO_SUBMIT.stream().anyMatch(fileName::startsWith)
                        || SUFFIXES_TO_SUBMIT.stream().anyMatch(fileName::endsWith));
            });

            var zipArchive = createZipArchive(ICMAZE.resolve("src"), paths);

            if(TARGET_FOLDER.toFile().mkdirs()) {
                System.out.printf("Dossier cible introuvable, création d’un nouveau à l’emplacement: %s.",
                        TARGET_FOLDER);
            }
            Files.write(SUBMISSION_FILE, zipArchive);

            long archiveSizeBytes = Files.size(SUBMISSION_FILE);
            double archiveSizeKBs = archiveSizeBytes / Math.pow(2, 10);
            double archiveSizeMBs = archiveSizeBytes / Math.pow(2, 20);
            System.out.printf("Archive de soumission créée avec succès. Taille de l’archive: %.1f KB (%.2f MB).\n",
                    archiveSizeKBs, archiveSizeMBs);
            System.out.printf("L’archive prête pour les soumissions a été créée à: %s.\n",
                    SUBMISSION_FILE.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur inattendue !");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static List<Path> filesToSubmit(Path root, Predicate<Path> keepFile) throws IOException {
        try (var paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(keepFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static byte[] createZipArchive(Path root, List<Path> paths) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var zipStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (var path : paths) {
                var relative = root.relativize(path);
                var entryPath = IntStream.range(0, relative.getNameCount())
                        .mapToObj(relative::getName)
                        .map(Path::toString)
                        .collect(Collectors.joining(File.separator, "", ""));
                zipStream.putNextEntry(new ZipEntry(entryPath));
                try (var fileStream = new FileInputStream(path.toFile())) {
                    fileStream.transferTo(zipStream);
                }
                zipStream.closeEntry();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}