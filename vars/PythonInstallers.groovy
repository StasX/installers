def installBandit() {
    sh 'pip install --user bandit'
}

def installCheckov() {
    sh '''
    python3 -m venv .venv
    . .venv/bin/activate
    pip install checkov
    '''
}

def installSemgrep() {
    sh '''
    python3 -m venv .venv
    . .venv/bin/activate
    .venv/bin/python -m pip install semgrep
    '''
}
