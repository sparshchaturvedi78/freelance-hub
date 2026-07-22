# FreelanceHub Design System

## Vision
Modern SaaS dashboard that feels premium but not stuffy. Professional enough for invoicing, fun enough to use daily. Glassmorphism accents, smooth animations, dark-first with light mode support.

## Color Palette

### Primary Colors
- **Brand Blue**: `#3B82F6` (accent, CTAs, active states)
- **Brand Gradient**: Blue (`#3B82F6`) → Purple (`#8B5CF6`) — hero sections, highlights
- **Dark Background**: `#0F172A` (primary), `#1E293B` (secondary)
- **Light Background**: `#F8FAFC` (primary), `#F1F5F9` (secondary)

### Status Colors
- **Success**: `#10B981` (green)
- **Error**: `#EF4444` (red)
- **Warning**: `#F59E0B` (amber)
- **Info**: `#3B82F6` (blue)

### Semantic Colors
- **Text Primary Dark**: `#F1F5F9`
- **Text Secondary Dark**: `#CBD5E1`
- **Text Primary Light**: `#0F172A`
- **Text Secondary Light**: `#475569`
- **Border Dark**: `#334155`
- **Border Light**: `#E2E8F0`

## Typography

### Font Stack
```css
font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
```

### Scale
- **Display**: 48px / 56px (hero titles)
- **H1**: 36px / 44px (page titles)
- **H2**: 28px / 36px (section titles)
- **H3**: 20px / 28px (subsections)
- **Body Large**: 16px / 24px (main content)
- **Body**: 14px / 20px (default)
- **Body Small**: 12px / 16px (secondary info)
- **Label**: 12px / 16px bold (labels, badges)

## Component Library

### Buttons
- **Primary**: Solid blue gradient, white text, shadow on hover
- **Secondary**: Ghost (outline on dark, fill on hover)
- **Danger**: Solid red, white text
- **Size**: sm (28px), md (36px), lg (44px)

### Cards
- **Dark**: `#1E293B` bg, `#334155` border, glassmorphism blur effect
- **Light**: `#FFFFFF` bg, `#E2E8F0` border, subtle shadow
- **Hover**: Lift effect (transform: translateY), border glow

### Inputs
- **Base**: Bordered, rounded (8px)
- **Focus**: Blue glow, border color change
- **Invalid**: Red border, error message below
- **Placeholder**: Muted text, system font

### Navigation
- **Sidebar**: Fixed, dark, collapsible (toggle on mobile)
- **Topbar**: Gradient background, user menu, org switcher
- **Mobile**: Bottom tab bar with 5 main routes

### Badges & Chips
- **Status Badges**: Colored bg + text (PENDING, SENT, PAID)
- **Role Badges**: OWNER (purple), ADMIN (blue), MEMBER (gray), CLIENT (green)

## Animations

### Transitions
- **Default**: 200ms ease-in-out
- **Hover**: 150ms ease-out
- **Slow**: 400ms ease-in-out (modals, page transitions)

### Effects
- **Hover Scale**: 1.02x on interactive elements
- **Focus Ring**: 2px blue/purple glow
- **Page Load**: Fade-in + subtle slide-up (staggered for lists)
- **Skeleton Loaders**: Pulse animation while loading

## Breakpoints
- **Mobile**: 0–640px
- **Tablet**: 641–1024px
- **Desktop**: 1025px+

## Layout Grid
- **Desktop**: 12-column grid, 20px gutter
- **Mobile**: 4-column grid, 16px gutter
- **Padding**: 24px (desktop), 16px (mobile)

## Icons
- **Library**: Heroicons (24px primary, 16px secondary)
- **Color**: Match text color (inherit)

## Accessibility
- Minimum contrast ratio: 4.5:1 (text), 3:1 (graphics)
- Focus visible: 2px outline on all interactive elements
- Semantic HTML: proper heading hierarchy, ARIA labels where needed
- Keyboard navigation: Tab order makes sense, Enter/Space on buttons
