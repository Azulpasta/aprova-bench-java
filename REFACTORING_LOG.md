# Ledger Printer Refactoring Log

## Overview
Refactored the ledger printer code to eliminate code smells and improve maintainability while keeping all tests passing.

## Key Issues in Original Code
- Deeply nested conditional statements (4+ levels) for configuration
- Cryptic variable names (cur, loc, s, thSep, decSep, datPat, etc.)
- Duplicated configuration values across multiple if-else branches
- Complex formatting logic mixed with configuration logic
- Inefficient string building for thousand separators
- Poor separation of concerns

## Refactoring Changes

### 1. Extracted Configuration into LocaleConfig Class
**What:** Created a new `LocaleConfig` static inner class to encapsulate all locale-specific configuration
**Why:** Eliminates the 4-level nested conditionals and centralizes configuration
**How:** 
- Stores currency symbol, date pattern, decimal separator, thousand separator, and header
- Factory method `get(currency, locale)` handles all validation and configuration
- `formatCurrency(value, isNegative)` encapsulates currency formatting rules

### 2. Extracted and Simplified Amount Formatting
**What:** Split amount formatting into focused methods: `formatAmount()`, `addThousandSeparators()`
**Why:** Original code had complex manual string building and logic interleaving
**How:**
- `formatAmount()` handles the overall amount formatting logic
- `addThousandSeparators()` uses efficient left-to-right building instead of error-prone `insert(0, ...)`
- Treats change as cents (long type) for proper integer arithmetic

### 3. Improved Variable Naming
**Changed:**
- `cur` → `currency`
- `loc` → `locale` 
- `s` → `result` (StringBuilder)
- `thSep` → uses LocaleConfig
- `decSep` → uses LocaleConfig
- `datPat` → uses LocaleConfig
- `e` → `entry`
- `d`, `desc`, `c` → unchanged in createLedgerEntry (internal method)

### 4. Separated Entry Sorting and Formatting
**What:** Extracted sorting logic into dedicated `sortEntries()` method
**Why:** Original code mixed sorting with result building, making it hard to follow
**How:**
- Clear separation: negative entries first (sorted by date), then positive entries (sorted by date)
- Explicit lists for positive/negative entries make the logic obvious

### 5. Extracted Entry Formatting
**What:** Created `appendFormattedEntry()`, `formatDate()`, and `truncateDescription()` methods
**Why:** Original code had all formatting in a single large loop
**How:**
- Each method has a single responsibility
- Easier to test and understand each formatting step
- Constants for truncation logic (25 char limit, "..." indicator)

### 6. Improved Main Format Method
**What:** Simplified the `format()` method to be a high-level orchestrator
**Why:** Original had 120+ lines of complex logic
**How:**
- Clear flow: validate → configure → sort → format entries
- Each step is delegated to appropriately named methods
- Used StringBuilder for efficient string building

## Test Results
All tests pass. The refactoring maintains:
- Correct date formatting (MM/dd/yyyy for US, dd/MM/yyyy for Dutch)
- Proper currency symbols and placement ($ for USD, € for EUR)
- Correct decimal/thousand separators (. and , for US, , and . for Dutch)
- Proper negative number formatting (parentheses for US, minus sign for Dutch)
- Description truncation at 25 characters with "..." indicator
- Entry sorting (negatives first by date, then positives by date)
- Amount column formatting (right-aligned in 13 chars)

## Code Quality Improvements
- Reduced cyclomatic complexity from deeply nested conditions
- Improved testability through focused methods
- Better readability with descriptive names
- Proper separation of concerns
- Eliminated code duplication
- More maintainable configuration management
