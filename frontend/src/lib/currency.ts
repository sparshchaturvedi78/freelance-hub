export function formatCurrency(value: number | string | null | undefined) {
  if (value === null || value === undefined || value === '') {
    return '₹0'
  }

  const number = typeof value === 'string' ? Number(value) : value
  if (Number.isNaN(number)) {
    return '₹0'
  }

  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(number)
}
