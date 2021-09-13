import jenkins.model.*
import hudson.model.*
import jenkins.security.s2m.*
import hudson.security.csrf.DefaultCrumbIssuer

def instance = Jenkins.getInstance()

// No executor for master
instance.setNumExecutors(0)
instance.setLabelString('master')
instance.setMode(Node.Mode.EXCLUSIVE)

instance.save()
