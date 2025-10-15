# PoEAA Integration with DDD

## Overview

Martin Fowler's **Patterns of Enterprise Application Architecture** (PoEAA) provides complementary patterns that work well with Domain-Driven Design. While DDD focuses on domain modeling, PoEAA provides broader architectural patterns including presentation, persistence, and session state management.

**Key Insight:** PoEAA and DDD are not competing approaches - they solve different problems and integrate naturally.

---

## Pattern Catalog Mapping

### Domain Logic Patterns

#### Domain Model (PoEAA) = Domain Layer (DDD)

**PoEAA Domain Model:**
- An object model of the domain incorporating behavior and data
- Rich objects with business logic
- Complex business rules in objects

**DDD Domain Model:**
- Entities, Value Objects, Aggregates
- Domain Services
- Enforces invariants

**Integration:**
- Same concept, DDD provides more detailed patterns
- PoEAA introduces the pattern, DDD elaborates how to implement it
- DDD's tactical patterns (Entity, Value Object, Aggregate) are refinements

**Example:**
```java
// PoEAA Domain Model - general concept
public class Product {
    private Money price;
    private Inventory inventory;

    public void adjustPrice(Money newPrice) {
        // Business logic in domain object
    }
}

// DDD elaborates with Entity, Value Object, Aggregate
public class Product {  // Entity
    private ProductId id;  // Value Object for identity
    private Money price;   // Value Object
    private Inventory inventory;  // Value Object

    // Business method - same idea as PoEAA
    public void adjustPrice(Money newPrice, PricingPolicy policy) {
        if (!policy.allows(newPrice)) {
            throw new InvalidPriceException();
        }
        Money oldPrice = this.price;
        this.price = newPrice;
        DomainEvents.raise(new ProductPriceChanged(this.id, oldPrice, newPrice));
    }
}
```

---

#### Service Layer (PoEAA) = Application Service (DDD)

**PoEAA Service Layer:**
- Defines application boundary
- Orchestrates domain objects
- Transaction control
- Coordinates responses

**DDD Application Service:**
- Use case orchestration
- Transaction boundaries
- Delegates to domain layer
- No business logic

**Integration:**
- Essentially the same pattern
- Both sit above domain layer
- Both coordinate, don't contain business logic
- DDD emphasizes distinction from Domain Services

**Example:**
```java
// PoEAA Service Layer
public class OrderService {
    @Transactional
    public void placeOrder(OrderRequest request) {
        // Orchestration
        Customer customer = customerRepo.find(request.customerId);
        Order order = new Order(customer);
        order.addItems(request.items);
        orderRepo.save(order);
    }
}

// DDD Application Service - same pattern
public class PlaceOrderService {
    @Transactional
    public void execute(PlaceOrderCommand command) {
        // Orchestration only, no business logic
        Customer customer = customerRepo.find(command.customerId);
        Order order = customer.createOrder();  // Domain logic in domain
        order.addItems(command.items);
        orderRepo.save(order);
        eventPublisher.publish(new OrderPlaced(order.id()));
    }
}
```

---

### Data Source Patterns

#### Repository (PoEAA) vs Repository (DDD)

**PoEAA Repository:**
- Mediates between domain and data mapping layers
- Collection-like interface
- Queries using Specification pattern

**DDD Repository:**
- One per Aggregate Root
- Reconstitutes entire aggregate
- Collection metaphor
- Domain-oriented queries

**Differences:**
- **Scope**: PoEAA can be per entity; DDD is per aggregate
- **Granularity**: DDD repository manages entire aggregate atomically
- **Purpose**: DDD emphasizes bounded context isolation

**Integration:**
```java
// Both use collection-like interface
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomer(CustomerId customerId);
}

// DDD adds: One repo per aggregate, loads entire aggregate
// PoEAA adds: Can use Specification for complex queries
public interface OrderRepository {
    List<Order> find(Specification<Order> spec);  // PoEAA pattern
}
```

---

#### Data Mapper (PoEAA)

**PoEAA Data Mapper:**
- Moves data between objects and database
- Keeps domain objects independent of persistence
- Mapper knows both domain and database schemas

**DDD Integration:**
- Data Mapper implements DDD Repository
- Maps between domain model and persistence model
- Preserves domain model purity

**Example:**
```java
// Data Mapper implementing DDD Repository
public class JpaOrderMapper implements OrderRepository {
    private EntityManager em;

    @Override
    public Order findById(OrderId id) {
        OrderEntity entity = em.find(OrderEntity.class, id.value());
        return toDomain(entity);  // Data Mapper translation
    }

    private Order toDomain(OrderEntity entity) {
        // Map from persistence to domain
        Order order = new Order(
            new OrderId(entity.getId()),
            new CustomerId(entity.getCustomerId())
        );
        for (OrderLineEntity lineEntity : entity.getLines()) {
            order.addLine(
                new ProductId(lineEntity.getProductId()),
                Quantity.of(lineEntity.getQuantity()),
                Money.of(lineEntity.getPrice(), lineEntity.getCurrency())
            );
        }
        return order;
    }
}
```

---

### Presentation Patterns (View Models)

#### Presentation Model / MVVM

**PoEAA Presentation Model:**
- State and behavior of presentation
- Separate from view
- View observes presentation model

**DDD Integration:**
- Lives in presentation layer
- Uses domain model as source
- Transforms domain for UI needs

**Example:**
```java
// Presentation Model for Order view
public class OrderViewModel {
    private Order order;  // Domain model
    private Customer customer;
    private List<Product> products;

    // UI-specific properties
    public String getFormattedTotal() {
        return order.total().format(Locale.US);
    }

    public String getStatusDisplay() {
        return order.status().displayName();
    }

    public List<OrderLineViewModel> getLines() {
        return order.lines().stream()
            .map(this::toViewModel)
            .collect(toList());
    }

    // Transform domain to UI-friendly format
    private OrderLineViewModel toViewModel(OrderLine line) {
        Product product = products.stream()
            .filter(p -> p.id().equals(line.productId()))
            .findFirst()
            .orElseThrow();

        return new OrderLineViewModel(
            product.name(),
            line.quantity().amount(),
            line.unitPrice().format(Locale.US),
            line.subtotal().format(Locale.US)
        );
    }
}
```

---

#### Model-View-Controller (MVC)

**PoEAA MVC:**
- Separates presentation into three roles
- Model holds data and logic
- View displays
- Controller handles input

**DDD Integration:**
- Model = Domain Model (or ViewModel)
- Controller delegates to Application Service
- View uses Ubiquitous Language

**Example:**
```java
// Controller in MVC/DDD architecture
@Controller
public class OrderController {
    private PlaceOrderService placeOrderService;  // Application Service
    private OrderRepository orderRepo;

    @PostMapping("/orders")
    public String placeOrder(@ModelAttribute PlaceOrderForm form) {
        // Controller delegates to Application Service
        PlaceOrderCommand command = new PlaceOrderCommand(
            new CustomerId(form.getCustomerId()),
            form.getItems()
        );

        OrderId orderId = placeOrderService.execute(command);

        return "redirect:/orders/" + orderId.value();
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable String id, Model model) {
        Order order = orderRepo.findById(new OrderId(id))
            .orElseThrow(() -> new OrderNotFoundException(id));

        // Transform domain to view model
        OrderViewModel viewModel = OrderViewModel.from(order);
        model.addAttribute("order", viewModel);

        return "order-detail";
    }
}
```

---

## Layered Architecture Integration

### Four-Layer Architecture (PoEAA + DDD)

```
┌─────────────────────────────────────┐
│   Presentation Layer                │  PoEAA: MVC, Presentation Model
│   - Controllers                     │  DDD: Uses Ubiquitous Language
│   - View Models                     │
│   - Views                           │
├─────────────────────────────────────┤
│   Application Layer                 │  PoEAA: Service Layer
│   - Application Services            │  DDD: Use Case Orchestration
│   - DTOs                            │      Transaction Boundaries
│   - Transaction Management          │
├─────────────────────────────────────┤
│   Domain Layer                      │  PoEAA: Domain Model
│   - Entities                        │  DDD: Rich Domain Model
│   - Value Objects                   │       Aggregates, Services
│   - Aggregates                      │       Domain Events
│   - Domain Services                 │
│   - Domain Events                   │
├─────────────────────────────────────┤
│   Infrastructure Layer              │  PoEAA: Data Mappers, Repositories
│   - Repository Implementations      │  DDD: Technical Details
│   - Data Mappers                    │       Persistence, External Services
│   - External Service Clients        │
│   - Messaging                       │
└─────────────────────────────────────┘
```

### Pattern Allocation by Layer

**Presentation Layer:**
- PoEAA: MVC, MVP, MVVM, Presentation Model
- DDD: View Models using Ubiquitous Language

**Application Layer:**
- PoEAA: Service Layer, Transaction Script (for simple cases)
- DDD: Application Services, Command Handlers

**Domain Layer:**
- PoEAA: Domain Model
- DDD: Entities, Value Objects, Aggregates, Domain Services, Domain Events

**Infrastructure Layer:**
- PoEAA: Repository, Data Mapper, Unit of Work, Identity Map
- DDD: Repository implementations, Anti-Corruption Layers

---

## Pattern Combinations

### Repository + Data Mapper

```java
// Repository interface (DDD - domain layer)
public interface CustomerRepository {
    void save(Customer customer);
    Optional<Customer> findById(CustomerId id);
}

// Data Mapper implementation (PoEAA - infrastructure layer)
public class JpaCustomerRepository implements CustomerRepository {
    private EntityManager entityManager;

    @Override
    public void save(Customer customer) {
        CustomerEntity entity = toEntity(customer);  // Data Mapper
        entityManager.merge(entity);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        CustomerEntity entity = entityManager.find(
            CustomerEntity.class,
            id.value()
        );
        return Optional.ofNullable(entity).map(this::toDomain);  // Data Mapper
    }

    // Data Mapper methods
    private CustomerEntity toEntity(Customer customer) { }
    private Customer toDomain(CustomerEntity entity) { }
}
```

### Service Layer + Domain Model

```java
// Service Layer (Application Service in DDD)
public class TransferMoneyService {
    private AccountRepository accountRepo;
    private TransferPolicy transferPolicy;  // Domain Service

    @Transactional
    public void execute(TransferMoneyCommand command) {
        // Service Layer orchestrates
        Account fromAccount = accountRepo.findById(command.fromAccountId());
        Account toAccount = accountRepo.findById(command.toAccountId());

        // Domain Service contains business logic
        transferPolicy.validateTransfer(fromAccount, toAccount, command.amount());

        // Domain Model executes transfer
        fromAccount.debit(command.amount());
        toAccount.credit(command.amount());

        // Service Layer saves
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);

        // Note: In strict DDD, modifying two aggregates violates rules
        // Should use domain events for eventual consistency
    }
}
```

---

## Complementary Patterns Summary

| PoEAA Pattern | DDD Equivalent/Integration | Notes |
|---------------|---------------------------|-------|
| Domain Model | Domain Layer (Entities, VOs, Aggregates) | DDD elaborates implementation |
| Service Layer | Application Service | Same concept |
| Repository | Repository (per Aggregate) | DDD adds aggregate scoping |
| Data Mapper | Repository Implementation | Implements persistence |
| Unit of Work | Transaction Management | Often in Application Service |
| Identity Map | Entity caching | ORM feature |
| Lazy Load | Aggregate loading strategy | Be careful with aggregate boundaries |
| Transaction Script | Simple Application Service | For simple cases |
| Table Module | Not used in DDD | DDD prefers Domain Model |
| Presentation Model | View Model | Transforms domain for UI |
| MVC / MVP / MVVM | Presentation patterns | UI organization |

---

## Best Practices

### Use PoEAA for:
1. **Presentation Layer**: MVC, MVVM for UI organization
2. **Persistence**: Data Mapper, Repository implementation strategies
3. **Session Management**: Unit of Work, Identity Map
4. **Service Layer**: Transaction and orchestration patterns

### Use DDD for:
1. **Domain Modeling**: Tactical patterns (Entity, VO, Aggregate)
2. **Strategic Design**: Bounded Contexts, Context Mapping
3. **Domain Logic**: Where business rules live
4. **Ubiquitous Language**: Naming and communication

### Integration Points

**Clear Separation:**
- Domain layer knows nothing of persistence (PoEAA patterns)
- Presentation uses domain model but doesn't contain business logic
- Application layer uses both: domain model + PoEAA transaction patterns

**Dependency Direction:**
```
Presentation → Application → Domain
                    ↓
            Infrastructure (PoEAA patterns)
```

---

## References

- Fowler, Martin (2002). "Patterns of Enterprise Application Architecture"
- Fowler, Martin. "Catalog of Patterns" - https://martinfowler.com/eaaCatalog/
- Evans, Eric (2003). "Domain-Driven Design"
- Microsoft Learn. "DDD with CQRS Patterns"

---

*Document Status: PoEAA integration complete*
