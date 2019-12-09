package com.github.mitschi.sedashboard;

import com.github.mitschi.sedashboard.messaging.MessagingCommit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SonarMetricsExtractionWorker implements Runnable{

    private MessagingCommit messagingCommit;
    private File tmpFolder;
    private SonarCommitAnalysis sonarCommitAnalysis;

    public SonarMetricsExtractionWorker(MessagingCommit messagingCommit, File tmpFolder) {
        this.messagingCommit = messagingCommit;
        this.tmpFolder = tmpFolder;
    }

    public void run() {
        doJob();
    }

    public void doJob() {
        try {
            File targetDir = new File(tmpFolder, messagingCommit.getProjectName());
            System.out.println("Checking Repo...");
            if (!targetDir.exists()) {
                System.out.println("Not there -> checking out");
                cloneRepo(messagingCommit.getCloneUrl(), targetDir);
                System.out.println("finished");
            }
            System.out.println("resetting repo");
            resetRepo(targetDir,messagingCommit.getCommitId());
            System.out.println("retrieving file name");

//            extractSourceCodeChanges(targetDir);

            SonarMetricsAnalyzer sma = new SonarMetricsAnalyzer();
            SonarConnectionDetails sonarConnectionDetails = new SonarConnectionDetails("jdbc://...","username","password");
            sma.analyzeCommit(tmpFolder, new File(tmpFolder,"target"),sonarConnectionDetails);

        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    public SonarCommitAnalysis getSonarCommitAnalysis() {
        return sonarCommitAnalysis;
    }

    private void cloneRepo(String remoteUrl, File targetDir) throws GitAPIException {
        Git.cloneRepository().setURI(remoteUrl).setDirectory(targetDir).call();
    }

    private void resetRepo(File repoFolder, String version) throws IOException, GitAPIException {
        Repository repository =  FileRepositoryBuilder.create(new File(repoFolder, ".git"));
        Git git = new Git(repository);
        git.reset().setMode(ResetCommand.ResetType.HARD).setRef(Constants.HEAD).call();
        git.checkout().setName(version).call();
    }
}
