<?php
// Auto-login wrapper for Adminer - just serve adminer.php with JS injection
ob_start();
include 'adminer.php';
$content = ob_get_clean();

// Inject auto-login script
$script = "<script>
(function() {
    const fillAndSubmit = function() {
        const driver = document.querySelector('select[name=\"auth[driver]\"]');
        const server = document.querySelector('input[name=\"auth[server]\"]');
        const username = document.querySelector('input[name=\"auth[username]\"]');
        const password = document.querySelector('input[name=\"auth[password]\"]');
        const db = document.querySelector('input[name=\"auth[db]\"]');

        if (driver && server && username && password) {
            driver.value = 'pgsql';
            server.value = 'postgres';
            username.value = 'postgres';
            password.value = 'postgres';
            if (db) db.value = 'bpkad_kepegawaian';

            password.form.submit();
        }
    };
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', fillAndSubmit);
    } else {
        setTimeout(fillAndSubmit, 100);
    }
})();
</script>";

echo str_replace('</body>', $script . '</body>', $content);
