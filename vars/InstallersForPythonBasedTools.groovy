def installBandit() {
    sh 'pip install --user bandit'
}

def installCheckov() {
    sh '''
    set -eu
    python3 -m venv .venv
    .venv/bin/python -m pip install checkov
    '''
    configureVirtualEnvironment()
}

def installSemgrep() {
    sh '''
    set -eu
    python3 -m venv .venv
    .venv/bin/python -m pip install semgrep
    '''
    configureVirtualEnvironment()
}

private void configureVirtualEnvironment() {
    String virtualEnv = "${pwd()}/.venv"
    String binPath = "${virtualEnv}/bin"

    env.VIRTUAL_ENV = virtualEnv
    if (!env.PATH.tokenize(':').contains(binPath)) {
        env.PATH = "${binPath}:${env.PATH}"
    }
}
