# For developers

Searck provides small bits for developers to add on to the mod if they want to,
designed to be as simple as possible for people to understand.

## Gradle Setup ![Release](https://maven.tiazzz.me/api/badge/latest/releases/org/tywrapstudios/searck?color=40c14a&name=Maven&prefix=v)

### Groovy

```groovy
repositories {
    maven {
        name = "Tywrap Studios Releases"
        url = "https://maven.tiazzz.me/releases"
    }
}

dependencies {
    implementation "org.tywrapstudios:searck:${project.searck_version}"
}
```

### Kotlin DSL

```kotlin
repositories {
    maven("https://maven.tiazzz.me/releases") {
        name = "Tywrap Studios Releases"
    }
}

dependencies {
    implementation("org.tywrapstudios:searck:${project.searck_version}")
}
```

Make a value in your `gradle.properties` called `searck_version` and set it to the version you want to use:
```properties
searck_version=<VERSION>
```

Alternatively, you can use a version catalogue with `libs.versions.toml`:

```toml
[versions]
searck = "<VERSION>"

[libraries]
searck = { module = "org.tywrapstudios:searck", version.ref = "searck" }
```

```kotlin
/* repositories */
dependencies {
    implementation(libs.searck)
}
```

The Main Maven repository is self-hosted and may occasionally have an outage. Check our
official [Status Page](https://status.tiazzz.me) to check. In the case it is down, there
is a backup repository available
at `https://repo.repsy.io/itstiazzz/maven`. Simply replace the
URL in `repositories` with this one, or add it as a second repository.

## Implementation

### Adding calculation operators

Operators implement the `IOperator` interface, a recommended implementation
of this is through the use of an `enum` like in Searck itself.

Simply provide all the values:  
`val associativity: Associativity`: See [this Wikipedia page](https://en.wikipedia.org/wiki/Operator_associativity)  
`val precedence: Int`: The higher the number, the higher the precedence when it comes to calculation order (a.k.a. PEMDAS). Check Searck's `Operators` enum for predefined precedences.  
`val symbol: String`: The "symbol" of the operator, like `+` or `-`. This is what the user will type in to use the operator. For functions, do NOT include `()` in the `symbol`!  
`val singleOperand: Boolean`: Whether the operator takes in just one operand, like `sqrt()` or `tan()`.  
`val operation: Operation`: `Operation` is actually a function, it takes in two doubles, and outputs a single double. In the lambda function you can define what the actual operation is when done on the two operands. If the operator is `singleOperand`, both values will be the same, however, it is recommended to use the `right` value, as most `singleOperand` operators are considered right-associative.

To allow users to actually use the operators, you need to "register" them in a sense.
"Registering" them is actually just adding the values to a few maps in `ShuntingYard`.  
`ShuntingYard.OPS`, map ALL your new operators here, functions, actual operators, whatever. It all goes in here! Map the `symbol` to the `IOperator` object.  
`ShuntingYard.FUNCTIONS`, map all operators in here that are functions (multi-character operators). Map the `symbol` to the `IOperator` object.  
`ShuntingYard.FUNCTION_CHARS`, add every character of your functions in here. This is important! What we mean by this, is that you break apart every
single one of your functions, and grab every character of it, and add it to this set. E.g. for `sqrt`, we added `s`, `q`, `r` and `t`.

### Adding a custom `ItemIndexer`

In order for the search menu to be able to look up every item or block, we index it and
provide a couple of functions for it to properly search the items.

Indexers implement the `ItemIndexer` interface, which already has some KDocs to get
you started.

`ItemIndexer`:  
Interface that can be used to make different "indexers" for use during search.

Implementation has to be a class with an empty constructor. It
cannot be an `object` or have parameters, because this will break the reflection
used in `ItemIndex.getActive`.

You have to implement a few functions:

`index(): Unit`:  
Run logic to "index" items to names (or vice versa, just make sure you can
properly implement `getNames` and `getItemsForName`).

This gets called when Minecraft resources are reloaded (for the language)
or if the indexer is fetched using `ItemIndex.getActive` and has not yet
been indexed at least once. It can be run at any arbitrary time during the
lifecycle though, so beware of that in your implementation.

When this indexer is the selected indexer in config, this method is guaranteed to be called
twice during initial resources load. Once because it's technically not yet been indexed, and then once
because the resource manager has also technically been reloaded.

`getNames(): List<String>`:  
Returns a list of all the names (as Strings) of the indexed `ItemLike`s.

`getItemsForName(): List<ItemLike>`:  
Returns a list of `ItemLike`s which name match the name given.  
Parameter: `name` - Name of the `ItemLike`(s) searched for

#### Using the indexer
In config, select the Custom Indexer (`CUSTOM`) as your search
indexer. Then, provide in the text box below, the package and class name to your custom
indexer. For example: `com.example.search.ExampleIndexer`