package com.matthewprenger.cursegradle

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.util.GradleVersion

class Integration {

    private static final Logger log = Logging.getLogger(Integration)

    static void checkJava(Project project, CurseProject curseProject) {
        try {
            if (project.plugins.hasPlugin('java')) {
                log.info 'Java plugin detected, adding integration...'
                if (curseProject.mainArtifact == null) {
                    Task jarTask = project.tasks.getByName('jar')
                    log.info "Setting main artifact for CurseForge Project $curseProject.id to the java jar"
                    CurseArtifact artifact = new CurseArtifact()
                    artifact.artifact = jarTask
                    curseProject.mainArtifact = artifact
                    curseProject.uploadTask.dependsOn jarTask
                }
            }
        } catch (Throwable t) {
            log.warn("Failed Java integration", t)
        }
    }

    static void checkJavaVersion(Project project, CurseProject curseProject) {

        try {
            JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java")
            JavaVersion javaVersion = JavaVersion.toVersion(javaConv.targetCompatibility)

            if (JavaVersion.VERSION_1_6.compareTo(javaVersion) >= 0) {
                curseProject.addGameVersion('Java 6')
            }
            if (JavaVersion.VERSION_1_7.compareTo(javaVersion) >= 0) {
                curseProject.addGameVersion('Java 7')
            }
            if (JavaVersion.VERSION_1_8.compareTo(javaVersion) >= 0) {
                curseProject.addGameVersion('Java 8')
            }
            if (project.extensions.getByType(CurseExtension).curseGradleOptions.detectNewerJava) {
                if (GradleVersion.current() >= GradleVersion.version('1.11')) {
                    if (JavaVersion.VERSION_1_9.compareTo(javaVersion) >= 0) {
                        curseProject.addGameVersion('Java 9')
                    }
                    if (GradleVersion.current() >= GradleVersion.version('4.1.0')) {
                        if (JavaVersion.VERSION_1_10.compareTo(javaVersion) >= 0) {
                            curseProject.addGameVersion('Java 10')
                        }
                        if (GradleVersion.current() >= GradleVersion.version('4.7.0')) {
                            if (JavaVersion.VERSION_11.compareTo(javaVersion) >= 0) {
                                curseProject.addGameVersion('Java 11')
                            }
                            if (GradleVersion.current() >= GradleVersion.version('5.0')) {
                                if (JavaVersion.VERSION_12.compareTo(javaVersion) >= 0) {
                                    curseProject.addGameVersion('Java 12')
                                }
                                if (GradleVersion.current() >= GradleVersion.version('6.0')) {
                                    if (JavaVersion.VERSION_13.compareTo(javaVersion) >= 0) {
                                        curseProject.addGameVersion('Java 13')
                                    }
                                    if (GradleVersion.current() >= GradleVersion.version('6.3')) {
                                        if (JavaVersion.VERSION_14.compareTo(javaVersion) >= 0) {
                                            curseProject.addGameVersion('Java 14')
                                        }
                                        if (JavaVersion.VERSION_15.compareTo(javaVersion) >= 0) {
                                            curseProject.addGameVersion('Java 15')
                                        }
                                        if (JavaVersion.VERSION_16.compareTo(javaVersion) >= 0) {
                                            curseProject.addGameVersion('Java 16')
                                        }
                                        if (JavaVersion.VERSION_17.compareTo(javaVersion) >= 0) {
                                            curseProject.addGameVersion('Java 17')
                                        }
                                        if (GradleVersion.current() >= GradleVersion.version('7.0')) {
                                            if (JavaVersion.VERSION_18.compareTo(javaVersion) >= 0) {
                                                curseProject.addGameVersion('Java 18')
                                            }
                                            if (JavaVersion.VERSION_19.compareTo(javaVersion) >= 0) {
                                                curseProject.addGameVersion('Java 19')
                                            }
                                            if (JavaVersion.VERSION_20.compareTo(javaVersion) >= 0) {
                                                curseProject.addGameVersion('Java 20')
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
          }
        } catch (Throwable t) {
            log.warn("Failed to check Java Version", t)
        }
    }

    static void checkForgeGradle(Project project, CurseProject curseProject) {
        if (project.hasProperty('minecraft')) {
            log.info "ForgeGradle plugin detected, adding integration..."

            // FG 3+ doesn't set MC_VERSION until afterEvaluate
            project.gradle.taskGraph.whenReady {
                try {
                    if (project.minecraft.hasProperty('version')) {
                        log.info 'Found Minecraft version in FG < 3'
                        curseProject.addGameVersion(project.minecraft.version)
                        curseProject.addGameVersion('Forge')
                    } else if (project.getExtensions().getExtraProperties().has('MC_VERSION')) {
                        log.info 'Found Minecraft version in FG >= 3'
                        curseProject.addGameVersion(project.getExtensions().getExtraProperties().get('MC_VERSION'))
                        curseProject.addGameVersion('Forge')
                    } else {
                        log.warn 'Unable to extract Minecraft version from ForgeGradle'
                    }
                } catch (Throwable t) {
                    log.warn('Failed ForgeGradle integration', t)
                }
            }
        }
    }
}
