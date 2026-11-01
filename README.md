# installers
Jenkins Shared Library for Package Installation

The Checkov and Semgrep installers create `.venv` in the current Jenkins
directory and update the Pipeline's `PATH` and `VIRTUAL_ENV`. Later `sh` steps
on the same agent can run `checkov` and `semgrep` directly without activating
the environment again. Run these installers inside a Jenkins `node` context.

## PHP installers

Call `InstallersForPHPBasedTools` methods inside a Jenkins `node` context,
from the consuming project's root directory. The agent needs a Unix shell,
PHP and Composer on `PATH`, and the PHP version and extensions required by
the selected packages. The project must supply its own `composer.json`,
tool configuration files, application code, and tests.

Except for `installAll`, each method installs development dependencies with
`composer require --dev` and immediately runs the corresponding check.
These calls can update `composer.json`, `composer.lock`, and `vendor/` in the
workspace, resolving packages within the version constraints in the library.
They do not commit those changes. Installation errors and nonzero check exit
codes fail the Jenkins step unless the calling Pipeline handles the failure.

| Method | Dependencies installed | Check executed | Required configuration |
| --- | --- | --- | --- |
| `installPHPCodeSniffer()` | PHP_CodeSniffer, Slevomat Coding Standard, Composer installer plugin | `phpcs` | `phpcs.xml.dist` |
| `installPHPCSFixer()` | PHP CS Fixer | `php-cs-fixer fix --dry-run --diff` (reports changes without applying fixes) | `.php-cs-fixer.dist.php` |
| `installPHPStan(false)` (default) | PHPStan, deprecation rules, strict rules | `phpstan analyse` | `phpstan.neon` |
| `installPHPStan(true)` | The same PHPStan packages plus Larastan | `phpstan analyse` | `phpstan.neon`, configured for the project's Laravel application |
| `installPHPUnit()` | PHPUnit | `phpunit` | `phpunit.xml.dist` |
| `installInfection()` | Infection and PHPUnit | `infection --threads=max` | `infection.json5` and the test-suite setup referenced by it |

`installPHPCodeSniffer()` also enables the
`dealerdirect/phpcodesniffer-composer-installer` Composer plugin in the
project's configuration. `installInfection()` enables
`infection/extension-installer`. Infection also needs a supported coverage
driver available on the agent, such as Xdebug or PCOV.

`installAll(boolean optimize = false, boolean isDev = false)` runs
`composer install --no-interaction --prefer-dist --no-progress` without
running checks. By default it adds `--no-dev`; pass `isDev = true` to include
development dependencies. Pass `optimize = true` to add
`--optimize-autoloader`. Commit a `composer.lock` in the consuming project
for reproducible dependency installation; without one, Composer resolves
dependencies and creates a lock file.

```groovy
@Library('installers') _ // Use the library name configured in Jenkins.

node {
    checkout scm
    InstallersForPHPBasedTools.installAll(false, true)
    InstallersForPHPBasedTools.installPHPStan(true)
    InstallersForPHPBasedTools.installPHPUnit()
}
```

In this example, PHPStan and PHPUnit run after their dependencies are
installed. Their configuration files must already exist in the checkout.
