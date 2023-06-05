package com.lowdragmc.lowdraglib.utils.fabric;

import com.lowdragmc.lowdraglib.LDLib;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.file.AccumulatorPathVisitor;
import org.apache.commons.io.file.Counters;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.spongepowered.asm.util.asm.ASM;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class ReflectionUtilsImpl {

    private static final Map<Class<? extends Annotation>, List<Pair<Consumer<Class<?>>, Runnable>>> QUEUE = new HashMap<>();

    public static <A extends Annotation> void findAnnotationClasses(Class<A> annotationClass, Consumer<Class<?>> consumer, Runnable onFinished) {
        QUEUE.computeIfAbsent(annotationClass, a -> new ArrayList<>()).add(Pair.of(consumer, onFinished));
    }

    public static void execute() {
        LDLib.LOGGER.info("Start to search classes for notation: {}", QUEUE.keySet());
        findAnnotationCandidates(QUEUE.keySet()).forEach((annotation, names) ->{
            for (String name : names) {
                try {
                    var clazz = Class.forName(name.replace("/", "."), false, ReflectionUtilsImpl.class.getClassLoader());
                    for (var consumer : QUEUE.get(annotation)) {
                        consumer.left().accept(clazz);
                    }
                } catch (Throwable throwable) {
                    LDLib.LOGGER.error("Failed to load class {} for notation {} ", name, annotation.getName(), throwable);
                }
            }
        });
        for (var pairs : QUEUE.values()) {
            for (var pair : pairs) {
                pair.right().run();
            }
        }
        QUEUE.clear();
        LDLib.LOGGER.info("Finish searching for notation");
    }

    public static Map<Class<? extends Annotation>, Set<String>> findAnnotationCandidates(Set<Class<? extends Annotation>> annotationTypes) {
        final Map<Class<? extends Annotation>, Set<String>> annotationCandidates = new HashMap<>();

        for (var allMod : FabricLoader.getInstance().getAllMods()) {
            for (Path rootPath : allMod.getRootPaths()) {
                final AccumulatorPathVisitor visitor = new AccumulatorPathVisitor(Counters.noopPathCounters(), new RegexFileFilter(".*.class"), TrueFileFilter.TRUE);
                try {
                    Files.walkFileTree(rootPath, visitor);
                } catch (IOException e) {
                    LDLib.LOGGER.warn("Failed to discover plugins from path: %s".formatted(rootPath), e);
                    continue;
                }

                final List<Path> classFiles = visitor.getFileList();
                for (Path classFile : classFiles) {
                    final ClassReader reader;
                    try {
                        reader = new ClassReader(Files.newInputStream(classFile));
                    } catch (IOException e) {
                        LDLib.LOGGER.warn("Failed to read class for annotation detection: %s".formatted(classFile.toAbsolutePath()), e);
                        continue;
                    }

                    reader.accept(new AnnotationSearchingClassVisitor(annotationCandidates, annotationTypes), ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                }
            }
        }

        return annotationCandidates;
    }


    private static final class AnnotationSearchingClassVisitor extends ClassVisitor {

        private final Map<Class<? extends Annotation>, Set<String>> annotationCandidates;
        private final Map<String, Class<? extends Annotation>> annotationDescMap;
        private String name = "";

        public AnnotationSearchingClassVisitor(Map<Class<? extends Annotation>, Set<String>> annotationCandidates, Set<Class<? extends Annotation>> annotationTypes) {
            super(ASM.API_VERSION);
            this.annotationCandidates = annotationCandidates;
            this.annotationDescMap = new HashMap<>();
            for (var annotationType : annotationTypes) {
                annotationDescMap.put(annotationType.descriptorString(), annotationType);
            }
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.name = name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (annotationDescMap.containsKey(descriptor) ) {
                annotationCandidates.computeIfAbsent(annotationDescMap.get(descriptor), a -> new HashSet<>()).add(this.name);
            }
            return super.visitAnnotation(descriptor, visible);
        }
    }
}
