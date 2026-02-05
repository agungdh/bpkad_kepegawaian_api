<?php
// Auto-login wrapper for Adminer
function adminer_object() {
    require_once '/var/www/html/adminer/include/adminer.inc.php';

    class CustomAdminer extends Adminer {
        public function name() {
            return 'BPKAD Kepegawaian';
        }

        public function credentials() {
            return ['postgres', 'postgres', 'postgres'];
        }

        public function database() {
            return 'bpkad_kepegawaian';
        }

        public function login($login, $password) {
            return true;
        }
    }

    return new CustomAdminer();
}

require_once '/var/www/html/adminer/include/bootstrap.inc.php';
