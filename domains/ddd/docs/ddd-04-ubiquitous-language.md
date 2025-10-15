# Ubiquitous Language

## Definition

**Ubiquitous Language** is a common, rigorous language built collaboratively between developers and domain experts, based on the domain model. It is used pervasively in all team communication, documentation, and code.

**Key Insight:** The language should be "ubiquitous" - used everywhere by everyone involved in the software.

---

## Purpose & Benefits

### Why Ubiquitous Language Matters

1. **Eliminates Translation**: No gap between how experts talk and how code is written
2. **Precision**: Software requires unambiguous terminology
3. **Shared Understanding**: Everyone discusses the same concepts consistently
4. **Living Documentation**: Code itself documents the domain
5. **Discovery Tool**: Language evolution reveals deeper domain insights

### Problems It Solves

**Without Ubiquitous Language:**
- Developers use technical terms, experts use business terms
- Constant mental translation introduces errors
- Requirements documents use different language than code
- Subtle concept differences get lost
- Communication breakdowns

**With Ubiquitous Language:**
- Everyone uses the same terms
- Code is readable by domain experts (conceptually)
- Changes map directly from conversation to code
- Precision in communication
- Shared mental model

---

## Development Process

### Phase 1: Discovery

**Activities:**
1. **Domain Exploration Sessions**
   - Developers and domain experts meet
   - Explore domain through scenarios
   - Identify key concepts
   - Surface implicit knowledge

2. **Listen for Terms**
   - Pay attention to expert vocabulary
   - Note when experts correct you
   - Identify ambiguous terms
   - Find synonyms

3. **Model Sketching**
   - Draw simple diagrams
   - Use terms experts recognize
   - Test understanding with examples
   - Refine based on feedback

**Example Session:**
```
Developer: "So when a customer places an order..."
Expert: "Well, actually we don't call it 'placing' - we say the customer 'submits' an order."
Developer: "Okay, so when they submit an order, how do we handle inventory?"
Expert: "We 'reserve' inventory for the order. It's not 'allocated' until the order is 'confirmed'."

New terms discovered:
- Submit (not place)
- Reserve (not allocate)
- Confirm (distinct step)
```

### Phase 2: Refinement

**Activities:**
1. **Challenge Terminology**
   - Is this term precise enough?
   - Does it mean one thing?
   - Is it used consistently?
   - Does it work in code?

2. **Resolve Ambiguities**
   - When experts use same word differently
   - When one concept needs multiple terms
   - When existing terms are vague

3. **Test with Scenarios**
   - Walk through business processes
   - Use only defined terms
   - Identify gaps
   - Refine definitions

**Example Refinement:**
```
Initial: "Process the payment"

Problems:
- "Process" is vague
- What does it mean exactly?

Refined terms:
- "Authorize" the payment (check funds available)
- "Capture" the payment (actually charge)
- "Settle" the payment (transfer funds)

Code now uses: authorizePayment(), capturePayment(), settlePayment()
```

### Phase 3: Documentation

**Document the Language:**
1. **Glossary**: Define each term
2. **Context**: Note when terms apply
3. **Relationships**: How concepts relate
4. **Examples**: Concrete instances
5. **Evolution**: Track changes over time

### Phase 4: Maintenance

**Keep Language Alive:**
1. **Regular Review**: Language evolves with understanding
2. **Update Code**: Refactor when language changes
3. **Onboard New Members**: Teach the language
4. **Challenge Staleness**: Remove obsolete terms
5. **Document Changes**: Track evolution

---

## Documentation Strategies

### Glossary Structure

**Glossary Template:**

```markdown
## Term: [ConceptName]

**Definition**: One-sentence precise definition

**Description**:
Detailed explanation of what this concept means in our domain.
Include context, why it matters, and how it's used.

**Synonyms**: [Other names for this, if any]

**Related Terms**:
- [OtherConcept]: How they relate
- [AnotherConcept]: Dependency or association

**Examples**:
1. Concrete example 1
2. Concrete example 2

**Code Representation**:
- Class: `ConceptName`
- Key methods: `doSomething()`, `validateState()`

**Business Rules**:
- Rule 1: Description
- Rule 2: Description

**Last Updated**: [Date]
**Changed By**: [Team member]
**Reason**: [Why it changed]
```

**Example Entry:**

```markdown
## Term: Order

**Definition**: A customer's request to purchase products

**Description**:
An Order represents a customer's intention to buy one or more products.
An order goes through several states from creation (draft) through submission,
payment, fulfillment, and completion. Each state transition has specific
business rules that must be enforced.

**Synonyms**: None (previously called "Shopping Transaction" - deprecated)

**Related Terms**:
- OrderLine: Contains multiple OrderLines
- Customer: Belongs to a Customer
- Payment: Associated with Payment
- Shipment: Results in Shipment

**States**:
- Draft: Being composed, can be modified
- Submitted: Customer has submitted, awaiting payment
- Paid: Payment confirmed
- Fulfilled: Products shipped
- Completed: Customer received products
- Cancelled: Order cancelled

**Examples**:
1. Customer submits Order with 3 OrderLines totaling $150
2. B2B customer creates draft Order, saves, returns later to submit

**Code Representation**:
- Class: `Order` (Aggregate Root)
- Key methods: `submit()`, `addLine()`, `cancel()`
- Events: `OrderSubmitted`, `OrderPaid`, `OrderFulfilled`

**Business Rules**:
- Order must have at least one OrderLine to be submitted
- Order cannot be modified after submission (except cancellation)
- Total equals sum of all OrderLine subtotals
- Only draft Orders can have lines added/removed

**Last Updated**: 2025-01-15
**Changed By**: Development Team
**Reason**: Added "Fulfilled" state to distinguish from "Completed"
```

### Term Categories

**Organize by:**

**1. Core Domain Concepts**
- Main business entities
- Key processes
- Primary workflows

**2. Supporting Concepts**
- Helper concepts
- Technical necessities with domain meaning
- Constraints and rules

**3. Events**
- Things that happen
- State transitions
- Important occurrences

**4. Rules and Policies**
- Business rules
- Validation rules
- Calculation formulas

---

## Language in Code

### Naming Conventions

**Classes match terms:**
```java
// Domain term: "Customer"
public class Customer { }

// Domain term: "Order"
public class Order { }

// Domain term: "Payment Authorization"
public class PaymentAuthorization { }
```

**Methods use domain verbs:**
```java
// Experts say "submit an order"
public void submit() { }

// Experts say "suspend a customer"
public void suspend() { }

// Experts say "authorize payment"
public PaymentAuthorization authorize(Money amount) { }
```

**Avoid technical jargon in domain layer:**
```java
// ❌ Technical language
public void persist() { }
public void setStatus(int statusCode) { }

// ✓ Domain language
public void save() { }  // If "save" is domain term
public void submit() { }  // Use actual business action
```

### Package/Module Names

**Use domain terms:**
```
com.company.ecommerce.sales      // "Sales" is domain term
com.company.ecommerce.inventory  // "Inventory" is domain term
com.company.ecommerce.shipping   // "Shipping" is domain term

// Not technical categories:
com.company.ecommerce.entities  // ❌ Technical
com.company.ecommerce.services  // ❌ Technical
```

### Value Objects for Concepts

**Make concepts explicit:**
```java
// ❌ Primitive obsession
String email;
double amount;
String currency;

// ✓ Domain concepts explicit
Email email;           // "Email" is domain concept
Money amount;          // "Money" is domain concept (amount + currency)
```

### Enums for Fixed Sets

**Domain-meaningful names:**
```java
public enum OrderStatus {
    DRAFT,      // Customer composing
    SUBMITTED,  // Awaiting payment
    PAID,       // Payment confirmed
    FULFILLED,  // Products shipped
    COMPLETED,  // Customer received
    CANCELLED   // Order cancelled
}
```

### Comments Use Domain Language

**Explain why in domain terms:**
```java
// Business rule: Orders below minimum must be rejected
if (total.isLessThan(Money.minimumOrder())) {
    throw new BelowMinimumOrderException();
}

// Customers with overdue invoices cannot place new orders
if (customer.hasOverdueInvoices()) {
    throw new CustomerCreditHoldException();
}
```

---

## Model Exploration Techniques

### Event Storming

**Process:**
1. Gather domain experts and developers
2. Identify domain events (things that happen)
3. Place events on timeline
4. Identify commands that trigger events
5. Identify aggregates that handle commands
6. Group into bounded contexts

**Output:**
- List of events
- Commands
- Aggregates
- Process flows
- Context boundaries

**Language Discovery:**
- Events are named (OrderSubmitted, PaymentAuthorized)
- Commands are named (SubmitOrder, AuthorizePayment)
- Aggregates are named (Order, Payment)

### Domain Storytelling

**Process:**
1. Expert tells story of domain process
2. Developers illustrate with simple icons
3. Identify actors, work objects, activities
4. Map out the sequence

**Example Story:**
```
"A Customer browses the Catalog and adds Products to their Shopping Cart.
When ready, they submit their Order. The system reserves Inventory and
authorizes Payment. Once authorized, the Order is confirmed and sent to
the Warehouse for fulfillment."

Terms discovered:
- Customer, Catalog, Product, Shopping Cart, Order
- submit, reserve, authorize, confirm, fulfill
- Inventory, Payment, Warehouse
```

### Example Mapping

**Process:**
1. Start with user story or feature
2. Provide concrete examples
3. Identify business rules
4. Surface questions

**Template:**
```
Story: Customer submits order

Examples:
- Happy path: Customer submits order with valid payment
- Alternative: Customer submits but payment fails
- Edge case: Customer submits order at exactly midnight

Rules:
- Order must have at least one item
- Payment must be authorized
- Inventory must be available

Questions:
- What happens if inventory becomes unavailable after authorization?
- Can customer modify order after submission?
```

---

## Language Evolution

### Recognizing Need for Change

**Signs language needs refinement:**
- Team members use different terms for same concept
- Developers translate between code and conversation
- Expert corrections are frequent
- Terms are ambiguous
- New understanding of domain emerges

### Managing Change

**Process:**
1. **Identify Issue**: Term is unclear or incorrect
2. **Propose Change**: Suggest better term
3. **Validate with Experts**: Does new term work?
4. **Update Glossary**: Document the change
5. **Refactor Code**: Rename in code
6. **Communicate**: Tell team about change

**Example Evolution:**
```
Version 1: "Transaction" (too vague)
↓
Version 2: "Sale" (closer, but not quite right)
↓
Version 3: "Order" (correct term experts use)

Code evolution:
Transaction → Sale → Order
processTransaction() → completeSale() → submitOrder()
```

### Deprecation

**When removing terms:**
1. Mark as deprecated in glossary
2. Note replacement term
3. Set deadline for migration
4. Track remaining usages
5. Remove from glossary when fully migrated

**Example:**
```markdown
## Term: Shopping Transaction [DEPRECATED]

**Status**: Deprecated as of 2025-01-10
**Replacement**: Use "Order" instead
**Reason**: Domain experts consistently use "Order", not "Transaction"
**Migration Deadline**: 2025-02-01
```

---

## Team Communication

### In Meetings

**Use the language:**
- "The Customer submits an Order" (not "user saves a transaction")
- "We authorize the Payment" (not "we process the credit card")
- "Inventory is reserved" (not "we decrement stock")

**Correct immediately:**
```
Developer: "When the user checkouts..."
Expert: "We actually call that 'submitting the order'"
Developer: "Right, when they submit the order..."
```

### In Documentation

**Requirements:**
```
✓ "Customer must be able to submit Order"
✗ "User must be able to save shopping cart transaction"
```

**User Stories:**
```
✓ "As a Customer, I want to submit my Order so that it can be fulfilled"
✗ "As a user, I want to save my transaction so it processes"
```

### In Code Reviews

**Review for language:**
- Are domain terms used consistently?
- Do method names reflect business operations?
- Are comments in domain language?
- Are technical terms isolated to infrastructure?

---

## Documentation Templates

### Bounded Context Glossary

```markdown
# [Context Name] Bounded Context - Glossary

## Context Description
[What is this bounded context responsible for?]

## Core Concepts

### [Concept 1]
[Definition and description]

### [Concept 2]
[Definition and description]

## Events

### [Event 1]
What happened and when it's raised

## Commands

### [Command 1]
What action triggers what

## Business Rules

### [Rule 1]
Description and rationale
```

### Cross-Context Term Mapping

```markdown
# Term Translations Between Contexts

## "Product"

**In Sales Context:**
- Meaning: Item for sale, with price and description
- Attributes: name, price, description, category

**In Inventory Context:**
- Meaning: Physical stock item with location
- Attributes: SKU, quantity, warehouse location, reorder point

**In Shipping Context:**
- Meaning: Item to ship with dimensions
- Attributes: dimensions, weight, fragility

**Translation:**
- Sales.Product.id maps to Inventory.Product.sku
- Different models, same business concept
```

---

## Examples from Different Domains

### E-Commerce

**Key Terms:**
- Customer, Product, Order, OrderLine, Shopping Cart
- submit, authorize, capture, fulfill, ship
- Inventory, Warehouse, Supplier

**Sample Phrase:**
"Customer adds Products to Shopping Cart, then submits Order, which reserves Inventory and authorizes Payment"

### Healthcare

**Key Terms:**
- Patient, Provider, Appointment, Encounter, Treatment Plan
- schedule, check-in, diagnose, prescribe, bill
- Medical Record, Insurance, Claim

**Sample Phrase:**
"Patient schedules Appointment with Provider, checks in, has Encounter where Provider creates Treatment Plan"

### Finance

**Key Terms:**
- Account, Transaction, Balance, Transfer, Statement
- deposit, withdraw, transfer, reconcile, settle
- Ledger, Journal Entry, Posting

**Sample Phrase:**
"Customer initiates Transfer from one Account to another, system posts Journal Entries and updates Balances"

---

## Common Pitfalls

### 1. Technical Terms in Domain

**❌ Don't:**
```java
customer.persist();
order.setStatusCode(3);
product.getData();
```

**✓ Do:**
```java
customerRepository.save(customer);
order.submit();
product.specifications();
```

### 2. Ambiguous Terms

**Problem:** "Process" the order
**Solution:** Be specific:
- submit the order
- fulfill the order
- ship the order
- complete the order

### 3. Developer-Only Language

**Problem:** Code uses terms experts don't recognize

**❌ Bad:**
```java
class OrderDTO { }
class OrderEntity { }
class OrderManager { }
```

**✓ Good:**
```java
class Order { }
class OrderRepository { }
class OrderService { }  // If "service" is domain term
```

### 4. Expert Jargon Without Clarity

**Problem:** Using insider terms without defining them

**Solution:** Define even "obvious" terms:
- What exactly is a "submission"?
- When is an order "confirmed" vs "completed"?
- What makes inventory "available" vs "reserved"?

### 5. Stale Language

**Problem:** Code uses old terms after domain understanding evolved

**Solution:** Continuous refactoring to match current understanding

---

## Integration with Other Practices

### With Bounded Contexts

- Each bounded context has its own ubiquitous language
- Same term may mean different things in different contexts
- Explicitly document translations between contexts

### With Aggregates

- Aggregate names come from ubiquitous language
- Methods on aggregates use domain verbs
- Events raised are named in domain terms

### With Domain Events

- Event names are past-tense domain actions
- "OrderSubmitted", not "OrderSubmitEvent"
- Include domain-relevant data

### With Testing

**Tests use domain language:**
```java
@Test
void customer_cannot_submit_empty_order() {
    Order order = new Order(customerId);
    // No lines added

    assertThrows(EmptyOrderException.class, () -> order.submit());
}
```

---

## Metrics for Success

**Language is working when:**
- ✓ Domain experts recognize code structure
- ✓ Developers and experts use same terminology
- ✓ No translation needed in conversations
- ✓ New team members learn language quickly
- ✓ Code changes map directly to business changes
- ✓ Tests read like business specifications

**Language needs work when:**
- ✗ Frequent misunderstandings in meetings
- ✗ Code uses different terms than conversation
- ✗ Glossary is out of date
- ✗ Multiple terms for same concept
- ✗ Terms are ambiguous

---

## References

- Evans, Eric (2003). "Domain-Driven Design" - Chapters 1-3
- Fowler, Martin. "Ubiquitous Language" - https://martinfowler.com/bliki/UbiquitousLanguage.html
- Vernon, Vaughn (2013). "Implementing Domain-Driven Design" - Chapter on Ubiquitous Language

---

*Document Status: Ubiquitous Language framework complete*
