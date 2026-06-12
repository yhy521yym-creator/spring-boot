<template>
  <div class="page-view">
    <div v-if="title || desc || $slots.extra" class="page-head">
      <div class="page-head__main">
        <h1 v-if="title" class="page-head__title">{{ title }}</h1>
        <p v-if="desc" class="page-head__desc">{{ desc }}</p>
      </div>
      <div v-if="$slots.extra" class="page-head__extra">
        <slot name="extra" />
      </div>
    </div>
    <div v-if="$slots.filters" class="page-filters-bar">
      <slot name="filters" />
    </div>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  desc: { type: String, default: '' },
})
</script>

<style scoped>
.page-filters-bar {
  margin-bottom: 16px;
  padding: 14px 18px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}
</style>
