package org.jumpmind.metl.ui.views.design.menu;

import org.jumpmind.metl.core.model.AbstractNamedObject;
import org.jumpmind.metl.ui.common.AbstractSelectedValueMenuManager;
import org.jumpmind.metl.ui.views.design.DesignNavigator;

public class AbstractDesignSelectedValueMenuManager extends AbstractSelectedValueMenuManager {

    protected DesignNavigator navigator;

    public AbstractDesignSelectedValueMenuManager(DesignNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public boolean handle(String menuSelected, Object valueSelected) {
        boolean handled = false;
        if (!super.handle(menuSelected, valueSelected)) {
            if ("File|New|Project".equals(menuSelected)) {
                navigator.addNewProject();
                return true;
            } else if ("Edit|Rename".equals(menuSelected)) {
                navigator.startEditingItem((AbstractNamedObject) valueSelected);
                return true;
            } else if ("File|New|Project Dependency".equals(menuSelected)) {
                navigator.promptForNewDependency();
                return true;                
            } else if ("File|New|Flow|Design".equals(menuSelected)) {
                navigator.addNewFlow(false);
                return true;
            } else if ("File|New|Flow|Test".equals(menuSelected)) {
                navigator.addNewFlow(true);
                return true;
            } else if ("File|New|Model".equals(menuSelected)) {
                navigator.addNewModel();
                return true;
            } else if ("File|New|Resource|Database".equals(menuSelected)) {
                navigator.addNewDatabase();
                return true;
            } else if ("File|New|Resource|Directory|FTP".equals(menuSelected)) {
                navigator.addNewFtpFileSystem();
                return true;
            } else if ("File|New|Resource|Directory|File System".equals(menuSelected)) {
                navigator.addNewLocalFileSystem();
                return true;
            } else if ("File|New|Resource|Directory|JMS Topic".equals(menuSelected)) {
                navigator.addNewJMSFileSystem();
                return true;
            } else if ("File|New|Resource|Directory|SFTP".equals(menuSelected)) {
                navigator.addNewSftpFileSystem();
                return true;
            } else if ("File|New|Resource|Directory|SMB".equals(menuSelected)) {
                navigator.addNewSMBFileSystem();
                return true;
            } else if ("File|New|Resource|Directory|HTTP Resource".equals(menuSelected)) {
                navigator.addNewHttpResource();
                return true;
            } else if ("File|New|Resource|Mail Session".equals(menuSelected)) {
                navigator.addNewMailSession();
                return true;
            } else if ("File|Import...".equals(menuSelected)) {
                navigator.doImport();
                return true;
            } else if ("File|Export...".equals(menuSelected)) {
                navigator.doExport();
                return true;
            } else if ("File|Open".equals(menuSelected)) {
                navigator.doOpen();
                return true;
            } else if ("Edit|Remove".equals(menuSelected)) {
                navigator.doRemove();
                return true;
            } else if ("Edit|Copy".equals(menuSelected)) {
                navigator.doCopy();
                return true;
            }
        }
        return handled;
    }

    @Override
    protected String[] getEnabledPaths() {
        return new String[] { "File|New|Project", "View|Hidden", "File|Import..." };
    }

}
