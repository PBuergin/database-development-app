package ch.hftm.db2.ticketsystem;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CourseCheckpointTest {

    @TempDir
    private Path tempDir;

    @Test
    void definesExpectedCourseStates() throws IOException {
        String states = Files.readString(Path.of("course/states.yml"));

        assertThat(states)
                .contains("block-2-start:")
                .contains("block-2-complete:")
                .contains("block-3-start:")
                .contains("block-3-complete:")
                .contains("extends: block-2-complete");
    }

    @Test
    void block3CompleteOverlayContainsStackedApplicationState() throws IOException {
        Path target = tempDir.resolve("block-3-complete");
        copyDirectory(Path.of("."), target);
        copyDirectory(Path.of("course/overlays/block-2-complete"), target);
        copyDirectory(Path.of("course/overlays/block-3-complete"), target);

        assertThat(target.resolve("src/main/resources/db/migration/V2__enforce_ticket_rules.sql")).exists();
        assertThat(target.resolve("src/main/resources/db/migration/V3__add_ticket_workflow_tables.sql")).exists();

        String service = Files.readString(target.resolve(
                "src/main/java/ch/hftm/db2/ticketsystem/ticket/TicketService.java"
        ));

        assertThat(service)
                .contains("@Transactional")
                .contains("createTicketAndFailForRollbackProbe")
                .contains("status_changed");
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(dir);
                if (shouldSkip(relative)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Files.createDirectories(target.resolve(relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(file);
                if (!shouldSkip(relative)) {
                    Files.createDirectories(target.resolve(relative).getParent());
                    Files.copy(file, target.resolve(relative), StandardCopyOption.REPLACE_EXISTING);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static boolean shouldSkip(Path relative) {
        if (relative.getNameCount() == 0) {
            return false;
        }
        String first = relative.getName(0).toString();
        return ".git".equals(first) || "target".equals(first);
    }
}
