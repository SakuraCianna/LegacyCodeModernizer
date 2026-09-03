/**
 * Vue 2 Custom Directive for button click debouncing.
 * Prevents multiple rapid clicks causing duplicate submissions.
 */
export default {
  inserted(el, binding) {
    let timer = null;
    const delay = binding.value && binding.value.delay ? binding.value.delay : 800;

    el.addEventListener('click', () => {
      if (el.disabled) return;
      el.disabled = true;
      el.classList.add('is-debouncing');

      if (binding.value && typeof binding.value.handler === 'function') {
        binding.value.handler();
      }

      timer = setTimeout(() => {
        el.disabled = false;
        el.classList.remove('is-debouncing');
      }, delay);
    });
  }
};
