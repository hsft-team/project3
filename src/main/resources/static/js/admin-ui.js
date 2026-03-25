(function () {
    function findSubmitter(form) {
        return form.querySelector('button[type="submit"], input[type="submit"]');
    }

    function createConfirmLayer() {
        const backdrop = document.createElement('div');
        backdrop.className = 'confirm-backdrop';
        backdrop.innerHTML = `
            <div class="confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="confirm-dialog-title">
                <h3 id="confirm-dialog-title">확인</h3>
                <p id="confirm-dialog-message">이 작업을 진행할까요?</p>
                <div class="button-row">
                    <button type="button" class="ghost-link" data-confirm-cancel="true">취소</button>
                    <button type="button" class="primary-button" data-confirm-accept="true">확인</button>
                </div>
            </div>
        `;
        return backdrop;
    }

    document.addEventListener('DOMContentLoaded', function () {
        let confirmInProgress = false;

        document.addEventListener('submit', function (event) {
            if (confirmInProgress) {
                confirmInProgress = false;
                return;
            }

            const form = event.target;
            if (!(form instanceof HTMLFormElement)) {
                return;
            }

            const submitter = event.submitter || findSubmitter(form);
            if (form.dataset.skipConfirm === 'true' || submitter?.dataset.skipConfirm === 'true') {
                return;
            }

            event.preventDefault();

            const message = submitter?.dataset.confirmMessage
                || form.dataset.confirmMessage
                || '이 작업을 진행할까요?';

            const confirmText = submitter?.dataset.confirmAccept || '확인';
            const cancelText = submitter?.dataset.confirmCancel || '취소';

            const backdrop = createConfirmLayer();
            const messageNode = backdrop.querySelector('#confirm-dialog-message');
            const acceptButton = backdrop.querySelector('[data-confirm-accept="true"]');
            const cancelButton = backdrop.querySelector('[data-confirm-cancel="true"]');

            messageNode.textContent = message;
            acceptButton.textContent = confirmText;
            cancelButton.textContent = cancelText;

            function closeDialog() {
                backdrop.remove();
                document.body.classList.remove('confirm-open');
            }

            acceptButton.addEventListener('click', function () {
                closeDialog();
                confirmInProgress = true;
                if (submitter instanceof HTMLElement && typeof submitter.click === 'function') {
                    submitter.click();
                } else {
                    form.requestSubmit();
                }
            });

            cancelButton.addEventListener('click', closeDialog);
            backdrop.addEventListener('click', function (clickEvent) {
                if (clickEvent.target === backdrop) {
                    closeDialog();
                }
            });

            document.body.classList.add('confirm-open');
            document.body.appendChild(backdrop);
            acceptButton.focus();
        });
    });
})();
