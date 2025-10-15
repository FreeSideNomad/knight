# Tactical Design Patterns

## Overview

Tactical Design Patterns in Domain-Driven Design provide the building blocks for implementing the domain model within a bounded context. While strategic patterns address system organization and context boundaries, tactical patterns focus on the internal structure of a rich domain model.

**Purpose of Tactical Patterns:**
- Structure domain logic clearly
- Maintain business invariants
- Express domain concepts explicitly
- Enable testability
- Facilitate change

**Key Principle:** The domain layer should be isolated from infrastructure and application concerns, containing pure business logic expressed in the Ubiquitous Language.

---

## Building Blocks Overview

### Core Patterns
1. **Entity**: Object with identity and lifecycle
2. **Value Object**: Object defined by its attributes
3. **Aggregate**: Consistency boundary
4. **Aggregate Root**: Entry point to aggregate
5. **Repository**: Persistence abstraction
6. **Domain Service**: Stateless domain operations
7. **Factory**: Complex object creation
8. **Domain Event**: Something that happened
9. **Module**: Code organization

### Pattern Relationships

```
Aggregate
 ├─ Aggregate Root (Entity)
 ├─ Other Entities
 └─ Value Objects
        ↓
    Repository (stores/retrieves entire aggregate)
        ↓
    Domain Events (published by aggregate)
```

---

## Entities

### Definition
An **Entity** is an object that is defined by its identity rather than its attributes. It has a continuous lifecycle and its attributes may change, but it remains the same entity.

### Identity

**What is Identity?**
- A unique identifier that distinguishes one entity from another
- Persists throughout the entity's lifecycle
- Does not change even if all other attributes change

**Identity Generation Strategies:**
1. **User-provided**: Email address, username, SSN
2. **Auto-generated**: UUID, GUID, database sequence
3. **Derived**: Combination of attributes (composite key)
4. **External**: Provided by external system

**Identity Immutability:**
- Identity should be assigned at creation
- Should never change throughout lifecycle
- Should be final/readonly in code

### Lifecycle

**Creation:**
- Entity comes into existence
- Identity is assigned
- Initial state established
- Invariants must be satisfied

**Modification:**
- Attributes change over time
- Identity remains constant
- Invariants maintained at all times
- Changes may trigger domain events

**Deletion/Archival:**
- Entity no longer active
- May be soft-deleted (marked inactive)
- May be archived
- Identity preserved for historical reference

### Characteristics

1. **Continuity**: Same entity over time despite changes
2. **Identity**: Unique identifier
3. **Mutability**: Attributes can change
4. **Equality**: Based on identity, not attributes
5. **Lifecycle**: Created, modified, deleted/archived

### Design Guidelines

1. **Model Identity Explicitly**
   ```java
   public class Customer {
       private final CustomerId id;  // Immutable identity
       private String name;          // Mutable attributes
       private Email email;
   }
   ```

2. **Enforce Invariants**
   ```java
   public void changeEmail(Email newEmail) {
       if (newEmail == null) {
           throw new IllegalArgumentException("Email cannot be null");
       }
       this.email = newEmail;
       // Publish domain event
       DomainEvents.raise(new CustomerEmailChanged(this.id, newEmail));
   }
   ```

3. **Use Value Objects for Attributes**
   - Prefer value objects over primitives
   - Encapsulates validation and behavior
   - Example: `Email`, `Money`, `Address`

4. **Minimize Public Setters**
   - Use intention-revealing methods
   - `changeAddress()` vs `setAddress()`
   - Encapsulate business rules

### Entity vs Value Object Decision

**Choose Entity if:**
- ✓ Object must be tracked over time
- ✓ Object has a unique identity
- ✓ Object's history matters
- ✓ Objects with same attributes are different
- ✓ Object participates in relationships

**Choose Value Object if:**
- ✓ Only attributes matter
- ✓ Two objects with same attributes are interchangeable
- ✓ Immutability is natural
- ✓ No lifecycle to track

### Examples

**Customer Entity:**
```java
public class Customer {
    private final CustomerId id;
    private PersonName name;
    private Email email;
    private Address shippingAddress;
    private CustomerStatus status;
    private List<Order> orderHistory;

    // Constructor enforces invariants
    public Customer(CustomerId id, PersonName name, Email email) {
        if (id == null || name == null || email == null) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = CustomerStatus.ACTIVE;
        this.orderHistory = new ArrayList<>();
    }

    // Business methods
    public void suspend() {
        if (this.status == CustomerStatus.SUSPENDED) {
            throw new CustomerAlreadySuspendedException();
        }
        this.status = CustomerStatus.SUSPENDED;
        DomainEvents.raise(new CustomerSuspended(this.id));
    }

    // Identity-based equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }
}
```

**Order Entity:**
```java
public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private OrderStatus status;
    private List<OrderLine> lines;
    private Money total;
    private LocalDateTime placedAt;

    public void addLine(Product product, Quantity quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new OrderNotModifiableException();
        }
        lines.add(new OrderLine(product, quantity));
        recalculateTotal();
    }

    public void submit() {
        if (lines.isEmpty()) {
            throw new EmptyOrderException();
        }
        this.status = OrderStatus.SUBMITTED;
        this.placedAt = LocalDateTime.now();
        DomainEvents.raise(new OrderSubmitted(this.id));
    }
}
```

### Common Mistakes

1. **Anemic Entities**: Just data containers with getters/setters
2. **Public Setters**: Breaking encapsulation
3. **Missing Invariants**: Not enforcing business rules
4. **Mutable Identity**: Allowing ID to change
5. **Attribute-based Equality**: Comparing attributes instead of ID

---

## Value Objects

### Definition
A **Value Object** is an immutable object that is defined entirely by its attributes. Two value objects with the same attributes are considered equal and interchangeable.

### Core Characteristics

1. **Immutability**
   - Values cannot change after creation
   - To "change" a value, create a new instance
   - Thread-safe by nature
   - Can be shared safely

2. **Attribute-Based Equality**
   - Two value objects are equal if all attributes match
   - No unique identifier
   - Structural equality, not reference equality

3. **Side-Effect-Free Behavior**
   - Methods don't modify state
   - Return new instances instead
   - Functional style operations

4. **Self-Validation**
   - Constructor validates all invariants
   - Invalid state cannot be represented
   - Always-valid domain model

### When to Use Value Objects

**Use Value Objects for:**
- ✓ Measurements and quantities (Money, Weight, Temperature)
- ✓ Ranges (DateRange, AgeRange)
- ✓ Descriptive aspects (Name, Address, Email)
- ✓ Complex calculations (Percentage, Ratio)
- ✓ Domain concepts without identity (ProductSpecification)

**Benefits:**
- Explicit domain concepts
- Encapsulated validation
- Type safety
- Reusability
- Testability

### Design Guidelines

1. **Always Immutable**
```java
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException();
        }
        this.amount = amount;
        this.currency = currency;
    }

    // No setters!

    // Side-effect-free operations return new instances
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException();
        }
        return new Money(
            this.amount.add(other.amount),
            this.currency
        );
    }
}
```

2. **Implement Equality**
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Money)) return false;
    Money money = (Money) o;
    return amount.equals(money.amount) &&
           currency.equals(money.currency);
}

@Override
public int hashCode() {
    return Objects.hash(amount, currency);
}
```

3. **Make Concepts Explicit**
```java
// Instead of primitive obsession
String email;  // ❌

// Use value object
Email email;   // ✓

public final class Email {
    private final String value;

    public Email(String value) {
        if (!isValid(value)) {
            throw new InvalidEmailException(value);
        }
        this.value = value.toLowerCase();
    }

    private boolean isValid(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

### Replaceability

Since value objects are immutable, to "modify" them you replace the entire object:

```java
// Entity holds value object
public class Customer {
    private Email email;

    // To "change" email, replace the entire value object
    public void changeEmail(Email newEmail) {
        this.email = newEmail;  // Replace, not modify
    }
}
```

### Examples

**Address Value Object:**
```java
public final class Address {
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String postalCode, String country) {
        // Validation
        this.street = requireNonBlank(street, "Street");
        this.city = requireNonBlank(city, "City");
        this.postalCode = requireNonBlank(postalCode, "Postal code");
        this.country = requireNonBlank(country, "Country");
    }

    // Side-effect-free operation
    public Address withNewStreet(String newStreet) {
        return new Address(newStreet, this.city, this.postalCode, this.country);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return street.equals(address.street) &&
               city.equals(address.city) &&
               postalCode.equals(address.postalCode) &&
               country.equals(address.country);
    }
}
```

**DateRange Value Object:**
```java
public final class DateRange {
    private final LocalDate start;
    private final LocalDate end;

    public DateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new InvalidDateRangeException();
        }
        this.start = start;
        this.end = end;
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public boolean overlaps(DateRange other) {
        return !this.end.isBefore(other.start) &&
               !other.end.isBefore(this.start);
    }

    public int lengthInDays() {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }
}
```

### Common Patterns

**Money:**
- Amount + Currency
- Arithmetic operations
- Currency conversion

**Quantity:**
- Amount + Unit
- Unit conversion
- Comparisons

**PersonName:**
- First name, last name, etc.
- Formatting variations
- Display logic

**Email, Phone, URL:**
- String wrapper with validation
- Formatting
- Normalization

### Common Mistakes

1. **Mutable Value Objects**: Allowing state to change
2. **Reference Equality**: Using == instead of .equals()
3. **Missing Validation**: Not validating in constructor
4. **Primitive Obsession**: Not creating value objects for domain concepts
5. **Too Large**: Value objects that are really entities

---

## Aggregates

### Definition
An **Aggregate** is a cluster of associated entities and value objects that are treated as a single unit for data changes. Each aggregate has a root entity (Aggregate Root) and a boundary that defines what's inside.

### Purpose

**Consistency Boundary:**
- Defines transactional boundary
- All changes to aggregate happen in single transaction
- Ensures business invariants are maintained
- Atomic unit of persistence

**Encapsulation:**
- Hides internal structure
- Controls access through root
- Protects invariants

### Aggregate Design Rules

Vaughn Vernon's four rules for effective aggregate design:

#### Rule 1: Protect True Invariants in Consistency Boundaries

**True Invariants:**
- Business rules that must always be consistent
- Cannot be "eventually consistent"
- Must be enforced within a single transaction

**Example:**
- Order total must equal sum of line items (true invariant)
- Customer lifetime value across all orders (eventually consistent)

**Implementation:**
```java
public class Order {  // Aggregate Root
    private List<OrderLine> lines;
    private Money total;

    public void addLine(Product product, Quantity quantity, Money price) {
        lines.add(new OrderLine(product, quantity, price));
        recalculateTotal();  // Maintain invariant immediately
    }

    private void recalculateTotal() {
        this.total = lines.stream()
            .map(OrderLine::subtotal)
            .reduce(Money.ZERO, Money::add);
    }
}
```

#### Rule 2: Design Small Aggregates

**Guidance:**
- Prefer single-entity aggregates
- Only include what's necessary for invariants
- Large aggregates cause:
  - Concurrency issues
  - Performance problems
  - Complex logic
  - Difficult testing

**Examples:**

**❌ Too Large:**
```java
public class Customer {  // Aggregate Root
    private List<Order> allOrders;  // Thousands of orders!
    private List<Address> addresses;
    private PaymentMethods paymentMethods;
    private ShoppingCart cart;
    // Loading customer loads everything - too much!
}
```

**✓ Right-Sized:**
```java
public class Customer {  // Aggregate Root
    private CustomerId id;
    private PersonName name;
    private Email email;
    // Orders are separate aggregates
    // Reference by ID only
}

public class Order {  // Separate Aggregate Root
    private OrderId id;
    private CustomerId customerId;  // Reference, not embedded
    private List<OrderLine> lines;
    // Only what's needed for order invariants
}
```

#### Rule 3: Reference Other Aggregates by Identity

**Principle:**
- Don't hold direct object references to other aggregates
- Use IDs instead
- Prevents inadvertent modification across aggregate boundaries

**Example:**

**❌ Direct Reference:**
```java
public class Order {
    private Customer customer;  // Direct reference - wrong!

    public void updateCustomer() {
        customer.changeEmail(...);  // Modifying different aggregate - wrong!
    }
}
```

**✓ Reference by ID:**
```java
public class Order {
    private CustomerId customerId;  // ID only - correct!

    // If you need Customer data, load it separately
}
```

#### Rule 4: Use Eventual Consistency Outside the Boundary

**Principle:**
- One aggregate per transaction
- Use domain events for cross-aggregate updates
- Accept eventual consistency between aggregates

**Example:**

**Scenario:** When order is placed, update customer's lifetime value

**❌ Wrong - Multiple Aggregates in Transaction:**
```java
public class OrderService {
    public void placeOrder(Order order) {
        order.submit();
        Customer customer = customerRepo.find(order.customerId);
        customer.updateLifetimeValue(order.total);  // Modifying two aggregates!
        // Both changes in same transaction - creates coupling
    }
}
```

**✓ Correct - Domain Events:**
```java
public class Order {
    public void submit() {
        this.status = OrderStatus.SUBMITTED;
        DomainEvents.raise(new OrderSubmitted(this.id, this.customerId, this.total));
    }
}

// Event handler in separate transaction
public class CustomerLifetimeValueUpdater {
    @EventHandler
    public void on(OrderSubmitted event) {
        Customer customer = customerRepo.find(event.customerId);
        customer.updateLifetimeValue(event.total);
        customerRepo.save(customer);  // Separate transaction
    }
}
```

### Aggregate Boundaries

**How to Determine Boundaries:**
1. Start with single-entity aggregates
2. Add only what's needed for true invariants
3. Ask: "Must this be consistent immediately or eventually?"
4. Prefer eventual consistency when possible

**Consistency Decision:**
- **Immediate (same aggregate)**: Order total = sum of lines
- **Eventual (separate aggregates)**: Customer lifetime value
- **Eventual (separate aggregates)**: Inventory after order

### Transaction Scope

**One Aggregate Rule:**
- Modify only one aggregate per transaction
- Load one aggregate
- Make changes
- Save one aggregate
- Domain events notify other aggregates

**Exception:**
- UI-driven transactions may coordinate multiple aggregates
- Application service orchestrates, but each save is separate

### Reference Patterns

**Internal References (within aggregate):**
```java
public class Order {
    private List<OrderLine> lines;  // Composition - owns the lines
}
```

**External References (to other aggregates):**
```java
public class Order {
    private CustomerId customerId;  // ID reference only
    private ProductId productId;    // ID reference only
}
```

### Examples

**Order Aggregate:**
```java
public class Order {  // Aggregate Root
    private OrderId id;
    private CustomerId customerId;  // External reference (ID only)
    private OrderStatus status;
    private List<OrderLine> lines;  // Internal - part of aggregate
    private Money total;

    // Constructor enforces invariants
    public Order(OrderId id, CustomerId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.status = OrderStatus.DRAFT;
        this.lines = new ArrayList<>();
        this.total = Money.zero();
    }

    // Business method maintaining invariants
    public void addLine(ProductId productId, Quantity quantity, Money unitPrice) {
        if (status != OrderStatus.DRAFT) {
            throw new OrderNotModifiableException();
        }
        lines.add(new OrderLine(productId, quantity, unitPrice));
        recalculateTotal();
    }

    public void submit() {
        if (lines.isEmpty()) {
            throw new EmptyOrderException();
        }
        if (total.isLessThan(Money.minimumOrder())) {
            throw new BelowMinimumOrderException();
        }
        this.status = OrderStatus.SUBMITTED;
        DomainEvents.raise(new OrderSubmitted(this.id, this.customerId, this.total));
    }

    private void recalculateTotal() {
        this.total = lines.stream()
            .map(OrderLine::subtotal)
            .reduce(Money.ZERO, Money::add);
    }
}

// OrderLine is not an aggregate root - always accessed through Order
class OrderLine {
    private ProductId productId;
    private Quantity quantity;
    private Money unitPrice;

    Money subtotal() {
        return unitPrice.multiply(quantity.amount());
    }
}
```

### Sizing Guidelines

**Aggregate is too large when:**
- Loading is slow
- Frequent concurrency conflicts
- Complex invariant logic
- Many entities included
- Difficult to understand/test

**Aggregate is too small when:**
- True invariants span multiple aggregates
- Excessive eventual consistency complexity
- Poor user experience from delays

### Common Mistakes

1. **Large-Cluster Aggregates**: Including too much
2. **Modifying Multiple Aggregates**: Breaking the one-aggregate-per-transaction rule
3. **Missing Invariants**: Not identifying true consistency requirements
4. **Object References**: Holding references to other aggregates
5. **Anemic Aggregate Root**: Root doesn't enforce invariants

---

## Aggregate Root

### Definition
The **Aggregate Root** is the single entity within an aggregate that serves as the entry point for all access to the aggregate. External objects may only hold references to the root.

### Responsibilities

1. **Control Access**
   - Only entry point to aggregate
   - All changes go through root
   - Protects internal structure

2. **Enforce Invariants**
   - Ensures business rules across entire aggregate
   - Validates all state changes
   - Maintains consistency

3. **Coordinate Lifecycle**
   - Controls creation of internal entities
   - Manages addition/removal of parts
   - Handles aggregate deletion

4. **Publish Domain Events**
   - Notifies external systems of changes
   - Communicates with other aggregates

### Access Control

**External Access Rules:**
- ✓ External objects can hold reference to root
- ✗ External objects cannot hold references to internal entities
- ✓ External objects invoke methods on root only
- ✗ External objects cannot directly modify internal parts

**Example:**

**❌ Wrong:**
```java
Order order = orderRepo.find(orderId);
OrderLine line = order.getLines().get(0);  // Getting internal entity
line.setQuantity(newQuantity);  // Directly modifying - bypasses root!
```

**✓ Correct:**
```java
Order order = orderRepo.find(orderId);
order.updateLineQuantity(lineId, newQuantity);  // Through root
```

### Encapsulation

**Hide Internal Structure:**
```java
public class Order {
    private List<OrderLine> lines;

    // ❌ Don't expose internal collection
    public List<OrderLine> getLines() {
        return lines;
    }

    // ✓ Expose specific queries only
    public int lineCount() {
        return lines.size();
    }

    public boolean hasProduct(ProductId productId) {
        return lines.stream()
            .anyMatch(line -> line.productId().equals(productId));
    }

    // ✓ Expose operations, not data
    public void updateLineQuantity(OrderLineId lineId, Quantity newQuantity) {
        OrderLine line = findLine(lineId);
        line.changeQuantity(newQuantity);
        recalculateTotal();
    }
}
```

### Invariant Enforcement

**Root validates all changes:**
```java
public class ShoppingCart {  // Aggregate Root
    private CustomerId customerId;
    private List<CartItem> items;
    private int maxItems = 50;

    public void addItem(ProductId productId, Quantity quantity) {
        // Root enforces invariants
        if (items.size() >= maxItems) {
            throw new CartFullException();
        }
        if (hasItem(productId)) {
            updateItemQuantity(productId, quantity);
        } else {
            items.add(new CartItem(productId, quantity));
        }
        // Root coordinates changes
    }
}
```

### Design Guidelines

1. **One Root per Aggregate**
   - Exactly one entity is the root
   - All others are internal
   - Root is often the "main" concept

2. **Root ID is Aggregate ID**
   - Aggregate identified by root's ID
   - Repository uses root ID

3. **Intention-Revealing Methods**
   - Methods express business operations
   - `placeOrder()` not `setStatus(PLACED)`
   - `suspend()` not `setActive(false)`

4. **Minimal Public Surface**
   - Expose only what's necessary
   - Hide implementation details
   - Return value objects when possible

### Examples

**Order Aggregate Root:**
```java
public class Order {  // ROOT
    private OrderId id;  // Root ID = Aggregate ID
    private CustomerId customerId;
    private OrderStatus status;
    private List<OrderLine> lines;  // Internal entities
    private ShippingAddress shippingAddress;  // Internal value object
    private Money total;

    // Only through root
    public void addLine(ProductId productId, Quantity quantity, Money price) {
        if (status != OrderStatus.DRAFT) {
            throw new OrderNotModifiableException();
        }
        lines.add(new OrderLine(productId, quantity, price));
        recalculateTotal();
    }

    // Only through root
    public void changeShippingAddress(ShippingAddress newAddress) {
        if (status == OrderStatus.SHIPPED) {
            throw new OrderAlreadyShippedException();
        }
        this.shippingAddress = newAddress;
        DomainEvents.raise(new OrderShippingAddressChanged(this.id, newAddress));
    }
}
```

---

*[Continuing in next section due to length]*

## Repositories

### Definition
A **Repository** provides the illusion of an in-memory collection of aggregates, abstracting the underlying persistence mechanism. There is one repository per aggregate root.

### Purpose
- **Persistence Abstraction**: Hide database details from domain
- **Collection Metaphor**: Acts like an in-memory set of objects
- **Reconstitution**: Rebuild aggregates from storage
- **Lifecycle Management**: Add, remove, find aggregates

### One Repository per Aggregate Root

**Rule:**
- Each aggregate root has exactly one repository
- Repository stores/retrieves entire aggregate
- Internal entities never have their own repositories

**Example:**
```java
// ✓ Correct
OrderRepository orderRepo;
CustomerRepository customerRepo;

// ❌ Wrong
OrderRepository orderRepo;
OrderLineRepository orderLineRepo;  // NO! OrderLine is internal to Order
```

### Repository Interface

**Design Principles:**
1. **Collection-like**: Add, remove, find methods
2. **Domain-focused**: Uses domain language
3. **Technology-agnostic**: No SQL or persistence details
4. **Simple queries**: Complex queries may need separate read models

**Example Interface:**
```java
public interface OrderRepository {
    // Save (insert or update)
    void save(Order order);

    // Find by ID
    Optional<Order> findById(OrderId id);

    // Simple domain queries
    List<Order> findByCustomer(CustomerId customerId);
    List<Order> findRecentOrders(int limit);

    // Remove (rarely used, often soft-delete in domain instead)
    void remove(Order order);
}
```

### Implementation

Repository implementations belong in infrastructure layer:

```java
// Infrastructure layer
public class JpaOrderRepository implements OrderRepository {
    private EntityManager entityManager;

    @Override
    public void save(Order order) {
        entityManager.persist(order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(
            entityManager.find(OrderEntity.class, id.value())
        ).map(this::toDomain);
    }

    private Order toDomain(OrderEntity entity) {
        // Map persistence model to domain model
    }
}
```

### Query Strategies

**Simple Queries:**
- By ID (most common)
- By simple attributes
- Recent items
- Belong in repository

**Complex Queries:**
- Multi-criteria searches
- Aggregations/reporting
- Cross-aggregate queries
- May need separate read model (CQRS)

### Repository vs DAO

**Repository (DDD):**
- Domain concept
- Aggregate-focused
- Collection metaphor
- Part of domain layer (interface)
- Domain language

**DAO (Data Access Object):**
- Technical pattern
- Table-focused
- CRUD operations
- Infrastructure concern
- Database language

---

## Domain Services

### Definition
A **Domain Service** is a stateless operation in the domain layer that doesn't naturally belong to an Entity or Value Object. It implements domain logic that involves multiple domain objects.

### When to Use Domain Services

**Use Domain Service when:**
- ✓ Operation involves multiple entities/aggregates
- ✓ Logic doesn't belong to any single entity
- ✓ Operation is stateless
- ✓ It's a significant domain concept
- ✓ Putting it on an entity would be awkward

**Don't use Domain Service when:**
- ✗ Logic belongs to a specific entity
- ✗ It's just orchestration (use Application Service)
- ✗ It's infrastructure concern (use Infrastructure Service)

### Characteristics

1. **Stateless**: No instance variables
2. **Domain Logic**: Contains business rules
3. **Multi-Object Operations**: Works with several domain objects
4. **Part of Ubiquitous Language**: Named after domain concept
5. **Domain Layer**: Lives in domain layer

### Domain Service vs Application Service

**Domain Service:**
```java
// Domain layer - contains business logic
public class PricingService {
    public Money calculatePrice(Product product, Customer customer, Quantity quantity) {
        Money basePrice = product.price().multiply(quantity.amount());

        // Business logic - volume discounts
        if (quantity.isGreaterThan(Quantity.of(100))) {
            basePrice = basePrice.applyDiscount(Percentage.of(10));
        }

        // Business logic - customer tier discounts
        if (customer.tier() == CustomerTier.GOLD) {
            basePrice = basePrice.applyDiscount(Percentage.of(5));
        }

        return basePrice;
    }
}
```

**Application Service:**
```java
// Application layer - orchestration only
public class OrderApplicationService {
    private OrderRepository orderRepo;
    private PricingService pricingService;

    @Transactional
    public void placeOrder(PlaceOrderCommand command) {
        // Load domain objects
        Customer customer = customerRepo.find(command.customerId);
        Product product = productRepo.find(command.productId);

        // Delegate to domain service for business logic
        Money price = pricingService.calculatePrice(
            product, customer, command.quantity
        );

        // Create aggregate
        Order order = new Order(customer.id());
        order.addLine(product.id(), command.quantity, price);
        order.submit();

        // Persist
        orderRepo.save(order);

        // No business logic here - just orchestration!
    }
}
```

### Examples

**Transfer Service:**
```java
public class MoneyTransferService {
    public void transfer(Account from, Account to, Money amount) {
        from.debit(amount);
        to.credit(amount);
        // Coordinates two aggregates
        // Domain logic: ensure both happen together
    }
}
```

**Authentication Service:**
```java
public class AuthenticationService {
    private PasswordHasher hasher;  // Infrastructure interface

    public boolean authenticate(User user, String password) {
        // Domain logic
        if (user.isLocked()) {
            throw new AccountLockedException();
        }

        boolean valid = hasher.verify(password, user.passwordHash());

        if (!valid) {
            user.recordFailedAttempt();
            if (user.failedAttempts() >= 3) {
                user.lock();
            }
        }

        return valid;
    }
}
```

### Design Guidelines

1. **Name from Domain**: Use Ubiquitous Language
2. **Keep Stateless**: No instance variables (except injected dependencies)
3. **Thin Services**: If service is doing too much, logic might belong in entities
4. **Clear Dependencies**: Depend only on domain objects and other services

### Common Mistakes

1. **Anemic Domain Model**: Putting all logic in services, entities become data containers
2. **Application Logic in Domain Service**: Orchestration belongs in application layer
3. **Infrastructure in Domain Service**: Database, HTTP, etc., belong in infrastructure
4. **Too Many Services**: Most logic should be in entities/aggregates

---

## Factories

### Definition
A **Factory** encapsulates the knowledge needed to create complex domain objects, especially aggregates, ensuring that all invariants are satisfied at creation time.

### When to Use Factories

**Use Factory when:**
- ✓ Object creation is complex
- ✓ Multiple steps or validations required
- ✓ Invariants must be satisfied at creation
- ✓ Hide creation details from client
- ✓ Reconstitution from storage is complex

**Simple creation - no factory needed:**
```java
// Simple enough, no factory needed
Email email = new Email("user@example.com");
```

**Complex creation - use factory:**
```java
// Complex, use factory
Order order = OrderFactory.createFrom(cart, customer, shippingAddress);
```

### Types of Factories

**1. Factory Method (on Entity/Aggregate):**
```java
public class Customer {
    // Factory method
    public static Customer register(Email email, PersonName name) {
        CustomerId id = CustomerId.generate();
        Customer customer = new Customer(id, email, name);
        customer.status = CustomerStatus.PENDING_VERIFICATION;
        DomainEvents.raise(new CustomerRegistered(id, email));
        return customer;
    }
}
```

**2. Dedicated Factory Class:**
```java
public class OrderFactory {
    public Order createFromCart(
        ShoppingCart cart,
        Customer customer,
        ShippingAddress address
    ) {
        OrderId id = OrderId.generate();
        Order order = new Order(id, customer.id());

        for (CartItem item : cart.items()) {
            Money price = pricingService.calculatePrice(
                item.product(),
                customer,
                item.quantity()
            );
            order.addLine(item.productId(), item.quantity(), price);
        }

        order.setShippingAddress(address);
        return order;
    }
}
```

**3. Abstract Factory (multiple related objects):**
```java
public interface PaymentFactory {
    Payment createPayment(Money amount);
    Refund createRefund(Payment original, Money amount);
}

public class CreditCardPaymentFactory implements PaymentFactory {
    // Creates credit card specific implementations
}
```

### Reconstitution

Factories can also rebuild aggregates from storage:

```java
public class OrderFactory {
    // Creation
    public Order createNew(CustomerId customerId) {
        return new Order(OrderId.generate(), customerId);
    }

    // Reconstitution from database
    public Order reconstitute(
        OrderId id,
        CustomerId customerId,
        OrderStatus status,
        List<OrderLineData> lineData,
        Money total
    ) {
        // Use special constructor or builder that bypasses
        // validation (data from DB is assumed valid)
        return Order.reconstitute(id, customerId, status, lineData, total);
    }
}
```

### Factory Location

**Where do factories live?**
- Factory methods: On the entity/aggregate itself
- Factory classes with domain dependencies: Domain layer
- Factory classes with infrastructure dependencies: Infrastructure layer
- Repository often plays factory role for reconstitution

---

## Domain Events

### Definition
A **Domain Event** is something that happened in the domain that domain experts care about. Events are immutable facts about the past.

### Characteristics

1. **Past Tense**: "OrderPlaced", "CustomerRegistered"
2. **Immutable**: Cannot be changed after creation
3. **Contain Relevant Data**: What happened and when
4. **Published by Aggregates**: Raised when state changes

### Purpose

**Why Domain Events?**
- Make side effects explicit
- Decouple aggregates
- Enable eventual consistency
- Create audit trail
- Support event sourcing
- Enable reactive architectures

### Event Naming

**Pattern:** `<AggregateType><Action>` in past tense

**Examples:**
- `OrderPlaced`
- `CustomerEmailChanged`
- `PaymentProcessed`
- `InventoryAdjusted`
- `AccountSuspended`

### Event Structure

```java
public class OrderPlaced implements DomainEvent {
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money total;
    private final Instant occurredAt;

    public OrderPlaced(OrderId orderId, CustomerId customerId, Money total) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.total = total;
        this.occurredAt = Instant.now();
    }

    // Getters only - immutable
}
```

### Publishing Events

**Option 1: Static Event Registry**
```java
public class Order {
    public void submit() {
        this.status = OrderStatus.SUBMITTED;
        DomainEvents.raise(new OrderSubmitted(this.id, this.customerId, this.total));
    }
}
```

**Option 2: Aggregate Collects Events**
```java
public abstract class AggregateRoot {
    private List<DomainEvent> events = new ArrayList<>();

    protected void addEvent(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}

public class Order extends AggregateRoot {
    public void submit() {
        this.status = OrderStatus.SUBMITTED;
        addEvent(new OrderSubmitted(this.id, this.customerId, this.total));
    }
}

// Repository publishes events
public class OrderRepository {
    public void save(Order order) {
        persist(order);
        publishEvents(order.getEvents());
    }
}
```

### Handling Events

**Handlers are separate from publishers:**

```java
@EventHandler
public class CustomerLifetimeValueUpdater {
    public void on(OrderSubmitted event) {
        Customer customer = customerRepo.find(event.customerId());
        customer.addToLifetimeValue(event.total());
        customerRepo.save(customer);
    }
}

@EventHandler
public class InventoryManager {
    public void on(OrderSubmitted event) {
        for (OrderLine line : event.orderLines()) {
            inventory.reserve(line.productId(), line.quantity());
        }
    }
}
```

### Benefits

1. **Decoupling**: Aggregates don't know about side effects
2. **Extensibility**: Add new handlers without modifying aggregates
3. **Audit Trail**: Events provide history
4. **Integration**: Events can be published to external systems
5. **Testability**: Easy to test event handlers in isolation

---

## Modules

### Definition
**Modules** organize domain concepts into cohesive groups, following the domain structure rather than technical concerns.

### Purpose
- Organize code by domain concepts
- Create high cohesion within modules
- Create low coupling between modules
- Reflect Ubiquitous Language
- Manage complexity

### Guidelines

1. **Name from Domain**: Module names should be domain concepts
2. **High Cohesion**: Related concepts in same module
3. **Low Coupling**: Minimal dependencies between modules
4. **Align with Bounded Contexts**: Modules often map to contexts

**Example Structure:**
```
com.example.ecommerce.sales
├── Order
├── OrderLine
├── OrderRepository
├── OrderFactory
└── events
    ├── OrderPlaced
    └── OrderShipped

com.example.ecommerce.inventory
├── Product
├── Inventory
├── InventoryRepository
└── events
    └── InventoryAdjusted

com.example.ecommerce.customer
├── Customer
├── CustomerRepository
└── events
    ├── CustomerRegistered
    └── CustomerSuspended
```

---

## Tactical Patterns Summary

### Pattern Selection Guide

**For modeling domain concepts:**
- Has identity & lifecycle? → **Entity**
- Defined by attributes only? → **Value Object**

**For grouping and consistency:**
- Need consistency boundary? → **Aggregate**
- Entry point to aggregate? → **Aggregate Root**

**For persistence:**
- Save/retrieve aggregates? → **Repository**

**For domain logic:**
- Logic on single entity? → **Entity method**
- Logic spans multiple objects? → **Domain Service**
- Orchestration only? → **Application Service**

**For creation:**
- Simple creation? → **Constructor**
- Complex creation? → **Factory**

**For communication:**
- Something happened? → **Domain Event**

**For organization:**
- Group related concepts → **Module**

---

## References

- Evans, Eric (2003). "Domain-Driven Design" - Part II: The Building Blocks
- Vernon, Vaughn (2013). "Implementing Domain-Driven Design" - Chapters on Entities, Value Objects, Aggregates
- Fowler, Martin. "Value Object" - https://martinfowler.com/bliki/ValueObject.html
- Microsoft Learn. "Implementing Value Objects" and "Domain Events"

---

*Document Status: Tactical patterns complete - ready for Ubiquitous Language framework*
