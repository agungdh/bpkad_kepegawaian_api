<?php
// Auto-login for Adminer - only use for local development!
function adminer_object() {
    class CustomAdminer extends Adminer {
        public function name() {
            return 'BPKAD Kepegawaian';
        }

        public function credentials() {
            return ['postgres', 'postgres', 'postgres']; // server, username, password
        }

        public function database() {
            return 'bpkad_kepegawaian';
        }

        public function login($login, $password) {
            return true; // Auto-login
        }
    }

    return new CustomAdminer();
}

require_once 'adminer.php';
