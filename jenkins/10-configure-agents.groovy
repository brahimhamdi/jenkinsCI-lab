import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry
import hudson.plugins.sshslaves.verifiers.*

import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*

// SET THE INITIAL SSH CREDENTIALS
global_domain = Domain.global()

credentials_store = Jenkins.instance.getExtensionList(
  'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
)[0].getStore()

credentials = new BasicSSHUserPrivateKey(
  CredentialsScope.SYSTEM,
  "ssh-agent-key",
  "jenkins",
  new BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource(
    '/ssh-keys/vagrant_insecure_key'
  ),
  '',
  "SSH Key for the Agent"
)

credentials_store.addCredentials(global_domain, credentials)

SshHostKeyVerificationStrategy doNotVerifyHostKey = new NonVerifyingKeyVerificationStrategy()

// Get environment variable for autoconfiguration
def env = System.getenv()
String baseJvmOpts = env['BASE_JVM_OPTS']

// CREATE THE JDK8 AGENT
SSHLauncher jdk8Launcher = new SSHLauncher("jdk8-ssh-agent", 22, "ssh-agent-key", baseJvmOpts, "", "", "", 33, 3, 5, doNotVerifyHostKey)
Slave jdk8SSHAgent = new DumbSlave("jdk8-node", "/home/jenkins", jdk8Launcher)
jdk8SSHAgent.setLabelString("docker maven jdk8 jdk-8 java8 java-8 maven-jdk8 java docker dind docker-in-docker")
jdk8SSHAgent.setNodeDescription("Agent node for JDK8")
jdk8SSHAgent.setNumExecutors(2)

List<Entry> jdk8SSHAgentEnv = new ArrayList<Entry>();
jdk8SSHAgentEnv.add(new Entry("JAVA_HOME","/usr/lib/jvm/java-1.8-openjdk"))
EnvironmentVariablesNodeProperty jdk8SSHAgentEnvPro = new EnvironmentVariablesNodeProperty(jdk8SSHAgentEnv);
jdk8SSHAgent.getNodeProperties().add(jdk8SSHAgentEnvPro)

Jenkins.instance.addNode(jdk8SSHAgent)

// CREATE THE JDK7 AGENT
SSHLauncher jdk7Launcher = new SSHLauncher("jdk7-ssh-agent", 22, "ssh-agent-key", "", "/usr/lib/jvm/java-1.8-openjdk/bin/java", "", "", 33, 2, 5, doNotVerifyHostKey)
Slave jdk7SSHAgent = new DumbSlave("jdk7-node", "/home/jenkins", jdk7Launcher)
jdk7SSHAgent.setLabelString("maven jdk7 jdk-7 java7 java-7 maven-jdk7 java")
jdk7SSHAgent.setNodeDescription("Agent node for JDK7")
jdk7SSHAgent.setNumExecutors(2)

List<Entry> jdk7SSHAgentEnv = new ArrayList<Entry>();
jdk7SSHAgentEnv.add(new Entry("JAVA_HOME","/usr/lib/jvm/java-1.7-openjdk"))
EnvironmentVariablesNodeProperty jdk7SSHAgentEnvPro = new EnvironmentVariablesNodeProperty(jdk7SSHAgentEnv);
jdk7SSHAgent.getNodeProperties().add(jdk7SSHAgentEnvPro)

Jenkins.instance.addNode(jdk7SSHAgent)
