import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

// manager.listener.logger.println new Date(System.currentTimeMillis()).format('MM/dd/yyyy hh:mm:ss a') + " / " + " -- Start Time"

// Get value from String Parameter
MAX_BUILDS = manager.build.buildVariables.get("MAX_BUILDS").toInteger()

Jenkins.instance.getAllItems().each { job ->
  int count = 0
  manager.listener.logger.println("\n***Job Name: " + job.name + "***")

  if (job instanceof AbstractProject) {
    AbstractProject project = (AbstractProject) job
    FilePath workspacePath = project.getSomeWorkspace()

    if (workspacePath != null) { // Check if there is a workspace associated with the job
      String workspace = workspacePath.getRemote()
      manager.listener.logger.println("Workspace path: " + workspace)

      File folder = new File(workspace)

      if (folder != null && folder.exists()) { // Check if the Workspace folder exists
        // Get all files and folders within the Workspace of the current job.
        // Iterate through only folders and sort them by Modified Date.

        File[] files = folder.listFiles().sort { a, b ->
          b.lastModified().compareTo(a.lastModified())
        }.each { file ->
          if (!file.isFile()) { // Check only for folders
            if (count < MAX_BUILDS) {
              manager.listener.logger.println(new Date(file.lastModified()).format('MM/dd/yyyy hh:mm:ss a') + " / " + file.name + " -- Save")
            } else {
              manager.listener.logger.println(new Date(file.lastModified()).format('MM/dd/yyyy hh:mm:ss a') + " / " + file.name + " ** Deleted")
              file.deleteDir()
            }
            count++
          }
        }
      } else {
        manager.listener.logger.println("Workspace is empty or doesn't exist")
      }
    } else {
      manager.listener.logger.println("No Workspace associated with this job")
    }
  } else {
    manager.listener.logger.println("Skipping unsupported job type: " + job.name)
  }
}