# installers
Jenkins Shared Library for Package Installation

The Checkov and Semgrep installers create `.venv` in the current Jenkins
directory and update the Pipeline's `PATH` and `VIRTUAL_ENV`. Later `sh` steps
on the same agent can run `checkov` and `semgrep` directly without activating
the environment again. Run these installers inside a Jenkins `node` context.
