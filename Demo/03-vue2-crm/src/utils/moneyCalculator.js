/**
 * Financial calculation helper to avoid JavaScript floating point errors (e.g., 0.1 + 0.2 = 0.30000000000000004).
 */
export function addMoney(a, b) {
  const factor = 10000;
  return Math.round((Number(a || 0) + Number(b || 0)) * factor) / factor;
}

export function subtractMoney(a, b) {
  const factor = 10000;
  return Math.round((Number(a || 0) - Number(b || 0)) * factor) / factor;
}

export function multiplyMoney(amount, rate) {
  const factor = 10000;
  return Math.round(Number(amount || 0) * Number(rate || 0) * factor) / factor;
}

export function formatCurrency(value, currencySymbol) {
  const symbol = currencySymbol || '$';
  if (value === null || value === undefined || isNaN(value)) {
    return symbol + '0.00';
  }
  const num = Number(value);
  return symbol + num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
