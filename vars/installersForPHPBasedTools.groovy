def installPHPCodeSniffer() {
    sh '''
        set -eu

        composer config \
            allow-plugins.dealerdirect/phpcodesniffer-composer-installer \
            true

        composer require --dev \
            dealerdirect/phpcodesniffer-composer-installer:^1.2 \
            phpcsstandards/php_codesniffer:^4.0 \
            slevomat/coding-standard:^8.31 \
            --no-interaction \
            --no-progress

        vendor/bin/phpcs \
            --standard=phpcs.xml.dist
    '''
}

def installPHPCSFixer() {
    sh '''
        set -eu

        composer require --dev \
            friendsofphp/php-cs-fixer:^3.95 \
            --no-interaction \
            --no-progress

        vendor/bin/php-cs-fixer fix \
            --dry-run \
            --diff \
            --config=.php-cs-fixer.dist.php
    '''
}

def installPHPStan(boolean useLarastan = false) {
    if (useLarastan) {
        sh '''
            set -eu

            composer require --dev \
                phpstan/phpstan:^2.2 \
                larastan/larastan:^3.10 \
                phpstan/phpstan-deprecation-rules:^2.0 \
                phpstan/phpstan-strict-rules:^2.0 \
                --no-interaction \
                --no-progress

            vendor/bin/phpstan analyse \
                --configuration=phpstan.neon \
                --no-progress
        '''
        return
    }

    sh '''
        set -eu

        composer require --dev \
            phpstan/phpstan:^2.2 \
            phpstan/phpstan-deprecation-rules:^2.0 \
            phpstan/phpstan-strict-rules:^2.0 \
            --no-interaction \
            --no-progress

        vendor/bin/phpstan analyse \
            --configuration=phpstan.neon \
            --no-progress
    '''
}

def installPHPUnit() {
    sh '''
        set -eu

        composer require --dev \
            phpunit/phpunit:^12.5 \
            --no-interaction \
            --no-progress

        vendor/bin/phpunit \
            --configuration=phpunit.xml.dist
    '''
}

def installInfection() {
    sh '''
        set -eu

        composer config \
            allow-plugins.infection/extension-installer \
            true

        composer require --dev \
            infection/infection:^0.34.0 \
            phpunit/phpunit:^12.5 \
            --no-interaction \
            --no-progress

        vendor/bin/infection \
            --configuration=infection.json5 \
            --threads=max \
            --no-progress
    '''
}

def installAll(boolean optimize = false, boolean isDev = false) {
    List<String> arguments = [
        '--no-interaction',
        '--prefer-dist',
        '--no-progress'
    ]

    if (!isDev) {
        arguments.add('--no-dev')
    }

    if (optimize) {
        arguments.add('--optimize-autoloader')
    }

    sh "composer install ${arguments.join(' ')}"
}
