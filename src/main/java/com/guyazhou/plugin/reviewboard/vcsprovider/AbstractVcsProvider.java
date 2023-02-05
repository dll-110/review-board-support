package com.guyazhou.plugin.reviewboard.vcsprovider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * Abstract vcs provider
 *
 * @author YaZhou.Gu 2016/12/30
 */
public abstract class AbstractVcsProvider implements VcsProvider {

    protected AbstractVcs abstractVcs;
    private String differences;
    protected String repositoryURL;
    protected String workingCopyPathInRepository;
    protected String workingCopyDir;

    protected String diffPaths;

    protected AbstractVcsProvider(AbstractVcs abstractVcs) {
        this.abstractVcs = abstractVcs;
    }

    @Override
    public AbstractVcs getVCS() {
        return this.abstractVcs;
    }

    /**
     * Set some vcs info and generate differeces
     *
     * @param project current project
     * @param virtualFiles selected files
     */
    @Override
    public void build(Project project, List<VirtualFile> virtualFiles) {
        setRepositoryRootAndWorkingCopyPath(project, virtualFiles);
        differences = generateDifferences(project, virtualFiles);
    }

    @Override
    public String getDifferences() {
        return this.differences;
    }

    @Override
    public String getRepositoryURL() {
        return this.repositoryURL;
    }

    @Override
    public String getWorkingCopyPathInRepository() {
        return this.workingCopyPathInRepository;
    }

    /**
     * Set repository root url and working copy path in repository according to the given selected virtual files
     *
     * @param project project
     * @param virtualFiles selected files
     */
    protected abstract void setRepositoryRootAndWorkingCopyPath(Project project, List<VirtualFile> virtualFiles);

    /**
     * Generate differences between local and remote repository
     *
     * @param project current project
     * @param virtualFiles virtural files
     * @return diff string
     */
    protected abstract String generateDifferences(Project project, List<VirtualFile> virtualFiles);
    @Override
    public String getDiffPaths() {
        return this.diffPaths;
    }
}
