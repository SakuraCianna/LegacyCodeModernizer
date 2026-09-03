import Vue from 'vue';

// Global EventBus singleton for legacy cross-component decoupled communication
const EventBus = new Vue();

export default EventBus;
